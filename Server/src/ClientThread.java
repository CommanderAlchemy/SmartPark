import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;

/**
 * The ClientThread class represent a single connection between the server and a
 * client. The class can receive String messages from clients
 * 
 * @author Truls Haraldsson
 * @author Artur Olech
 * @author Saeed Ghasemi
 */
public class ClientThread extends Thread {
	private Socket socket;
	private Handler handler;
	
//	private static HashMap<String, Socket> reConnection = new HashMap<String, Socket>();
	
	private boolean running = false;

	private String message;
	private BufferedReader bufferIn;
	private PrintWriter bufferOut;

	/**
	 * The constructor takes one parameter
	 * <tt> socket <tt> that got created when
	 * the serversocket invoked the method <tt>accept()<tt>
	 * 
	 * @param socket
	 * @throws IOException
	 */
	public ClientThread(Socket socket) throws IOException {
		this.handler = new Handler(this);
		this.socket = socket;
		this.bufferIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		bufferOut = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
	}

	/**
	 * The method runs as long as the client is connected to the server. The
	 * method will end if the client sends a string equal to "Close Connection"
	 */
	public void run() {
		try {
			running = true;

			while (running) {
				if (socket.isConnected()) {
					if (bufferIn.ready()){
						System.out.println("Rec.. DATA");
						message = bufferIn.readLine();
						System.out.println(message);
					}
					
					if (message != null) {
						handler.checkCommand(message);
						message = null;
					}
					System.out.println(this.getName());
					
				} else
					closeConnecton();
				
			}
		} catch (Exception e) {
			System.out.println("Exception In ClientThread @ 72");
			e.printStackTrace();
		}
	}

	public void closeConnecton() {
		System.out.println("Closing Connection");
		try {
			running = false;
			socket.close();
		} catch (IOException e) {
			System.out.println(e);
		}
	}

	public void sendMessage(String message) {
		if (bufferOut != null && !bufferOut.checkError()) {
			bufferOut.println(message);
			bufferOut.flush();
		}
	}
}
