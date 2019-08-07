import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.io.File;
import java.util.AbstractMap.SimpleEntry; 

public class sweepLine {

	public sweepLine() {
	}
	private boolean on_segment(int xi,int yi,int xj, int yj,int xk,int yk) {
		if(Math.min(xi,xj)<=xk && xk<=Math.max(xi,xj) && Math.min(yi,yj)<=yk && yk<=Math.max(yi,yj)){
			return true;
		}
		return false;
	}
	private int sub(int x1,int y1,int x2,int y2) {///????????????????
		return((x1-x2)*(y1-y2));
	}
	private int direction(int xi,int yi,int xj, int yj,int xk,int yk) {
		return(sub(xk,yk,xi,yi)*sub(xj,yj,xi,yi));
	}
	public boolean segments_intersect(int x1,int y1,int x2, int y2,int x3,int y3,int x4,int y4) {
		int d1=direction(x3,y3,x4,y4,x1,y1);
		int d2=direction(x3,y3,x4,y4,x2,y2);
		int d3=direction(x1,y1,x2,y2,x3,y3);
		int d4=direction(x1,y1,x2,y2,x4,y4);
		if(((d1>0 && d2<0) || (d1<0 && d2>0)) && ((d3>0 && d4<0)||(d3<0 && d4>0)))
			return true;
		else if(d1==0 && on_segment(x3,y3,x4,y4,x1,y1))
			return true;
		else if(d2==0 && on_segment(x3,y3,x4,y4,x2,y2))
			return true;
		else if(d3==0 && on_segment(x1,y1,x2,y2,x3,y3))
			return true;
		else if(d4==0 && on_segment(x1,y1,x2,y2,x4,y4))
			return true;
		else return false;
	}
	
