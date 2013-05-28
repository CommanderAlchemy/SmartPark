package com.smartpark.bluetooth;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.smartpark.activities.MainActivity;

public class BlueController {
	/*
	 * Since there is only one bluetooth adapter in every handheld device, all
	 * variables are defined using the static modifier, because they only
	 * represent settings and findings of the same adapter. However, they are
	 * all private to this class.
	 */

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
	
	// Debugging and stuff
	private static final boolean D = false;
	private static final String TAG = "BlueController";

	// CONNECTION STATE-FLAG
	private static int connectionState = STATE_NOT_CONNECTED;

	// Device to connect to
	private final static String SMARTPARK_DEVICE = "HC-06-SLAVE";

	// RequestCodes for controlling the bluetooth
	public static final int REQUEST_ENABLE_BT = 1;
	public static final int REQUEST_DISCOVERABLE_BT = 2;

	private static ArrayList<BluetoothDevice> foundDevices = new ArrayList<BluetoothDevice>();
	private static Set<BluetoothDevice> pairedDevices;

	// Constants used locally
	private static final UUID SerialPort = UUID
			.fromString("00001101-0000-1000-8000-00805F9B34FB");

	// Device, Socket and IO-Streams remove static
	private static BluetoothDevice btDevice;
	private static BluetoothSocket btSocket;
	private static InputStream btInStream;
	private static OutputStream btOutStream;

	private static BufferedReader bufferedReader;

	// change to private
	private static BluetoothAdapter btAdapter;

	// change to private
	private static Context applicationContext;

	// -------------------------------------------------------------------------------
	// public BlueController(Context instantiatorClass) {
	public BlueController(Context applicationContext) {
		BlueController.applicationContext = applicationContext;
		if (D)
			if(D)Log.e(TAG, "++ Constructor: BlueController ++");

		// Get the adapter and store it in a static variable
		// This initializes the class
		if (btAdapter == null) {
			btAdapter = BluetoothAdapter.getDefaultAdapter();
		}

	}
	// -------------------------------------------------------------------------------

	public static int disconnect() {
		Log.e(TAG, "++ disconnect ++");
		/*
		 * This method will close and unregister and set everything to null to
		 * make ready for a clean transition for shutdown.
		 */
		/*
		 * This method is intended to be used in the class that is more likely
		 * to be the last class to exit and should not be invoked like in
		 * orientation changes.
		 */
		setDisconnected();
		try {
			btSocket.close();
			return RESULT_OK;
		} catch (Exception e) {
			if(D)Log.e(TAG, "IOException: ", e);
			return RESULT_IO_EXCEPTION;
		}finally{
			btInStream = null;
			btOutStream = null;
			btSocket = null;
			btDevice = null;
		}
	}
	
	// -------------------------------------------------------------------------------

	/**
	 * This method will first register a BroadcastReceiver for intents carrying
	 * new devices found by the bluetooth adapter, and then start the device
	 * discovery of bluetooth adapter. The method startDiscovery() is
	 * Asynchronous and will quickly return a boolean for whether or not the
	 * discovery started successfully. After this method returns, the found
	 * devices can be received by invoking getFoundDevices().
	 */
	public static void findNearbyDevices() {
		if (D)
			if(D)Log.e(TAG, "++ findNearbyDevices ++");

		if (!btAdapter.isDiscovering()) {
			btAdapter.startDiscovery();
		}
	}

	public static Set<BluetoothDevice> getPairedDevicesList() {
		if (D)
			if(D)Log.e(TAG, "++ getPairedDevicesList ++");

		if (!btAdapter.isEnabled()) {
			if (D)
				if(D)Log.e(TAG, "adapter not enabled");
		} else {
			pairedDevices = btAdapter.getBondedDevices();
		}
		return pairedDevices;
	}

	/**
	 * This method searches for a BluetoothDevice that matches the specified
	 * name. It will only search for the device among paired devices.
	 * 
	 * @param name
	 *            The name of the bluetooth device
	 * @return device BluetoothDevice
	 */
	public static BluetoothDevice getPairedDeviceByName(String name) {
		if (D)
			if(D)Log.e(TAG, "++ getPairedDeviceByName ++");

		if (D)
			if(D)Log.d(TAG, "--> Getting BluetoothDevice for: " + name);

		BluetoothDevice device;
		Set<BluetoothDevice> h = getPairedDevicesList();

		if (h != null) {
			if(D)Log.e(TAG, h.toString());
			Iterator<BluetoothDevice> iter = h.iterator();
			while (iter.hasNext()) {
				device = iter.next();
				if (device.getName().equals(name)) {
					return device;
				}
			}
		}
		return null;
	}

	/**
	 * This method searches for a BluetoothDevice that matches the specified
	 * name. It will only search for the device among found devices.
	 * 
	 * @param name
	 *            The name of the bluetooth device
	 * @return device BluetoothDevice
	 */
	public static BluetoothDevice getFoundDeviceByName(String name) {
		if (D)
			if(D)Log.e(TAG, "++ getFoundDeviceByName ++");

		BluetoothDevice device;
		Iterator<BluetoothDevice> iter;
		if(D)Log.i(TAG, "Found devices: " + foundDevices.size());
		if (foundDevices.size() > 0) {
			iter = foundDevices.iterator();
			while (iter.hasNext()) {
				device = iter.next();
				if (device.getName().equals(name)) {
					return device;
				}
			}
		}
		return null;
	}

