package com.smartpark.gps;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

import com.smartpark.R;

public class MainActivity2 extends Activity {

	TextView gps_text;
	GPSReceiver gpsReceiver;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		gps_text = (TextView) findViewById(R.id.gps_text);
		gpsReceiver = new GPSReceiver(gps_text);
		registerReceiver(gpsReceiver, new IntentFilter("GPSUPDATE"));
		gps_text.setText(gpsReceiver.getGPSinfo());
	}
	
	@Override
	protected void onPause() {
		unregisterReceiver(gpsReceiver);
		super.onPause();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public void startGPS(View view) {
		gps_text = (TextView) findViewById(R.id.gps_text);
		gps_text.setText("Starting");
		startService(new Intent(getBaseContext(), GPSService.class));
	}
	
	public void endGPS(View view) {
		gps_text = (TextView) findViewById(R.id.gps_text);
		gps_text.setText("Ending");
		stopService(new Intent(getBaseContext(), GPSService.class));
	}
	
}
