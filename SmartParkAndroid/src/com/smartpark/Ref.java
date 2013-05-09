package com.smartpark;

import java.io.InputStream;
import java.io.OutputStream;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import com.smartpark.bluetooth.BlueController;
import com.smartpark.tcp.TCPClient;

public class Ref {

	// ACOLLECTION OF REFERENCES THAT MOST OF THE CLASSES NEED TO OPERATE
	// THIS IS INSPIERED BY THE ANDROID R.CLASS THAT HOUSES ALL REFERENCES
	// FOR THE COMPONENTS ON THE DIFFERENT LAYOUTS.
	// THE ALTERNATIVE WAS TO ALWAYS PASS REFERENCES TO OTHER CLASSES.
	
	// CONNECTION STATE INTEGERS
	public final static int STATE_NOT_CONNECTED = -1;
	public final static int STATE_DISCONNECTING = 0;
	public final static int STATE_CONNECTING = 1;
	public final static int STATE_CONNECTED = 2;
	
	// Global control-variables
	public static boolean D = true;
	
	// Objects used for Internet communication
	public static TCPClient tcpClient;
	public static Thread clientThread;
	public static BackgoundOperationThread bgThread;
	
	// Objects for use with the bluetooth adapter
	public static BlueController btController;
	public static BluetoothSocket btSocket;
	public static BluetoothDevice btDevice;
	public static InputStream btInStream;
	public static OutputStream btOutStream;

	
	// Global control-flags
	public static int tcpState = STATE_NOT_CONNECTED; 		// -1 not connected / 0 disconnecting / 1 - connecting / 2 - connected
	public static int btState = STATE_NOT_CONNECTED; 			// -1 not connected / 0 disconnecting / 1 - connecting / 2 - connected
	
	
	
	
	
	
	// Changing tcpState
	public static void setTcpState(int tcpState) {
		Ref.tcpState = tcpState;
	}
	public static int getTcpState() {
		return tcpState;
	}
	
	// Changing btState
	public static void setbtState(int btState) {
		Ref.btState = tcpState;
	}
	public static int getbtState() {
		return btState;
	}
	
	
}
