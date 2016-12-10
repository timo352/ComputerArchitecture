package cachesimulator;

import java.util.LinkedList;

public class Set {

	// a little class to store the tag and data of the memory block
	private class Block {

		int tag;
		int data;

		public Block(int tg, int dt) {
			tag = tg;
			data = dt;
		}

		public boolean equals(Object b) {
			return tag == ((Block) b).tag && data == ((Block) b).data;
		}
	}

	private int type;
	private LinkedList<Block> data;
	private LinkedList<Integer> lruQueue;
	private int count;

	public Set(int size, int tp) {

		data = new LinkedList<Block>();
		lruQueue = new LinkedList<Integer>();
		for (int i = 0; i < size; i++) {
			data.add(i, new Block(-1, -1));
			lruQueue.addLast(i);
		}
		
		type = tp;
		count = 0;
	}

	public boolean insert(int tag, int dt) {

		Block b = new Block(tag, dt);

		// check if it is already in the cache
		if (data.contains(b)) {
			if (type == Cache.LRU) {
				// put the already loaded block at the end of the queue
				lruQueue.removeFirstOccurrence(data.indexOf(b));
				lruQueue.addLast(data.indexOf(b));
			}
			return true;
		}

		// if it isn't insert it using FIFO or LRU
		if (type == Cache.FIFO) {
			data.set(count % data.size(), b);
		} else if (type == Cache.LRU) {

			// put the block at the end of the queue
			int x = lruQueue.removeFirst();
			lruQueue.addLast(x);
			data.set(x, b);
		}

		count++;

		return false;
	}

	public void printSet() {
		
		// decide how long to make the tag in hex
		int tagLength = (int)(Math.log10(data.size()) /  Math.log10(2));
		
		for (int k = 0; k < data.size(); k++) {
			if (data.get(k).tag == -1) {
				System.out.print("-:__\t");
			} else {
				String tag = Integer.toHexString(data.get(k).tag).toUpperCase();
				tag = String.format("%" + String.valueOf(tagLength) + "s", tag).replaceAll(" ", "0");
								
				System.out.print(tag + "\t");
			}
		}

		System.out.println();
	}
}
