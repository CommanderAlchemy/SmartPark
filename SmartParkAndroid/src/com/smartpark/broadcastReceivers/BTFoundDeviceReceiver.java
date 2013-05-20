package com.smartpark.broadcastReceivers;

import com.smartpark.background.Ref;
import com.smartpark.bluetooth.BlueController;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BTFoundDeviceReceiver extends BroadcastReceiver {

	private static final boolean D = Ref.D;
	private static final String TAG = "BT_FoundDeviceReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		// may need to chain this to a recognizing function
		String action = intent.getAction();
		Log.e(TAG, "-----------------------------");
		if (BluetoothDevice.ACTION_FOUND.equals(action)) {
			// Get the BluetoothDevice object from the Intent
			BluetoothDevice device = intent
					.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
			// Add the found device to the foundDevices ArrayList
			Log.e(TAG, "insert" + device.getName());
			BlueController.setFoundDevice(device);
		}
	}
}
