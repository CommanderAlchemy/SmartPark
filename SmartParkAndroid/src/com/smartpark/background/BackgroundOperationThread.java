package com.smartpark.background;

import java.util.LinkedList;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.widget.Toast;

import com.smartpark.bluetooth.BlueController;
import com.smartpark.tcp.TCPController;

public class BackgroundOperationThread extends Thread {

	private static long shutdownTime = 0; // 0 = never

	// TRANSMITBUFFERS
	private LinkedList<String> btTransmitBuffer = new LinkedList<String>();
	private LinkedList<String> tcpTransmitBuffer = new LinkedList<String>();

	// Debugging and stuff
	private static final String TAG = "bgThread";
	private static final boolean D = Ref.D;

	private static Context applicationContext;

	// USED WHEN INITIATING SOFT SHUTDOWN (RECOMMENDED ON THE INTERNET)
	private boolean keepRunning = true;

	// CONTROL FLAGS
	private boolean userIsAlreadyAsked = false;

	// REFERENCES TO CONTROL-CLASSES
	private BlueController btController;
	private TCPController tcpController;

	// =========== END OF CLASS VARIABLES ===============================

	public BackgroundOperationThread(Context applicationContext,
			BlueController btController, TCPController tcpController) {
		Log.e(TAG, "++ bgThread Constructor ++");

		this.applicationContext = applicationContext;

		// BT
		this.btController = btController;

		// TCP
		this.tcpController = tcpController;

		// Check to see if bluetooth is available
		if (!btController.isBluetoothAdapterAvailable()) {
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

	}// ==================================================================

	public void powerDown() {
		// When this flag gets set, the thread is told to shut it self down
		keepRunning = false;
	}// ==================================================================

	private void fixConnections() {
		Log.e(TAG, "++ fixConnections ++");
		if (!btController.isConnecting() || !btController.isConnected()) {
			btController.setConnecting();
			boolean discovering;

			// Enable bluetooth if disabled by asking the user first (only once)
			if (!userIsAlreadyAsked && !btController.isEnabled()) {
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
				btController.enableAdapter();
				userIsAlreadyAsked = true;
				Log.d(TAG, "--> Enabling done");
				// Toast.makeText(applicationContext, "Enabled",
				// Toast.LENGTH_SHORT)
				// .show();
			}
			if (D)
				Log.e(TAG, "isConnected? " + btController.isConnected());
			btController.closeConnection();
			btController.connectBT();
		}
	}// ================================================================

	@Override
	public void run() {
		if (D)
			Log.e(TAG, "++  run  ++");
		String btInData = null;
		String tcpInData = null;

		keepRunning = true;

		while (keepRunning) {
			if (btController.isConnected()) {
				// Code to process
				try {
					Log.d(TAG, "--> reading started");
					// TODO
					btInData = btRead();
					Log.i(TAG, "--> DATA read     " + btInData);
					if (btInData != null) {
						Integer t = Integer.parseInt(btInData);
						if (t != 1000000) {
							t++;
							Log.d(TAG, "Will now send: " + t.toString());
							sendByBT(t.toString());
							Log.w(TAG, "Just send: " + t);
						} else {
							t = 0;
							sendByBT(t.toString());
						}
					}
					Log.d(TAG, "--> reading ended");
				} catch (NumberFormatException e) {
					Log.e(TAG, "NumberFormatException");
				}

				while (btTransmitBuffer.size() > 0
						&& btController.isConnected()) {
					Log.d(TAG, "BT sending data");
					btWrite();
				}
				// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&
			} else {
				// Handle reconnection
				Log.e(TAG, "BT disconnected");
				fixConnections();
			}// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&

			if (tcpController.isConnected()) {
				// Code to process

				// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&
			} else {
				// Handle reconnection

			}// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&

			Log.i(TAG, "Connection state: " + (btController.isConnected()));

			// -----------------------------------------------------
			// -----------------------------------------------------
			// -----------------------------------------------------
			// -----------------------------------------------------

			Log.d(TAG, "BT buffer size: " + btTransmitBuffer.size());

			// Check to see if the thread needs to start shutting down
			// Log.d(TAG, "--> thread running");
			try {
				Thread.sleep(1200);
			} catch (InterruptedException e) {
				Log.e(TAG, "InterruptedException: ", e);
			}
			// if (Ref.flagMainActivityInFront ||
			// Ref.flagSettingsActivityInFront
			// || Ref.flagLoginActivityInFront) {
			// shutdownTime = 0;
			// } else {
			// if (shutdownTime == 0) {
			// shutdownTime = System.currentTimeMillis();
			// } else if (System.currentTimeMillis() - shutdownTime > 30000) {
			// cleanUp();
			// keepRunning = false;
			// Log.i(TAG, "--> Shutting down thread");
			// }
			// if (D)
			// Log.d("BackThread",
			// "thread is shutting down"
			// + (System.currentTimeMillis() - shutdownTime));
			// try {
			// Thread.sleep(3000);
			// } catch (InterruptedException e) {
			// Log.e(TAG, "InterruptedException: ", e);
			// }
			// }
		}
		Log.d(TAG, "--> Thread is shutdown");

		cleanUp();

	}// ==================================================================

	private void btWrite() {
		Log.e(TAG, "++ btWrite ++");
		if (btController.isConnected()) {
			byte[] data = btTransmitBuffer.removeFirst().getBytes();
			btController.sendBytes(data);
		}
	}// ==================================================================

	/**
	 * Returns a String from the receivebuffer of the bluetooth adapter.
	 * 
	 * @return inData null if not connected or buffer not ready
	 */
	private String btRead() {
		Log.e(TAG, "++ btRead ++");
		String inData = null;
		if (btController.isConnected()) {
			inData = btController.receiveString();
		}
		return inData;
	}// ==================================================================

	private void cleanUp() {
		Log.e(TAG, "++ cleanUp ++");
		// Do not invoke method that forcefully shut a thread down.
		// Let the run method run out.
		// this.shutdownThread(); wont work, just like suspend() and stop()

		btController.cleanUp();
		tcpController.disconnect();

		btTransmitBuffer = null;
		tcpTransmitBuffer = null;
		applicationContext = null;
		btController = null;
		tcpController = null;

	}// ==================================================================

	// The next two methods put strings in transmitbuffer
	public void sendByBT(String data) {
		Log.e(TAG, "++ sendByBT ++");
		btTransmitBuffer.addLast(data + "\r\n");
	}// ==================================================================

	public void sendByTCP(String data) {
		Log.e(TAG, "++ sendByTCP ++");
		tcpTransmitBuffer.addLast(data);
	}// ==================================================================

}
