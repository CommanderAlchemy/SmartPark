package com.smartpark.tcp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

import android.util.Log;

import com.smartpark.Settings;
import com.smartpark.activities.MainActivity;

/**
 * TCP Client, this class holds the application layer communication protocol for
 * communications by TCP.
 * 
 * @author Saeed, Truls, Artur
 * 
 */
public class TCPController {

	// CONNECTION STATE INTEGERS / TODO put his into BT and TCP
	public final static int STATE_NOT_CONNECTED = -1;
	public final static int STATE_DISCONNECTING = 0;
	public final static int STATE_CONNECTING = 1;
	public final static int STATE_CONNECTED = 2;
	
	// SOME RESPONSES / TODO move
	public final static int RESULT_OK = 0;
	public final static int RESULT_IO_EXCEPTION = -1;
	public final static int RESULT_UNKNOWN_HOST_EXCEPTION = -2;
	public final static int RESULT_EXCEPTION = -3;
	
	// Debug
	private static final String TAG = "TCPController";
	private static boolean D = MainActivity.D;

	// CONNECTION STATE-FLAG
	private static int connectionState = STATE_NOT_CONNECTED;

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
//		// Instantiating listener
//		mMessageListener = new OnMessageReceivedListener() {
//			// messageReceived method is implemented. It is a listener
//			// for incoming messages from the TCP connection.
//			@Override
//			public void messageReceived(String message) {
//				Log.i(TAG, "++ messageReceived ++ TCP: " + message);
//				// this method calls the onProgressUpdate
//				// publishProgress(message);
//			}
//		};
	}// ==================================================================

	public void connectTCP() {
		new Thread() {
			public void run() {
				Log.e(TAG, "++ connectTCP ++");
				if (connect()) {
					Log.e(TAG,
							"--> connected to: " + tcpSocket.getInetAddress());

				} else {
					if (tcpSocket == null) {
						Log.e(TAG, "--> did not connected to: ");
					}
				}
			}
		}.start();
	}

	// ===================================================================
	private boolean connect() {
		if (D)
			Log.e(TAG, "++ connect ++");
		try {
			InetAddress serverAddr = InetAddress.getByName(Settings.Server_IP);
			// create a socket to make the connection with the server
			try {
				tcpSocket = new Socket(serverAddr, Settings.Server_Port);
				setConnected();
				tcpSocket.setKeepAlive(true);
				Log.e(TAG, "TCP Connected");
				setConnected();
				mBufferOut = new PrintWriter(new BufferedWriter(
						new OutputStreamWriter(tcpSocket.getOutputStream())),
						true);
				mBufferIn = new BufferedReader(new InputStreamReader(
						tcpSocket.getInputStream()));
			} catch (Exception e) {
				setDisconnected();
				Log.e(TAG, "Connection failed !" + serverAddr.toString());
			}

		} catch (UnknownHostException e) {
			Log.e(TAG, "UnknownHostException: ", e);
			setDisconnected();
		} catch (Exception e) {
			Log.e(TAG, "Exception: ", e);
		}
		if (tcpSocket != null) {
			if (tcpSocket.isConnected()) {
				setConnected();
			}
		} else {
			setDisconnected();
		}
		return isConnected();
	}

	// ===================================================================

	/**
	 * Sends the message entered by client to the server
	 * 
	 * @param message
	 *            text entered by client
	 */
	public void sendMessage(String message) {
		if (tcpSocket != null && tcpSocket.isConnected()) {
			if (mBufferOut != null) {
				mBufferOut.println(message);
				mBufferOut.flush();
			}
		}
	}

	// ===================================================================

	/**
	 * Close the connection and release the members
	 */
	public void disconnect() {
		Log.e(TAG, "++ disconnect ++");
		// send message that we are closing the connection
		sendMessage(Settings.Close_Connection);
		if (mBufferOut != null) {
			mBufferOut.flush();
			mBufferOut.close();
			try {
				mBufferIn.close();
				tcpSocket.close();
			} catch (IOException e) {
				Log.e(TAG, "Exception trying to close mBufferIn and tcpSocket",
						e);
			}
		}
	}

	// ===========================================================================

	// CONNECTION STATE SETTERS AND GETTERS

	public boolean isConnected() {
		if (tcpSocket != null) {
			if (tcpSocket.isConnected()) {
				setConnected();
				return true;
			} else {
				setDisconnected();
				return false;
			}
		}
		return false;
	}

	public boolean isConnecting() {
		return connectionState == STATE_CONNECTING;
	}

	public void setConnecting() {
		connectionState = STATE_CONNECTING;
	}

	public void setConnected() {
		connectionState = STATE_CONNECTED;
	}

	public void setDisconnected() {
		Log.e(TAG, "Set Disconnected !");
		connectionState = STATE_NOT_CONNECTED;
	}

	public int closeConnection() {
		if (D)
			Log.e(TAG, "++ closeConnection ++");
		try {
			/*
			 * The use of && prohibits isConnected() to be invoked if btSocket
			 * is null.
			 */
			if (tcpSocket != null) {
				tcpSocket.close();
			}
			return RESULT_OK;
		} catch (Exception e) {
			if (D)
				Log.e(TAG, "Error closing btSocket: ", e);
			return RESULT_IO_EXCEPTION;
		}
	}

	// ===========================================================================

	public String receiveString() {
		if (D)
			Log.e(TAG, "++ receiveString ++");
		if (tcpSocket != null && tcpSocket.isConnected()) {
			if (D)
				Log.d(TAG, "iStream good");
			String inData = null;
			try {
				if (mBufferIn.ready()) {
					if (D)
						Log.d(TAG, "reader ready");
					inData = mBufferIn.readLine();
					if (D)
						Log.d(TAG, "DATA= " + inData);
					return inData;
				}
			} catch (Exception e1) {
				setDisconnected();
				if (D)
					Log.e(TAG, "btSocket not connected");
			}
		} else {
			setDisconnected();
		}
		return null;
	}

	public boolean testTCPConnection() {
		Log.e(TAG, "++ testConnection ++");
		byte[] echo = { 'e', 'c', 'h', 'o', ';', 'e', 'c', 'h', 'o', '\n' };
		try {
			if (tcpSocket != null && tcpSocket.isConnected()) {
				tcpSocket.getOutputStream().write(echo);
				setConnected();
				return true;
			} else {
				setDisconnected();
				return false;
			}
		} catch (SocketException e) {
			setDisconnected();
			e.printStackTrace();
			return false;
		} catch (Exception e) {
			setDisconnected();
			e.printStackTrace();
			return false;
		}

	}

	// ===================================
}