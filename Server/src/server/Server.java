package server;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * The Server class got one thread that connects clients and threads that keep
 * the connection with all the different clients
 * 
 * @author Truls Haraldsson
 * @author Artur Olech
 * @author Saeed Ghasemi
 * 
 */
public class Server extends Thread {

	private int port;
	private boolean runningConnectThread = true;

	/**
	 * Contructor for the class Server
	 * 
	 * @param port
	 *            where the server is listening for connections
	 * @throws IOException
	 */
	public Server(int port) {
		this.port = port;
	}

	/**
	 * This is the servers connect thread that runs at all time. The
	 * ServerSocket got an argument 'port' which specify the port the server
	 * should listen to. The accept method waits until a client connects and
	 * then finish.
	 */
	public void run() {
		ServerSocket serverSocket = null;
		Socket clientSocket;
		Thread clientThread;
		try {
			serverSocket = new ServerSocket(port);
			while (runningConnectThread) {
				clientSocket = serverSocket.accept();
				System.out.println("Starts ClientThread");
				clientThread = new ClientThread(clientSocket);
				clientThread.start();
				System.out.println("New connection accepted "
						+ clientSocket.getInetAddress() + ":"
						+ clientSocket.getPort());
				
				Thread.sleep(1000);
			}
		} catch (Exception e) {
			System.out.println("Exception in Server @ 55");
			e.printStackTrace();
		} finally {
			try {
				serverSocket.close();
			} catch (IOException e) {
				System.out.println("Exception: in Server @ 64");
				e.printStackTrace();
			}
		}
	}

	/**
	 * Starts the server application and creates a new instance of a server
	 * 
	 * @param args
	 */
	public static void main(String args[]) {
		int port = 25565;

		new Server(port).start();
		System.out.println("Created Server");
	}
}
