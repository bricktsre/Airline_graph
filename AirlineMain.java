import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Scanner;
import java.util.Stack;

public class AirlineMain{
	private File f;
	private EdgeWeightedGraph graph;
	private String[] citylookup;
	private HashMap<String,Integer> cityidtable= new HashMap<String,Integer>();
	private ArrayList<Edge> edges = new ArrayList<Edge>();
	
	public AirlineMain(String s) {
		f = new File(s);
		createGraph(f);
		int a=0;
		a=a+1;
	}
	
	/**
	 * Creates and EdgeWeightedGraph based on what is in the inputted text file
	 * 
	 * @param f the file to read in
	 */
	private void createGraph(File f) {
		try {
			Scanner read = new Scanner(f);
			int vertices =0;
			if(read.hasNextLine()) 
				vertices = Integer.parseInt(read.nextLine());
			graph = new EdgeWeightedGraph(vertices);
			citylookup=new String[vertices];
			for(int i=0;i<vertices;i++) {
				if(read.hasNextLine()) {
					String s = read.nextLine();
					citylookup[i]= s;
					cityidtable.put(s, i);
				}
			}while(true) {
				if(!read.hasNextInt()) break;
				int v = read.nextInt()-1;
				int w = read.nextInt()-1;
				int distance = read.nextInt();
				double price = read.nextDouble();
				Edge e = new Edge(v,w,distance,price);
				graph.addEdge(e);
				edges.add(e);
			}
			read.close();
		}catch(IOException e1) {
			System.out.println("Problem reading file");
		}
	}

	/**
	 * Creates a minimum spanning tree of the graph using Kruskal's algorithm
	 * based on distance weight of the edges
	 */
	public void minimumSpanningTree() {
		LinkedList<Edge> mst = new LinkedList<Edge>();
		double weight=0.0;
		PriorityQueue<Edge> pq = new PriorityQueue<Edge>(graph.V(), new DistanceCompare());
        for (Edge e : graph.edges()) {
            pq.add(e);
        }

        // run greedy algorithm
        UF uf = new UF(graph.V());
        while (!pq.isEmpty() && mst.size() < graph.V() - 1) {
            Edge e = pq.poll();
            int v = e.either();
            int w = e.other(v);
            if (!uf.connected(v, w)) { // v-w does not create a cycle
                uf.union(v, w);  // merge v and w components
                mst.offer(e);  // add edge e to mst
                weight += e.getDistance();
            }
        }
        System.out.println("\nThe edges in the MST based on distance follow:");
        for(Edge e: mst) {
        	System.out.println(citylookup[e.either()] + "," + citylookup[e.other(e.either())] + " : " + e.getDistance());
        }
	}
	
	/**
	 * Finds the shortest distance between two cities
	 * 
	 * @param city1 start city
	 * @param city2 end city
	 */
	public void shortestDistancePath(String city1,String city2) {
		int s=0,d =0;
		try{
			s = cityidtable.get(city1);
			d = cityidtable.get(city2);
		}catch(NullPointerException e) {
			System.out.println("\nOne of the two cities entered is not valid, please check capitalization and spelling");
			return;
		}	
		double[] distTo;          // distTo[v] = distance  of shortest s->v path
	    Edge[] edgeTo;            // edgeTo[v] = last edge on shortest s->v path
	    IndexMinPQ<Double> pq; 
		
	    for (Edge e : graph.edges()) {
            if (e.getDistance() < 0)
                throw new IllegalArgumentException("edge " + e + " has negative weight");
        }

        distTo = new double[graph.V()];
        edgeTo = new Edge[graph.V()];

        for (int v = 0; v < graph.V(); v++)
            distTo[v] = Double.POSITIVE_INFINITY;
        distTo[s] = 0.0;

        // relax vertices in order of distance from s
        pq = new IndexMinPQ<Double>(graph.V());
        pq.insert(s, distTo[s]);
        while (!pq.isEmpty()) {
            int v = pq.delMin();
            for (Edge e : graph.adj(v))
                relaxD(e, v,distTo, edgeTo, pq);
        }
        if (!(distTo[d] < Double.POSITIVE_INFINITY)) {
        	System.out.println(city1 +" to " + city2 + " is not a possible route");
        	return;
        }	
        Stack<Edge> path = new Stack<Edge>();
        int x = d;
        for (Edge e = edgeTo[d]; e != null; e = edgeTo[x]) {
            path.push(e);
            x = e.other(x);
        }
        System.out.println("\nPath with edges (in reverse order):");
        System.out.print(city2+ " ");
        int total =0;
        for(Edge e: path) {
        	System.out.print(e.getDistance() +" " +citylookup[e.either()] +" ");
        	total+=e.getDistance();
        }
        System.out.println("\nShortest distance from " + city1+" to "+city2+" is " +total);   	
	}
	
