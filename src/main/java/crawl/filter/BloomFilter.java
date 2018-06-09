package crawl.filter;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.BitSet;

public class BloomFilter implements Serializable{
	
	/*
	 * 
	 * 首先把1转换为二进制数字0000 0000 0000 0000 0000 0000 0000 0001，
	然后把该数字高位（左侧）的两个零移出，其他的数字都朝左平移2位，最后在低位（右侧）
	的两个空位补零。则得到的最终结果是0000 0000 0000 0000 0000 0000 0000 0100，
	则转换为十进制是4.数学意义：
	 在数字没有溢出的前提下，对于正数和负数，左移一位都相当于乘以2的1次方，
	 左移n位就相当于乘以2的n次方。
	 */
	
	private static final int DEFAULT_SIZE = 1 << 25;
	
	private static final int[] seeds = new int[]{5, 7, 11, 13, 31, 37, 61};
	
	private BitSet bits = null;
	private SimpleHash[] func = new SimpleHash[seeds.length];
	
	public  BloomFilter() {
		for (int i = 0; i < seeds.length; i++) {
			func[i] = new SimpleHash(DEFAULT_SIZE, seeds[i]);
		}
		File filterSer = new File("bits.ser");
		if(filterSer.exists()) {
			try {
				ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filterSer));
				bits = (BitSet) ois.readObject();
				ois.close();
 			} catch (Exception e) {
				// TODO: handle exception
 				e.printStackTrace();
			}
		}else {
			bits = new BitSet(DEFAULT_SIZE);
		}
	}
	
	public synchronized void add(String value) {
		for (SimpleHash f : func) {
			bits.set(f.hash(value), true);
		}
	}
	
	public BitSet getBitset() {
		return bits;
	}
	
	public boolean contains(String value) {
		if(value == null) {
			return false;
		}
		boolean ret = true;
		for (SimpleHash f : func) {
			ret = ret && bits.get(f.hash(value));
		}
		
		return ret;
	}
	
	public static class SimpleHash {
		private int cap;
		private int seed;
		
		public SimpleHash(int cap, int seed) {
			this.cap = cap;
			this.seed = seed;
		}
		
		public int hash(String value) {
			int result = 0;
			int len  = value.length();
			for (int i = 0; i < len; i++) {
				result = seed * result + value.charAt(i);
			}
			return (cap - 1) & result;
		}
	}

	  
}




