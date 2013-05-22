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
import android.view.ViewDebug.FlagToString;
import android.widget.Toast;

import com.smartpark.activities.MainActivity;
import com.smartpark.background.Ref;

public class BlueController {
	/*
	 * Since there is only one bluetooth adapter in every handheld device, some
	 * variables are defined using the static modifier, because they only
	 * represent settings and findings of the same adapter. However, they are
	 * all private to this class.
	 */

	// Debugging and stuff
	private static final String TAG = "BlueController";
	private static final boolean D = Ref.D;

	// CONNECTION STATE-FLAG
	private static int connectionState = Ref.STATE_NOT_CONNECTED;

	// Device to connect to
	private final static String SMARTPARK_DEVICE = "HC-06-SLAVE";

	// Flags
	private static boolean BroacastReceiverIsRegistered = false;

	// RequestCodes for controlling the bluetooth
	public static final int REQUEST_ENABLE_BT = 1;
	public static final int REQUEST_DISCOVERABLE_BT = 2;

	private static ArrayList<BluetoothDevice> foundDevices = new ArrayList<BluetoothDevice>();
	private static Set<BluetoothDevice> pairedDevices;

	// Constants used locally
	private static final UUID SerialPort = UUID
			.fromString("00001101-0000-1000-8000-00805F9B34FB");

	// Device, Socket and IO-Streams
	// remove static
	private static BluetoothDevice btDevice;
	private static BluetoothSocket btSocket;
	private static InputStream btInStream;
	private static OutputStream btOutStream;

	private BufferedReader bufferedReader;

	// change to private
	private static BluetoothAdapter btAdapter;

	// change to private
	private static Context applicationContext;

	// -------------------------------------------------------------------------------
	// public BlueController(Context instantiatorClass) {
	public BlueController(Context applicationContext) {
		this.applicationContext = applicationContext;
		if (D)
			Log.e(TAG, "++ Constructor: BlueController ++");

		// Get the adapter and store it in a static variable
		// This initializes the class
		if (btAdapter == null) {
			btAdapter = BluetoothAdapter.getDefaultAdapter();
		}

	}// -------------------------------------------------------------------------------

	public void cleanUp() {
		/*
		 * This method will close and unregister and set everything to null to
		 * make ready for a clean transition for shutdown.
		 */
		/*
		 * This method is intended to be used in the class that is more likely
		 * to be the last class to exit and should not be invoked like in
		 * orientation changes.
		 */
		try {
			btSocket.close();
		} catch (IOException e) {
			Log.e(TAG, "IOException: ", e);
		}
		btInStream = null;
		btOutStream = null;
		btSocket = null;
		btDevice = null;
	}

	/**
	 * This method will first register a BroadcastReceiver for intents carrying
	 * new devices found by the bluetooth adapter, and then start the device
	 * discovery of bluetooth adapter. The method startDiscovery() is
	 * Asynchronous and will quickly return a boolean for whether or not the
	 * discovery started successfully. After this method returns, the found
	 * devices can be received by invoking getFoundDevices().
	 */
	public void findNearbyDevices() {
		if (D)
			Log.e(TAG, "++ findNearbyDevices ++");

		if (!btAdapter.isDiscovering()) {
			btAdapter.startDiscovery();
		}
	}