	/**
	 * This method can be called whenever needed. It should however be used when
	 * the bluetooth adapter is discovering. The ArrayList, foundDevices, is
	 * populated by the onReceive-method of the
	 * 
	 * @return foundDevices These are the found devices so far
	 */
	public static ArrayList<BluetoothDevice> getFoundDevicesList() {
		if (D)
			if(D)Log.e(TAG, "++ getFoundDevices ++");
		return foundDevices;
	}

	// ================================================================

	public static void connectBT() {
		new Thread() {
			public void run() {

				if(D)Log.e(TAG, "++ reconnectBT ++");

				setConnecting();

				if (btDevice == null) {
					btDevice = getPairedDeviceByName(SMARTPARK_DEVICE);
					if (btDevice == null) {
						if (isEnabled()) {
							if(D)Log.i(TAG,
									"The device is not previously paired with"
											+ " this phone or bluetooth is disabled.");
							findNearbyDevices();
							// The adapter will only search for 12 seconds
							for (int i = 0; i < 15; i++) {
								if(D)Log.i(TAG,
										"Discovering? "
												+ btAdapter.isDiscovering());
								try {
									Thread.sleep(1200);
								} catch (InterruptedException e) {
									if(D)Log.e(TAG,
											"--> Thread.sleep was interrupted!");
								}
								btDevice = getFoundDeviceByName(SMARTPARK_DEVICE);

								if (btDevice != null) {
									stopDiscovery();
									if(D)Log.i(TAG, "Found SP-Device");
									Toast.makeText(applicationContext,
											"SmartPark-device found",
											Toast.LENGTH_SHORT).show();
									break;
								}
							}
						}
					}
				}

				if (btDevice != null) {
					if(D)Log.e(TAG, "--> Connecting to: " + btDevice.toString());
					// This connect will start a new thread.
					if (connect()) {
						if(D)Log.e(TAG, "--> connected to: " + btDevice.getName());
					}
				} else {
					if(D)Log.w(TAG, "--> device is null, bluetooth not found");
				}
			}
		}.start();
	}

	// ================================================================

	/**
	 * This method aims at connecting to the device and is private to the class.
	 * 
	 * @return
	 */
	private static boolean connect() {
		if (D)
			if(D)Log.e(TAG, "++ connect ++");
		boolean isConnected = false;

		try {
			btSocket = btDevice.createRfcommSocketToServiceRecord(SerialPort);
			try {
				/*
				 * This method blocks till it connects or returns an exception.
				 * 
				 * Always stop discovery before connect for good measure. (Tip
				 * from google developer)
				 */
				stopDiscovery();
				if(D)Log.e(TAG, "-------1a-------");
				btSocket.connect();
				isConnected = true;
				setConnected();
				if(D)Log.e(TAG, "-------2-------");
				/*
				 * We are changing state to connected since we have a
				 * BroadcastReceiver for it and it's more reliable.
				 */
			} catch (Exception e) {
				if(D)Log.e(TAG, "-------3-------");
				// Close the socket upon error
				try {
					if (D)
						if(D)Log.e(TAG, "Connection Exception: ", e);
					setDisconnected();
					btSocket.close();
					if(D)Log.e(TAG, "-------4-------");
				} catch (Exception e2) {
					if (D)
						if(D)Log.e(TAG, "Socket Close Exception: " + e2);
				}
			}
		} catch (Exception e) {
			if (D)
				if(D)Log.e(TAG, "Socket init Exception: " + e);
			setDisconnected();
		}

		try {
			if (isConnected()) {
				if (D)
					if(D)Log.d(TAG, "--> Init btSocket I/O Streams");
				btInStream = btSocket.getInputStream();
				btOutStream = btSocket.getOutputStream();
			}
		} catch (Exception e) {
			if (D)
				if(D)Log.e(TAG, "Socket I/O Streams Exception" + e);
			setDisconnected();
		}
		return isConnected;
	}

	public static int sendBytes(byte[] data) {
		if (D)
			if(D)Log.e(TAG, "++ sendString ++");
		try {
			if (btSocket.isConnected()) {
				btOutStream.write(data);
				return RESULT_OK;
			}
		} catch (IOException e1) {
			if (D)
				if(D)Log.e(TAG, "Sending of data with bt failed" + e1);
			setDisconnected();
			return RESULT_IO_EXCEPTION;
		} catch (Exception e1) {
			if(D)Log.e(TAG, "ggggggggggggggggggg" + e1);
			return RESULT_EXCEPTION;
		}
		return RESULT_IO_EXCEPTION;
	}

	// ================================================================

