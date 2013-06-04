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

	// CONTROL FLAGS
	private boolean userIsAlreadyAsked = false;

	// REFERENCE TO APLLICATIONCONTEXT
	private Context applicationContext;

	private BackOperationService backOperationService;

	// REFERENCES TO CONTROL-CLASSES
	private BlueController btController;
	private static TCPController tcpController;
	private static Handler handler;

	// SharedPreferences for login settings
	private static SharedPreferences mainPreference;

	// USED WHEN INITIATING SOFT SHUTDOWN (RECOMMENDED ON THE INTERNET)
	private boolean keepRunning = true;
	// The state of execution
	private boolean amIRunning = false;
	// Am I logged in
	private boolean isLoggedIn;

	// Upon starting a new parking, did the server respond
	private static boolean parkingLotdataReceived;
	// Indicates whether or not we have an ongoing parking sequence
	private static boolean parkingInitiated;
	// Currently in parking
	static boolean isParking = false;

	// == Datacenter section ========
	// Info about the last parkingLot
	public static String[] parkingLot;
	public static String[] startParkString;
	public static String[] stopParkString;

	// ==============================

	// =========== END OF CLASS VARIABLES ===============================

	public BackgroundOperationThread(Context applicationContext,
			BackOperationService backOperationService,
			BlueController btController, TCPController tcpController,
			Handler handler, SharedPreferences mainPreference) {

		this.setPriority(MAX_PRIORITY);
		if (D)
			Log.e(TAG, "++ bgThread Constructor ++");

		btTransmitBuffer = new LinkedList<String>();
		tcpTransmitBuffer = new LinkedList<String>();

		this.applicationContext = applicationContext;
		this.backOperationService = backOperationService;

		// BT
		this.btController = btController;

		// TCP
		BackgroundOperationThread.tcpController = tcpController;

		BackgroundOperationThread.handler = handler;

		BackgroundOperationThread.mainPreference = mainPreference;

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

						BackgroundOperationThread.handler
								.getMessageFromBT(inData);
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

						BackgroundOperationThread.handler
								.getMessageFromTCP(inData);

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

	public static TCPController getTCPReference() {
		return tcpController;
	}

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

	public static boolean startPark(String licensePlate, String carModel) {
		Log.e(TAG, "++ startPark ++");

		String startPark = "StartPark;";
		String ssNbr = mainPreference.getString("ssNbr", "error");
		if (!ssNbr.equals("error")) {
			Location location = GPSReceiver.getLocation();
			if (location == null) {
				return false;
			}

			Log.e(TAG,
					"location: " + location.getLatitude() + " "
							+ location.getLongitude());

			Calendar cal = Calendar.getInstance();
			long startTimestamp = cal.getTimeInMillis();

			// StartPark;ssNbr:55.3452324:26.3423423:2342133424:0:ADT-435:Renault:0:0
			// param-size = 9
			startPark += ssNbr + ":" + location.getLatitude() + ":"
					+ location.getLongitude() + ":" + startTimestamp + ":0:"
					+ licensePlate + ":" + carModel + ":0:0";

			Log.e(TAG, "--> Send parking request:\n" + startPark);

			startParkString = startPark.split(";")[1].split(":");

			mainPreference.edit().putString("StartPark", startPark).apply();

			sendByTCP(startPark);
			return true;
		} else {
			Toast.makeText(Ref.activeActivity, "Error,  please login again",
					Toast.LENGTH_LONG).show();
			return false;
		}
	}

	// =============================================================
	public static boolean stopPark(String licensePlate, String carModel) {
		Log.e(TAG, "++ stopPark ++");

		String stopString = mainPreference.getString("StartPark", "no data");
		if (stopString.equals("no data")) {
			Log.e(TAG, "--> No parking to stop");
			Toast.makeText(Ref.activeActivity, "No parking to stop",
					Toast.LENGTH_LONG).show();
			return false;
		} else {

			String stopPark = "StopPark;";
			String ssNbr = mainPreference.getString("ssNbr", "error");
			if (!ssNbr.equals("error")) {
				Calendar cal = Calendar.getInstance();
				long stopTimestamp = cal.getTimeInMillis();

				String startPark = mainPreference.getString("StartPark", "0");

				String startTimeStamp = startPark.split(";")[1].split(":")[3];

				String parkID = BackgroundOperationThread.parkingLot[7];

				// StopPark;ssNbr:55.3452324:26.3423423:2342133424:2342143424:ADT-435:Renault:price:parkID

				String price = BackgroundOperationThread.parkingLot[0];

				Location location = GPSReceiver.getLocation();

				stopPark += ssNbr + ":" + location.getLongitude() + ":"
						+ location.getLatitude() + ":" + startTimeStamp + ":"
						+ stopTimestamp + ":" + licensePlate + ":" + carModel
						+ ":" + price + ":" + parkID;

				stopParkString = stopPark.split(";")[1].split(":");

				Log.e(TAG, "--> Send parking request: " + stopPark);
				sendByTCP(stopPark);

				mainPreference.edit().putString("LastParkingStop", stopPark)
						.apply();

				return true;

			} else {
				Toast.makeText(Ref.activeActivity,
						"Error,  please login again", Toast.LENGTH_LONG).show();
				return false;
			}
		}
	}

	public static void getHistory(long fromDate, long toDate) {
		Log.e(TAG, "++ getHistory ++: " + fromDate + " " + toDate);
		// History;startDate:stopDate
		// HistoryACK;longitute:latitute:startStamp:stopStamp:price:parkID
		// duration parkID position price

		// History;millis:millis
		String query = "History;" + fromDate + ":" + toDate;

		// sendByTCP(query);
		sendByTCP(query);
	}

	private int getMin(double[] array) {
		if (array.length > 0) {
			int index = 0;
			for (int i = 0; i < array.length; i++) {
				if (array[index] > array[i]) {
					index = i;
				}
			}
			return index;
		}
		return -1;
	}

	// Parking state controller-booleans

	// the first to be used
	public static void setParkingInitiated() {
		parkingInitiated = true;
	}

	// the second to be used (if any)
	public static void cancelParkingSequence() {
		// TODO Auto-generated method stub
		setParkingEnded();
	}

	// the third to be used
	public static boolean isParkingLotdataReceived() {
		return parkingLotdataReceived;
	}

	// the forth to be used
	public static void setParkingLotdataReceived() {
		parkingLotdataReceived = true;
	}

	// the fifth to be used
	public static void setParking() {
		isParking = true;
	}

	// the sixth to be used
	public static boolean isParking() {
		return isParking;
	}

	// the last to be used
	public static void setParkingEnded() {
		parkingInitiated = false;
		parkingLotdataReceived = false;
		isParking = false;
	}

}
