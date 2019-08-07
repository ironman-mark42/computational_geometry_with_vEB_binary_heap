import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.AbstractMap.SimpleEntry;

@SuppressWarnings("rawtypes")
public class BinaryHeap<T extends Comparable> {
	SimpleEntry<T, T>[] A;
	int size;
	int maxsize;
	T max;
	T min;

	@SuppressWarnings("unchecked")
	public BinaryHeap(int maxsize, T max, T min) {
		this.maxsize = maxsize + 1;
		this.size = 0;
		A = (SimpleEntry<T, T>[]) Array.newInstance(SimpleEntry.class, this.maxsize);
		A[0] = new SimpleEntry<T, T>(max, max);
		this.max = max;
		this.min = min;
	}

	private int parent(int i) {
		return i / 2;
	}

	private int left(int i) {
		return (2 * i);
	}

	private int right(int j) {
		return (2 * j) + 1;
	}

	protected void swap(int i, int j) {
		SimpleEntry<T, T> tmp;
		tmp = A[i];
		A[i] = A[j];
		A[j] = tmp;
	}

	protected void maxHeapify(int i) {
		int l = left(i);
		int r = right(i);
		int largest = i;
		if (l <= size && compareTo(A[l], A[i]) > 0) {
			largest = l;
		} else {
			largest = i;
		}
		if (r <= size && compareTo(A[r], A[largest]) > 0) {
			largest = r;
		}

		if (largest != i) {
			swap(i, largest);
			maxHeapify(largest);
		}
	}

	/**
	 * Compare 2 elements of heap
	 * 
	 * @param entry1
	 * @param entry2
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private int compareTo(SimpleEntry<T, T> entry1, SimpleEntry<T, T> entry2) {
		if (entry1.getValue().equals(entry2.getValue())) {
			return entry1.getKey().compareTo(entry2.getKey());
		}
		return entry1.getValue().compareTo(entry2.getValue());
	}

	public void insert(T key, T priority) {
		size = size + 1;
		A[size] = new SimpleEntry<T, T>(this.min, this.min);
		SimpleEntry<T, T> newVal = new SimpleEntry<T, T>(key, priority);
		increaseKey(size, newVal);
	}

	private void increaseKey(int i, SimpleEntry<T, T> key) {
		if (compareTo(key, A[i]) < 0) {
			System.err.println("New key is smaller than current key");
			return;
		}
		A[i] = key;
		while (i > 1 && compareTo(A[parent(i)], A[i]) < 0) {
			swap(i, parent(i));
			i = parent(i);
		}
	}

	/**
	 * Get and remove max element
	 * 
	 * @return
	 */
	public SimpleEntry<T, T> extractMax() {
		if (size < 1) {
			System.err.println("Heap underflow");
			return null;
		}
		SimpleEntry<T, T> max = A[1];
		A[1] = A[size];
		size -= 1;
		maxHeapify(1);
		return max;
	}
//////////////////////////////////////////////
	public T getValue(int i) {
		T v = null;
		v=A[i].getValue();
		return v;
	}
	public T getPriority(int i) {
		T v = null;
		v=A[i].getKey();
		return v;
	}
	public static void main(String[] arg) {
		
		BinaryHeap<Integer> maxHeap = new BinaryHeap<Integer>(1000000, Integer.MAX_VALUE, Integer.MIN_VALUE);
		List<Integer> values = new ArrayList<>();
		List<Integer> priorites = new ArrayList<>();
		int n = 10;
		for (int i = 0; i < n; i++) {
			values.add(i*2);
			priorites.add(i*2);
		}
		
		Collections.shuffle(values);
		Collections.shuffle(priorites);
		long startTime = System.nanoTime();
		long insertTime = System.nanoTime() - startTime;
		long extractMax = System.nanoTime() - startTime;
		startTime = System.nanoTime();
		for (int i = 0; i < n; i++) {
			int value = values.get(i);
			int priority = priorites.get(i);
			maxHeap.insert(value, priority);
			System.out.println(String.format("INSERT: priority(%d),value(%d)",priority, value));
		}
		System.out.println("*********** heap sort result ****************");
		int maxP=0,maxV=0;
		maxP=maxHeap.getPriority(1);
		maxV=maxHeap.getValue(1);
		for(int i=1;i<=n;i++) {
			int v=0,p=0;
			v=maxHeap.getValue(1);
			p=maxHeap.getPriority(1);
			System.out.println(String.format("priority(%d),value(%d)",v,p));
			if (i<n) maxHeap.extractMax();
		}
		System.out.println("****************************************");		
		//SimpleEntry<Integer, Integer> max = maxHeap.extractMax();
		System.out.println(String.format("RUNNING TIME FOR INSERT FUNCTION: %d ns", System.nanoTime() - startTime));
		System.out.println(String.format("EXTRACT MAX PRIORITY(%d), VALUE(%d)", maxV, maxP));
		System.out.println(String.format("TIME TO EXTRACT MAX ELEMENT: %d ns", extractMax));
		System.out.println(String.format("INCREASE KEY FOR N ELEMENTS: %d ns", insertTime));
		System.out.println(String.format("TOTAL RUNNING TIME FOR ALGORITHM: %d ns", System.nanoTime() - startTime + extractMax + insertTime));
		maxHeap = null;
		values = null;
		priorites = null;
	}
}
