import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class ARQServer {

	//Define socket port
	final static int cport = 9876;
	
	public static void main(String[] args) throws IOException {
		
		ServerSocket serverSocket = new ServerSocket(cport);
		System.out.println("Socket opened in port "+cport);
		
		Socket clSocket = serverSocket.accept();
		System.out.println("Client connected");
		
		PrintWriter out = new PrintWriter(clSocket.getOutputStream(), true);
		BufferedReader in = new BufferedReader(new InputStreamReader(clSocket.getInputStream()));
		
		String msg;
		while((msg = in.readLine()) != null)
		{
			
			switch (Integer.parseInt(msg)) {
			case 1:
				stopAndWait(in, out);
				break;

			case 2:
				goBackN(in, out);
				break;
				
			default:
				break;
			}
		}
			
	
		out.close();
		in.close();
		serverSocket.close();
		System.out.println("Resources freed");

	}
	public static void stopAndWait(BufferedReader in, PrintWriter out) throws IOException
	{
		int numpackets = 0, curpacket = 0;
		String msg;
		
		if((msg = in.readLine()) != null)
			numpackets = Integer.parseInt(msg);
		
		System.out.println("Receiving "+ numpackets +" packets with Stop and Wait");
		
		while(curpacket < numpackets && (msg = in.readLine()) != null)
		{
			int recv = Integer.parseInt(msg);
			if(recv == curpacket + 1)
			{
				out.println(++curpacket);
				System.out.println("Acked: "+curpacket+"/"+numpackets);
			}
			else
			{
				out.println(curpacket);
				System.out.println("Acked: "+curpacket+"/"+numpackets);
			}
		}
		
		System.out.println("Packets received");
	}
	
	public static void goBackN(BufferedReader in, PrintWriter out) throws IOException
	{
		int numpackets = 0, curpacket = 0;
		String msg;
		
		if((msg = in.readLine()) != null)
			numpackets = Integer.parseInt(msg);
		
		System.out.println("Receiving "+ numpackets +" packets with Stop and Wait");
		
		while(curpacket < numpackets && (msg = in.readLine()) != null)
		{
			int recv = Integer.parseInt(msg);
			if(recv == curpacket + 1)
			{
				out.println(++curpacket);
				System.out.println("Acked: "+curpacket+"/"+numpackets);
			}
			else
			{
				out.println(curpacket);
				System.out.println("Acked: "+curpacket+"/"+numpackets);
			}
		}
		
		System.out.println("Packets received");
	}
}
