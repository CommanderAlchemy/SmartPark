package com.smartpark.gps;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.util.Log;

import com.smartpark.background.Ref;

public class GPSReceiver extends BroadcastReceiver {

	private static final boolean D = Ref.D;
	private static final String TAG = "GPSReceiver";

	private PositionEMA positionEMA;

	// ============================================

	public GPSReceiver() {
		this.positionEMA = new PositionEMA(10);
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		if(D)Log.e(TAG, "++ onReceive ++");
		String action = intent.getAction();
		if (action.equals("com.smartpark.gpsinfo")) {
			Location location = intent.getParcelableExtra("location");
			positionEMA.put(location);
		}
	}
}
