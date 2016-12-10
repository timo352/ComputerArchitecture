package ManoAssembler;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import javax.swing.*;

public class ManoAssemblerGUI extends javax.swing.JFrame {

	private JFrame frame;
	private JTabbedPane contentPanel;

	private JScrollPane AssemblerScroll;
	private JPanel AssemblerPanel;
	private JTextArea CharacterCounterArea;
	private JTextArea AssemblerTextArea;

	private JLabel LabelLabel;
	private JLabel MachineLabel;
	private JScrollPane LabelScroll;
	private JScrollPane MachineScroll;
	private JPanel OutputPanel;
	private JTextArea LabelOutputArea;
	private JTextArea MachineCodeArea;

	private JMenuBar MenuBar;
	private JMenu FileMenu;
	private JMenu LoadMenu;
	private JMenu OptionsMenu;
	private JMenuItem ChooseAssemblerFile;
	private JMenuItem ChooseOutputNames;
	private JMenuItem ExitItem;
	private JMenuItem GenerateItem;

	private JFileChooser fileChooser;
	private JDialog fileDialog;

	private ManoAssembler assembler;
	private String AssemblerFile;
	private String MachineFile;
	private String LabelFile;

	public ManoAssemblerGUI() {

		frame = new JFrame("Mano Assembler");

		initComponents();
		initListeners();
		
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		Dimension s = Toolkit.getDefaultToolkit().getScreenSize();
		frame.setBounds((int) (s.width * .06125), (int) (s.height * .06125), (int) (s.width * .825), (int) (s.height * .825));
		frame.setResizable(false);
		frame.setVisible(true);
	}

