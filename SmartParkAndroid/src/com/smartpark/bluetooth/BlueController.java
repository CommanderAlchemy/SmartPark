package com.smartpark.bluetooth;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import com.smartpark.MainActivity;
import com.smartpark.Ref;

public class BlueController {
	/*
	 * Since there is only one bluetooth adapter in every handheld device, the
	 * variables are defined using the static modifier, because they only
	 * represent settings and findings of the same adapter.
	 */
	private static final int REQUEST_ENABLE_BT = 1;
	private static final int REQUEST_DISCOVERABLE_BT = 2;
	private static boolean BroacastReceiverIsRegistered = false;

	public static BluetoothAdapter btAdapter;

	private static MyBroadcastReceiver mReceiver;

	private static IntentFilter findFilter;

	private static ArrayList<BluetoothDevice> foundDevices;
	private static Set<BluetoothDevice> pairedDevices;

	private static final UUID MY_UUID = UUID
			.fromString("00001101-0000-1000-8000-00805F9B34FB");

	// Debugging and stuff
	private static final String TAG = "BlueController";
	private static final boolean D = Ref.D;

	// -------------------------------------------------------------------------------

	public BlueController() {
		// Get the adapter and store it in a static variable
		// This initializes the class
		btAdapter = BluetoothAdapter.getDefaultAdapter();

		// Create a BroadcastReceiver for ACTION_FOUND
		mReceiver = new MyBroadcastReceiver();
		/*
		 * Create a filter so that we only receive intent created by newly found
		 * devices
		 */
		findFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
	}// -------------------------------------------------------------------------------

	/**
	 * This method will first register a BroadcastReceiver for intents carrying
	 * new devices found by the bluetooth adapter, and then start the device
	 * discovery of bluetooth adapter. The method startDiscovery() is
	 * asyncronuos and will quickly return a boolean for wheather or not the
	 * discovery started successfully.
	 */
	public boolean findNearbyDevices(MainActivity invokerActivity) {
		// This makes a broadcast receiver to register our adapter's findings
		// but only if not already registered.
		if (!BroacastReceiverIsRegistered) {
			// Register the BroadcastReceiver
			invokerActivity.registerReceiver(mReceiver, findFilter);
			/*
			 * We do not want duplicated registrations and use variable to store
			 * the state of the registration.
			 */
			BroacastReceiverIsRegistered = true;
			// Don't forget to unregister during onDestroy
		}
		return btAdapter.startDiscovery();
	}

	public Set<BluetoothDevice> getPairedDevicesList() {
		if (!btAdapter.isEnabled()) {
			Log.d("new", "adapter not enabled");
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
		BluetoothDevice device;
		Set<BluetoothDevice> h = getPairedDevicesList();
		Iterator<BluetoothDevice> iter = h.iterator();
		while (iter.hasNext()) {
			device = iter.next();
			if (device.getName() == name) {
				return device;
			}
		}
		if (foundDevices.size() > 0) {
			iter = foundDevices.iterator();

			while (iter.hasNext()) {
				device = iter.next();
				if (device.getName() == name) {
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
		BluetoothDevice device;
		Iterator<BluetoothDevice> iter;
		if (foundDevices.size() > 0) {
			iter = foundDevices.iterator();
			while (iter.hasNext()) {
				device = iter.next();
				if (device.getName() == name) {
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
		return foundDevices;
	}

	public void connectAsynchroniouslyTo() {
		// TODO
		// start a thread to manage connection to a BluetoothDevice saved in
		// Ref.java

		Ref.btState = Ref.STATE_CONNECTING;
		new Thread() {

			public void run() {
				try {
					Ref.btSocket = Ref.btDevice
							.createRfcommSocketToServiceRecord(UUID
									.fromString("00001101-0000-1000-8000-00805F9B34FB"));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					Log.e(TAG, "Socket init Exception: " + e);
					e.printStackTrace();
				}

				try {
					// This is a blocking call and will only return on a
					// successful connection or an exception
					Ref.btSocket.connect();
				} catch (IOException e) {
					// Close the socket
					try {
						Log.e(TAG, "Connection Exception: ", e);
						Ref.btSocket.close();
					} catch (IOException e2) {
						Log.e(TAG, "Socket Close Exception: " + e2);
					}
					Ref.btState = Ref.STATE_NOT_CONNECTED;
				}
				try {
					Log.d(TAG, "Init btSocket I/O Streams");
					Ref.btInStream = Ref.btSocket.getInputStream();
					Ref.btOutStream = Ref.btSocket.getOutputStream();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					Log.e(TAG, "Socket I/O Streams Exception" + e);
				}
				Log.d(TAG, "BlueTooth Connection Successfull");
				Ref.btState = Ref.STATE_CONNECTED;
			}
		}.start();
	}

	public void sendString(ArrayList<String> data) {
		// TODO
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO
	}

	public boolean isDiscovering(BluetoothAdapter sd) {
		return btAdapter.isDiscovering();
	}

	/**
	 * This method will unregister the BroadcastReceiver for ACTION_FOUND of the
	 * Bluetooth device. The registration happen in StartDiscovery().
	 */
	public void unRegisterBroadcastReceiver(MainActivity invokerActivity) {
		invokerActivity.unregisterReceiver(mReceiver);
		BroacastReceiverIsRegistered = false;
	}

	public boolean isBluetoothAdapterAvailable() {
		return btAdapter != null;
	}

	public void cancelDiscovery() {
		// cancel any prior BT device discovery
		if (btAdapter.isDiscovering()) {
			btAdapter.cancelDiscovery();
		}
	}

	public void enableAdapterNoUserInteraction() {
		btAdapter.enable();
	}

	public boolean enableAdapter(MainActivity mainActivity) {
		Log.d("new2", "invoke enableAdapter");
		if (!btAdapter.isEnabled()) {
			Log.d("new2", "enabling adapter 2");
			Intent enableBtIntent = new Intent(
					BluetoothAdapter.ACTION_REQUEST_ENABLE);
			// startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
			mainActivity.startActivityForResult(enableBtIntent,
					REQUEST_ENABLE_BT);
		}
		return btAdapter.getState() == BluetoothAdapter.STATE_TURNING_ON;
	}

	public boolean disableAdapter() {
		if (btAdapter.getState() == BluetoothAdapter.STATE_OFF) {
			return true;
		} else {
			btAdapter.disable();
		}
		return btAdapter.getState() == BluetoothAdapter.STATE_TURNING_OFF;
	}

	public void makeDiscoverable(MainActivity invokerActivity) {
		if (!btAdapter.isEnabled()) {
			Intent enableBtIntent = new Intent(
					BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
			invokerActivity.startActivityForResult(enableBtIntent,
					REQUEST_DISCOVERABLE_BT);
		}
	}

	private class MyBroadcastReceiver extends BroadcastReceiver {

		public void onReceive(Context context, Intent intent) {
			// may need to chain this to a recognizing function
			String action = intent.getAction();
			if (BluetoothDevice.ACTION_FOUND.equals(action)) {
				// Get the BluetoothDevice object from the Intent
				BluetoothDevice device = intent
						.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				// Add the found device to the foundDevices ArrayList
				foundDevices.add(device);
			}
		}
	}

}
