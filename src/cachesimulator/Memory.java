package cachesimulator;

public class Memory {

	private final static int size = 64;
	
	// hardcoded main mem
	private final static int[] mem =
			   {0x92, 0x70, 0x8C, 0xFD, 0xB9, 0xE2, 0x40, 0xC2,
				0x0D, 0x9A, 0xD1, 0xF8, 0x43, 0x7E, 0xB7, 0x75,
				0xFB, 0x44, 0xDD, 0xF6, 0xA6, 0x43, 0x11, 0x17,
				0x98, 0x88, 0x08, 0x6A, 0x6D, 0xB8, 0xBC, 0x12,
				0x0A, 0xF1, 0x4C, 0x45, 0x63, 0x2C, 0x40, 0x98,
				0x91, 0x65, 0x0E, 0x76, 0xEE, 0x5D, 0x18, 0x29,
				0x85, 0x13, 0x60, 0xC5, 0x56, 0xF2, 0x89, 0x9E,
				0x06, 0xE2, 0x0B, 0xA2, 0xB2, 0x41, 0xB1, 0x7B};

	// returns the value at the specified index
	public static int getData(int index) {
		if (index < size && index >= 0) {
			return mem[index];
		} else {
			// throw exception
			throw new RuntimeException("Cannot access index " + index);
		}
	}
}