	public Set<BluetoothDevice> getPairedDevicesList() {
		if (D)
			Log.e(TAG, "++ getPairedDevicesList ++");

		if (!btAdapter.isEnabled()) {
			if (D)
				Log.e(TAG, "adapter not enabled");
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
	public BluetoothDevice getPairedDeviceByName(String name) {
		if (D)
			Log.e(TAG, "++ getPairedDeviceByName ++");

		if (D)
			Log.d(TAG, "--> Getting BluetoothDevice for: " + name);

		BluetoothDevice device;
		Set<BluetoothDevice> h = getPairedDevicesList();

		if (h != null) {
			Log.e(TAG, h.toString());
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
	public BluetoothDevice getFoundDeviceByName(String name) {
		if (D)
			Log.e(TAG, "++ getFoundDeviceByName ++");

		BluetoothDevice device;
		Iterator<BluetoothDevice> iter;
		Log.i(TAG, "Found devices: " + foundDevices.size());
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
	public ArrayList<BluetoothDevice> getFoundDevicesList() {
		if (D)
			Log.e(TAG, "++ getFoundDevices ++");
		return foundDevices;
	}

	// ================================================================

	public int disconnect() {
		if (D)
			Log.e(TAG, "++ disconnect ++");
		setDisconnected();
		try {
			btSocket.close();
			return Ref.RESULT_OK;
		} catch (Exception e) {
			if (D)
				Log.e(TAG, "IO Exception: ", e);
			return Ref.RESULT_IO_EXCEPTION;
		}
	}

	// ================================================================

	public void connectBT() {
		new Thread() {
			public void run() {

				Log.e(TAG, "++ reconnectBT ++");

				setConnecting();

				if (btAdapter == null) {
					btAdapter = BluetoothAdapter.getDefaultAdapter();
				}// TODO remove carefully test everything

				if (btDevice == null) {
					btDevice = getPairedDeviceByName(SMARTPARK_DEVICE);
					if (btDevice == null) {
						if (isEnabled()) {
							Log.i(TAG,
									"The device is not previously paired with"
											+ " this phone or bluetooth is disabled.");
							findNearbyDevices();
							// The adapter will only search for 12 seconds
							for (int i = 0; i < 15; i++) {
								Log.i(TAG,
										"Discovering? "
												+ btAdapter.isDiscovering());
								try {
									Thread.sleep(1200);
								} catch (InterruptedException e) {
									Log.e(TAG,
											"--> Thread.sleep was interrupted!");
								}
								btDevice = getFoundDeviceByName(SMARTPARK_DEVICE);

								if (btDevice != null) {
									stopDiscovery();
									Log.i(TAG, "Found SP-Device");
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
					Log.e(TAG, "--> Connecting to: " + btDevice.toString());
					// This connect will start a new thread.
					if (connect()) {
						Log.e(TAG, "--> connected to: " + btDevice.getName());
					} else {
						Log.e(TAG,
								"--> did not connected to: "
										+ btDevice.getName());
					}

				} else {
					Log.w(TAG, "--> device is null, bluetooth not found");
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
	private boolean connect() {
		if (D)
			Log.e(TAG, "++ connect ++");
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
				Log.e(TAG, "-------1a-------");
				btSocket.connect();
				isConnected = true;
				setConnected();
				Log.e(TAG, "-------2-------");
				/*
				 * We are changing state to connected since we have a
				 * BroadcastReceiver for it and it's more reliable.
				 */
			} catch (Exception e) {
				Log.e(TAG, "-------3-------");
				// Close the socket upon error
				try {
					if (D)
						Log.e(TAG, "Connection Exception: ", e);
					setDisconnected();
					btSocket.close();
					Log.e(TAG, "-------4-------");
				} catch (Exception e2) {
					if (D)
						Log.e(TAG, "Socket Close Exception: " + e2);
				}
			}
		} catch (Exception e) {
			if (D)
				Log.e(TAG, "Socket init Exception: " + e);
			setDisconnected();
		}

		try {
			if (isConnected()) {
				if (D)
					Log.d(TAG, "--> Init btSocket I/O Streams");
				btInStream = btSocket.getInputStream();
				btOutStream = btSocket.getOutputStream();
			}
		} catch (Exception e) {
			if (D)
				Log.e(TAG, "Socket I/O Streams Exception" + e);
			setDisconnected();
		}
		return isConnected;
	}

	public int sendBytes(byte[] data) {
		if (D)
			Log.e(TAG, "++ sendString ++");
		try {
			if (btSocket.isConnected()) {
				btOutStream.write(data);
				return Ref.RESULT_OK;
			}
		} catch (IOException e1) {
			if (D)
				Log.e(TAG, "Sending of data with bt failed" + e1);
			setDisconnected();
			return Ref.RESULT_IO_EXCEPTION;
		} catch (Exception e1) {
			Log.e(TAG, "ggggggggggggggggggg" + e1);
			return Ref.RESULT_EXCEPTION;
		}
		return Ref.RESULT_IO_EXCEPTION;
	}

	// ================================================================

	public String receiveString() {
		if (D)
			Log.e(TAG, "++ receiveString ++");
		if (btInStream != null) {
			if (D)
				Log.d(TAG, "iStream good");
			String inData = null;
			try {
				if (bufferedReader == null) {
					if (D)
						Log.e(TAG, "bufferedReader was = null");
					bufferedReader = new BufferedReader(new InputStreamReader(
							btInStream));
				}
				if (bufferedReader.ready()) {
					if (D)
						Log.d(TAG, "reader ready");
					inData = bufferedReader.readLine();
					if (D)
						Log.d(TAG, "DATA= " + inData);
					return inData;
				}
			} catch (Exception e1) {
				setDisconnected();
				if (D)
					Log.e(TAG, "btSocket not connected");
			}
		} else {
			setDisconnected();
		}
		return null;
	}

	// ================================================================

	public boolean isDiscovering() {
		if (D)
			Log.e(TAG, "++ isDiscovering ++");
		return btAdapter.isDiscovering();
	}

	// ================================================================

	// /**
	// * This method will unregister the BroadcastReceiver for ACTION_FOUND of
	// the
	// * Bluetooth device. The registration happen in StartDiscovery().
	// *
	// * @param invokerActivity
	// * The reference to the invoking activity
	// */
	// public void unRegister_DeviceFoundReceiver() {
	// if (D)
	// Log.e(TAG, "++ unRegister_DeviceFoundReceiver ++");
	//
	// applicationContext.unregisterReceiver(bt_foundDeviceReceiver);
	// }
	// }

	public int closeConnection() {
		if (D)
			Log.e(TAG, "++ closeConnection ++");
		try {
			/*
			 * The use of && prohibits isConnected() to be invoked if btSocket
			 * is null.
			 */
			if (btSocket != null) {
				btSocket.close();
			}
			return Ref.RESULT_OK;
		} catch (Exception e) {
			if (D)
				Log.e(TAG, "Error closing btSocket: ", e);
			return Ref.RESULT_IO_EXCEPTION;
		}
	}

	// /**
	// * This method will unregister the BroadcastReceiver for ACTION_FOUND of
	// the
	// * Bluetooth device. The registration happen in startDiscovery().
	// *
	// * @param invokerActivity
	// * The reference to the invoking activity
	// */
	// public void unRegister_AdapterStateReceiver() {
	// if (D)
	// Log.e(TAG, "++ unRegister_AdapterStateReceiver ++");
	//
	// if (btStateIntentIsRegistered) {
	// applicationContext.unregisterReceiver(bt_foundDeviceReceiver);
	// Ref.bt_stateIntentIsRegistered = false;
	// }
	// }

	public boolean isBluetoothAdapterAvailable() {
		return btAdapter != null;
	}

	public void stopDiscovery() {
		if (D)
			Log.e(TAG, "++ stopDiscovery ++");

		// cancel any prior BT device discovery
		if (btAdapter.isDiscovering()) {
			btAdapter.cancelDiscovery();
		}
	}

	public static void enableAdapterNoUserInteraction() {
		if (D)
			Log.e(TAG, "++ enableAdapterNoUserInteraction ++");
		btAdapter.enable();
	}

	public boolean enableAdapter() {
		if (D)
			Log.e(TAG, "++ enableAdapter ++");
		if (btAdapter != null)
			Log.e(TAG, "btAdapter != null");
		if (!btAdapter.isEnabled()) {
			if (D)
				Log.d(TAG, "enabling adapter");
			Intent enableBtIntent = new Intent(
					BluetoothAdapter.ACTION_REQUEST_ENABLE);
			Ref.activeActivity.startActivityForResult(enableBtIntent,
					REQUEST_ENABLE_BT);
		}
		int state = btAdapter.getState();
		return state == BluetoothAdapter.STATE_TURNING_ON
				|| state == BluetoothAdapter.STATE_ON;
	}

	public boolean isEnabled() {
		return btAdapter.isEnabled();
	}

	public boolean disableAdapter() {
		if (D)
			Log.e(TAG, "++ disableAdapter ++");
		if (btAdapter.getState() == BluetoothAdapter.STATE_OFF) {
			return true;
		} else {
			btAdapter.disable();
		}
		int state = btAdapter.getState();
		return state == BluetoothAdapter.STATE_TURNING_OFF
				|| state == BluetoothAdapter.STATE_OFF;
	}

	public void makeDiscoverable(MainActivity invokerActivity) {
		if (D)
			Log.e(TAG, "++ makeDiscoverable ++");
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
	public static void setFoundDevice(BluetoothDevice device) {
		if (!foundDevices.contains(device)) {
			foundDevices.add(device);
		}
	}

	// CONNECTION STATE SETTERS AND GETTERS

	public boolean isConnected() {
		return connectionState == Ref.STATE_CONNECTED;
	}

	public boolean isConnecting() {
		return connectionState == Ref.STATE_CONNECTING;
	}

	public void setConnecting() {
		connectionState = Ref.STATE_CONNECTING;
	}

	public void setConnected() {
		Log.e(TAG, "++ setConnected ++");
		connectionState = Ref.STATE_CONNECTED;
	}

	public void setDisconnected() {
		connectionState = Ref.STATE_NOT_CONNECTED;

	}

	// ===================================
}