	/**
	 * relax edge e and update pq if changed	
	 * 
	 * @param e edge
	 * @param v vertex
	 * @param distTo distance of shortest path
	 * @param edgeTo last edge on shortest path
	 * @param pq	 priority queue of edges
	 */
	 @SuppressWarnings("unchecked")
	private void relaxD(Edge e, int v, double[] distTo, Edge[] edgeTo,IndexMinPQ pq) {
	        int w = e.other(v);
	        if (distTo[w] > distTo[v] + e.getDistance()) {
	            distTo[w] = distTo[v] + e.getDistance();
	            edgeTo[w] = e;
	            if (pq.contains(w)) pq.decreaseKey(w, distTo[w]);
	            else                pq.insert(w, distTo[w]);
	        }
	    }
	 
	 /**
		 * Finds the smallest price between two cities
		 * 
		 * @param city1 start city
		 * @param city2 end city
		 */
	public void shortestPricePath(String city1,String city2) {
		int s=0,d =0;
		try{
			s = cityidtable.get(city1);
			d = cityidtable.get(city2);
		}catch(NullPointerException e) {
			System.out.println("\nOne of the two cities entered is not valid, please check capitalization and spelling");
			return;
		}	
		double[] distTo;          // distTo[v] = distance  of shortest s->v path
	    Edge[] edgeTo;            // edgeTo[v] = last edge on shortest s->v path
	    IndexMinPQ<Double> pq; 
			
	    for (Edge e : graph.edges()) {
	       if (e.getPrice() < 0)
	           throw new IllegalArgumentException("edge " + e + " has negative weight");
	       }

	    distTo = new double[graph.V()];
	    edgeTo = new Edge[graph.V()];

        for (int v = 0; v < graph.V(); v++)
	         distTo[v] = Double.POSITIVE_INFINITY;
	    distTo[s] = 0.0;

	        // relax vertices in order of distance from s
	        pq = new IndexMinPQ<Double>(graph.V());
	        pq.insert(s, distTo[s]);
	        while (!pq.isEmpty()) {
	            int v = pq.delMin();
	            for (Edge e : graph.adj(v))
	                relaxP(e, v,distTo, edgeTo, pq);
	        }
	        if (!(distTo[d] < Double.POSITIVE_INFINITY)) {
	        	System.out.println(city1 +" to " + city2 + " is not a possible route");
	        	return;
	        }	
	        Stack<Edge> path = new Stack<Edge>();
	        int x = d;
	        for (Edge e = edgeTo[d]; e != null; e = edgeTo[x]) {
	            path.push(e);
	            x = e.other(x);
	        }
	       System.out.println("\nPath with edges (in reverse order):");
	        System.out.print(city2+ " ");
	       double total =0;
	       for(Edge e: path) {
	        	System.out.print(e.getDistance() +" " +citylookup[e.either()] +" ");
	        	total+=e.getPrice();
	       }
	       System.out.println("\nShortest cost from " + city1+" to "+city2+" is " +total);   	
	}
		
