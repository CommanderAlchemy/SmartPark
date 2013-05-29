package com.smartpark.gps;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.smartpark.activities.MainActivity;
import com.smartpark.background.Ref;

public class GPSService extends Service {

	public static boolean gpsReceiverIsRegistered;

	private LocationManager locationManager;

	private static final int LOCATION_INTERVAL = 100; // Millisecond
	private static final float LOCATION_DISTANCE = 1f; // meter

	private static final boolean D = MainActivity.D;
	private static final String TAG = "GPSService";

	// used in GPS-fragment class
	// private TextView gps_text;
	private GPSReceiver gpsReceiver;
	
	OurLocationListener gpsLocationListener = new OurLocationListener(
			LocationManager.GPS_PROVIDER);
	OurLocationListener networkLocationListener = new OurLocationListener(
			LocationManager.NETWORK_PROVIDER);

	// ==========================================================

	/**
	 * Creates a LocationManager and request gps-coordinates using the method
	 * requestLocationUpdates. It tries to use NETWORK_PROVIDER and GPS_PROVIDER
	 */
	@Override
	public void onCreate() {
		super.onCreate(); // Not needed
		if(D)Log.i(TAG, "++ onCreate ++: ");
		
		if (locationManager == null) {
			locationManager = (LocationManager) getApplicationContext()
					.getSystemService(Context.LOCATION_SERVICE);
		}

		try {
			locationManager.requestLocationUpdates(
					LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL,
					LOCATION_DISTANCE, networkLocationListener);
		} catch (java.lang.SecurityException ex) {
			if(D)Log.e(TAG, "fail to request location update", ex);
		} catch (IllegalArgumentException ex) {
			if(D)Log.e(TAG, "network provider does not exist, " + ex);
		}

		try {
			locationManager.requestLocationUpdates(
					LocationManager.GPS_PROVIDER, LOCATION_INTERVAL,
					LOCATION_DISTANCE, gpsLocationListener);
		} catch (java.lang.SecurityException ex) {
			if(D)Log.e(TAG, "fail to request location update", ex);
		} catch (IllegalArgumentException ex) {
			if(D)Log.e(TAG, "gps provider does not exist " + ex);
		}
		
		gpsReceiver = new GPSReceiver();
	}// ==========================================================

	/**
	 * Called when an Activity calls <tt>stopService<tt>
	 */
	@Override
	public void onDestroy() {
		super.onDestroy(); // this is not needed in service
		if(D)Log.i(TAG, "++ onDestroy ++: ");
		
		// Disable receiving of new updates from providers
		if (locationManager != null) {
			try {
				locationManager.removeUpdates(networkLocationListener);
				locationManager.removeUpdates(gpsLocationListener);
			} catch (Exception ex) {
				if(D)Log.e(TAG, "fail to remove location listners", ex);
			}
		}

		try {
			if (gpsReceiverIsRegistered) {
				unregisterReceiver(gpsReceiver);
				gpsReceiverIsRegistered = false;
			}
		} catch (Exception e) {
			if(D)Log.e(TAG, "unregistration failed", e);
			gpsReceiverIsRegistered = false;
		}
	}

	// ==========================================================

	/**
	 * Called when an Activity calls <tt>startService()<tt>
	 */
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		/*
		 * Invoking superclass method are not needed in Services, but we make a
		 * habit out of it, cause it's needed almost everywhere else.
		 */
		super.onStartCommand(intent, flags, startId);
		if(D)Log.i(TAG, "++ onStartCommand ++: ");

		/*
		 * The intent is not used here because this service only has one
		 * purpose, thus need no command.
		 */

		/*
		 * Registers the receiver that is to receive broadcasts from this
		 * service. It is unregistered in onDestroy of this service. This
		 * service is started by the BackOperationService.
		 */
		if (!gpsReceiverIsRegistered) {
			try {
				registerReceiver(gpsReceiver, new IntentFilter(
						"com.smartpark.gpsinfo"));
				gpsReceiverIsRegistered = true;
			} catch (Exception e) {
				if(D)Log.e(TAG, "registration failed", e);
				gpsReceiverIsRegistered = false;
			}
		}

		Toast.makeText(this, "GPS-service started", Toast.LENGTH_SHORT).show();
		if(D)Log.i(TAG, "GPS-service started");

		/*
		 * In case this service is stopped by the system due to lack of
		 * resources, it will be restarted by the system when resources are
		 * available again, if this method returns START_STICKY to the system.
		 */
		return START_STICKY;
	}// ==========================================================

	/**
	 * We do not wish to enable binding and return a null as an interface to
	 * inhibit interprocess communication with this service.
	 */
	public IBinder onBind(Intent arg0) {
		return null;
	}

	private class OurLocationListener implements
			android.location.LocationListener {
		/*
		 * this is used when determining whether the new location is better than
		 * current.
		 */
//		private static final int HALF_MINUTES = 1000 * 30; // 30 seconds
//		Location mLastLocation;

		// ==========================================================

		public OurLocationListener(String provider) {
			// if(D)Log.e(TAG, "LocationListener " + provider);
//			mLastLocation = new Location(provider);
		}// ==========================================================

		@Override
		public void onLocationChanged(Location location) {
			if(D)Log.i(TAG, "++ onLocationChanged ++: ");
			
			Intent gpsIntent = new Intent("com.smartpark.gpsinfo");
			gpsIntent.putExtra("location", location);
			
			sendBroadcast(gpsIntent);
		}// ==========================================================

		@Override
		public void onProviderDisabled(String provider) {
			if(D)Log.i(TAG, "++ onProviderDisabled ++ : " + provider);
			/*
			 * Alert the user that the GPS-provider is disabled and this should
			 * be enabled.
			 */
			AlertDialog.Builder builder1 = new AlertDialog.Builder(
					Ref.activeActivity);
			builder1.setTitle("GPS Disabled!");
			builder1.setMessage("GPS is disabled and this is vital for the operation of this application.\n\n"
					+ "Do you wish to enable it now?");
			builder1.setCancelable(false);
			builder1.setNegativeButton("No",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
						}
					});
			builder1.setPositiveButton("Yes",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							Ref.activeActivity.startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
						}
					});
			AlertDialog alert = builder1.create();
			alert.show();
		}// ==========================================================

		@Override
		public void onProviderEnabled(String provider) {
			if(D)Log.i(TAG, "++ onProviderEnabled ++ : " + provider);
			Toast.makeText(Ref.activeActivity,"++ onProviderEnabled ++ : " + provider, Toast.LENGTH_SHORT)
					.show();
		}// ==========================================================

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			if(D)Log.i(TAG, "++ onStatusChanged ++ : " + provider + " " + status
					+ " " + extras);
			
		}// ==========================================================

		
	}
}
