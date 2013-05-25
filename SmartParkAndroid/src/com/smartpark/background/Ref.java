package com.smartpark.background;

import android.app.Activity;

public class Ref {

	// :::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
	/*
	 * A COLLECTION OF REFERENCES THAT MOST OF THE CLASSES NEED TO OPERATE. THIS
	 * IS INSPIERED BY THE ANDROID R.CLASS THAT HOUSES ALL REFERENCES FOR THE
	 * COMPONENTS ON THE DIFFERENT LAYOUTS. THE ALTERNATIVE WAS TO ALWAYS PASS
	 * REFERENCES TO OTHER CLASSES.
	 */
	// :::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
	
	// Global control-variable
	public static boolean D = false;

	/*
	 * keeps a list of booleans to determine if all activities have been
	 * destroyed/paused so that the thread wont continue running for ever.
	 */
	// GLOBAL APPLICATION STATE FLAGS / TODO put these into service
	public static boolean flagMainActivityInFront = false;
	public static boolean flagSettingsActivityInFront = false;
	public static boolean flagLoginActivityInFront = false;

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

	// Reference to the background thread (needed for sending data)
	public static BackgroundOperationThread bgThread;

	// === Runtime references===============================================
	/*
	 * This will hold a reference to the applicationContext. This is the main
	 * context the application is running in. This gives great flexibility in
	 * our program. However, with great power come great responsibility, since
	 * everything invoked with this will not automatically be removed and will
	 * lead to leaking if not manually removed. This is the task of the
	 * BackOperationService. In our design the BackOperationService takes care
	 * of the entire application lifecycle and is the last to exit
	 */
	// public static Context applicationContext;

	/*
	 * The application context can't invoke all needed method, in those cases we
	 * use a reference for an Activity class
	 */
	// Reference to the currently active activity
	public static Activity activeActivity;

	// /////////////////////////////////////////////////////////////////////

}
