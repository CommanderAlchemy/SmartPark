package com.smartpark.background;

import java.util.Calendar;
import java.util.LinkedList;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.location.Location;
import android.util.Log;
import android.widget.Toast;

import com.smartpark.activities.MainActivity;
import com.smartpark.bluetooth.BlueController;
import com.smartpark.gps.GPSReceiver;
import com.smartpark.tcp.TCPController;

public class BackgroundOperationThread extends Thread {

	// TRANSMITBUFFERS
	private static LinkedList<String> btTransmitBuffer;
	private static LinkedList<String> tcpTransmitBuffer;

	// Debugging and stuff
	private static final String TAG = "bgThread";
	private static final boolean D = MainActivity.D;

	// USED WHEN INITIATING SOFT SHUTDOWN (RECOMMENDED ON THE INTERNET)
	private boolean keepRunning = true;

	// CONTROL FLAGS
	private boolean userIsAlreadyAsked = false;

	// REFERENCE TO APLLICATIONCONTEXT
	private Context applicationContext;

	private BackOperationService backOperationService;

	// REFERENCES TO CONTROL-CLASSES
	private BlueController btController;
	private TCPController tcpController;
	private Handler handler;

	// SharedPreferences for login settings
	private SharedPreferences mainPreference;

	// The state of execution
	private boolean amIRunning = false;
	private boolean isLoggedIn;

	// =========== END OF CLASS VARIABLES ===============================