	/**
	 * relax edge e and update pq if changed	
	 * 
	 * @param e edge
	 * @param v vertex
	 * @param distTo distance of shortest path
	 * @param edgeTo last edge on shortest path
	 * @param pq	 priority queue of edges
	 */
	 @SuppressWarnings("unchecked")
	private void relaxP(Edge e, int v, double[] distTo, Edge[] edgeTo,IndexMinPQ pq) {
		       int w = e.other(v);
		      if (distTo[w] > distTo[v] + e.getPrice()) {
		           distTo[w] = distTo[v] + e.getPrice();
		           edgeTo[w] = e;
		           if (pq.contains(w)) pq.decreaseKey(w, distTo[w]);
		           else                pq.insert(w, distTo[w]);
		     }
	}
	
	 /**
	  * Finds the path with the smallest number of hops between the two cities
	  * 
	  * @param city1  starting city
	  * @param city2  destination city
	  */
	 public void shortestHops(String city1, String city2) {
		 int s=0,d =0;
			try{
				s = cityidtable.get(city1);
				d = cityidtable.get(city2);
			}catch(NullPointerException e) {
				System.out.println("\nOne of the two cities entered is not valid, please check capitalization and spelling");
				return;
			}
		    boolean[] marked = new boolean[graph.V()];  // marked[v] = is there an s-v path
		    int[] edgeTo = new int[graph.V()];;      	// edgeTo[v] = previous edge on shortest s-v path
		    int[] distTo = new int[graph.V()];;    		// distTo[v] = number of edges shortest s-v path
		    
		    LinkedList<Integer> q = new LinkedList<Integer>();
	        for (int v = 0; v < graph.V(); v++)
	            distTo[v] = Integer.MAX_VALUE;
	        distTo[s] = 0;
	        marked[s] = true;
	        q.offer(s);

	        while (!q.isEmpty()) {
	            int v = q.poll();
	            for (int w : graph.adjI(v)) {
	                if (!marked[w]) {
	                    edgeTo[w] = v;
	                    distTo[w] = distTo[v] + 1;
	                    marked[w] = true;
	                    q.offer(w);
	                }
	            }
	        }
	        if (!marked[d]) {
	        	System.out.println(city1 +" to " + city2 + " is not a possible route");
	        	return;
	        }
	        Stack<Edge> path = new Stack<Edge>();
	        int x = d;
	        for (int e = edgeTo[d]; distTo[x] != 0; e = edgeTo[x]) {
	            for(Edge a: graph.adj(x)) {
	            	if(a.other(x)==e) { 
	            		path.push(a);
	            		x = e;
	            		break;
	            	}
	            }
	        }
	       System.out.println("\nPath with edges (in reverse order):");
	       int total =0;
	       for(Edge e: path) {
	        	System.out.print(citylookup[e.either()] +" ");
	        	total++;
	       }
	       System.out.print(city1+ " ");
	       System.out.println("\nShortest hops from " + city1+" to "+city2+" is " +total);
	 }
	 
	 /**
	  * prints out all possible routes under the maximum route
	  * 
	  * @param maxprice maximum price of a route
	  */
	  @SuppressWarnings("rawtypes")
	 public void underPrice(double maxprice) {	 
		ArrayList<ArrayList<Stack>> a = new ArrayList<ArrayList<Stack>>(graph.V()); 
		for(int i=0;i<graph.V();i++) 
			a.add(runDFS(i,maxprice));
		System.out.println("\nAll Paths at most " + maxprice);
		System.out.println("Note that paths are duplicated, once from each end city's point of view");
		for(int i=0;i<graph.V();i++) {
			for(Stack<Edge> s: a.get(i)) {
				double price = 0.0;
				Edge e=null;
				while(!s.isEmpty()) {
					e=s.pop();
					System.out.print(citylookup[e.either()]+" " + e.getPrice()+ " ");
					price+=e.getPrice();
				}System.out.println(citylookup[e.other(e.either())] + " Cost: " + price);
			}
		}
	 }
	 
