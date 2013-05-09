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
	public static String tcpState = "not connected"; 		// -1 not connected / 0 disconnecting / 1 - connecting / 2 - connected
	public static String btState = "not connected"; 		// -1 not connected / 0 disconnecting / 1 - connecting / 2 - connected
	
	
	
	
	
	
	// Changing tcpState
	public static void setTcpState(String tcpState) {
		Ref.tcpState = tcpState;
	}
	public static String getTcpState() {
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
