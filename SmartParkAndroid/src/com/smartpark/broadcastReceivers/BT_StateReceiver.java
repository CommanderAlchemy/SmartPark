package com.smartpark.broadcastReceivers;

import com.smartpark.background.Ref;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.widget.Toast;

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
				giveWarningDialog();
				break;
			case BluetoothAdapter.STATE_ON:
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
				Ref.btState = Ref.STATE_NOT_CONNECTED;
				break;
			case BluetoothAdapter.STATE_CONNECTED:
				Ref.btState = Ref.STATE_CONNECTED;
				break;
			case BluetoothAdapter.STATE_CONNECTING:
				Ref.btState = Ref.STATE_CONNECTING;
				break;
			case BluetoothAdapter.STATE_DISCONNECTING:
				Ref.btState = Ref.STATE_DISCONNECTING;
				break;
			default:
				Ref.btState = Ref.STATE_NOT_CONNECTED;
			}
		}
	}
	
	private void giveWarningDialog(){
		AlertDialog.Builder builder1 = new AlertDialog.Builder(Ref.activeActivity);
		builder1.setTitle("Problem");
		builder1.setMessage("You shouldn't disable Bluetooth while running this application.\n\n"
		+"Do you wish to reenable?");
		builder1.setCancelable(true);
		builder1.setNegativeButton(android.R.string.no,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						Toast.makeText(Ref.activeActivity, "Bluetooth remained disable", 
								Toast.LENGTH_SHORT).show();
					}
				});
		builder1.setPositiveButton(android.R.string.yes,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						Ref.btController.enableAdapterNoUserInteraction();
					}
				});
		AlertDialog alert = builder1.create();
		alert.show();
	}
}

