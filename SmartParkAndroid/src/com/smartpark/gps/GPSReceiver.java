package com.smartpark.gps;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.util.Log;
import android.widget.TextView;

import com.smartpark.background.Ref;

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
		
		Location location = intent.getParcelableExtra("location");
		
		System.out.println(location.toString());
		
		if (action.equals("com.smartpark.gpsinfo")) {
			gpsinfo = intent.getStringExtra("GPS_COORDINATES");

		
		}
		Log.e(TAG, "" + invoke);
		invoke++;
	}

	public String getGPSinfo() {
		return gpsinfo;
	}

}
