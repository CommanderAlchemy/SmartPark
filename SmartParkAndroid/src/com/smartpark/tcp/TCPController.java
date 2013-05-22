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

import android.bluetooth.BluetoothAdapter;
import android.util.Log;
import android.widget.Toast;

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
	private static final String TAG = "TCPController";
	private static boolean D = Ref.D;

	// CONNECTION STATE-FLAG
	private static int connectionState = Ref.STATE_NOT_CONNECTED;

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

	public void connectTCP() {
		new Thread() {
			public void run() {

				Log.e(TAG, "++ reconnectTCP ++");

				setConnecting();

				if (tcpSocket != null) {
					Log.e(TAG, "--> Connecting to: " + tcpSocket.toString());
					// This connect will start a new thread.
					if (connect()) {
						Log.e(TAG,
								"--> connected to: "
										+ tcpSocket.getInetAddress());
					} else {
						Log.e(TAG,
								"--> did not connected to: "
										+ tcpSocket.getInetAddress());
					}
				} else {
					Log.w(TAG, "--> tcpSocket == null. NOT Connected");
				}
			}
		}.start();
	}

	// ===================================================================
	private boolean connect() {
		if (D)
			Log.e(TAG, "++ connect ++");
		boolean isConnected = false;
		try {
			setConnecting();
			InetAddress serverAddr = InetAddress.getByName(Settings.Server_IP);
			// create a socket to make the connection with the server
			tcpSocket = new Socket(serverAddr, Settings.Server_Port);
			tcpSocket.setKeepAlive(true);
			if (tcpSocket.isConnected()) {
				isConnected = true;
				setConnected();
				mBufferOut = new PrintWriter(new BufferedWriter(
						new OutputStreamWriter(tcpSocket.getOutputStream())),
						true);
				mBufferIn = new BufferedReader(new InputStreamReader(
						tcpSocket.getInputStream()));

			} else {
				setDisconnected();
			}
		} catch (UnknownHostException e) {
			Log.e(TAG, "UnknownHostException: ", e);
			setDisconnected();
		} catch (IOException e) {
			Log.e(TAG, "IOException: ", e);
			setDisconnected();
		}
		return isConnected;
	}

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
		// && !mBufferOut.checkError()
		if (tcpSocket.isConnected()) {
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
		if (D)
			Log.d(TAG, "Closing Connection");
		// send message that we are closing the connection
		sendMessage(Settings.Close_Connection);
		mRun = false;
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
	}

	// ===========================================================================

	// CONNECTION STATE SETTERS AND GETTERS

	public boolean isConnected() {
		if (tcpSocket != null) {
			if (tcpSocket.isConnected()) {
				connectionState = Ref.STATE_CONNECTED;
				return true;
			} else {
				connectionState = Ref.STATE_NOT_CONNECTED;
				return false;
			}
		}
		return false;
	}

	public boolean isConnecting() {
		return connectionState == Ref.STATE_CONNECTING;
	}

	public void setConnecting() {
		connectionState = Ref.STATE_CONNECTING;
	}

	public void setConnected() {
		connectionState = Ref.STATE_CONNECTED;
	}

	public void setDisconnected() {
		connectionState = Ref.STATE_NOT_CONNECTED;
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
			return Ref.RESULT_OK;
		} catch (Exception e) {
			if (D)
				Log.e(TAG, "Error closing btSocket: ", e);
			return Ref.RESULT_IO_EXCEPTION;
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

	// ===================================
}