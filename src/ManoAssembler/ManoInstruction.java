package ManoAssembler;

public class ManoInstruction {

	// static variables for the size of the memory
	private static final String machine_codeLength = "%4s"; // machine code is 16 bits
	private static final String addressLength = "%3s"; // address is 12 bits

	private int address = 0;
	private int code = 0;
	private String comment = "";

	public ManoInstruction(int av, int cv, String cmt) {
		address = av;
		code = cv;

		comment = cmt;
	}

	public String getAddress() {
		return String.format(addressLength, Integer.toHexString(address)).replaceAll(" ", "0").toUpperCase();
	}

	public String getMachineCode() {
		int offset = (code < 0) ? 4 : 0;
		return String.format(machine_codeLength, Integer.toHexString(code)).replaceAll(" ", "0").toUpperCase().substring(offset);
	}
	
	public String getComment(){
		return comment;
	}
}
