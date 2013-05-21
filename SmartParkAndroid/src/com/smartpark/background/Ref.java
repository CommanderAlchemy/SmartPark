package com.smartpark.background;

import android.app.Activity;
import android.widget.TextView;

import com.smartpark.tcp.TCPController;

public class Ref {

	/*
	 * A COLLECTION OF REFERENCES THAT MOST OF THE CLASSES NEED TO OPERATE. THIS
	 * IS INSPIERED BY THE ANDROID R.CLASS THAT HOUSES ALL REFERENCES FOR THE
	 * COMPONENTS ON THE DIFFERENT LAYOUTS. THE ALTERNATIVE WAS TO ALWAYS PASS
	 * REFERENCES TO OTHER CLASSES.
	 */

	// Global control-variable
	public static boolean D = true;

	/*
	 * keeps a list of booleans to determine if all activities have been
	 * destroyed/paused so that the thread wont continue running for ever.
	 */
	// GLOBAL APPLICATION STATE FLAGS
	public static boolean flagMainActivityInFront = false;
	public static boolean flagSettingsActivityInFront = false;
	public static boolean flagLoginActivityInFront = false;

	/*
	 * This will hold a reference to the applicationContext. This is the main
	 * context the application is running in. This gives great flexibility in
	 * our program. However, with great power come great responsibility, since
	 * everything invoked with this will not automatically be removed and will
	 * lead to leaking if not manually removed. This is the task of the
	 * BackOperationService.
	 */
	// public static Context applicationContext;

	/*
	 * The application context can't invoke all needed method, in those cases we
	 * use a reference for an Activity class
	 */
	// Reference to the currently active activity
	public static Activity activeActivity;

	// CONNECTION STATE INTEGERS
	public final static int STATE_NOT_CONNECTED = -1;
	public final static int STATE_DISCONNECTING = 0;
	public final static int STATE_CONNECTING = 1;
	public final static int STATE_CONNECTED = 2;

	// SOME RESPONSES
	public final static int RESULT_OK = 0;
	public final static int RESULT_IO_EXCEPTION = -1;
	public final static int RESULT_UNKNOWN_HOST_EXCEPTION = -2;
	public final static int RESULT_EXCEPTION = -3;

	// TODO move to the classes
	// Global control-flags
	public static int flagTcpState = STATE_NOT_CONNECTED;
	public static int flagBtState = STATE_NOT_CONNECTED;

	// TODO remove entirely
	// Objects used for Internet communication
	public static TCPController tcpClient;

	// Reference to the background thread (needed for sending data)
	public static BackgroundOperationThread bgThread;
	public static boolean isBackgroundOperationThreadRunning = false;

	// TODO remove entirely
	public static TextView gps_text;

	// ==================================================

	// TODO remove
	// getters and setters for Internet state
	public static boolean tcpIsConnected() {
		return flagTcpState == STATE_CONNECTED;
	}

	public static void setTcpState(int state) {
		flagTcpState = state;
	}

	public static int getTcpState() {
		return flagTcpState;
	}

	// TODO remove
	// getters and setters for bluetooth state
	public static boolean btIsConnected() {
		return flagBtState == STATE_CONNECTED;
	}

	public static void setbtState(int state) {
		flagBtState = state;
	}

	public static int getbtState() {
		return flagBtState;
	}
}
