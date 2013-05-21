package com.smartpark.broadcastReceivers;

import com.smartpark.background.Ref;
import com.smartpark.bluetooth.BlueController;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class BTAdapterStateReceiver extends BroadcastReceiver {

	// Debugging and stuff
	private static final String TAG = "BT_StateReceiver";
	private static final boolean D = Ref.D;
	
	BlueController btController;
	
	public BTAdapterStateReceiver(BlueController btController){
		super();
		this.btController = btController;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		final String action = intent.getAction();
		Log.e(TAG, "++ BTAdapterStateReceiver ++ onReceive ++ " + action);
		
		int event;
		if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
			// --> getIntExtra(int state, default int)
			event = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
			
			switch (event) {
			case BluetoothAdapter.STATE_OFF:
				break;
			case BluetoothAdapter.STATE_TURNING_OFF:
				giveWarningDialog();
				break;
			case BluetoothAdapter.STATE_ON:
				break;
			case BluetoothAdapter.STATE_TURNING_ON:
				break;
			default:
			}
			
		} else 
			
			if (action.equals(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED)) {
				Log.i(TAG, "BT State changed!");
			// getIntExtra(int state, default int)
			event = intent.getIntExtra(BluetoothAdapter.EXTRA_CONNECTION_STATE, BluetoothAdapter.ERROR);
			
			switch (event) {
			case BluetoothAdapter.STATE_DISCONNECTED:
				btController.setDisconnected();
				if(D) Log.d(TAG, "--> BT - STATE_NOT_CONNECTED");
				break;
			case BluetoothAdapter.STATE_CONNECTED:
				btController.setConnected();
				if(D) Log.d(TAG, "--> BT - STATE_CONNECTED");
				break;
			case BluetoothAdapter.STATE_CONNECTING:
				btController.setConnecting();
				if(D) Log.d(TAG, "--> BT - STATE_CONNECTING");
				break;
			case BluetoothAdapter.STATE_DISCONNECTING:
				btController.setDisconnected();
				if(D) Log.d(TAG, "--> BT - STATE_DISCONNECTING");
				break;
			default:
				btController.setDisconnected();
				if(D) Log.e(TAG, "--> BT - STATE_DISCONNECTING (default case = inconclusive)");
			}
		}
	}

	private void giveWarningDialog() {
		AlertDialog.Builder builder1 = new AlertDialog.Builder(
				Ref.activeActivity);
		builder1.setTitle("Problem");
		builder1.setMessage("You shouldn't disable Bluetooth while running this application.\n\n"
				+ "Do you wish to reenable?");
		builder1.setCancelable(true);
		builder1.setNegativeButton(android.R.string.no,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						Toast.makeText(Ref.activeActivity,
								"Bluetooth remained disable",
								Toast.LENGTH_SHORT).show();
					}
				});
		builder1.setPositiveButton(android.R.string.yes,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						BlueController.enableAdapterNoUserInteraction();
					}
				});
		AlertDialog alert = builder1.create();
		alert.show();
	}
}
