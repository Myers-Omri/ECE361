import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Scanner;
import java.util.StringTokenizer;

// The network is represented by a graph, that contains nodes and edges
class Node implements Comparable<Node>
{
	public final int name;
	public Edge[] neighbors;
	public double minDistance = Double.POSITIVE_INFINITY;
	public Node previous = null;     // to keep the path
	public Node(int argName) 
	{ 
		name = argName; 
	}

	public int compareTo(Node other)
	{
		return Double.compare(minDistance, other.minDistance);
	}
}

class Edge
{
	public final Node target;
	public final double weight;
	public Edge(Node argTarget, double argWeight)
	{ 
		target = argTarget;
		weight = argWeight; 
	}
}

public class RoutingClient {

	static String mode;
	static String host;
	static int port;

	public static void adjacenyToEdges(double[][] matrix, List<Node> v)
	{
		for(int i = 0; i < matrix.length; i++)
		{
			v.get(i).neighbors = new Edge[matrix.length];
			for(int j = 0; j < matrix.length; j++)
			{
				v.get(i).neighbors[j] =  new Edge(v.get(j), matrix[i][j]);	
			}
		}
	}
	
	public static void computePaths(Node source)
	{
		source.minDistance = 0;
		PriorityQueue<Node> NodeQueue = new PriorityQueue<Node>();
		NodeQueue.add(source);
		
		while (NodeQueue.size() != 0){
			Node sourceNode = NodeQueue.poll();
			Edge [] edge_arr = source.neighbors;
			
			int num_edges = edge_arr.length;
			for (int e = 0; e < num_edges; ++e){
				Edge curr_edge = edge_arr[e];
				Node targetNode = curr_edge.target;
				double distanceThroughSource = sourceNode.minDistance + curr_edge.weight;
				if (distanceThroughSource < targetNode.minDistance){
					System.out.println("hello");
					NodeQueue.remove(targetNode);
					targetNode.minDistance = distanceThroughSource;
					targetNode.previous = sourceNode;
					System.out.println("Set the previous of "+targetNode.name+" to "+targetNode.previous.name);
					NodeQueue.add(targetNode);
					System.out.println(curr_edge.target.name + " " + curr_edge.weight);
				}
			}
			
		}
		System.out.println("finished");
		// Complete the body of this function
	}

	public static List<Integer> getShortestPathTo(Node target)
	{
		List<Integer> path = new ArrayList<Integer>();
		Node tmpTarget = target;

		while (tmpTarget != null) { //how to check if not null??
			path.add(tmpTarget.name);
			tmpTarget = tmpTarget.previous;
		}
		
		Collections.reverse(path);
		return path;
	}


		// Complete the body of this function

	/**
	 * @param args
	 */

	public static void main(String[] args) {

		if(args.length<=0)
		{
			mode="client";
			host="localhost";
			port=9876;
		}
		else if(args.length==1)
		{
			mode=args[0];
			host="localhost";
			port=9876;
		}
		else if(args.length==3)
		{
			mode=args[0];
			host=args[1];
			port=Integer.parseInt(args[2]);
		}
		else
		{
			System.out.println("improper number of arguments.");
			return;
		}

		try 
		{
			Socket socket=null;
			if(mode.equalsIgnoreCase("client"))
			{
				socket=new Socket(host, port);
			}
			else if(mode.equalsIgnoreCase("server"))
			{
				ServerSocket ss=new ServerSocket(port);
				socket=ss.accept();
			}
			else
			{
				System.out.println("improper type.");
				return;
			}
			System.out.println("Connected to : "+ host+ ":"+socket.getPort());

			//reader and writer:
			BufferedReader reader=new BufferedReader(new InputStreamReader(socket.getInputStream())); //for reading lines
			DataOutputStream writer=new DataOutputStream(socket.getOutputStream());	//for writing lines.
			Scanner scr = new Scanner(System.in);
			
			while(socket!=null && socket.isConnected() && !socket.isClosed()){
				System.out.println("Enter number of nodes in the network, 0 to Quit: ");
				int noNodes = scr.nextInt();
				

				// Send noNodes to the server, and read a String from it containing adjacency matrix
				writer.write(noNodes);
						// TODO: Done
				String string_mat = reader.readLine();

				// Create an adjacency matrix after reading from server
				double[][] matrix = new double[noNodes][noNodes];
				
				// Use StringToenizer to store the values read from the server in matrix
				StringTokenizer st_mat = new StringTokenizer(string_mat);
				for (int r = 0; r < noNodes ; ++r){
					for (int c = 0; c < noNodes ; ++c){
						matrix[r][c] = Double.parseDouble(st_mat.nextToken());

					}

				}
				// Done
				
				System.out.println("Adjacency matrix");
				for(double[] i : matrix)
				{
					for(double j : i)
						System.out.print(j+" ");
					System.out.println();
				}
			
				//The nodes are stored in a list, nodeList
				ArrayList<Node> nodeList = new ArrayList<Node>();
				for(int i = 0; i < noNodes; i++){
					nodeList.add(new Node(i));
				}
				
				// Create edges from adjacency matrix
				adjacenyToEdges(matrix, nodeList);
				
				// Finding shortest path for all nodes
				for(int j=0; j<noNodes; ++j) {
					computePaths(nodeList.get(j));
					System.out.println("Node " + j);
					for (int n = 0; n < noNodes; n++) {
						List<Integer> tmp_path = getShortestPathTo(nodeList.get(n));
						double total_dist = 0;
						for (int opn = 0; opn < tmp_path.size(); opn++) {
							tmp_path = getShortestPathTo(nodeList.get(opn));
							for (int l = 0; l < tmp_path.size(); l++) {
								int curNodeTarget = tmp_path.get(l);
								total_dist += (nodeList.get(curNodeTarget)).minDistance;

							}
							System.out.print("Total time to reach node " + opn + ":");
							System.out.print(total_dist + "ms, Path: [ ");
								for(int i : tmp_path)
									System.out.print(i+", ");
							System.out.println(" ]");
						}
						tmp_path.clear();

					}
	//initialize the nodes for the next iteration.
					for (int nr = 0; nr < noNodes; ++nr) {
						(nodeList.get(nr)).minDistance = 0;
						(nodeList.get(nr)).previous = null;

					}
					// Complete the code here

				}
					socket.close();
			}
			System.out.println("Quit");

			scr.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		


	}

}
