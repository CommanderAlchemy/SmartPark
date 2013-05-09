package com.smartpark;

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
	public final static int NOT_CONNECTED = -1;
	public final static int DISCONNECTING = 0;
	public final static int CONNECTING = 1;
	public final static int CONNECTED = 2;
	
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
	
	// Global control-flags
	public static int tcpState = NOT_CONNECTED; 		// -1 not connected / 0 disconnecting / 1 - connecting / 2 - connected
	public static int btState = NOT_CONNECTED; 			// -1 not connected / 0 disconnecting / 1 - connecting / 2 - connected
	
	
	
	
	
	
	// Changing tcpState
	public static void setTcpState(int tcpState) {
		Ref.tcpState = tcpState;
	}
	public static int getTcpState() {
		return tcpState;
	}
	
	// Changing btState
	public static void setbtState(String btState) {
		Ref.btState = tcpState;
	}
	public static String getbtState() {
		return btState;
	}
	
	
}