	  /**
	   * Returns an arryalist of stacks each being a route that is under the maxprice
	   * all starting from the argument vertex
	   * 
	   * @param v 		 starting vertex
	   * @param maxprice maximum price one a route
	   * @return 		 arraylist of stacks each representing one path
	   */
	 @SuppressWarnings("rawtypes")
	private ArrayList<Stack> runDFS(int v, double maxprice) {
		 	boolean[] marked = new boolean[graph.V()];  // marked[v] = true iff v is reachable from s
		    int[] edgeTo = new int[graph.V()];      // edgeTo[v] = last edge on path from s to v
		    dfs(v,marked,edgeTo);
		    ArrayList<Stack> temp = new ArrayList<Stack>();
		    for(int i=0;i<graph.V();i++) {
		    	if(i!=v) {
		    		Stack<Edge> a = pathTo(v,i,marked,edgeTo,maxprice);
		    		if(a!=null) temp.add(a);
		    	}
		    }
		    return temp;
	 }
	 
	 /**
	  * Recursive Implementation of a depth first search
	  * 
	  * @param v       starting vertex
	  * @param marked  boolean array of which vertices have been touched
	  * @param edgeTo  last edge on path from s to v
	  */
	 private void dfs(int v, boolean[] marked, int[] edgeTo) { 
	        marked[v] = true;
	        for (int w : graph.adjI(v)) {
	            if (!marked[w]) {
	                edgeTo[w] = v;
	                dfs(w,marked,edgeTo);
	            }
	        }
	    }
	 
	 /**
	     * Returns a directed path from the source vertex {@code s} to vertex {@code v}, or
	     * {@code null} if no such path.
	     * @param  v the vertex
	     * @return the sequence of vertices on a directed path from the source vertex
	     *         {@code s} to vertex {@code v}, as an Iterable
	     * @throws IllegalArgumentException unless {@code 0 <= v < V}
	     */
	    private Stack<Edge> pathTo(int s, int d, boolean[] marked,int[] edgeTo,double maxprice) {
	        if (!marked[d]) return null;        
	        Stack<Edge> path = new Stack<Edge>();
	        int x = d;
	        double currentprice = 0;
	        for (int e = edgeTo[d]; x != s; e = edgeTo[x]) {
	            for(Edge a: graph.adj(x)) {
	            	if(a.other(x)==e) { 
	            		currentprice+=a.getPrice();
	            		if(currentprice>maxprice) return null;
	            		path.push(a);
	            		x = e;
	            		break;
	            	}
	            }
	        }
	        return path;
	    }
	 
	/**
	 * Adds a new edge to the graph between two existing vertices
	 * 
	 * @param city1	   vertex one
	 * @param city2	   vertex two
	 * @param distance distance weight of edge
	 * @param price    price weight of edge
	 */
	public void addRoute(String city1, String city2, int distance, double price) {
		int v=0,w =0;
		try{
			v = cityidtable.get(city1);
			w = cityidtable.get(city2);
		}catch(NullPointerException e) {
			System.out.println("\nOne of the two cities entered is not valid, please check capitalization and spelling");
			return;
		}	
		Edge e = new Edge(v,w,distance,price);
		edges.add(e);
		graph.addEdge(e);
		System.out.println("\nAdded route of " + distance + " miles from " + city1 + " to " + city2 + " for $" + price);
	}
	
	/**
	 * Removes an existing edge from the graph
	 * 
	 * @param city1 vertex one
	 * @param city2 vertex two
	 */
	public void removeRoute(String city1, String city2) {
		int v=0,w =0;
		try{
			v = cityidtable.get(city1);
			w = cityidtable.get(city2);
		}catch(NullPointerException e) {
			System.out.println("\nOne of the two cities entered is not valid, please check capitalization and spelling");
			return;
		}
		Edge e = graph.removeEdge(v, w);
		edges.remove(e);
		System.out.println("\nRemoved route of " + e.getDistance() + " miles from " + city1 + " to " + city2 + " for $" + e.getPrice());
	}
	
