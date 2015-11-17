import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;



public class CCClient {

	static String host;
	static int port;
	final static String CRLF="\r\n";
	public static int wstart;
	static long totalTime;
	public static AtomicInteger lastAck;
	static int sent = 1;
	static long[] send_timer;
	
	static long startTime;
	static long endTime;
	public static int EstimatedRTT;
	public static int DevRTT;
	public static int SampleRTT;
	public static long timeOut;
	public static final double alpha=0.125;
	public static final double beta=0.25;
	
	public static long timer;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {

		host="localhost";
		port=9876;
		try 
		{
			Socket socket= new Socket(host, port);
			socket.setTcpNoDelay(true); 
			System.out.println("Connected to : "+ host+ ":"+socket.getPort());

			//reader and writer:
			final DataInputStream reader= new DataInputStream(socket.getInputStream());
			final DataOutputStream writer= new DataOutputStream(socket.getOutputStream());
			Scanner scr = new Scanner(System.in);

			//define the thread and start it
			Thread thread = new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						int ack;
						while((ack = reader.read()) != -1)
							CCClient.update(ack);
					} catch (Exception e) {
					} 
				}
			});
			
			thread.start();			 

			System.out.println("Enter number of packets to be sent to the server [0-127], 0 to Quit: ");
			int noPackets = scr.nextInt();
			
			writer.write(noPackets);
			
			
			lastAck= new AtomicInteger(0);
			sent=0;
			int cwnd=1;
			int ssthresh=16;
			timer = -1;
			EstimatedRTT = 1200;
			DevRTT = 0;
			timeOut = EstimatedRTT;
			startTime=System.currentTimeMillis();
			try {
				while(getLastAck()<noPackets)
					//update time out
				{
					if(getLastAck() == sent)
					{			
						if(timer != -1)
						{
							long RTT = System.currentTimeMillis()-timer;
							SetEstimatedRTT(RTT);	
						}
						for(int i=0; i<cwnd; i++)
						{
							writer.write(++sent);
							if(i == 0)
								timer = System.currentTimeMillis();
							System.out.println("Sent["+(i+1)+"]: "+sent);							
						}
						cwnd = (cwnd < ssthresh) ? cwnd * 2 : cwnd + 1;
						cwnd = Math.min(cwnd, noPackets-sent);
						System.out.println();						
					}
					else if((System.currentTimeMillis() - timer) > timeOut)
					{
						System.out.println("TIMEOUT");
						ssthresh = cwnd / 2;
						cwnd = 1;
						sent = getLastAck();
						timer = -1;
					}
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			finally {
				endTime = System.currentTimeMillis();
				totalTime = endTime - startTime;
			}
			
			System.out.println("Packets sent successfuly: "+getLastAck());
			System.out.println("Elapsed time:" + totalTime + "ms");
			System.out.println("Average RTT:" + EstimatedRTT + "ms");
			System.out.println("Number of RTTs: " + totalTime/(float)EstimatedRTT);
			System.out.println("Average Rate: "+noPackets*8/(float)totalTime*1000+"bps");
			
			writer.flush();
			socket.close();
			System.out.println("Quitting...");
			socket.close();
			scr.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public static void update(int ackNum)
	{
		if(lastAck.get() < ackNum)
			lastAck.set(ackNum);		
	}
	
	public static int getLastAck()
	{
		return lastAck.get();
	}
	
	public static int getEstimatedRTT()
	{
		return EstimatedRTT;
	}
	
	public static void SetEstimatedRTT(long sampleRTT)
	{
		DevRTT = (int) ((1-beta)*DevRTT + beta*Math.abs(sampleRTT-EstimatedRTT));
		EstimatedRTT = (int) ((1-alpha)*EstimatedRTT + alpha*sampleRTT);
		timeOut = EstimatedRTT + 4*DevRTT;
		System.out.println("Timeout updated to: "+timeOut);
	}


}
