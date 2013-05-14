package com.smartpark.bluetooth;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import com.smartpark.MainActivity;
import com.smartpark.background.Ref;
import com.smartpark.broadcastReceivers.BT_FoundDeviceReceiver;
import com.smartpark.broadcastReceivers.BT_StateReceiver;

public class BlueController {
	/*
	 * Since there is only one bluetooth adapter in every handheld device, the
	 * variables are defined using the static modifier, because they only
	 * represent settings and findings of the same adapter. However, they are
	 * all private to this class. All public class-variables are moved to
	 * Ref.java to avoid code duplication.
	 */

	// Flags
	private static boolean BroacastReceiverIsRegistered = false;

	// Class-instances used locally
	private static BT_FoundDeviceReceiver bt_foundDeviceReceiver;
	private static BT_StateReceiver bt_stateReceiver;

	private static IntentFilter bt_findFilter;
	private static IntentFilter bt_stateFilter;
	private static IntentFilter bt_connectionStateFilter;

	private static ArrayList<BluetoothDevice> foundDevices = new ArrayList<BluetoothDevice>();
	private static Set<BluetoothDevice> pairedDevices = null;

	// Constants used locally
	private static final UUID SerialPort = UUID
			.fromString("00001101-0000-1000-8000-00805F9B34FB");

	// Debugging and stuff
	private static final String TAG = "BlueController";
	private static final boolean D = Ref.D;

	// -------------------------------------------------------------------------------

	public BlueController() {
		Log.d(TAG, "++ Constructor: BlueController ++");

		// Get the adapter and store it in a static variable
		// This initializes the class
		Ref.btAdapter = BluetoothAdapter.getDefaultAdapter();
		/*
		 * Create a filter so that we only receive intent for events that we are
		 * interested in.
		 */
		bt_findFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		bt_stateFilter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
		bt_connectionStateFilter = new IntentFilter(
				BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED);

		// Create a BroadcastReceiver for ACTION_FOUND
		bt_foundDeviceReceiver = new BT_FoundDeviceReceiver();
		bt_stateReceiver = new BT_StateReceiver();

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
		Ref.activeActivity.unregisterReceiver(bt_foundDeviceReceiver);
		Ref.activeActivity.unregisterReceiver(bt_stateReceiver);

	}

	/**
	 * This method will first register a BroadcastReceiver for intents carrying
	 * new devices found by the bluetooth adapter, and then start the device
	 * discovery of bluetooth adapter. The method startDiscovery() is
	 * Asynchronous and will quickly return a boolean for whether or not the
	 * discovery started successfully. After this method returns, the found
	 * devices can be received by invoking getFoundDevices().
	 */
	public boolean findNearbyDevices(Activity invokerActivity) {
		Log.d("tag", "++ findNearbyDevices ++");

		// This makes a broadcast receiver to register our adapter's findings
		// but only if not already registered.
		if (!BroacastReceiverIsRegistered) {
			// Register the BroadcastReceiver
			invokerActivity.registerReceiver(bt_foundDeviceReceiver,
					bt_findFilter);
			/*
			 * We do not want duplicated registrations and use variable to store
			 * the state of the registration.
			 */
			BroacastReceiverIsRegistered = true;
			// Don't forget to unregister during onDestroy
		}
		return Ref.btAdapter.startDiscovery();
	}