	/**
	 * Prints out the graph in the form: City1 to City2 is distance miles for $price
	 */
	public void printGraph() {
		for(Edge e: edges) 
			System.out.println(citylookup[e.either()] + " to " + citylookup[e.other(e.either())] + " is " + e.getDistance()+ " miles for $" + e.getPrice());
	}
	
	/**
	 * Writes out the current graph to the file it came from and in the same style
	 */
	public void writeOutGraph() {
		try {
			FileWriter fw = new FileWriter(f);
			PrintWriter pw = new PrintWriter(fw);
			pw.write(citylookup.length+"");
			pw.println();
			for(int i =0;i<citylookup.length;i++) {
				pw.write(citylookup[i]);
				pw.println();
			}for(Edge e: edges) {
				pw.write(e.either()+1 + " " + (e.other(e.either())+1) + " " + e.getDistance() + " " + e.getPrice());
				pw.println();
			}pw.close();
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Comparator for Edges based on distance
	 * @author Greg
	 *
	 */
	class DistanceCompare implements Comparator<Edge> { 
	    public int compare(Edge a, Edge b) { 
	    	return Integer.compare(a.getDistance(), b.getDistance());
	    } 
	} 
	 
	/**
	 * Comparator for edges based on price
	 * @author Greg
	 *
	 */
	class PriceCompare implements Comparator<Edge> { 
	    public int compare(Edge a, Edge b) { 
	        return Double.compare(a.getPrice(), b.getPrice()) ;
	    } 
	}
	
	public static void main(String[] args){
		AirlineMain am = new AirlineMain(args[0]);
		Scanner read = new Scanner(System.in);
		boolean q = false;
		String s1="";
		String s2 ="";
		int distance = -1;
		double price = -1.0;
		while(!q) {
			System.out.println("\nWhat would you like to do: Print (G)raph, (M)inimum Spanning Tree, (D)istance Shortest Path, (P)rice Shortest Path, (H)ops Shortest Path, (U)nder a Certain Price, (N)ew Route, (R)emove Route, (Q)uit");
			String s = read.nextLine();
			switch(s) {
				case "G":   am.printGraph();
						    break;
				case "M":   am.minimumSpanningTree();
						    break;
				case "D":   System.out.print("City1: ");
							s1 = read.nextLine();
							System.out.print("City2: ");
							s2 = read.nextLine();
							am.shortestDistancePath(s1, s2);
							break;
				case "P":	System.out.print("City1: ");
							s1 = read.nextLine();
							System.out.print("City2: ");
							s2 = read.nextLine();
							am.shortestPricePath(s1, s2);
							break;		
				case "H":   System.out.print("City1: ");
							s1 = read.nextLine();
							System.out.print("City2: ");
							s2 = read.nextLine();
							am.shortestHops(s1, s2);
							break;
				case "U":   System.out.print("Maximum Price: ");
							price = read.nextDouble();
							am.underPrice(price);
						    break;
				case "N":	System.out.print("City1: ");
							s1 = read.nextLine();
							System.out.print("City2: ");
							s2 = read.nextLine();
							System.out.print("Distance: ");
							distance = read.nextInt();
							System.out.print("Price: ");
							price = read.nextDouble();
							am.addRoute(s1, s2,distance,price);
							break;	
				case "R":   System.out.print("City1: ");
							s1 = read.nextLine();
							System.out.print("City2: ");
							s2 = read.nextLine();
							am.removeRoute(s1, s2);
							break;	
				case "Q":   am.writeOutGraph();
						    q=true;
						    break;
			    default:    break;
			}
		}
		read.close();
	}
}