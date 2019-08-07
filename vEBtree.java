import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class vEBtree<T extends Comparable<T>> {

	class Node {
		public int u;
		public SimpleEntry<T, T> min;
		public SimpleEntry<T, T> max;
		public Node summary;
		public Node[] cluster;

		public Node(int u) {
			this.u = u;
			min = NIL;
			max = NIL;

			initialize(u);
		}

		@SuppressWarnings("unchecked")
		private void initialize(int u) {
			if (u <= 2) {
				summary = null;
				cluster = null;
			} else {
				int size = higherSquareRoot();

				summary = new Node(size);
				cluster = new vEBtree.Node[size];

				for (int i = 0; i < size; i++) {
					cluster[i] = new Node(size);
				}
			}
		}

		/**
		 * Higher Square Root
		 */
		private int higherSquareRoot() {
			return (int) Math.pow(2, Math.ceil((Math.log10(u) / Math.log10(2)) / 2));
		}
	}


	// Define NIL value to initialize min, max;
	private SimpleEntry<T, T> NIL;
	private SimpleEntry<T, T> O;
	private T ONE;
	private T ZERO;
	private Node root;

	/*
	 * Construction method
	 */
	public vEBtree(int u, T NIL, T ONE, T ZERO) throws Exception {
		this.NIL = new SimpleEntry<T, T>(NIL, NIL);
		this.O=new SimpleEntry<T,T>(ZERO,ZERO);
		this.ONE = ONE;
		this.ZERO = ZERO;
		if (!isPowerOf2(u)) {
			throw new Exception("Tree size must be a power of 2!");
		}
		root = new Node(u);
	}

	/*
	 * Insert x
	 */
	public void insert(T value, T priority) {
		insert(root, value, priority);
	}

	/*
	 * Delete x
	 */
	public boolean decreaseKey(T value, T priority) {
		return decreaseKey(root, new SimpleEntry<T, T>(value, priority));
	}

	/*
	 * Returns the maximum value in the tree or -1 if the tree is empty.
	 */
	public SimpleEntry<T, T> extractMax() {
		SimpleEntry<T, T> max = root.max;
		decreaseKey(max.getKey(), max.getValue());
		return max;
	}

	private void insertEmptyNode(Node v, SimpleEntry<T, T> s) {
		v.min = s;
		v.max = s;
	}

	/**
	 * Insert new value to vEBTree
	 * 
	 * @param v        Root
	 * @param priority value to insert
	 */
	private void insert(Node v, T value, T priority) {
		SimpleEntry<T, T> x = new SimpleEntry<T, T>(value, priority);
		if (v.min == null) {
			insertEmptyNode(v, x);
			return;
		}

		increaseKey(v, x);

	}

	private void increaseKey(Node v, SimpleEntry<T, T> x) {
		if (compareTo(x, v.min) < 0) {
			// Exchange x with v.min
			SimpleEntry<T, T> temp = x;
			x = v.min;
			v.min = temp;
		}
		if (v.u > 2) {
			if (v.cluster[(int) high(v, x)].min == null) {
				insert(v.summary, x.getKey(), high(v, x));
				insertEmptyNode(v.cluster[(int) high(v, x)], new SimpleEntry<T, T>(x.getKey(), low(v, x)));
			} else {
				insert(v.cluster[(int) high(v, x)], x.getKey(), low(v, x));
			}
		}

		if (compareTo(x, v.max) > 0) {
			v.max = x;
		}
	}

	/**
	 * Compare 2 elements of heap
	 * 
	 * @param min
	 * @param max
	 * @return
	 */
	private int compareTo(SimpleEntry<T, T> min, SimpleEntry<T, T> max) {

		if (min.getValue().equals(max.getValue())) {
			return min.getKey().compareTo(max.getKey());
		}
		return min.getValue().compareTo(max.getValue());
	}

	/**
	 * Compare 2 elements of heap equals or not
	 * 
	 * @param x
	 * @param min
	 * @return
	 */
	private boolean equals(SimpleEntry<T, T> x, SimpleEntry<T, T> min) {
		if (x == null || min == null) {
			return false;
		}
		return x.getKey().equals(min.getKey()) && x.getValue().equals(min.getValue());
	}

	private boolean decreaseKey(Node v, SimpleEntry<T, T> x) {
		if (compareTo(v.min, v.max) == 0) {
			v.min = NIL;
			v.max = NIL;
			return false;
		}
		if (v.u == 2) {
			v.min = ZERO.equals(x.getValue()) ? new SimpleEntry<T, T>(x.getKey(), ONE)
					: new SimpleEntry<T, T>(x.getKey(), ZERO);
			v.max = v.min;
			return false;
		}
		if (!equals(x, v.min)) {//v.max  ???
			return false;
		}

		SimpleEntry<T, T> first_cluster = v.summary.min;
		T priority = index(v, first_cluster, v.cluster[(int) first_cluster.getValue()].min);
		v.min = new SimpleEntry<T, T>(x.getValue(), priority);

		decreaseKey(v.cluster[(int) high(v, x)], new SimpleEntry<T, T>(x.getKey(), low(v, x)));
		if (v.cluster[(int) high(v, x)].min == null) {
			decreaseKey(v.summary, new SimpleEntry<T, T>(x.getKey(), high(v, x)));
			if (equals(x, v.max)) {
				SimpleEntry<T, T> summary_max = v.summary.max;
				if (summary_max == null) {
					v.max = v.min;
				} else {
					priority = index(v, summary_max, v.cluster[(int) summary_max.getValue()].max);
					v.max = new SimpleEntry<T, T>(x.getValue(), priority);
				}
			}
		} else if (equals(x, v.max)) {
			priority = index(v, new SimpleEntry<T, T>(x.getValue(), high(v, x)), v.cluster[(int) high(v, x)].max);
			v.max = new SimpleEntry<T, T>(x.getValue(), priority);
		}
		return true;

	}
////////////////////////////////////////////////////////////////////////////////////////
	/*
	 * Returns the predecessor of x
	 */
	public SimpleEntry<T, T> predecessor(SimpleEntry<T, T> x)
	{
		return predecessorR(root, x);
	}

	public SimpleEntry<T, T> predecessorR(Node node,SimpleEntry<T, T> x){
		if(node.u==2) {
			if(x.getValue()==ONE && node.min.getValue()==ZERO){
				return O;
			}
			else {
				return NIL;
			}
		}
		else if(!equals(node.max,NIL) && compareTo(x,node.max)>0) {
			return node.max;
		}
		else{
			T highOfX=high(node,x);
			T lowOfX=low(node,x);
			/////////////////////////////????????????////////////////////////////////
			SimpleEntry<T, T> minCluster=node.cluster[(int) highOfX].min;/////////////////
			if(!equals(minCluster,NIL) && lowOfX.compareTo(minCluster.getValue())>0) {
				SimpleEntry<T, T> mmin=predecessorR(node.cluster[(int) highOfX],new SimpleEntry<T, T>(lowOfX,x.getKey()));
				T priority=index(node,new SimpleEntry<T, T>(highOfX,x.getKey()),mmin);
				T value=value(node,new SimpleEntry<T, T>(highOfX,x.getKey()),mmin);
				return new SimpleEntry<T, T>(priority,value);
			}
			else {
				SimpleEntry<T, T> clusterPred=predecessorR(node.summary,new SimpleEntry<T, T>(highOfX,x.getKey()));
				if(equals(clusterPred,NIL)) {
					if(!equals(node.min,NIL) && compareTo(x,node.min)>0) {
						return node.min;
					}
					else {
						return NIL;
					}
				}
				else {
					T priority=index(node,clusterPred,node.cluster[(int) clusterPred.getValue()].max);
					T value=value(node,clusterPred,node.cluster[(int) clusterPred.getValue()].max);			
					return new SimpleEntry<T, T>(priority,value);
				}
			}
		}
	}
	
////////////////////////////////////////////////////////////////////////////////////////
	/*
	 * Returns the integer value of the first half of the bits of x.
	 */
	@SuppressWarnings("unchecked")
	private T high(Node node, SimpleEntry<T, T> x) {
		return (T) new Integer((int) Math.floor((int) x.getValue() / lowerSquareRoot(node)));
	}

	/**
	 * The integer value of the second half of the bits of x.
	 */
	@SuppressWarnings("unchecked")
	private T low(Node node, SimpleEntry<T, T> x) {
		return (T) new Integer((int) x.getValue() % (int) lowerSquareRoot(node));
	}

	/**
	 * The value of the least significant bits of x.
	 */
	private double lowerSquareRoot(Node node) {
		return Math.pow(2, Math.floor((Math.log10(node.u) / Math.log10(2)) / 2.0));
	}

	/**
	 * The index in the tree of the given value.
	 */
	@SuppressWarnings("unchecked")
	private T index(Node node, SimpleEntry<T, T> first_cluster, SimpleEntry<T, T> min) {
		return (T) new Integer((int) ((int) first_cluster.getValue() * lowerSquareRoot(node) + (int) min.getValue()));
	}
	
	/**
	 * The index in the tree of the given value.
	 */
	@SuppressWarnings("unchecked")
	private T value(Node node, SimpleEntry<T, T> first_cluster, SimpleEntry<T, T> min) {
		return (T) new Integer((int) ((int) first_cluster.getValue() * lowerSquareRoot(node) + (int) min.getKey()));
	}

	/**
	 * Returns true if x is a power of 2, false otherwise.
	 */
	private static boolean isPowerOf2(int x) {
		if (0 == x) {
			return false;
		}

		while (x % 2 == 0) {
			x = x / 2;
		}

		if (x > 1) {
			return false;
		}

		return true;
	}

	public static void main(String[] args) throws Exception {
		int n = 10;
		long startTime = System.nanoTime();
		vEBtree<Integer> tree = new vEBtree<Integer>((int) Math.pow(2, 20), -1, 1, 0);
		List<Integer> values = new ArrayList<>();
		List<Integer> priorites = new ArrayList<>();
		for (int i = 0; i < n; i++) {
			values.add(i);
			priorites.add(i);
		}
		Collections.shuffle(values);
		Collections.shuffle(priorites);
		long buildTime = System.nanoTime() - startTime;
		startTime = System.nanoTime();
		for (int i = 1; i < n; i++) {
			int value = values.get(i);
			int priority = priorites.get(i);
			tree.insert(value, priority);
			System.out.println(String.format("INSERT: priority(%d), value(%d)", priority, value));
		}
///////////////////////////////////////////////////////////////////////////
		System.out.println("/////////////// Radix Sorting result ///////////////");
		SimpleEntry<Integer, Integer> aa;
		aa =tree.extractMax();
		for (int i = 1; i < n; i++) {
			int priority=aa.getKey();
			int value=aa.getValue();
			System.out.println(String.format("priority(%d), value(%d)", value, priority));
			aa=tree.predecessor(aa);
		}
///////////////////////////////////////////////////////////////////////////
		SimpleEntry<Integer, Integer> max = tree.extractMax();
		System.out.println(String.format("EXTRACT MAX VALUE(%d), PRIORITY(%d)", max.getKey(), max.getValue()));
		System.out.println(String.format("BUILD TIME: %d ns", buildTime));
		System.out.println(String.format("RUNNING TIME: %d ns", System.nanoTime() - startTime));
	}
}