	public Set<BluetoothDevice> getPairedDevicesList() {
		Log.d(TAG, "++ getPairedDevicesList ++");

		if (!Ref.btAdapter.isEnabled()) {
			Log.d(TAG, "adapter not enabled");
		} else {
			pairedDevices = Ref.btAdapter.getBondedDevices();
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
		Log.d(TAG, "++ getPairedDeviceByName ++");

		Log.d(TAG, "--> Getting BluetoothDevice for: " + name);
		BluetoothDevice device;
		Set<BluetoothDevice> h = getPairedDevicesList();
		Iterator<BluetoothDevice> iter = h.iterator();
		while (iter.hasNext()) {
			device = iter.next();
			if (device.getName().equals(name)) {
				return device;
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
		Log.d("tag", "++ getFoundDeviceByName ++");

		BluetoothDevice device;
		Iterator<BluetoothDevice> iter;
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
	public ArrayList<BluetoothDevice> getFoundDevices() {
		Log.d("tag", "++ getFoundDevices ++");

		return foundDevices;
	}

	/**
	 * This method aims at connecting to the device that is stored as
	 * BluetoothDevice in Ref.java
	 */
	public void connect() {
		Log.e(TAG, "++ connect ++");

		Ref.btState = Ref.STATE_CONNECTING;

		// new Thread() {
		//
		// public void run() {
		try {
			// fa87c0d0-afac-11de-8a39-0800200c9a66
			// 00001101-0000-1000-8000-00805F9B34FB
			Ref.btSocket = Ref.btDevice
					.createRfcommSocketToServiceRecord(SerialPort);
			try {
				// This will only return on a successful connection
				// or an exception
				Ref.btController.cancelDiscovery();
				Ref.btSocket.connect();
				/* Next line not needed after implementing BroadcastReceiver for
				 * it. */
				// Ref.btState = Ref.STATE_CONNECTED;
			} catch (IOException e) {
				// Close the socket upon error
				try {
					Log.e(TAG, "Connection Exception: ", e);
					Ref.btSocket.close();
				} catch (IOException e2) {
					Log.e(TAG, "Socket Close Exception: " + e2);
				}
				Ref.btState = Ref.STATE_NOT_CONNECTED;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.e(TAG, "Socket init Exception: " + e);
			Ref.btState = Ref.STATE_NOT_CONNECTED;
		}

		try {
			Log.d(TAG, "--> Init btSocket I/O Streams");
			Ref.btInStream = Ref.btSocket.getInputStream();
			Ref.btOutStream = Ref.btSocket.getOutputStream();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.e(TAG, "Socket I/O Streams Exception" + e);
			Ref.btState = Ref.STATE_NOT_CONNECTED;
		}
	}

	public void sendString(ArrayList<String> data) {
		Log.d("tag", "++ sendString ++");
		// TODO
	}

	public boolean isDiscovering(BluetoothAdapter sd) {
		Log.d("tag", "++ isDiscovering ++");

		return Ref.btAdapter.isDiscovering();
	}

	/**
	 * This method will unregister the BroadcastReceiver for ACTION_FOUND of the
	 * Bluetooth device. The registration happen in StartDiscovery().
	 */
	public void unRegister_DeviceFoundReceiver(Activity invokerActivity) {
		Log.d("tag", "++ unRegister_DeviceFoundReceiver ++");

		invokerActivity.unregisterReceiver(bt_foundDeviceReceiver);
		BroacastReceiverIsRegistered = false;
	}

	/**
	 * This method will unregister the BroadcastReceiver for ACTION_FOUND of the
	 * Bluetooth device. The registration happen in StartDiscovery().
	 */
	public void unRegister_AdapterStateReceiver(Activity invokerActivity) {
		Log.d("tag", "++ unRegister_AdapterStateReceiver ++");

		invokerActivity.unregisterReceiver(bt_foundDeviceReceiver);
		BroacastReceiverIsRegistered = false;
	}
	
	public boolean isBluetoothAdapterAvailable() {
		return Ref.btAdapter != null;
	}
	
	public void cancelDiscovery() {
		Log.d("tag", "++ cancelDiscovery ++");

		// cancel any prior BT device discovery
		if (Ref.btAdapter.isDiscovering()) {
			Ref.btAdapter.cancelDiscovery();

		}
	}

	public void enableAdapterNoUserInteraction() {
		Log.d("tag", "++ enableAdapterNoUserInteraction ++");

		Ref.btAdapter.enable();
	}

	public boolean enableAdapter(MainActivity mainActivity) {
		Log.d("tag", "++ enableAdapter ++");
		if (!Ref.btAdapter.isEnabled()) {
			Log.d("TAG", "enabling adapter");
			Intent enableBtIntent = new Intent(
					BluetoothAdapter.ACTION_REQUEST_ENABLE);
			// startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
			mainActivity.startActivityForResult(enableBtIntent,
					Ref.REQUEST_ENABLE_BT);
		}
		int state = Ref.btAdapter.getState();
		return state == BluetoothAdapter.STATE_TURNING_ON
				|| state == BluetoothAdapter.STATE_ON;
	}

	public boolean disableAdapter() {
		Log.d("tag", "++ disableAdapter ++");
		if (Ref.btAdapter.getState() == BluetoothAdapter.STATE_OFF) {
			return true;
		} else {
			Ref.btAdapter.disable();
		}
		int state = Ref.btAdapter.getState();
		return state == BluetoothAdapter.STATE_TURNING_OFF
				|| state == BluetoothAdapter.STATE_OFF;
	}

	public void makeDiscoverable(MainActivity invokerActivity) {
		Log.d("tag", "++ makeDiscoverable ++");
		Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
		intent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 120);
		invokerActivity.startActivityForResult(intent,
				Ref.REQUEST_DISCOVERABLE_BT);
	}

	// // ================
	// // INTERNAL CLASSES
	// // ================
	//
	// private class BT_FoundDeviceReceiver extends BroadcastReceiver {
	//
	// public void onReceive(Context context, Intent intent) {
	// // may need to chain this to a recognizing function
	// String action = intent.getAction();
	// if (BluetoothDevice.ACTION_FOUND.equals(action)) {
	// // Get the BluetoothDevice object from the Intent
	// BluetoothDevice device = intent
	// .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
	// // Add the found device to the foundDevices ArrayList
	// foundDevices.add(device);
	// }
	// }
	// }

	public void setFoundDevice(BluetoothDevice device) {

		foundDevices.add(device);
	}

}
