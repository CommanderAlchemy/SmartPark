package com.smartpark.broadcastReceivers;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BT_StateReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		final String action = intent.getAction();
		int event;
		if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
			event = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
			switch (event) {
			case BluetoothAdapter.STATE_OFF:
				break;
			case BluetoothAdapter.STATE_TURNING_OFF:
				break;
			case BluetoothAdapter.STATE_ON:
				break;
			case BluetoothAdapter.STATE_TURNING_ON:
				break;
			default:
			}
		}else if(action.equals(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED)){
			event = intent.getIntExtra(BluetoothAdapter.EXTRA_CONNECTION_STATE, BluetoothAdapter.ERROR);
			switch (event) {
			case BluetoothAdapter.STATE_DISCONNECTED:
				break;
			case BluetoothAdapter.STATE_CONNECTED:
				break;
			case BluetoothAdapter.STATE_CONNECTING:
				break;
			case BluetoothAdapter.STATE_DISCONNECTING:
				break;
			default:
			}
		}
	}
}
