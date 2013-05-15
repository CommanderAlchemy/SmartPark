package com.smartpark.tcp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import android.util.Log;

import com.smartpark.Settings;
import com.smartpark.background.Ref;
import com.smartpark.interfaces.OnMessageReceivedListener;

/**
 * TCP Client, this class holds the application layer communication protocol for
 * communications by TCP.
 * 
 * @author Saeed, Truls, Artur
 * 
 */
public class TCPController {

	// Debug
	private static final String TAG = "TCPClient";
	private static boolean D = Ref.D;

	// message to send to the server
	private String mServerMessage;

	// sends message received notifications
	private OnMessageReceivedListener mMessageListener = null;

	// while this is true, the server will continue running
	private boolean mRun = false;

	// used to send messages
	private PrintWriter mBufferOut;

	// used to read messages from the server
	private BufferedReader mBufferIn;

	// The connection socket
	private Socket socket;

	/**
	 * Constructor of the class. OnMessagedReceived listens for the messages
	 * received from server
	 */
	public TCPController() {
		try {
			InetAddress serverAddr = InetAddress.getByName(Settings.Server_IP);
			// create a socket to make the connection with the server
			socket = new Socket(serverAddr, Settings.Server_Port);
			// sends the message to the server
			mBufferOut = new PrintWriter(new BufferedWriter(
					new OutputStreamWriter(socket.getOutputStream())), true);

			// receives the message which the server sends back
			mBufferIn = new BufferedReader(new InputStreamReader(
					socket.getInputStream()));
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			Log.e(TAG, "UnknownHostException: ", e);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.e(TAG, "IOException: ", e);
			e.printStackTrace();
		}
		// We now have a 

		mMessageListener = new OnMessageReceivedListener() {
			// here the messageReceived method is implemented
			@Override
			public void messageReceived(String message) {
				Log.i(TAG, "++ messageReceived ++ TCP: " + message);
				// this method calls the onProgressUpdate
				// publishProgress(message);
			}
		};
	}// ==================================================================

	public int connect(){
		try {
			InetAddress serverAddr = InetAddress.getByName(Settings.Server_IP);
			// create a socket to make the connection with the server
			socket = new Socket(serverAddr, Settings.Server_Port);
			// sends the message to the server
			mBufferOut = new PrintWriter(new BufferedWriter(
					new OutputStreamWriter(socket.getOutputStream())), true);

			// receives the message which the server sends back
			mBufferIn = new BufferedReader(new InputStreamReader(
					socket.getInputStream()));
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			Log.e(TAG, "UnknownHostException: ", e);
			return Ref.RESULT_UNKNOWN_HOST_EXCEPTION;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.e(TAG, "IOException: ", e);
			return Ref.RESULT_IO_EXCEPTION;
		}
		return 0;
	}
	
	/**
	 * Sends the message entered by client to the server
	 * 
	 * @param message
	 *            text entered by client
	 */
	public void sendMessage(String message) {

		// you might want to remove !mBufferOut.checkError()
		// if error occurs, messages will never be send TODO
		if (mBufferOut != null && !mBufferOut.checkError()) {
			mBufferOut.println(message);
			mBufferOut.flush();
		}
	}
	
	/**
	 * Close the connection and release the members
	 */
	public void stopClient() {
		if (D)
			Log.d(TAG, "Closing Connection");
		// send message that we are closing the connection
		sendMessage(Settings.Close_Connection);
		mRun = false;
		if (mBufferOut != null) {
			mBufferOut.flush();
			mBufferOut.close();
			try {
				mBufferIn.close(); // should this be added TODO
				socket.close(); // should this be added
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	public void run() {
		mRun = true;
		try {
			if (D)
				Log.d(TAG + " TCP Client", "C: Connecting...");

			try {

				// send login name and password
				sendMessage(Settings.Login_Name + "Commander" + " ; "
						+ Settings.Password + "Password");

				// in this while the client listens for the messages sent by the
				// server
				while (mRun) {
					Log.d(TAG, "before readLine");
					if (mBufferIn.ready()) {
						mServerMessage = mBufferIn.readLine();
					}
					Log.d(TAG, "readLine done: " + mServerMessage);
					if (mServerMessage != null && mMessageListener != null) {
						// call the method messageReceived from MyActivity class
						mMessageListener.messageReceived(mServerMessage);
					}
				}

				Log.d(TAG + "RESPONSE FROM SERVER", "S: Received Message: '"
						+ mServerMessage + "'");

			} catch (Exception e) {
				Log.e(TAG, "Error", e);
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