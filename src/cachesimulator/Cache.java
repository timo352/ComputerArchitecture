package cachesimulator;

import java.text.DecimalFormat;

public class Cache {

	public static final int FIFO = 1;
	public static final int LRU = 2;

	private int numSets;
	private int setSize;
	private Set[] cacheLines;

	private double hitRatio = 0;
	private int numInserts = 0;
	private int numHits = 0;

	// constructor for Cache
	public Cache(int num, int size, int tp) {
		numSets = num;
		setSize = size;

		cacheLines = new Set[numSets];
		for (int i = 0; i < numSets; i++) {
			cacheLines[i] = new Set(setSize, tp);
		}
	}

	// inserts an address (block of 1) into the cache
	public void insert(int address) {

		int setNum = address % numSets;
		int tag = address / numSets;
		int data = Memory.getData(address);

		// insert address into cache and check to see if it was a hit
		if (cacheLines[setNum].insert(tag, data)) {
			numHits++;
		}
		numInserts++;
		
		hitRatio = (double)numHits / (double)numInserts * 100;
	}

	// prints out the cache by formatting and calling the Set print method
	public void printCache() {
		for(int i=0; i < setSize; i++){
			System.out.print("\tTAG #" + (i+1));
		}
		System.out.println();
		for (int i = 0; i < cacheLines.length; i++) {
			System.out.print("Set " + i + ")\t");
			cacheLines[i].printSet();
		}
		
		// print out hit rate to three sig digs
		DecimalFormat df = new DecimalFormat("#.0");
		System.out.println("Hit Ratio: " + df.format(hitRatio) + "%");
	}

	
	// Method to simulate the cache
	// Creates two caches (one for FIFO and one for LRU) and prints out results
	// Change the calls array to change the blocks to load in
	public static void main(String[] args) {

		// Address array/ change when Dr. G posts the new one
		int[] calls = {0,1,2,3,1,10,11,3,46,47,0,3,2,60,1,21,2,0};
		
		// Change these numbers if Dr. Gallagher changes the project
		// ^^ in hindsight, the variable number of lines and sets was a good idea
		int numSets = 2;
		int setSize = 4;
		
		// FIFO CACHE
		Cache fifoC = new Cache(numSets, setSize, Cache.FIFO);
		for (int i = 0; i < calls.length; i++) {
			fifoC.insert(calls[i]);
		}
		System.out.println("FIFO CACHE:");
		fifoC.printCache();
		
		// LRU CACHE
		Cache lruC = new Cache(numSets, setSize, Cache.LRU);
		for (int i = 0; i < calls.length; i++) {
			lruC.insert(calls[i]);
		}
		System.out.println("\nLRU CACHE:");
		lruC.printCache();
	}
}
