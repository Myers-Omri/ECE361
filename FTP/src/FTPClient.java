import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class FTPClient {
	
	//Define server port
	final static int cport = 8081;
	final static int dport = 8080;

	public static void main(String[] args) throws UnknownHostException, IOException {
		
		//Open socket to server
		Socket svSocket = new Socket("127.0.0.1", cport);
		
		//Scan user input
		Scanner userIn = new Scanner(System.in);
		
		//Create IO buffers
		PrintWriter out = new PrintWriter(svSocket.getOutputStream(), true);
		BufferedReader in = new BufferedReader(new InputStreamReader(svSocket.getInputStream()));
		
		while(userIn.hasNext())
		{
			final String file = userIn.nextLine();
			out.println(file);
			switch (Integer.parseInt(in.readLine())) {
			case 150:
				System.out.println("File found. Receiving.");
				new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							receiveFile(file);
						} catch (Exception e) {
							e.printStackTrace();
						} 
					}
				}).start();
				break;

			case 450:
				System.out.println("File not found.");
				break;
			
			default:
				System.out.println("Unknown response");
				break;
			}
			
		}		

		//Close buffers
		in.close();
		out.close();
		//Close sockets
		svSocket.close();
		System.out.println("Resources freed");

	}
	
	public static int receiveFile(String filename) throws UnknownHostException, IOException
	{
		Socket svSocket = new Socket("127.0.0.1", dport);
		DataInputStream DataReader = new DataInputStream(svSocket.getInputStream());
		
		File tfile = new File(new File(".").getAbsoluteFile()+File.separator+filename);
		FileOutputStream fout = new FileOutputStream(tfile);
		
		String chunk;
		int ch;
		int count = 0;
		long t1 = System.nanoTime();
		do {
			chunk = DataReader.readUTF();
			ch = Integer.parseInt(chunk);
            if(ch != -1)
                fout.write(ch);
            //System.out.println("Chunk #"+(++count)+" received");
        } while (ch != -1);
		
		double tdif = (System.nanoTime() - t1)/Math.pow(10,9);
		double flen = tfile.length()/Math.pow(10,3);
		System.out.println("Received "+flen+"kB @ "+flen/tdif+"kB/s");
		fout.close();			
		svSocket.close();
		return 1;
	}

}
