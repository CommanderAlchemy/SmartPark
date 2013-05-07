package com.smartpark.bluetooth;

import java.util.ArrayList;
import java.util.Set;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

public class BlueController extends Activity {
	/* Since there is only one bluetooth adapter in every handheld device,
	 * the variables are defined using the static modifier, because they only
	 * represent settings and findings of the same adapter.  */
	private static final int REQUEST_ENABLE_BT = 1;
	private static final int REQUEST_DISCOVERABLE_BT = 2;
	private static boolean BroacastReceiverIsRegistered = false;
	
	public static BluetoothAdapter btAdapter;
	private static MyBroadcastReceiver mReceiver;
	
	private static IntentFilter findFilter;

	private static ArrayList<BluetoothDevice> foundDevices;
	private static ArrayList<BluetoothDevice> pairedDevices;
	// -------------------------------------------------------------------------------
	
	
	public BlueController() {
		// Get the adapter and store it in a static variable
		// This initializes the class
		btAdapter = BluetoothAdapter.getDefaultAdapter();
		
		// Create a BroadcastReceiver for ACTION_FOUND
		mReceiver = new MyBroadcastReceiver();
		/* Create a filter so that we only receive intent created by newly found
		 *  devices */
		findFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
	}// -------------------------------------------------------------------------------
	
	
	


	/**
	 * This method will first register a BroadcastReceiver for intents carrying new
	 * devices found by the bluetooth adapter, and then start the device discovery of
	 * bluetooth adapter. The method startDiscovery() is asyncronuos and will quickly
	 * return a boolean for wheather or not the discovery started successfully.
	 */
	public boolean findNearbyDevices() {
		// This makes a broadcast receiver to register our adapter's findings
		// but only if not already registred.
		if(!BroacastReceiverIsRegistered){
			// Register the BroadcastReceiver
			registerReceiver(mReceiver, findFilter);
			/* We do not want dublicated registrations and use variable
			 * to store the state of the registration. */
			BroacastReceiverIsRegistered = true;
			// Don't forget to unregister during onDestroy
		}
		return btAdapter.startDiscovery();
	}// -------------------------------------------------------------------------------
	
	

	
	public ArrayList<BluetoothDevice> getPairedDevicesList() {
		Set<BluetoothDevice> paired = btAdapter.getBondedDevices();
		// If there are paired devices
		if (paired.size() > 0) {
			// Loop through paired devices
			for (BluetoothDevice device : paired) {
				// Add the devices to Arraylist
				// Use getName() and getAddress on each element
				pairedDevices.add(device);
			}
		}
		return pairedDevices;
	}// -------------------------------------------------------------------------------
	
	
	
	/**
	 * This method can be called whenever needed. It should however be used when
	 * the bluetooth adapter is discovering. The ArrayList, foundDevices, is populated
	 * by the onReceive-method of the 
	 * @return foundDevices These are the found devices so far
	 */
	public ArrayList<BluetoothDevice> getFoundDevices(){
		return foundDevices;
	}// -------------------------------------------------------------------------------
	
	
	
	public void connectTo(){
		// TODO 
	}
	
	public void sendString(ArrayList<String> data) {
		// TODO
	}// -------------------------------------------------------------------------------
	
	public void onActivityResult(int requestCode, int resultCode, Intent data){
		// TODO
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
	}// -------------------------------------------------------------------------------

	

	
	public boolean isDiscovering() {
		return btAdapter.isDiscovering();
	}// -------------------------------------------------------------------------------
	
	
	/**
	 * This method will unregister the BroadcastReceiver for ACTION_FOUND of the
	 * Bluetooth device. The registration happen in StartDiscovery().
	 */
	public void unRegisterBroadcastReceiver(){
		unregisterReceiver(mReceiver);
		BroacastReceiverIsRegistered = false;
	}// -------------------------------------------------------------------------------
	
	
	
	public boolean isBluetoothAdapterAvailable() {
		return btAdapter != null;
	}// -------------------------------------------------------------------------------
	
	
	
	public void cancelDiscovery() {
		// cancel any prior BT device discovery
		if (btAdapter.isDiscovering()) {
			btAdapter.cancelDiscovery();
		}
	}// -------------------------------------------------------------------------------
	

	
	public void enableAdapter() {
		if (!btAdapter.isEnabled()) {
		    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
		    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
		}
	}// -------------------------------------------------------------------------------
	
	
	public void makeDiscoverable(){
		if (!btAdapter.isEnabled()) {
		    Intent enableBtIntent = new Intent(
		    		BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
		    startActivityForResult(enableBtIntent, REQUEST_DISCOVERABLE_BT);
		}
	}// -------------------------------------------------------------------------------
	
	
	
	
	
	
	
	
}
