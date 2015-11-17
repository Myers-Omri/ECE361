import java.io.BufferedReader;
import java.io.IOException;

public class ReceiverThread implements Runnable {

	BufferedReader in;
	
	public ReceiverThread(BufferedReader input) {
		in = input;
	}

	@Override
	public void run() {
		String msg;
		try {
			while((msg = in.readLine()) != null)
				System.out.println(msg);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
