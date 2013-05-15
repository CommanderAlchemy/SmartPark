package com.smartpark.background;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;

import com.smartpark.bluetooth.BlueController;
import com.smartpark.tcp.TCPController;

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
	
	// RESPONSES
	public final static int RESULT_OK = 0;
	public final static int RESULT_IO_EXCEPTION = -1;
	public final static int RESULT_UNKNOWN_HOST_EXCEPTION = -2;
	
	// Global control-flags
	public static int tcpState = STATE_NOT_CONNECTED;
	public static int btState = STATE_NOT_CONNECTED;
	
	// RequestCodes for controlling the bluetooth
	public static final int REQUEST_ENABLE_BT = 1;
	public static final int REQUEST_DISCOVERABLE_BT = 2;
	
	// Reference to the currently active activity
	public static Activity activeActivity;
	
	// Global control-variable
	public static boolean D = true;
	
	// Objects used for Internet communication
	public static TCPController tcpClient;
	
	// Reference to the background thread
	public static BackgroundOperationThread bgThread;
	public static boolean isBotRunning = false;
	
	// Objects for use with the bluetooth adapter
	public static BluetoothAdapter btAdapter;
	public static BlueController btController;
	
	// ==================================================
	
	// getters and setters for Internet state
	public static boolean tcpIsConnected() {
		return tcpState == STATE_CONNECTED;
	}
	public static void setTcpState(int state) {
		tcpState = state;
	}
	public static int getTcpState() {
		return tcpState;
	}
	
	// getters and setters for bluetooth state
	public static boolean btIsConnected() {
		return btState == STATE_CONNECTED;
	}
	public static void setbtState(int state) {
		btState = state;
	}
	public static int getbtState() {
		return btState;
	}
}
