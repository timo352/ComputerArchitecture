package ManoAssembler;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Scanner;

import java.io.FileNotFoundException;
import java.io.IOException;

import java.util.ArrayList;

/*
 Timothy Smith II
 Project #2: Assembler
 March 4th, 2016
 */
public class ManoAssembler {

	private int lineCounter = 0;

	private File assemblyFile = null;
	private File machineFile = null;
	private File labelFile = null;

	private final ArrayList<String> assemblyCode = new ArrayList<>();
	private final ArrayList<ManoLabel> labels = new ArrayList<>();
	private final ArrayList<ManoInstruction> instructions = new ArrayList<>();

	// constructor for a JavaAssembler
	public ManoAssembler(String assembly, String label, String machine) throws ManoAssemblerException{
		assemblyFile = new File(assembly);
		labelFile = new File(label);
		machineFile = new File(machine);

		readInAssembly();

		firstPass();
		secondPass();

		printLabelFile();
		printMachineFile();
	}

	// method to read in the assembly code to an ArrayList
	private void readInAssembly() {
		Scanner in;
		try {
			in = new Scanner(assemblyFile);

			while (in.hasNext()) {
				String line = in.nextLine();

				while (line.length() < 16) {
					line += " ";
				}
				assemblyCode.add(line);
			}

		} catch (FileNotFoundException ex) {
			System.out.println("Problem reading from assembly file. Program exiting....");
			System.exit(1);
		}
	}

	// method to run through the assembly code and create the label pointers
	private void firstPass() {

		lineCounter = 0;

		// read through each line
		for (int i = 0; i < assemblyCode.size(); i++) {
			String line = assemblyCode.get(i);

			// check for a label
			if (!line.substring(0, 3).equals("   ")) {

				if (line.charAt(0) != ' ' && line.charAt(1) != ' ' && line.charAt(2) != ' ') {
					labels.add(new ManoLabel(line.substring(0, 3).toUpperCase(), lineCounter));

				} else {
					displayError("Illegal label name. Must be three characters.", line);
				}

			} // check for an ORG
			else if (line.substring(5, 8).toUpperCase().equals("ORG")) {
				// add check to make sure the number exists

				// assign the memory address to line counter (translating from hex)
				// subtract one so the increment doesn't set it to the wrong location
				lineCounter = Integer.parseInt(line.substring(9, 12), 16) - 1;
			} else if (line.substring(5, 8).toUpperCase().equals("END")) {
				break;
			}

			lineCounter++;
		}
	}

	// method to run through the assembly code and create the machine code
	private void secondPass() {

		lineCounter = 0;

		// read through each line		
		for (int i = 0; i < assemblyCode.size(); i++) {
			String line = assemblyCode.get(i);

			if (line.charAt(4) != ' ' || line.charAt(8) != ' ') {
				displayError("Illegal characters where there should be spaces.", line);
			}

			// check for ORG (move line counter)
			if (line.substring(5, 8).toUpperCase().equals("ORG")) {
				// assign the memory address to line counter (translating from hex)
				// subtract one so the increment doesn't set it to the wrong location
				lineCounter = Integer.parseInt(line.substring(9, 12), 16) - 1;
			} // check for END (end assembling)
			else if (line.substring(5, 8).toUpperCase().equals("END")) {
				break;
			} // if it is a normal instruction
			else {
				String comment = "";
				// get machine code
				int code = retrieveManoIns(line);

				// check for a comment
				if (line.indexOf('/', 16) != -1) {
					int index = line.indexOf('/');
					comment = line.substring(index + 1).trim();
				}

				instructions.add(new ManoInstruction(lineCounter, code, comment));
			}
			lineCounter++;
		}
	}

