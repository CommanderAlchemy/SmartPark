package com.smartpark.background;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;

import com.smartpark.bluetooth.BlueController;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.util.Log;
import android.widget.Toast;

public class BackgroundOperationThread extends Thread {

	/*
	 * keeps a list of booleans to determine if all activities have been
	 * destroyed so that the thread wont continue running for ever.
	 */
	public boolean activityMAIN = false;
	public boolean activitySettings = false;
	public boolean activityLOGIN = false;

	private static long shutdownTime = 0; // 0 = never

	// TRANSMITBUFFERS
	private LinkedList<String> btTransmitBuffer = new LinkedList<String>();
	private LinkedList<String> tcpTransmitBuffer = new LinkedList<String>();

	// Debugging and stuff
	private static final String TAG = "bgThread";
	private static final boolean D = Ref.D;

	// Device to connect to
	private final String SMARTPARK_DEVICE = "HC-06-SLAVE";

	private BufferedReader bufferedReader;

	private boolean run = true;

	private boolean shutdownFlag = false;

	public BackgroundOperationThread() {
		Log.e(TAG, "++ bgThread Constructor ++");
		// BT
		Ref.btState = Ref.STATE_NOT_CONNECTED;
		Ref.btAdapter = BluetoothAdapter.getDefaultAdapter();
		Ref.btController = new BlueController();

		// TCP
		Ref.tcpState = Ref.STATE_NOT_CONNECTED;
		Ref.clientThread = null;
		Ref.tcpClient = null;
		// TODO add more to this
	}

	public void powerDown() {
		// When this flag gets set, the thread is told to shut it self down
		shutdownFlag = true;
	}

	@Override
	public void run() {
		// TODO remember to check for the shutdownFlag
		
		Log.e(TAG, "++ bgThread started ++");
		String btInData = null;
		String tcpInData = null;
		run = true;
		// ===========================================================
		while (run) {

			if (Ref.btState == Ref.STATE_CONNECTED) {
				// Code to process
				try {
					Log.d(TAG, "--> reading started");

					btInData = Ref.btController.receiveString();
					Log.d(TAG, "--> DATA read                  " + btInData);
					Integer t = Integer.parseInt(btInData);
					if (t != 10) {
						t++;
						Log.d(TAG, "Will now send: " + Integer.toString(t));
						sendByBT(Integer.toString(t));
					}
					Log.d(TAG, "--> reading started");
				} catch (NumberFormatException e) {
					Log.e(TAG, "NumberFormatException:\n" + e);
				}

				while (btTransmitBuffer.size() > 0
						&& Ref.getbtState() == Ref.STATE_CONNECTED) {
					Log.d(TAG, "BT sending data");
					btWrite();

				}
				// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&
			} else {
				// Handle reconnection
				Log.e(TAG, "BT not connected: handling error");
				if (BlueController.btDevice == null
						|| Ref.btState != Ref.STATE_CONNECTED) {
					Ref.btState = Ref.STATE_CONNECTING;

					if (Ref.btController == null) {
						Log.e(TAG, "BlueController intance recreate");
						Ref.btController = new BlueController();
					}

					BluetoothDevice device = Ref.btController
							.getPairedDeviceByName(SMARTPARK_DEVICE);
					if (device == null) {
						Ref.btController.findNearbyDevices(Ref.activeActivity);
						for (int i = 0; i < 10; i++) {
							device = Ref.btController
									.getFoundDeviceByName(SMARTPARK_DEVICE);

							if (device != null
									&& device.getName()
											.equals(SMARTPARK_DEVICE)) {

							}
							Toast.makeText(Ref.activeActivity,
									"Bluetooth available", Toast.LENGTH_SHORT)
									.show();
							try {
								BackgroundOperationThread.sleep(1200);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								Log.e(TAG, "Interrupted Exception occured" + e);
							}
						}
					}
					if (device != null) {
						BlueController.btDevice = device;
						Ref.btController.connect();
						Log.d(TAG, "--> connected to " + device.getAddress());
						bufferedReader = new BufferedReader(
								new InputStreamReader(BlueController.btInStream));
					} else {
						Log.w(TAG, "--> device is null, bluetooth not found");
					}
				}
			}// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&

			if (Ref.tcpState == Ref.STATE_CONNECTED) {
				// Code to process

				// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&
			} else {
				// Handle reconnection

			}// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&

			// -----------------------------------------------------
			// -----------------------------------------------------
			// -----------------------------------------------------
			// -----------------------------------------------------

			Log.d(TAG, "BT buffer size: " + btTransmitBuffer.size());

			// Check to see if the thread needs to start shutting down
			// Log.d(TAG, "--> thread running");
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
			}

			if (activityMAIN || activitySettings || activityLOGIN) {
				shutdownTime = 0;
				// Log.d(TAG, "thread not idled");
			} else {
				Log.d("TAG", "--> bgThread timer started");
				if (shutdownTime == 0) {
					shutdownTime = System.currentTimeMillis();
				} else if (System.currentTimeMillis() - shutdownTime > 30000) {
					shutdownThread();
					run = false;
					Log.i(TAG, "--> Shutting down thread");
				}
				if (D)
					Log.d("BackThread",
							"thread is shutting down"
									+ (System.currentTimeMillis() - shutdownTime));
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
				}
			}
		}
		Log.i(TAG, "--> Thread is shutdown");
		// In case the thread-instance is reused this will avoid a problem for
		// us
		shutdownFlag = false;
	}

	private void btWrite() {
		Log.e(TAG, "++ btWrite ++");
		if (Ref.btIsConnected()) {
			byte[] data = btTransmitBuffer.removeFirst().getBytes();
			Ref.btController.sendBytes(data);
		}
	}

	private void shutdownThread() {
		Log.e(TAG, "++ shutdownThread ++");
		// Do not invoke method that forcefully shut a thread down.
		// Let the run method run out.
		// this.shutdownThread(); wont work, just like suspend() and stop()

		Ref.btController.close();
		Ref.bgThread = null;
	}

	// The next two methods put strings in transmitbuffer
	public void sendByBT(String data) {
		Log.e(TAG, "++ sendByBT ++");
		btTransmitBuffer.addLast(data + "\r\n");
	}

	public void sendByTCP(String data) {
		Log.e(TAG, "++ sendByTCP ++");
		tcpTransmitBuffer.addLast(data);
	}

}