	public String receiveString() {
		if (D)
			if(D)Log.e(TAG, "++ receiveString ++");
		if (btInStream != null) {
			if (D)
				if(D)Log.d(TAG, "iStream good");
			String inData = null;
			try {
				if (bufferedReader == null) {
					if (D)
						if(D)Log.e(TAG, "bufferedReader was = null");
					bufferedReader = new BufferedReader(new InputStreamReader(
							btInStream));
				}
				if (bufferedReader.ready()) {
					if (D)
						if(D)Log.d(TAG, "reader ready");
					inData = bufferedReader.readLine();
					if (D)
						if(D)Log.d(TAG, "DATA= " + inData);
					return inData;
				}
			} catch (Exception e1) {
				setDisconnected();
				if (D)
					if(D)Log.e(TAG, "btSocket not connected");
			}
		} else {
			setDisconnected();
		}
		return null;
	}

	// ================================================================

	public static boolean isDiscovering() {
		if (D)
			if(D)Log.e(TAG, "++ isDiscovering ++");
		return btAdapter.isDiscovering();
	}

	// ================================================================

	public static int closeConnection() {
		if (D)
			if(D)Log.e(TAG, "++ closeConnection ++");
		try {
			/*
			 * The use of && prohibits isConnected() to be invoked if btSocket
			 * is null.
			 */
			if (btSocket != null) {
				btSocket.close();
			}
			return RESULT_OK;
		} catch (Exception e) {
			if (D)
				if(D)Log.e(TAG, "Error closing btSocket: ", e);
			return RESULT_IO_EXCEPTION;
		}
	}

	public static boolean isBluetoothAdapterAvailable() {
		return btAdapter != null;
	}

	public static void stopDiscovery() {
		if (D)
			if(D)Log.e(TAG, "++ stopDiscovery ++");

		// cancel any prior BT device discovery
		if (btAdapter.isDiscovering()) {
			btAdapter.cancelDiscovery();
		}
	}

	public static void enableAdapterNoUserInteraction() {
		if (D)
			if(D)Log.e(TAG, "++ enableAdapterNoUserInteraction ++");
		btAdapter.enable();
	}

	public static boolean enableAdapter(Activity invokerActivity) {
		if (D)
			if(D)Log.e(TAG, "++ enableAdapter ++");
		if (btAdapter != null)
			if(D)Log.e(TAG, "btAdapter != null");
		if (!btAdapter.isEnabled()) {
			if (D)
				if(D)Log.d(TAG, "enabling adapter");
			Intent enableBtIntent = new Intent(
					BluetoothAdapter.ACTION_REQUEST_ENABLE);
			invokerActivity.startActivityForResult(enableBtIntent,
					REQUEST_ENABLE_BT);
		}
		int state = btAdapter.getState();
		return state == BluetoothAdapter.STATE_TURNING_ON
				|| state == BluetoothAdapter.STATE_ON;
	}

	public static boolean isEnabled() {
		return btAdapter.isEnabled();
	}

	public static boolean disableAdapter() {
		if (D)
			if(D)Log.e(TAG, "++ disableAdapter ++");
		if (btAdapter.getState() == BluetoothAdapter.STATE_OFF) {
			return true;
		} else {
			btAdapter.disable();
		}
		int state = btAdapter.getState();
		return state == BluetoothAdapter.STATE_TURNING_OFF
				|| state == BluetoothAdapter.STATE_OFF;
	}

	public static void makeDiscoverable(MainActivity invokerActivity) {
		if (D)
			if(D)Log.e(TAG, "++ makeDiscoverable ++");
		Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
		intent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
		invokerActivity.startActivityForResult(intent, REQUEST_DISCOVERABLE_BT);
	}

	/**
	 * The BroadcastREceiver will invoke this method to put in more devices in
	 * the list.
	 * 
	 * @param device
	 */
	public static void addFoundDeviceTolist(BluetoothDevice device) {
		if (!foundDevices.contains(device)) {
			foundDevices.add(device);
		}
	}

	// CONNECTION STATE SETTERS AND GETTERS

	public static boolean isConnected() {
		try {
			if(D)Log.i(TAG, " btSocket state: " + btSocket.isConnected() + " boolean: " + (connectionState == STATE_CONNECTED));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return connectionState == STATE_CONNECTED && btSocket.isConnected(); 
	}

	public static boolean isConnecting() {
		return connectionState == STATE_CONNECTING;
	}

	public static void setConnecting() {
		connectionState = STATE_CONNECTING;
	}

	public static void setConnected() {
		if(D)Log.e(TAG, "++ setConnected ++");
		connectionState = STATE_CONNECTED;
	}

	public static void setDisconnected() {
		connectionState = STATE_NOT_CONNECTED;

	}

	public static boolean testBTConnection() {
		if(D)Log.e(TAG, "++ testConnection ++");
		byte[] echo = { 'e', 'c', 'h', 'o', ';', 'e', 'c', 'h', 'o', '\n' };
		try {
			if (btSocket != null && btSocket.isConnected()) {
				btSocket.getOutputStream().write(echo);
				setConnected();
				return true;
			}else{
				setDisconnected();
				return false;
			}
		} catch (Exception e) {
			setDisconnected();
			e.printStackTrace();
			return false;
		}

	}

	// ===================================
}
