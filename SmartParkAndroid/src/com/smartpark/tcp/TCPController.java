package com.smartpark.tcp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketAddress;
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
	private Socket tcpSocket;

	// ===================================================================
	/**
	 * Constructor of the class. OnMessagedReceived listens for the messages
	 * received from server
	 */
	public TCPController() {
		// This will take care of connection and update the tcpState in Ref
		connect(); // We are not error handling here

		// Instantiating listener
		mMessageListener = new OnMessageReceivedListener() {
			// messageReceived method is implemented. It is a listener
			// for incoming messages from the TCP connection.
			@Override
			public void messageReceived(String message) {
				Log.i(TAG, "++ messageReceived ++ TCP: " + message);
				// this method calls the onProgressUpdate
				// publishProgress(message);
			}
		};
	}// ==================================================================
	

	// ===================================================================
	public void connect() {
		new Thread(){
			public void run(){
				try {
					Ref.tcpState = Ref.STATE_CONNECTING;
					InetAddress serverAddr = InetAddress.getByName(Settings.Server_IP);
					// create a socket to make the connection with the server
					tcpSocket = new Socket(serverAddr, Settings.Server_Port);
					tcpSocket.setKeepAlive(true);
					if (tcpSocket.isConnected()){
						mBufferOut = new PrintWriter(new BufferedWriter(
								new OutputStreamWriter(tcpSocket.getOutputStream())),
								true);
						mBufferIn = new BufferedReader(new InputStreamReader(
								tcpSocket.getInputStream()));
						Ref.tcpState = Ref.STATE_CONNECTED;
					} else {
						Ref.tcpState = Ref.STATE_NOT_CONNECTED;
					}
				} catch (UnknownHostException e) {
					Log.e(TAG, "UnknownHostException: ", e);
					Ref.tcpState = Ref.STATE_NOT_CONNECTED;
				} catch (IOException e) {
					Log.e(TAG, "IOException: ", e);
					Ref.tcpState = Ref.STATE_NOT_CONNECTED;
				}
			}
		}.start();		
	}// ===================================================================
	// ===================================================================
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
	}// ===================================================================
	// ===================================================================
	/**
	 * Close the connection and release the members
	 */
	public void disconnect() {
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
				tcpSocket.close(); // should this be added
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}// ===================================================================
	// ===================================================================
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
				tcpSocket.close();
			}
		} catch (Exception e) {
			Log.e(TAG + " TCP", "C: Error", e);
		}
	}// ===========================================================================
}