	public BackgroundOperationThread(Context applicationContext,
			BackOperationService backOperationService,
			BlueController btController, TCPController tcpController,
			Handler handler, SharedPreferences mainPreference) {
		if (D)
			Log.e(TAG, "++ bgThread Constructor ++");

		btTransmitBuffer = new LinkedList<String>();
		tcpTransmitBuffer = new LinkedList<String>();

		this.applicationContext = applicationContext;
		this.backOperationService = backOperationService;

		// BT
		this.btController = btController;

		// TCP
		this.tcpController = tcpController;

		this.handler = handler;

		this.mainPreference = mainPreference;

		// Check to see if bluetooth is available
		if (!BlueController.isBluetoothAdapterAvailable()) {
			AlertDialog.Builder builder1 = new AlertDialog.Builder(
					applicationContext);
			builder1.setTitle("Problem");
			builder1.setMessage("Your phone does not seem to have Bluetooth. This is needed to conenct with the SP-device!");
			builder1.setCancelable(false);
			builder1.setPositiveButton(android.R.string.ok,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
						}
					});
			builder1.setNegativeButton("Exit",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							Ref.activeActivity.finish();
						}
					});
			AlertDialog alert = builder1.create();
			alert.show();
		} else {
			Toast.makeText(applicationContext, "Bluetooth avaiable",
					Toast.LENGTH_SHORT).show();
		}
	}

	// ==================================================================

	public boolean isLoggedIn() {
		return isLoggedIn;
	}

	// ==================================================================

	public void powerDown() {
		// When this flag gets set, the thread is told to shut it self down
		keepRunning = false;
	}// ==================================================================

	private void fixConnections() {
		if (D)
			Log.e(TAG, "++ fixConnections ++");

		// Fix Bluetooth Connection ===================================

		if (!(BlueController.isConnected() || BlueController.isConnecting())) {
			if (D)
				Log.e(TAG, "Fixing BT connection");
			BlueController.setConnecting();

			// Enable bluetooth if disabled by asking the user first (only once)
			if (!userIsAlreadyAsked && !BlueController.isEnabled()) {
				if (D)
					Log.d(TAG, "--> bluetooth is disabled");
				/*
				 * Certain methods need to invoke methods of an Activity-class.
				 * But in order to Categorize and keep method for certain
				 * functions in a single class, we let those method get the
				 * reference for the currently active Activity so to invoke
				 * their methods. Methods will get this reference from Ref,
				 * where it is maintained by the different activities. Only the
				 * activity currently running in thread can start other
				 * activities. Therefore, they provide their reference in Ref
				 * for other methods to ask them to invoke certain method. This
				 * is a wildly used method on the Internet beside using
				 * getApplicationContext() which is used by us for creating
				 * Toasts and others. enableAdapter() in BlueController is one
				 * of those methods.
				 */
				userIsAlreadyAsked = true;
				BlueController.enableAdapter(Ref.activeActivity);
			}
			if (D)
				if (D)
					Log.e(TAG, "isConnected? " + BlueController.isConnected());
			BlueController.closeConnection();
			BlueController.connectBT();
		}
		// Fix TCP Connection =======================================

		if (!(tcpController.isConnected() || tcpController.isConnecting())) {
			if (D)
				if (D)
					Log.e(TAG, "Fixing TCP connection");
			tcpController.setConnecting();

			if (D)
				if (D)
					Log.e(TAG, "isConnected? " + tcpController.isConnected());
			tcpController.closeConnection();
			tcpController.connectTCP();
		}
	}// ================================================================

	@Override
	public void run() {
		tcpController.setDisconnected();
		BlueController.setDisconnected();

		if (D)
			if (D)
				Log.e(TAG, "++  run  ++");

		keepRunning = true;
		int iterations = 0;
		String inData = null;

		while (keepRunning) {
			// Used to check that the thread is running for sure
			// All the other method are inaccurate.
			amIRunning = true;
			if (BlueController.isConnected()) {
				// Code to process
				try {
					inData = btRead();
					if (inData != null) {
						if (D)
							Log.d(TAG, "--> BT DATA read     " + inData);

						this.handler.getMessageFromBT(inData);
					}
				} catch (NumberFormatException e) {
					if (D)
						Log.w(TAG, "NumberFormatException");
				}
				if (btTransmitBuffer != null) {
					while (btTransmitBuffer.size() > 0
							&& BlueController.isConnected()) {
						if (D)
							Log.d(TAG, "BT sending data");
						btWrite();
					}
				}
				// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&
			} else {
				// Handle reconnection
				if (D)
					Log.e(TAG, "BT disconnected");
				fixConnections();
			}// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&

			if (tcpController.isConnected()) {
				// Code to process

				try {
					inData = tcpRead();
					if (D)
						Log.d(TAG, "--> TCP DATA read     " + inData);
					if (inData != null) {

						if (D)
							Log.e(TAG, "-----  inData = " + inData);

						this.handler.getMessageFromTCP(inData);

					}

				} catch (NumberFormatException e) {
					if (D)
						Log.w(TAG, "NumberFormatException");
				}
				if (tcpTransmitBuffer != null) {
					while (tcpTransmitBuffer.size() > 0
							&& tcpController.isConnected()) {
						tcpWrite();
					}
				}
				// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&
			} else {
				// Handle reconnection
				if (D)
					Log.e(TAG, "TCP disconnected");
				fixConnections();
			}// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&

			if (D)
				Log.i(TAG, "BT state: " + BlueController.isConnected()
						+ " TCP state: " + tcpController.isConnected());

			// -----------------------------------------------------
			// -----------------------------------------------------
			// -----------------------------------------------------
			// -----------------------------------------------------

			// ========================================
			// ===== THREAD MAINTENANCE ===============
			// ========================================

			// This gives fast processing if there are incoming data.
			// Saves battery
			if (inData == null) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					if (D)
						Log.w(TAG, "InterruptedException: ", e);
				}
			}

			// This will echo the server for testing purposes
			if (iterations == 60) {
				iterations = 0;
				if (D)
					Log.i(TAG,
							"BT Connection state: "
									+ (BlueController.isConnected()));
				if (D)
					Log.i(TAG,
							"TCP Connection state: "
									+ (tcpController.isConnected()));
				tcpController.testTCPConnection();
				BlueController.testBTConnection();
			} else {
				iterations++;
			}

		}

		cleanUp();
		if (D)
			Log.d(TAG, "--> Thread is shutdown");
		keepRunning = false;
		amIRunning = false;
	}

	// ==================================================================
	private void btWrite() {
		if (D)
			Log.e(TAG, "++ btWrite ++");
		if (BlueController.isConnected()) {
			if (btTransmitBuffer.size() > 0) {
				BlueController.sendBytes(btTransmitBuffer.removeFirst()
						.getBytes());
			}
		}
	}

	// ==================================================================
	private void tcpWrite() {
		if (D)
			Log.e(TAG, "++ btWrite ++");
		if (tcpController.isConnected()) {
			if (tcpTransmitBuffer.size() > 0) {
				tcpController.sendMessage(tcpTransmitBuffer.removeFirst());
			}
		}
	}// ==================================================================

	/**
	 * Returns a String from the receivebuffer of the bluetooth adapter.
	 * 
	 * @return inData null if not connected or buffer not ready
	 */
	private String btRead() {
		if (D)
			Log.e(TAG, "++ btRead ++");
		String inData = null;
		if (BlueController.isConnected()) {
			inData = btController.receiveString();
		}
		return inData;
	}// ==================================================================

	/**
	 * Returns a String from the receivebuffer of the bluetooth adapter.
	 * 
	 * @return inData null if not connected or buffer not ready
	 */
	private String tcpRead() {
		if (D)
			Log.e(TAG, "++ tcpRead ++");
		String inData = null;
		if (tcpController.isConnected()) {
			inData = tcpController.receiveString();
		}
		return inData;
	}// ==================================================================

	private void cleanUp() {
		if (D)
			Log.e(TAG, "++ cleanUp ++");
		// Do not invoke method that forcefully shut a thread down.
		// Let the run method run out.
		// this.shutdownThread(); wont work, just like suspend() and stop()

		BlueController.disconnect();
		tcpController.disconnect();

		btTransmitBuffer = null;
		tcpTransmitBuffer = null;
		applicationContext = null;
		btController = null;
		tcpController = null;

	}// ==================================================================

	// The next two methods put strings in transmitbuffer
	public static void sendByBT(String data) {
		if (D)
			Log.e(TAG, "++ sendByBT ++");
		btTransmitBuffer.addLast(data + "\r\n");
	}// ==================================================================

	public static void sendByTCP(String data) {
		if (D)
			Log.e(TAG, "++ sendByTCP ++");
		tcpTransmitBuffer.addLast(data);
	}// ==================================================================

	/**
	 * Run this method twice to get the state of the loop. This is proven a
	 * better check than the native once.
	 * 
	 * @return
	 */
	public boolean isRunning() {
		boolean temp = amIRunning;
		amIRunning = false;

		return temp;
	}

	public void startPark(String licensePlate, String carModel) {
		String startPark = "StartPark;";
		String ssNbr = mainPreference.getString("ssNbr", "error") + ":";
		if (!ssNbr.equals("error:")) {
			Calendar cal = Calendar.getInstance();
			long startTimestamp = cal.getTimeInMillis();

			Location location = GPSReceiver.getLocation();

			// StartPark;ssNbr:55.3452324:26.3423423:2342133424:0:ADT-435:Renault:0
			startPark += ssNbr + ":" + location.getLongitude() + ":"
					+ location.getLatitude() + ":" + startTimestamp + ":0:"
					+ licensePlate + ":" + carModel + ":0";

			Log.e(TAG, "--> Send parking request:\n" + startPark);

			mainPreference.edit().putString("StartPark", startPark);

			sendByTCP(startPark);
		} else {
			Toast.makeText(Ref.activeActivity, "Error,  please login again",
					Toast.LENGTH_LONG).show();
		}
	}

	public void stopPark(String licensePlate, String carModel) {
		String stopString = mainPreference.getString("StartPark", "no data");
		if (stopString.equals("no data")) {
			Log.e(TAG, "--> No parking to stop");
			Toast.makeText(Ref.activeActivity, "No parking to stop", Toast.LENGTH_LONG)
					.show();
			return;
		}
		
		String StopPark = "StopPark;";
		String ssNbr = mainPreference.getString("ssNbr", "error") + ":";
		if (!ssNbr.equals("error:")) {
			Calendar cal = Calendar.getInstance();
			long stopTimestamp = cal.getTimeInMillis();
			
			String startPark = mainPreference.getString("StartPark", "0");
			
			String startTimeStamp = startPark.split(";")[1].split(":")[3];
					
			String parkID = mainPreference.getString("parkID", "-1");
			
			// StopPark;ssNbr:55.3452324:26.3423423:2342133424:2342143424:ADT-435:Renault:price:parkID

			Location location = GPSReceiver.getLocation();

			StopPark += ssNbr + ":" 
					+ location.getLongitude() + ":"
					+ location.getLatitude() + ":" 
					+ startTimeStamp + ":" 
					+ stopTimestamp + ":"
					+ licensePlate+ ":" 
					+ carModel + ":" 
					+ parkID;
			
			Log.e(TAG, "--> Send parking request: " + StopPark);
			mainPreference.edit().putString("LastParkingStop", StopPark);
			sendByTCP(StopPark);
			
			while(mainPreference.getString("LastParkingStop", "-").equals(StopPark)){
				mainPreference.edit().putString("LastParkingStop", StopPark);
				Log.e(TAG, "Saving data in stopPark()");
			}
		} else {
			Toast.makeText(Ref.activeActivity, "Error,  please login again",
					Toast.LENGTH_LONG).show();
		}
	}
	
	public void getHistory(long fromDate, long toDate){
		// History;startDate:stopDate
		// HistoryACK;longitute:latitute:startStamp:stopStamp:price:parkID
		// duration parkID position price
		
		// History;
		String query = "History;" + fromDate + ":" + toDate;
		
		
		
		
	}
}
