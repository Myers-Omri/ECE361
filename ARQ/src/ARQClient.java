import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class ARQClient {
	
	//Define server port
	final private static int cport = 9876;
	
	private static int lastAck;
	

	public static void main(String[] args) throws UnknownHostException, IOException {
		
		//Open socket to server
		Socket svSocket = new Socket("127.0.0.1", cport);
		
		//Scan user input
		Scanner userIn = new Scanner(System.in);
		
		//Create IO buffers
		final DataOutputStream out = new DataOutputStream(svSocket.getOutputStream());
		final DataInputStream in = new DataInputStream(svSocket.getInputStream());
		
		while(userIn.hasNext())
		{
			switch (userIn.nextInt()) {
			case 1:
				//stopAndWait(userIn, in, out);
				break;
				
			case 2:
				goBackN(userIn, in, out);
				break;
			
			default:
				break;
			}		
		}		

		//Close buffers
		in.close();
		out.close();
		userIn.close();
		//Close sockets
		svSocket.close();
		System.out.println("Resources freed");

	}
	
	/*
	public static void stopAndWait(Scanner userIn, BufferedReader in, DataOutputStream out) throws IOException
	{
		String msg;
		System.out.println("Enter number of packets");
		int numpackets = userIn.nextInt();
		System.out.println("Sending with Stop and Wait");
		out.println("1");
		out.println(numpackets);
		int sent = 1;
		while(sent <= numpackets)
		{
			out.println(sent);
			System.out.println("Sent: "+sent+"/"+numpackets);
			if((msg = in.readLine()) != null)
			{
				if(Integer.parseInt(msg) == sent)
				{
					System.out.println("Acked: "+(sent++)+"/"+numpackets);
				}
			}
			else
			{
				System.out.println("Conection Lost");
				break;
			}
					
		}
		System.out.println("Packets sent");
	}*/
	
	public static void goBackN(Scanner userIn, final DataInputStream in, DataOutputStream out) throws IOException
	{
		System.out.println("Enter number of packets");
		int numpackets = userIn.nextInt();
		
		System.out.println("Enter probability of server dropping packets");
		int proberror  = userIn.nextInt();
		
		System.out.println("Enter window size");
		int wSize = userIn.nextInt();
		
		System.out.println("Enter timeout limit");
		int timeOut = userIn.nextInt();
		
		System.out.println("Sending Go Back N");
		
		out.write(numpackets);
		out.write(proberror);
		setLastAck(0);
		
		long[] timer = new long[wSize];
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					int ack;
					while((ack = in.read()) != -1)
					{
						ARQClient.setLastAck(ack);
						System.out.println("ACKed: "+ ARQClient.getLastAck());
					}
				} catch (Exception e) {
					e.printStackTrace();
				} 
			}
		}).start();
		
		int sent = 1, numsent = 1;
		long t1 = System.currentTimeMillis();
		while(getLastAck() < numpackets)
		{
			if(sent <= wSize && numsent <= numpackets)
			{
				timer[(sent-1) % wSize] = System.currentTimeMillis();
				out.write(numsent);
				System.out.println("Sent: "+(numsent++));
				sent++;
			}
			
			else if(getLastAck() >= (numsent - wSize))
			{
				sent = numsent - getLastAck();
			}
			
			else if((System.currentTimeMillis() - timer[0]) > timeOut)
			{
				numsent = getLastAck() + 1;
				sent = 1;
			}
			
			try {
			    Thread.sleep(1000);                 //1000 milliseconds is one second.
			} catch(InterruptedException ex) {
			    Thread.currentThread().interrupt();
			}
		}		
		
		System.out.println("Packets sent in "+(System.currentTimeMillis() - t1)/1000+"s");
	}
	
	public static void setLastAck(int lAck)
	{
		lastAck = lAck;
	}
	
	public static int getLastAck()
	{
		return lastAck;
	}
	
}