	// method to construct the machine code given a line of assembly code
	private int retrieveManoIns(String line) {

		int code = 0;

		// if the instruction is indirect, must add 8000 to memory instructions
		int indirect = (Character.toUpperCase(line.charAt(13)) == 'I') ? 0x8000 : 0x0000;
		boolean memory = false;

		String cmd = line.substring(5, 8).toUpperCase();
		String adr = line.substring(9).toUpperCase();

		Scanner in = new Scanner(adr);

		// check command		
		switch (cmd) {
			// memory instructions
			case "AND":
				code = 0x0000;
				memory = true;
				break;
			case "ADD":
				code = 0x1000;
				memory = true;
				break;
			case "LDA":
				code = 0x2000;
				memory = true;
				break;
			case "STA":
				code = 0x3000;
				memory = true;
				break;
			case "BUN":
				code = 0x4000;
				memory = true;
				break;
			case "BSA":
				code = 0x5000;
				memory = true;
				break;
			case "ISZ":
				code = 0x6000;
				memory = true;
				break;

			// register instructions
			case "CLA":
				code = 0x7800;
				break;
			case "CLE":
				code = 0x7400;
				break;
			case "CMA":
				code = 0x7200;
				break;
			case "CME":
				code = 0x7100;
				break;
			case "CIR":
				code = 0x7080;
				break;
			case "CIL":
				code = 0x7040;
				break;
			case "INC":
				code = 0x7020;
				break;
			case "SPA":
				code = 0x7010;
				break;
			case "SNA":
				code = 0x7008;
				break;
			case "SZA":
				code = 0x7004;
				break;
			case "SZE":
				code = 0x7002;
				break;
			case "HLT":
				code = 0x7001;
				break;

			// I/O instructions
			case "INP":
				code = 0xF800;
				break;
			case "OUT":
				code = 0xF400;
				break;
			case "SKI":
				code = 0xF200;
				break;
			case "SKO":
				code = 0xF100;
				break;
			case "ION":
				code = 0xF080;
				break;
			case "IOF":
				code = 0xF040;
				break;

			// misc instructions
			// interpret the rest of the line as an DECIMAL int
			case "DEC":
				if (in.hasNextInt(10)) {
					code = in.nextInt(10);

					// cannot read in -32768 as a decimal
					// it will map to the same thing as 32768
					if (code < -32767 || code > 32768) {
						displayError("Cannot read in DEC number " + code + ". Out of range.", line);
					}
				} else {
					displayError("Cannot find DEC number.", line);
				}
				break;
			// interpret the rest of the line as a HEX int
			case "HEX":
				if (in.hasNextInt(16)) {
					code = in.nextInt(16);

					// if the number read is in hex is negative (using 4 hex bits)
					// then the integer will store it wrong
					// so we must add FFFF to the front
					if (code > 32768) {
						code = 0xFFFF0000 | code;
					}

					if (code < -32768 || code > 32768) {
						displayError("Cannot read in HEX number " + Integer.toHexString(code) + ". Out of range.", line);
					}
				} else {
					displayError("Cannot find HEX number.", line);
				}
				break;

			// unrecognized command
			default:
				displayError("Command " + cmd + " is not recognized.", line);
				break;
		}

		// if the instruction is a memory instruction
		if (memory) {

			code += indirect;

			if (!in.hasNext()) {
				displayError("No memory address is provided", line);
			}
			// if it is just a number, go to that address (in HEX)
			if (in.hasNextInt(16)) {
				code += in.nextInt(16);
			} // otherwise it is a label
			else {
				// loop through the labels till you find it
				String labelStr = in.next();
				for (int i = 0; i < labels.size(); i++) {
					if (labels.get(i).getName().equals(labelStr.toUpperCase())) {
						code += labels.get(i).addressVal();
						break;
					} else if (i == labels.size() - 1) {
						// did not find the label
						displayError("Cannot find the provided label " + labelStr, line);
					}
				}
			}
		}

		return code;
	}

	// method to display any errors parsing the assembly file and throw an exception
	private void displayError(String msg, String line) {
		System.out.println("Cannot parse line: " + line);
		System.out.println("** " + msg + " **");
		throw new ManoAssemblerException(msg);
	}

	// method to save the assembler file
	public void printAssemblerFile(ArrayList<String> lines) {

		try {
			FileWriter fw = new FileWriter(assemblyFile);
			PrintWriter out = new PrintWriter(fw);

			// loop through all instructions, adding to file
			for (int i = 0; i < lines.size(); i++) {
				// print out the instruction to the file
				out.print(lines.get(i));
				out.println();
			}

			out.flush();
			out.close();

		} catch (FileNotFoundException ex) {
			System.out.println("Problem writing to assembler file. Program exiting....");
			System.exit(1);
		} catch (IOException ex) {
			System.out.println("Problem writing to assembler file. Program exiting....");
			System.exit(1);
		}
	}
	
	
	// method to print out the label pointers to a file
	private void printLabelFile() {

		try {
			FileWriter fw = new FileWriter(labelFile);
			PrintWriter out = new PrintWriter(fw);

			// loop through all instructions, adding to file
			for (int i = 0; i < labels.size(); i++) {
				ManoLabel lbl = labels.get(i);

				// print out the instruction to the file
				out.print(lbl.getName() + " : " + lbl.getAddress());
				out.println();
			}

			out.flush();
			out.close();

		} catch (FileNotFoundException ex) {
			System.out.println("Problem writing to label file. Program exiting....");
			System.exit(1);
		} catch (IOException ex) {
			System.out.println("Problem writing to label file. Program exiting....");
			System.exit(1);
		}
	}

	// method to print out the machine code addresses and instructions to file
	private void printMachineFile() {
		try {
			FileWriter fw = new FileWriter(machineFile);
			PrintWriter out = new PrintWriter(fw);

			// loop through all instructions, adding to file
			for (int i = 0; i < instructions.size(); i++) {
				ManoInstruction ins = instructions.get(i);

				// print out the instruction to the file
				out.print(ins.getAddress() + " : " + ins.getMachineCode());
				out.println();
			}

			out.flush();
			out.close();

		} catch (FileNotFoundException ex) {
			System.exit(1);
		} catch (IOException ex) {
			System.exit(1);
		}
	}

	public String getAssembly() {
		String out = "";

		for (int i = 0; i < assemblyCode.size(); i++) {
			out += (i+1) + ")\t" + assemblyCode.get(i) + "\n";
		}

		return out;
	}

	public String getLabels() {
		String out = "";

		for (int i = 0; i < labels.size(); i++) {
			ManoLabel lbl = labels.get(i);

			out += (lbl.getName() + " : " + lbl.getAddress()) + "\n";
		}

		return out;
	}

	public String getCode() {
		String out = "";

		for (int i = 0; i < instructions.size(); i++) {
			ManoInstruction ins = instructions.get(i);

			out += (ins.getAddress() + " : " + ins.getMachineCode()) + "\n";
		}

		return out;
	}
	
	// Command line main to be run if you do not want to use ManoAssemblerGUI
	public static void main(String[] args){
		
		Scanner in = new Scanner(System.in);
		
		System.out.print("Please enter the assembler file to generate from: ");
		String assembler = in.nextLine();
		String machine = assembler.substring(0, assembler.lastIndexOf(".")) + "_machineCode.mem";
		String label = assembler.substring(0, assembler.lastIndexOf(".")) + "_labels.txt";
		
		ManoAssembler m = new ManoAssembler(assembler, machine, label);
		System.out.println("Generated code successfully written to file.");		
	}
}
