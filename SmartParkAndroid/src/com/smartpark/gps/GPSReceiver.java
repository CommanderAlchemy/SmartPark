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

public class GPSReceiver extends BroadcastReceiver{
	
	private static final boolean D = Ref.D;
	private static final String TAG = "GPSReceiver";
	
	private String gpsinfo;
	private TextView gps_text;
	
	// ============================================
	
	public GPSReceiver(TextView gps_text){
		this.gps_text = gps_text;
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		if (action.equals(LocationManager.KEY_LOCATION_CHANGED)){
			Log.w(TAG, "-----------------");
		}
			
		gpsinfo = intent.getStringExtra("GPSCOORDINATES");
		Toast.makeText(context , gpsinfo, Toast.LENGTH_SHORT).show();
		gps_text.setText(gpsinfo);
	}
	
	public String getGPSinfo(){
		return gpsinfo;
	}

}
