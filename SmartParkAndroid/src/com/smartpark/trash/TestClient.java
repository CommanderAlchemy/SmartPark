package com.smartpark.trash;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;

/**
 * The class represents a client that can connect to a server.
 * The client can send a message to the server.
 * @author Truls Haraldsson
 * @author Artur Olech
 * @author Saeed Ghasemi 
 *
 */
public class TestClient{
	
	/**
	 * The method initiate a connection to a server and sends two messages
	 * @param serverIP
	 * @param serverPort
	 */
	public static void receive( String serverIP, int serverPort ) {
        Socket socket = null;
        boolean sending = true;
        try {
        	
            InetAddress adress = InetAddress.getByName( serverIP );
            socket = new Socket( adress , serverPort ); 	// koppla upp
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            while(sending) {
//            	System.out.println("Sending");
            	out.write("Login;Mario:1337");
            	out.newLine();
            	out.flush();
            	try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
//            	out.println("Login;UserName:Password");
            	out.write("Close Connection;0:0");
            	out.newLine();
//            	out.flush();
            	out.close();
            }
            
        } catch(IOException e) {
            System.out.println( e );
        }   
        finally{
	        try {
	            socket.close(); 		// avsluta Socket-objektet
	        } catch( Exception e ) {
	            System.out.println( e );
	        }
        }
    }
    
	/**
	 * Invokes the class-method <tt>receive()<tt> from the class TestClient 
	 * that connects to a server with the IP "195.178.233.49" to port 4444.
	 * @param args
	 */
    public static void main( String[] args ) {
        TestClient.receive( "195.178.233.53", 4444 );
    }
}
