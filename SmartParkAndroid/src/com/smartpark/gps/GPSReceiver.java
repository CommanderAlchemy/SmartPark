package com.smartpark.gps;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.TextView;
import android.widget.Toast;

public class GPSReceiver extends BroadcastReceiver{
	
	private String gpsinfo;
	private TextView t;
	
	public GPSReceiver(TextView t){
		this.t = t;
	}
	
	public void onReceive(Context context, Intent intent) {
		gpsinfo = intent.getStringExtra("GPSCOORDINATES");
		Toast.makeText(context , gpsinfo, Toast.LENGTH_SHORT).show();
		t.setText(gpsinfo);
	}
	
	public String getGPSinfo(){
		return gpsinfo;
	}

}
