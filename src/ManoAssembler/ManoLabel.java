package ManoAssembler;

public class ManoLabel {

	private static final String addressLength = "%3s";

	private String name = "";
	private int address = 0;

	public ManoLabel(String nm, int adr) {
		name = nm.toUpperCase();
		address = adr;
	}

	public String getAddress() {
		// return the address as a hex string with three characters (memory is only 2^12 bits)
		return String.format(addressLength, Integer.toHexString(address)).replaceAll(" ", "0").toUpperCase();
	}

	public int addressVal() {
		return address;
	}
	
	public String getName(){
		return name;
	}

}
