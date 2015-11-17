import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class FTPServer {

	//Define socket port
	final static int cport = 8081;
	final static int dport = 8080;
	
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
			File f = new File(msg);
			if(!f.exists())
			{
				out.println(450);
			}
			else
			{
				out.println(150);
				final String filename = msg;
				
				new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							sendFile(filename);
						} catch (Exception e) {
							e.printStackTrace();
						} 
					}
				}).start();
			}
		}
			
	
		out.close();
		in.close();
		serverSocket.close();
		System.out.println("Resources freed");

	}
	
	public static int sendFile(String filename) throws UnknownHostException, IOException
	{
		ServerSocket svSocket = new ServerSocket(dport);
		Socket clSocket = svSocket.accept();
		
		DataOutputStream DataWriter = new DataOutputStream(clSocket.getOutputStream());
		
		File tfile = new File(new File(".").getAbsoluteFile()+File.separator+filename);
		
		FileInputStream fin = new FileInputStream(tfile);
		
		String chunk;
		int ch;
		int count = 0;
		long t1 = System.nanoTime();
		do {
			ch = fin.read();
			chunk = String.valueOf(ch);
			DataWriter.writeUTF(chunk);
			//System.out.println("Chunk #"+(++count)+" sent");
        } while (ch != -1);
		
		double tdif = (System.nanoTime() - t1)/Math.pow(10,9);
		double flen = tfile.length()/Math.pow(10,3);
		System.out.println("Sent "+flen+"kB @ "+flen/tdif+"kB/s");
		fin.close();
		clSocket.close();
		svSocket.close();
		return 1;
	}

}
