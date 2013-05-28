package com.smartpark.broadcastReceivers;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.smartpark.activities.MainActivity;
import com.smartpark.bluetooth.BlueController;

public class BTFoundDeviceReceiver extends BroadcastReceiver {

	private static final boolean D = MainActivity.D;
	private static final String TAG = "BT_FoundDeviceReceiver";


	public BTFoundDeviceReceiver() {
		// TODO Auto-generated constructor stub
	}


	@Override
	public void onReceive(Context context, Intent intent) {
		// may need to chain this to a recognizing function
		String action = intent.getAction();
		Log.e(TAG, "++ BTFoundDeviceReceiver ++ onReceive ++ " + action);
		
		if (BluetoothDevice.ACTION_FOUND.equals(action)) {
			// Get the BluetoothDevice object from the Intent
			BluetoothDevice device = intent
					.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
			// Add the found device to the foundDevices ArrayList
			if(D)Log.e(TAG, "insert" + device.getName());
			BlueController.addFoundDeviceTolist(device);
		}
	}
}
