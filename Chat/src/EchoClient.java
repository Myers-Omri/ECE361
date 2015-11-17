import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class EchoClient {
	//Define user name
	final static String name = "Davi";
	
	//Define server port
	final static int port = 8080;

	public static void main(String[] args) throws UnknownHostException, IOException {
		
		//Open socket to server
		Socket svSocket = new Socket("127.0.0.1", port);
		
		//Create IO buffers
		PrintWriter out = new PrintWriter(svSocket.getOutputStream(), true);
		BufferedReader in = new BufferedReader(new InputStreamReader(svSocket.getInputStream()));
		
		Thread writerThread = new Thread(new WriterThread(name,out));
		Thread readerThread = new Thread(new ReceiverThread(in));
		
		writerThread.start();
		readerThread.start();
		
		try {
			writerThread.join();
			readerThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		//Close buffers
		in.close();
		out.close();
		//Close sockets
		svSocket.close();
		System.out.println("Resources freed");

	}

}