	private void initComponents() {

		Dimension s = Toolkit.getDefaultToolkit().getScreenSize();
		contentPanel = new JTabbedPane();

		AssemblerScroll = new JScrollPane();
		LabelScroll = new JScrollPane();
		MachineScroll = new JScrollPane();

		CharacterCounterArea = new JTextArea();
		AssemblerTextArea = new JTextArea();
		LabelOutputArea = new JTextArea();
		MachineCodeArea = new JTextArea();

		// **** MENU CREATION ****
		MenuBar = new JMenuBar();

		FileMenu = new JMenu("File");
		LoadMenu = new JMenu("Load");
		OptionsMenu = new JMenu("Options");

		ExitItem = new JMenuItem("Exit");
		GenerateItem = new JMenuItem("Save Assembler File and Generate New Code");
		ChooseAssemblerFile = new JMenuItem("Choose Assembly File...");
		ChooseOutputNames = new JMenuItem("Change Output Names...");

		FileMenu.add(GenerateItem);
		FileMenu.add(ExitItem);
		MenuBar.add(FileMenu);

		LoadMenu.add(ChooseAssemblerFile);
		MenuBar.add(LoadMenu);

		OptionsMenu.add(ChooseOutputNames);
		MenuBar.add(OptionsMenu);

		frame.setJMenuBar(MenuBar);

		// *** OUTPUT SECTION ***
		OutputPanel = new JPanel();
		LabelOutputArea.setEditable(false);
		LabelScroll.setViewportView(LabelOutputArea);
		LabelOutputArea.setFont(new Font("Courier New", Font.PLAIN, 14));

		LabelLabel = new JLabel("Labels (1st Pass)");
		MachineLabel = new JLabel("Machine Code (2nd Pass)");

		MachineCodeArea.setEditable(false);
		MachineScroll.setViewportView(MachineCodeArea);
		MachineCodeArea.setFont(new Font("Courier New", Font.PLAIN, 14));

		GroupLayout OutputLayout = new GroupLayout(OutputPanel);
		OutputPanel.setLayout(OutputLayout);
		OutputLayout.setHorizontalGroup(
				OutputLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(OutputLayout.createSequentialGroup()
						.addComponent(LabelScroll, GroupLayout.PREFERRED_SIZE, (int) (s.width * 0.4125), GroupLayout.PREFERRED_SIZE)
						.addComponent(MachineScroll))
				.addGroup(OutputLayout.createSequentialGroup()
						.addComponent(LabelLabel, GroupLayout.PREFERRED_SIZE, (int) (s.width * 0.4125), GroupLayout.PREFERRED_SIZE)
						.addComponent(MachineLabel))
		);
		OutputLayout.setVerticalGroup(
				OutputLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(OutputLayout.createSequentialGroup()
						.addComponent(LabelLabel)
						.addComponent(LabelScroll))
				.addGroup(OutputLayout.createSequentialGroup()
						.addComponent(MachineLabel)
						.addComponent(MachineScroll))
		);

		// *** ASSEMBLER TAB ***
		AssemblerPanel = new JPanel();
		AssemblerScroll.setViewportView(AssemblerTextArea);
		AssemblerTextArea.setFont(new Font("Courier New", Font.PLAIN, 14));
		CharacterCounterArea.setFont(new Font("Courier New", Font.PLAIN, 14));
		CharacterCounterArea.setText("#)\t123456789012345678901234567890");
		
		GroupLayout AssemblerTextLayout = new GroupLayout(AssemblerPanel);
		AssemblerPanel.setLayout(AssemblerTextLayout);
		AssemblerTextLayout.setHorizontalGroup(
				AssemblerTextLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addComponent(CharacterCounterArea)
				.addComponent(AssemblerScroll)
				.addComponent(OutputPanel)
		);
		AssemblerTextLayout.setVerticalGroup(
				AssemblerTextLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(AssemblerTextLayout.createSequentialGroup()
						.addComponent(CharacterCounterArea, GroupLayout.PREFERRED_SIZE, 20, GroupLayout.PREFERRED_SIZE)
						.addComponent(AssemblerScroll, GroupLayout.PREFERRED_SIZE, (int) (s.height * 0.4), GroupLayout.PREFERRED_SIZE)
						.addComponent(OutputPanel))
		);
		contentPanel.addTab("Assembly Code", AssemblerPanel);

		GroupLayout layout = new GroupLayout(frame.getContentPane());
		frame.getContentPane().setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addComponent(contentPanel)
		);
		layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addComponent(contentPanel)
		);

		
		// *** FILE CHOOSER ***
		fileDialog = new JDialog();
		fileChooser = new JFileChooser();
		fileChooser.setAcceptAllFileFilterUsed(true);

		pack();
	}

	private void initListeners() {

		// regenerate the text on screen
		GenerateItem.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				if (AssemblerFile != null) {
					try {
						
						ArrayList<String> lines = new ArrayList<>();
						
						String str = AssemblerTextArea.getText();
						
						String[] temp = str.split("\n");
						
						// NOT WORKING
						for(int i=0; i<temp.length; i++){
							lines.add(temp[i].substring(temp[i].indexOf("\t")+1));
						}								
						
						assembler.printAssemblerFile(lines);
						
						
						assembler = new ManoAssembler(AssemblerFile, LabelFile, MachineFile);
					} catch (ManoAssemblerException err) {
						displayError(err.getMessage());
						
						AssemblerFile = null;
						LabelFile = null;
						MachineFile = null;
						
						return;
					}

					AssemblerTextArea.setText(assembler.getAssembly());
					LabelOutputArea.setText(assembler.getLabels());
					MachineCodeArea.setText(assembler.getCode());
				}
			}

		});

		// close the program
		ExitItem.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				System.exit(1);
			}

		});

		// method to change the name of the output file
		ChooseOutputNames.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				// open up the dialog if an assembler file is loaded, otherwise just throw an error
				if (AssemblerFile != null) {
					String[] args = JOptionPane2.getNames(AssemblerFile, MachineFile, LabelFile);
					
					
					File machine = new File(MachineFile);
					File label = new File(LabelFile);
					
					File newMachine = new File(args[0]);
					File newLabel = new File(args[1]);
					
					if(args[0].isEmpty() || !machine.renameTo(newMachine)){
						displayError("Could not rename machine file. Please try a different name.");
					}
					if(args[1].isEmpty() || !label.renameTo(newLabel)){
						displayError("Could not rename label file. Please try a differe name.");
					}
				} 
				else {
					displayError("Please select an assembler file first.");
				}
			}

		});

		// method to load a file for the assembler
		ChooseAssemblerFile.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				if (chooseFile()) {
					try {
						assembler = new ManoAssembler(AssemblerFile, LabelFile, MachineFile);
						AssemblerTextArea.setText(assembler.getAssembly());
						LabelOutputArea.setText(assembler.getLabels());
						MachineCodeArea.setText(assembler.getCode());
					} catch (ManoAssemblerException err) {
						displayError(err.getMessage());
						
						AssemblerFile = null;
						LabelFile = null;
						MachineFile = null;
					}
				}
			}
		});
	}

	private boolean chooseFile() {

		fileChooser.setDialogTitle("Choose assembly file...");

		if (fileChooser.showOpenDialog(fileDialog) != JFileChooser.APPROVE_OPTION) {
			return false;
		}

		AssemblerFile = fileChooser.getSelectedFile().getPath();
		
		String str = fileChooser.getSelectedFile().getPath();
		str = str.substring(0, str.lastIndexOf('.'));

		MachineFile = str + "_machineCode.mem";
		LabelFile = str + "_labels.txt";

		return true;
	}

	private void displayError(String err) {
		String[] args = {"OK"};
		JOptionPane.showOptionDialog(frame, err, "Error", 1, JOptionPane.ERROR_MESSAGE, null, args, null);
	}

	public static class JOptionPane2 {

		String machine, label;

		public JOptionPane2(String a, String m, String l) {
			JTextField labelInput = new JTextField(l.substring(l.lastIndexOf("\\") + 1), 20);
			JTextField machineInput = new JTextField(m.substring(m.lastIndexOf("\\") + 1), 20);

			JPanel myPanel = new JPanel();
			myPanel.add(new JLabel("Label: "));
			myPanel.add(labelInput);
			myPanel.add(Box.createHorizontalStrut(15)); // a spacer
			myPanel.add(new JLabel("Machine: "));
			myPanel.add(machineInput);

			int result = JOptionPane.showConfirmDialog(null, myPanel, "Enter Names", JOptionPane.OK_CANCEL_OPTION);

			if (result == JOptionPane.OK_OPTION) {

				machine = a.substring(0, a.lastIndexOf("\\") + 1) + machineInput.getText();
				label = a.substring(0, a.lastIndexOf("\\") + 1) + labelInput.getText();
			}
		}

		public static String[] getNames(String a, String m, String l) {
			String[] out = new String[2];

			JOptionPane2 j = new JOptionPane2(a, m, l);

			out[0] = j.machine;
			out[1] = j.label;

			return out;
		}
	}

	public static void main(String args[]) {
		ManoAssemblerGUI g = new ManoAssemblerGUI();
	}
}