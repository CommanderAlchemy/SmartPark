package com.smartpark.broadcastReceivers;

import com.smartpark.background.Ref;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

public class BT_StateReceiver extends BroadcastReceiver {
	
	
	@Override
	public void onReceive(Context context, Intent intent) {
		final String action = intent.getAction();
		int event;
		if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
			event = intent.getIntExtra(
					BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
			switch (event) {
			case BluetoothAdapter.STATE_OFF:
				break;
			case BluetoothAdapter.STATE_TURNING_OFF:
				break;
			case BluetoothAdapter.STATE_ON:
				AlertDialog.Builder builder1 = new AlertDialog.Builder(Ref.activeActivity);
				builder1.setTitle("Problem");
				builder1.setMessage("You shouldn't disable Bluetooth while running this application.\n\nPlease reenable...");
				builder1.setCancelable(true);
				builder1.setNegativeButton(android.R.string.no,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								// TODO Add your code for the button here.
							}
						});
				builder1.setNeutralButton("neutral",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								// TODO Add your code for the button here.
							}
						});
				builder1.setPositiveButton(android.R.string.ok,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								// TODO Add your code for the button here.
							}
						});
				AlertDialog alert = builder1.create();
				alert.show();
				break;
			case BluetoothAdapter.STATE_TURNING_ON:
				break;
			default:
			}
		}else if(action.equals(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED)){
			event = intent.getIntExtra(
					BluetoothAdapter.EXTRA_CONNECTION_STATE, BluetoothAdapter.ERROR);
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
