import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * The ClientThread class represent a single connection between the server and a client.
 * The class can receive String messages from clients
 * @author Truls Haraldsson
 * @author Artur Olech
 * @author Saeed Ghasemi
 */
public class ClientThread extends Thread{
	private Socket socket;
	private boolean running = false;
	private Controller controller;
	private String message;
	
	/**
	 * The constructor takes one parameter <tt> socket <tt> that got created when
	 * the serversocket invoked the method <tt>accept()<tt>
	 * @param socket
	 * @throws IOException
	 */
	public ClientThread(Socket socket) throws IOException {
		this.socket = socket;
		controller  = new Controller(this);
	}
	
	/**
	 * The method runs as long as the client is connected to the server.
	 * The method will end if the client sends a string equal to "Close Connection"
	 */
	public void run(){
		try {
			running = true;
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			while (running) {
				message = in.readLine();
				
				if (message != null) {
					controller.checkCommand(message);
				}
				
				System.out.println(this.getName());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void closeConnecton(){
		try {
			running = false;
			socket.close();
		} catch (IOException e) {
			System.out.println(e);
		}
	}
}
