package com.smartpark.gps;

import com.smartpark.background.Ref;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

public class GPSReceiver extends BroadcastReceiver {

	private static final boolean D = Ref.D;
	private static final String TAG = "GPSReceiver";

	private String gpsinfo;
	private TextView gps_text;
	private int invoke = 0;

	// ============================================

	public GPSReceiver(TextView gps_text) {
		this.gps_text = gps_text;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();

		gpsinfo = intent.getStringExtra("GPSCOORDINATES");
		if (action.equals("com.smartpark.gpsinfo")) {
			gpsinfo = intent.getStringExtra("GPSCOORDINATES");

			Toast.makeText(context, gpsinfo, Toast.LENGTH_SHORT).show();

			if (gps_text != null) {
				gps_text.setText(gpsinfo);
			} else {
				Log.e(TAG, "gpsinfo == null");
			}
		}
		Log.e(TAG, "" + invoke);
		invoke++;
	}

	public String getGPSinfo() {
		return gpsinfo;
	}

}
