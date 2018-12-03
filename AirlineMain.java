import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class AirlineMain{
	private EdgeWeightedGraph graph;
	private String[] citylookup;
	
	public AirlineMain(String s) {
		File f = new File(s);
		createGraph(f);
		int a=0;
		a=a+1;
	}
	
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
				graph.addEdge(v,w,distance,price);
			}
			read.close();
		}catch(IOException e1) {
			System.out.println("Problem reading file");
		}
	}

	public static void main(String[] args){
		AirlineMain temp = new AirlineMain("a5data1.txt");
	}
}