import java.io.File;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Scanner;

public class AirlineMain{
	private EdgeWeightedGraph graph;
	private String[] citylookup;
	private ArrayList<Edge> edges = new ArrayList<Edge>();
	
	public AirlineMain(String s) {
		File f = new File(s);
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
			for(int i=0;i<vertices;i++)
				if(read.hasNextLine()) citylookup[i]=read.nextLine();
			while(true) {
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
		PriorityQueue<Edge> pq = new PriorityQueue<Edge>();
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
	 * Prints out the graph in the form: City1 to City2 is distance miles for $price
	 */
	public void printGraph() {
		for(Edge e: edges) 
			System.out.println(citylookup[e.either()] + " to " + citylookup[e.other(e.either())] + " is " + e.getDistance()+ " miles for $" + e.getPrice());
	}
	
	public static void main(String[] args){
		AirlineMain temp = new AirlineMain("a5data1.txt");
		temp.printGraph();
		temp.minimumSpanningTree();
	}
}