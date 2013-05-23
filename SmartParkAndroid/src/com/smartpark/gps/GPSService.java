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

import com.smartpark.background.Ref;

public class GPSService extends Service {

	public static boolean gpsReceiverIsRegistered;

	private LocationManager locationManager;

	private static final int LOCATION_INTERVAL = 100; // Millisecond
	private static final float LOCATION_DISTANCE = 1f; // meter

	private static final boolean D = Ref.D;
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
		Log.i(TAG, "++ onCreate ++: ");

		if (locationManager == null) {
			locationManager = (LocationManager) getApplicationContext()
					.getSystemService(Context.LOCATION_SERVICE);
		}

		try {
			locationManager.requestLocationUpdates(
					LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL,
					LOCATION_DISTANCE, networkLocationListener);
		} catch (java.lang.SecurityException ex) {
			Log.e(TAG, "fail to request location update", ex);
		} catch (IllegalArgumentException ex) {
			Log.e(TAG, "network provider does not exist, " + ex);
		}

		try {
			locationManager.requestLocationUpdates(
					LocationManager.GPS_PROVIDER, LOCATION_INTERVAL,
					LOCATION_DISTANCE, gpsLocationListener);
		} catch (java.lang.SecurityException ex) {
			Log.e(TAG, "fail to request location update", ex);
		} catch (IllegalArgumentException ex) {
			Log.e(TAG, "gps provider does not exist " + ex);
		}

		gpsReceiver = new GPSReceiver(null);
	}// ==========================================================

	/**
	 * Called when an Activity calls <tt>stopService<tt>
	 */
	@Override
	public void onDestroy() {
		super.onDestroy(); // this is not needed in service
		Log.i(TAG, "++ onDestroy ++: ");
		
		Toast.makeText(this, "GPS-service ended", Toast.LENGTH_SHORT).show();

		// Disable receiving of new updates from providers
		if (locationManager != null) {
			try {
				locationManager.removeUpdates(networkLocationListener);
				locationManager.removeUpdates(gpsLocationListener);
			} catch (Exception ex) {
				Log.e(TAG, "fail to remove location listners", ex);
			}
		}

		try {
			if (gpsReceiverIsRegistered) {
				unregisterReceiver(gpsReceiver);
				gpsReceiverIsRegistered = false;
			}
		} catch (Exception e) {
			Log.e(TAG, "unregistration failed", e);
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
		Log.i(TAG, "++ onStartCommand ++: ");

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
				Log.e(TAG, "registration failed", e);
				gpsReceiverIsRegistered = false;
			}
		}

		Toast.makeText(this, "GPS-service started", Toast.LENGTH_SHORT).show();
		Log.i(TAG, "GPS-service started");

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
		private static final int HALF_MINUTES = 1000 * 30; // 30 seconds
		Location mLastLocation;

		// ==========================================================

		public OurLocationListener(String provider) {
			// Log.e(TAG, "LocationListener " + provider);
			mLastLocation = new Location(provider);
		}// ==========================================================

		@Override
		public void onLocationChanged(Location location) {
			Log.i(TAG, "++ onLocationChanged ++: " + location);
			if (isBetterLocation(location, mLastLocation)) {
				mLastLocation.set(location);
			}

			double latitude = mLastLocation.getLatitude();
			double longitude = mLastLocation.getLongitude();
			Intent gpsinfo = new Intent("com.smartpark.gpsinfo");

			gpsinfo.putExtra("location", location);
			
			sendBroadcast(gpsinfo);

			Log.d(TAG, "Latitude: " + latitude + " Longitude: " + longitude);

			Toast.makeText(getApplicationContext(),
					"Latitude = " + latitude + "\nLongitud = " + longitude,
					Toast.LENGTH_SHORT).show();

		}// ==========================================================

		@Override
		public void onProviderDisabled(String provider) {
			Log.i(TAG, "++ onProviderDisabled ++ : " + provider);
			Toast.makeText(Ref.activeActivity,"++ onProviderDisabled ++ : " + provider,Toast.LENGTH_SHORT).show();
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
			Log.i(TAG, "++ onProviderEnabled ++ : " + provider);
			Toast.makeText(Ref.activeActivity,"++ onProviderEnabled ++ : " + provider, Toast.LENGTH_SHORT)
					.show();
		}// ==========================================================

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			Log.i(TAG, "++ onStatusChanged ++ : " + provider + " " + status
					+ " " + extras);
			Toast.makeText(getApplicationContext(),
					"++ onStatusChanged ++ : " + provider, Toast.LENGTH_SHORT)
					.show();
		}// ==========================================================

		/**
		 * Determines whether the newly received location is better than the
		 * current location fix.
		 * 
		 * @param location
		 *            The new Location that you want to evaluate
		 * @param currentBestLocation
		 *            The current Location fix, to which you want to compare the
		 *            new one
		 */
		protected boolean isBetterLocation(Location location,
				Location currentBestLocation) {
			if (currentBestLocation == null) {
				// A new location is always better than no location
				return true;
			}

			// Check whether the new location fix is newer or older
			long timeDelta = location.getTime() - currentBestLocation.getTime();
			boolean isSignificantlyNewer = timeDelta > HALF_MINUTES;
			boolean isSignificantlyOlder = timeDelta < -HALF_MINUTES;
			boolean isNewer = timeDelta > 0;

			// If it's been more than two minutes since the current location,
			// use the new location because the user has likely moved.
			if (isSignificantlyNewer) {
				return true;
				// If the new location is more than two minutes older, it must
				// be worse
			} else if (isSignificantlyOlder) {
				return false;
			}

			// Check whether the new location fix is more or less accurate
			int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation
					.getAccuracy());
			boolean isLessAccurate = accuracyDelta > 0;
			boolean isMoreAccurate = accuracyDelta < 0;
			boolean isSignificantlyLessAccurate = accuracyDelta > 200;

			// Check if the old and new location are from the same provider
			boolean isFromSameProvider = isSameProvider(location.getProvider(),
					currentBestLocation.getProvider());

			// Determine location quality using a combination of timeliness and
			// accuracy
			if (isMoreAccurate) {
				return true;
			} else if (isNewer && !isLessAccurate) {
				return true;
			} else if (isNewer && !isSignificantlyLessAccurate
					&& isFromSameProvider) {
				return true;
			}
			return false;
		}// ==========================================================

		/** Checks whether two providers are the same */
		private boolean isSameProvider(String provider1, String provider2) {
			if (provider1 == null) {
				return provider2 == null;
			}
			return provider1.equals(provider2);
		}// ==========================================================
	}
}
