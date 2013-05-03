package com.smartpark.tcp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

import com.smartpark.interfaces.OnMessageReceived;

import android.util.Log;

/**
 * TCP Client, this class holds the implementation of kommunication protocol for
 * the smartpark application.
 * 
 * @author commander
 * 
 */
public class TCPClient implements Runnable {

	// Debug
	private static final String TAG = "SmartPark";

	// message to send to the server
	private String mServerMessage;

	// sends message received notifications
	private OnMessageReceived mMessageListener = null;

	// while this is true, the server will continue running
	private boolean mRun = false;

	// used to send messages
	private PrintWriter mBufferOut;

	// used to read messages from the server
	private BufferedReader mBufferIn;
	
	Thread thread;
	

	/**
	 * Constructor of the class. OnMessagedReceived listens for the messages
	 * received from server
	 */
	public TCPClient(OnMessageReceived listener) {
		mMessageListener = listener;
	}// ===========================================================================

	public void setThread(Thread thread){
		
	}
	
	
	/**
	 * Sends the message entered by client to the server
	 * 
	 * @param message
	 *            text entered by client
	 */
	public void sendMessage(String message) {
		if (mBufferOut != null && !mBufferOut.checkError()) {
			mBufferOut.println(message);
			mBufferOut.flush();
		}
	}// ===========================================================================

	/**
	 * Close the connection and release the members
	 */
	public void stopClient() {
		Log.e(TAG, "Closing Connection");

		// send mesage that we are closing the connection
		sendMessage(Settings.Close_Connection);

		mRun = false;

		if (mBufferOut != null) {
			mBufferOut.flush();
			mBufferOut.close();
		}

	}// ===========================================================================

	public void run() {
		mRun = true;

		try {
			Log.e(TAG + " TCP Client", "C: Connecting...");

			InetAddress serverAddr = InetAddress.getByName(Settings.Server_IP);

			// create a socket to make the connection with the server
			Socket socket = new Socket(serverAddr, Settings.Server_Port);

			try {

				// sends the message to the server
				mBufferOut = new PrintWriter(new BufferedWriter(
						new OutputStreamWriter(socket.getOutputStream())), true);

				// receives the message which the server sends back
				mBufferIn = new BufferedReader(new InputStreamReader(
						socket.getInputStream()));

				// send login name and password
				sendMessage(Settings.Login_Name + "Commander" + " ; "
						+ Settings.Password + "Password");

				// in this while the client listens for the messages sent by the
				// server
				while (mRun) {
					System.out.println("before readLine");
					mServerMessage = mBufferIn.readLine();
					System.out.println("readLine done");
					if (mServerMessage != null && mMessageListener != null) {
						// call the method messageReceived from MyActivity class
						mMessageListener.messageReceived(mServerMessage);
					}
					
				}

				Log.e(TAG + " RESPONSE FROM SERVER", "S: Received Message: '"
						+ mServerMessage + "'");

			} catch (Exception e) {
				Log.e(TAG + " TCP", "S: Error", e);
			} finally {
				// the socket must be closed. It is not possible to reconnect to
				// this socket
				// after it is closed, which means a new socket instance has to
				// be created.
				socket.close();
			}
		} catch (Exception e) {
			Log.e(TAG + " TCP", "C: Error", e);
		}

	}// ===========================================================================

}