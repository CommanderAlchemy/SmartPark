package com.smartpark.broadcastReceivers;

import com.smartpark.background.Ref;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BT_FoundDeviceReceiver extends BroadcastReceiver {
	
	@Override
	public void onReceive(Context context, Intent intent) {
		// may need to chain this to a recognizing function
		String action = intent.getAction();
		if (BluetoothDevice.ACTION_FOUND.equals(action)) {
			// Get the BluetoothDevice object from the Intent
			BluetoothDevice device = intent
					.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
			// Add the found device to the foundDevices ArrayList
			Ref.btController.setFoundDevice(device);
		}
	}
}
