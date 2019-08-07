import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class vEBtree2 
{
	class VEBNode
	{	
		public int universeSize;
		public int min;
		public int max;
		public int min_v;
		public int max_v;
		public VEBNode summary;
		public VEBNode[] cluster;
		
		public VEBNode(int universeSize)
		{
			this.universeSize = universeSize;
			min = vEBtree2.NULL;
			max = vEBtree2.NULL;	
			min_v=vEBtree2.NULL;	
			max_v=vEBtree2.NULL;	
			/* Allocate the summary and cluster children. */
			initializeChildren(universeSize);
		}
		
		private void initializeChildren(int universeSize)
		{
			if(universeSize <= vEBtree2.BASE_SIZE)
			{
				summary = null;
				cluster = null;
			}
			else
			{
				int childUnivereSize = higherSquareRoot();
				
				summary = new VEBNode(childUnivereSize);
				cluster = new VEBNode[childUnivereSize];
				
				for(int i = 0; i < childUnivereSize; i++)
				{
					cluster[i] = new VEBNode(childUnivereSize);
				}
			} 
		}
		
		/*
		 * Returns the value of the most significant bits of x.
		 */
		private int higherSquareRoot()
		{
			return (int)Math.pow(2, Math.ceil((Math.log10(universeSize) / Math.log10(2)) / 2));
		}
	}
	public static int BASE_SIZE = 2; /* Base vEB Node size */
	public static int NULL = -1; /* Initial min and max values */
	
	private VEBNode root;
	
	/*
	 * Creates and returns an instance of a van Emde Boas Tree.
	 */
	public static vEBtree2 createVEBTree(int universeSize)
	{
		if(isPowerOf2(universeSize))
		{
			return new vEBtree2(universeSize);
		}
		else
		{
			System.out.println("ERROR: Must create a tree with size a power of 2!");
			return null;
		}
	}	
	
	
	/*
	 * Insert x into the tree.
	 */
	public void insert(int priority,int value)
	{
		insertR(root, priority,value);
	}
	
	
	/*
	 * Delete x from the tree.
	 */
	public void delete(int x)
	{
		deleteR(root, x);
	}
	
	
	/*
	 * Returns true if x is in the tree, false otherwise.
	 */
	public boolean search(int x)
	{
		return searchR(root, x);
	}
	
	/*
	 * Returns true if x is in the tree, false otherwise.
	 */
	public int search_val(int x)
	{
		return searchR_val(root, x);
	}
	
	/*
	 * Returns the predecessor of x, or -1 if x is the minimum.
	 */
	public int predecessor(int x)
	{
		return predecessorR(root, x);
	}
	

	/*
	 * Returns the minimum value in the tree or -1 if the tree is empty.
	 */
	public int min()
	{
		return root.min;
	}
	
	
	/*
	 * Returns the maximum value in the tree or -1 if the tree is empty.
	 */
	public int max()
	{
		return root.max;
	}
	
	
	/*
	 * Creates the tree structure with a universize of size
	 * universeSize.
	 */
	vEBtree2(int universeSize)
	{
		/* 
		 * This node will handle creating all the other nodes,
		 * and the full tree will be built.
		 */
		root = new VEBNode(universeSize);
	}
	
	
	private void insertR(VEBNode node, int priority,int value)
	{
		/* This node is empty */
		if(NULL == node.min)
		{
		    node.min = priority;
		    node.min_v=value;
		    node.max = priority;
		    node.max_v=value;
		}
		if(priority < node.min)
		{
		    int tempValue = priority;
		    int vvv=value;
		    priority = node.min;
		    value=node.min_v;
		    node.min = tempValue;
		    node.min_v=vvv;
		}
		if(priority > node.min && node.universeSize > BASE_SIZE)
		{
		    int highOfX = high(node, priority);
		    int lowOfX = low(node, priority);
		    
		    /* Case when the cluster is non-empty*/
		    if(NULL != node.cluster[highOfX].min)
		    {
		        /* Insert into the cluster recursively */
		        insertR(node.cluster[highOfX], lowOfX,value);
		    }
		    else
		    {
		        /* Insert into the summary recursively */
		        insertR(node.summary, highOfX,value);
		        node.cluster[highOfX].min = lowOfX;
		        node.cluster[highOfX].min_v = value;
		        node.cluster[highOfX].max = lowOfX;
		        node.cluster[highOfX].max_v = value;
		    }
		}
		if(priority > node.max)
		{
		    node.max = priority;
		    node.max_v=value;
		}
	}
	
	
	private void deleteR(VEBNode node, int x)
	{
		if(node.min == node.max)
		{
			node.min = NULL;
			node.max = NULL;
		}
		else if(BASE_SIZE == node.universeSize)
		{
			if(0 == x)
			{
				node.min = 1;
			}
			else
			{
				node.min = 0;
			}
			node.max = node.min;
		}
		else if(x == node.min)
		{
			int summaryMin = node.summary.min;
			x = index(node, summaryMin, node.cluster[summaryMin].min);
			node.min = x;
			
			int highOfX = high(node, x);
			int lowOfX = low(node, x);
			deleteR(node.cluster[highOfX], lowOfX);
			
			if(NULL == node.cluster[highOfX].min)
			{
				deleteR(node.summary, highOfX);
				if(x == node.max)
				{
					int summaryMax = node.summary.max;
					if(NULL == summaryMax)
					{
						node.max = node.min;
					}
					else
					{
						node.max = index(node, summaryMax, node.cluster[summaryMax].max);
					}
				}
			}
			else if(x == node.max)
			{
				node.max = index(node, highOfX, node.cluster[highOfX].max);
			}
		}
	}
	
	
	private boolean searchR(VEBNode node, int x)
	{
		if(x == node.min || x == node.max)
		{
			return true;
		}
		else if(BASE_SIZE == node.universeSize)
		{
			return false;
		}
		else
		{
			return searchR(node.cluster[high(node, x)], low(node, x));
		}
	}
///////////////////////////////////////////////////////////////////////////////////////////	
	private int searchR_val(VEBNode node, int x)
	{
		if( x == node.max)
		{
			return node.max_v;
		}
		if( x == node.min)
		{
			return node.min_v;
		}
		else if(BASE_SIZE == node.universeSize)
		{
			return -1;
		}
		else
		{
			return searchR_val(node.cluster[high(node, x)], low(node, x));
		}
	}
////////////////////////////////////////////////////////////////////////////////////////////	
	
	public int predecessorR(VEBNode node, int x)
	{
		if(BASE_SIZE == node.universeSize)
		{
			if(1 == x && 0 == node.min)
			{
				return 0;
			}
			else
			{
				return NULL;
			}
		}
		else if(NULL != node.max && x > node.max)
		{
			return node.max;
		}
		else
		{
			int highOfX = high(node, x);
			int lowOfX = low(node, x);
			
			int minCluster = node.cluster[highOfX].min;
			if(NULL != minCluster && lowOfX > minCluster)
			{
				return index(node, highOfX, predecessorR(node.cluster[highOfX], lowOfX));
			}
			else
			{
				int clusterPred = predecessorR(node.summary, highOfX);
				if(NULL == clusterPred)
				{
					if(NULL != node.min && x > node.min)
					{
						return node.min;
					}
					else
					{
						return NULL;
					}
				}
				else
				{
					return index(node, clusterPred, node.cluster[clusterPred].max);
				}
			}
		}
	}
	
	/*
	 * Returns the integer value of the first half of the bits of x.
	 */
	private int high(VEBNode node, int x)
	{
		return (int)Math.floor(x / lowerSquareRoot(node));
	}
	
	
	/*
	 * Returns the integer value of the second half of the bits of x.
	 */
	private int low(VEBNode node, int x)
	{
		return x % (int)lowerSquareRoot(node);
	}
	
	
	/*
	 * Returns the value of the least significant bits of x.
	 */
	private double lowerSquareRoot(VEBNode node)
	{
		/* Change bases to 2 since java api does not support this. */
		return Math.pow(2, Math.floor((Math.log10(node.universeSize) / Math.log10(2)) / 2.0));
	}
	
	
	/*
	 * Returns the index in the tree of the given value.
	 */
	private int index(VEBNode node, int x, int y)
	{
		return (int)(x * lowerSquareRoot(node) + y);
	}
	
	
	/*
	 * Returns true if x is a power of 2, false otherwise.
	 */
	private static boolean isPowerOf2(int x)
	{
		if(0 == x)
		{
			return false;
		}
		
		while(x % 2 == 0)
		{
			x = x / 2;
		}
		
		if(x > 1)
		{
			return false;
		}
		
		return true;
	}
	
	
	public static void main(String[] args) {
		int n=10;
		long startTime=System.nanoTime();
		
		vEBtree2 tree=new vEBtree2(100);
		
		List<Integer> values = new ArrayList<>();
		List<Integer> priorites = new ArrayList<>();
		for (int i = 0; i < n; i++) {
			values.add(i*300);
			priorites.add(i*4);
		}
		Collections.shuffle(values);
		Collections.shuffle(priorites);
		long buildTime = System.nanoTime() - startTime;
		startTime = System.nanoTime();
		for (int i = 0; i < n; i++) {
			int value = values.get(i);
			int priority = priorites.get(i);
			tree.insert(priority,value);
			System.out.println(String.format("INSERT: priority(%d), value(%d)", priority, value));
		}
		System.out.println("/////////////// Radix Sorting result ///////////////");
		int tmax=tree.max();
		int cur=tmax;
		for (int i = 0; i < n; i++) {
			int v=tree.search_val(cur);
			System.out.println(String.format("priority(%d),value(%d)",cur,v));
			cur=tree.predecessor(cur);
		}
		System.out.println(String.format("BUILD TIME: %d ns", buildTime));
		System.out.println(String.format("RUNNING TIME: %d ns", System.nanoTime() - startTime));
		
		tree=null;
		values = null;
		priorites =null;
	}
}
