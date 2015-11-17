import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class EchoServer {
	//Define user name
	final static String name = "Server";

	//Define socket port
	final static int port = 8080;
	
	public static void main(String[] args) throws IOException {
		
		ServerSocket serverSocket = new ServerSocket(port);
		System.out.println("Socket opened in port "+port);
		
		Socket clSocket = serverSocket.accept();
		System.out.println("Client connected");
		
		PrintWriter out = new PrintWriter(clSocket.getOutputStream(), true);
		BufferedReader in = new BufferedReader(new InputStreamReader(clSocket.getInputStream()));
		
		Thread writerThread = new Thread(new WriterThread(name, out));
		Thread readerThread = new Thread(new ReceiverThread(in));
		
		writerThread.start();	
		readerThread.start();	
		
		try {
			writerThread.join();
			readerThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		out.close();
		in.close();
		serverSocket.close();
		System.out.println("Resources freed");

	}

}
