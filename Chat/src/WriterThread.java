import java.io.PrintWriter;
import java.util.Scanner;

public class WriterThread implements Runnable {

	PrintWriter out;
	String user;
	
	Scanner userInput = new Scanner(System.in);
	
	public WriterThread(String username, PrintWriter output) {
		out = output;
		user = username;
	}

	@Override
	public void run() {
		while(userInput.hasNextLine())
			out.println(userInput.nextLine());
			
	}

}
