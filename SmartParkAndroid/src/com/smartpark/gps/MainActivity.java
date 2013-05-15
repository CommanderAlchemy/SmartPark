package com.smartpark.gps;

import com.smartpark.R;

import android.net.ConnectivityManager;
import android.os.Bundle;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	TextView t;
	GPSReceiver gpsReceiver;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
	}	
	
	@Override
    protected void onResume() {
        super.onResume();
        gpsReceiver = new GPSReceiver(t);
        registerReceiver(gpsReceiver, new IntentFilter("GPSUPDATE"));
//        TextView t = (TextView)findViewById(R.id.text_Hello);
//        t.setText(gpsReceiver.getGPSinfo());
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
	
	public void startGPS(View view){
		t = (TextView)findViewById(R.id.text_Hello);
		t.setText("Starting");
		startService(new Intent(getBaseContext(), GPSService.class));
	}
	
	public void endGPS(View view){
		t = (TextView)findViewById(R.id.text_Hello);
		t.setText("Ending");
		stopService(new Intent(getBaseContext(), GPSService.class));
	}
	
}