	public static void main(String[] args) throws Exception {
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		BinaryHeap<Integer> maxHeap = new BinaryHeap<Integer>(1000000, Integer.MAX_VALUE, Integer.MIN_VALUE);
		vEBtree<Integer> tree = new vEBtree<Integer>((int) Math.pow(2, 20), -1, 1, 0);
		vEBtree2 tree2=new vEBtree2(1000000);
		List<Integer> x1s = new ArrayList<>();
		List<Integer> y1s = new ArrayList<>();
		List<Integer> x2s = new ArrayList<>();
		List<Integer> y2s = new ArrayList<>();
		int x1=0,y1=0,x2=0,y2=0;
		int xmax=0,ymax=0,xmin=0,ymin=0;
		int n =0;
		List<Integer> sorted_index = new ArrayList<>();
	    Scanner s = new Scanner(new File("bin/smooth.txt"));//////////// Smooth
	    if (s.hasNext()&&s.hasNextInt()){
	    	x1=s.nextInt();
	    	y1=s.nextInt();
	    	x2=s.nextInt();
	    	y2=s.nextInt();
	    }
	    int u=0;
	    while (s.hasNext()){
	        if(s.hasNextInt()){
	        	n++;
	        	int buf=0;
	        	buf=s.nextInt();
	            x1s.add(buf);
	            if(buf>xmax) xmax=buf;
	            if(buf<xmin) xmin=buf;
	            
	            buf=s.nextInt();
	            y1s.add(buf);
	            if(buf>ymax) ymax=buf;
	            if(buf<ymin) ymin=buf;
	            
	        	buf=s.nextInt();
	            x2s.add(buf);
	            if(buf>xmax) xmax=buf;
	            if(buf<xmin) xmin=buf;
	            
	            buf=s.nextInt();
	            y2s.add(buf);
	            if(buf>ymax) ymax=buf;
	            if(buf<ymin) ymin=buf;
	        }
	    }
	    u=xmax-xmin;
	    u=ymax-ymin>u?ymax-ymin:u;
	    boolean sortMethod=false;
	    if(Math.log(n)/Math.log(2)<u && u<Math.log10(n)) {
	    	sortMethod=true;
	    }
	    s.close();
		Collections.shuffle(x1s);
		Collections.shuffle(y1s);
		Collections.shuffle(x2s);
		Collections.shuffle(y2s);
		long startTime = System.nanoTime();
		long insertTime = System.nanoTime() - startTime;
		long extractMax = System.nanoTime() - startTime;
		startTime = System.nanoTime();
		for (int i = 0; i < n; i++) {
			maxHeap.insert(i,y1s.get(i));
			tree.insert(i,y1s.get(i));
			tree2.insert(y1s.get(i),i);
			System.out.println(String.format("%d::(%d, %d)-(%d, %d)",i,x1s.get(i),y1s.get(i),x2s.get(i),y2s.get(i)));
		}
		System.out.println("*****************");
		if(sortMethod) {
			for(int i=1;i<n;i++) {
				int v=0,p=0;
				v=maxHeap.getValue(1);
				p=maxHeap.getPriority(1);
				sorted_index.add(p);// store indexes
				if (i<n) maxHeap.extractMax();
			}
		}
		else {
			int tmax=tree2.max();
			int cur=tmax;
			for (int i = 1; i < n; i++) {
				int v=tree2.search_val(cur);
				sorted_index.add(v);
				cur=tree2.predecessor(cur);
				//System.out.println(String.format("%d:%d",cur,v));
			}
		}
			
		//System.out.println(String.format("y1(%d), (%d, %d, %d, %d)",v,x1s.get(p),y1s.get(p),x2s.get(p),y2s.get(p)));
		n--;
		sweepLine sL=new sweepLine();
		boolean result = false;
		for(int i=1;i<n;i++) {
			for(int j=i+1;j<n;j++) {
				int index=sorted_index.get(n-i);
				//System.out.println(String.format("%d:%d",i,y1s.get(index)));
				result=sL.segments_intersect(x1,y1,x2,y2,x1s.get(i),y1s.get(i),x2s.get(i),y2s.get(i));
				if(result) break;
			}
			if(result) break;
			x1=x1s.get(i);y1=y1s.get(i);x2=x2s.get(i);y2=y2s.get(i);
		}
		System.out.println("------ Smooth segments process result ------");
		if(sortMethod) System.out.println("Binary heap selected!");
		else System.out.println("vEB tree selected!");
		n+=2;
		System.out.println(n+" segments!");
		System.out.println("Sweeping line result:"+result);

		SimpleEntry<Integer, Integer> max = maxHeap.extractMax();
		System.out.println(String.format("\nRUNNING TIME FOR INSERT FUNCTION: %d ns", System.nanoTime() - startTime));
		System.out.println(String.format("EXTRACT MAX VALUE(%d), PRIORITY(%d)", max.getKey(), max.getValue()));
		System.out.println(String.format("TIME TO EXTRACT MAX ELEMENT: %d ns", extractMax));
		System.out.println(String.format("INCREASE KEY FOR N ELEMENTS: %d ns", insertTime));
		System.out.println(String.format("TOTAL RUNNING TIME FOR ALGORITHM: %d ns", System.nanoTime() - startTime + extractMax + insertTime));
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////	
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	maxHeap = new BinaryHeap<Integer>(1000000, Integer.MAX_VALUE, Integer.MIN_VALUE);
	tree = new vEBtree<Integer>((int) Math.pow(2, 20), -1, 1, 0);
	tree2=new vEBtree2(1000000);
	x1s = new ArrayList<>();
	y1s = new ArrayList<>();
	x2s = new ArrayList<>();
	y2s = new ArrayList<>();
//	x1=0,y1=0,x2=0,y2=0;
//	xmax=0,ymax=0,xmin=0,ymin=0;
	n =0;
	sorted_index = new ArrayList<>();
	s = new Scanner(new File("bin/sparse.txt"));//////////// Smooth
	if (s.hasNext()&&s.hasNextInt()){
		x1=s.nextInt();
		y1=s.nextInt();
		x2=s.nextInt();
		y2=s.nextInt();
	}
	u=0;
	while (s.hasNext()){
		if(s.hasNextInt()){
			n++;
			int buf=0;
			buf=s.nextInt();
			x1s.add(buf);
			if(buf>xmax) xmax=buf;
			if(buf<xmin) xmin=buf;
			
			buf=s.nextInt();
			y1s.add(buf);
			if(buf>ymax) ymax=buf;
			if(buf<ymin) ymin=buf;
			
			buf=s.nextInt();
			x2s.add(buf);
			if(buf>xmax) xmax=buf;
			if(buf<xmin) xmin=buf;
			
			buf=s.nextInt();
			y2s.add(buf);
			if(buf>ymax) ymax=buf;
			if(buf<ymin) ymin=buf;
		}
	}
	u=xmax-xmin;
	u=ymax-ymin>u?ymax-ymin:u;
	sortMethod=false;
	if(Math.log(n)/Math.log(2)<u && u<Math.log10(n)) {
		sortMethod=true;
	}
	s.close();
	Collections.shuffle(x1s);
	Collections.shuffle(y1s);
	Collections.shuffle(x2s);
	Collections.shuffle(y2s);
	startTime = System.nanoTime();
	insertTime = System.nanoTime() - startTime;
	extractMax = System.nanoTime() - startTime;
	startTime = System.nanoTime();
	for (int i = 0; i < n; i++) {
		maxHeap.insert(i,y1s.get(i));
		tree.insert(i,y1s.get(i));
		tree2.insert(y1s.get(i),i);
		System.out.println(String.format("%d::(%d, %d)-(%d, %d)",i,x1s.get(i),y1s.get(i),x2s.get(i),y2s.get(i)));
	}
	System.out.println("*****************");
	if(sortMethod) {
		for(int i=1;i<n;i++) {
			int v=0,p=0;
			v=maxHeap.getValue(1);
			p=maxHeap.getPriority(1);
			sorted_index.add(p);// store indexes
			if (i<n) maxHeap.extractMax();
		}
	}
	else {
		int tmax=tree2.max();
		int cur=tmax;
		for (int i = 1; i < n; i++) {
			int v=tree2.search_val(cur);
			sorted_index.add(v);
			cur=tree2.predecessor(cur);
			//System.out.println(String.format("%d:%d",cur,v));
		}
	}
	
	//System.out.println(String.format("y1(%d), (%d, %d, %d, %d)",v,x1s.get(p),y1s.get(p),x2s.get(p),y2s.get(p)));
	n--;
	sL=new sweepLine();
	result = false;
	for(int i=1;i<n;i++) {
	for(int j=i+1;j<n;j++) {
	int index=sorted_index.get(n-i);
	//System.out.println(String.format("%d:%d",i,y1s.get(index)));
	result=sL.segments_intersect(x1,y1,x2,y2,x1s.get(i),y1s.get(i),x2s.get(i),y2s.get(i));
	if(result) break;
	}
	if(result) break;
	x1=x1s.get(i);y1=y1s.get(i);x2=x2s.get(i);y2=y2s.get(i);
	}
	System.out.println("------ Sparse segments process result ------");
	if(sortMethod) System.out.println("Binary heap selected!");
	else System.out.println("vEB tree selected!");
	n+=2;
	System.out.println(n+" segments!");
	System.out.println("Sweeping line result:"+result);
	
	max = maxHeap.extractMax();
	System.out.println(String.format("\nRUNNING TIME FOR INSERT FUNCTION: %d ns", System.nanoTime() - startTime));
	System.out.println(String.format("EXTRACT MAX VALUE(%d), PRIORITY(%d)", max.getKey(), max.getValue()));
	System.out.println(String.format("TIME TO EXTRACT MAX ELEMENT: %d ns", extractMax));
	System.out.println(String.format("INCREASE KEY FOR N ELEMENTS: %d ns", insertTime));
	System.out.println(String.format("TOTAL RUNNING TIME FOR ALGORITHM: %d ns", System.nanoTime() - startTime + extractMax + insertTime));
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////			
		maxHeap =null;
		tree = null;
		tree2=null;
		x1s = null;
		y1s = null;
		x2s = null;
		y2s = null;
	}
}
