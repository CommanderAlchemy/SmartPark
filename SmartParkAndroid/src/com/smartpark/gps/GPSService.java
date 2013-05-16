package com.smartpark.gps;

import com.smartpark.background.Ref;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class GPSService extends Service {

	private LocationManager locationManager;
	private static final int LOCATION_INTERVAL = 10;
	private static final float LOCATION_DISTANCE = 1f;

	private static final boolean D = Ref.D;
	private static final String TAG = "GPSService";

	LocationListener[] mLocationListeners = new LocationListener[] {
			new LocationListener(LocationManager.GPS_PROVIDER),
			new LocationListener(LocationManager.NETWORK_PROVIDER) };

	// ==========================================================

	/**
	 * Creates a LocationManager and request gps-coordinates using the method
	 * requestLocationUpdates. It tries to use NETWORK_PROVIDER and GPS_PROVIDER
	 */
	@Override
	public void onCreate() {
		super.onCreate();
		Log.i(TAG, "++ onCreate ++: ");

		if (locationManager == null) {
			locationManager = (LocationManager) getApplicationContext()
					.getSystemService(Context.LOCATION_SERVICE);
		}

		try {
			locationManager.requestLocationUpdates(
					LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL,
					LOCATION_DISTANCE, mLocationListeners[1]);
		} catch (java.lang.SecurityException ex) {
			Log.e(TAG, "fail to request location update, ignore", ex);
		} catch (IllegalArgumentException ex) {
			Log.e(TAG, "network provider does not exist, " + ex);
		}
		
		try {
			locationManager.requestLocationUpdates(
					LocationManager.GPS_PROVIDER, LOCATION_INTERVAL,
					LOCATION_DISTANCE, mLocationListeners[0]);
		} catch (java.lang.SecurityException ex) {
			Log.e(TAG, "fail to request location update, ignore", ex);
		} catch (IllegalArgumentException ex) {
			Log.e(TAG, "gps provider does not exist " + ex);
		}
	}// ==========================================================

	/**
	 * Called when an Activity calls <tt>stopService<tt>
	 */
	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.i(TAG, "++ onDestroy ++: ");

		Toast.makeText(this, "GPS-service ended", Toast.LENGTH_SHORT).show();
		if (locationManager != null) {
			for (int i = 0; i < mLocationListeners.length; i++) {
				try {
					locationManager.removeUpdates(mLocationListeners[i]);
				} catch (Exception ex) {
					Log.i(TAG, "fail to remove location listners, ignore", ex);
				}
			}
		}
	}// ==========================================================

	/**
	 * Called when an Activity calls <tt>startService()<tt>
	 */
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);
		Log.i(TAG, "++ onStartCommand ++: ");

		Toast.makeText(this, "GPS-service started", Toast.LENGTH_SHORT).show();
		return START_STICKY;
	}// ==========================================================

	/**
	 * Isn't used for this service
	 */
	public IBinder onBind(Intent arg0) {
		return null;
	}

	private class LocationListener implements android.location.LocationListener {

		private static final int HALF_MINUTES = 1000 * 30; // 30 seconds
		Location mLastLocation;

		public LocationListener(String provider) {
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
			
			gpsinfo.putExtra("GPSCOORDINATES", "Latitide " + latitude
					+ " Longitude " + longitude);
			sendBroadcast(gpsinfo);
			Log.d(TAG, "Latitude: " + latitude + " Longitude: " + longitude);
			String Text = "Latitude = " + latitude + "\nLongitud = "
					+ longitude;

			Toast.makeText(getApplicationContext(), Text, Toast.LENGTH_SHORT)
					.show();
			Log.i(TAG, "++ onLocationChanged done ++: ");

		}// ==========================================================

		@Override
		public void onProviderDisabled(String provider) {
			Log.i(TAG, "++ onProviderDisabled ++ : " + provider);
		}// ==========================================================
		
		@Override
		public void onProviderEnabled(String provider) {
			Log.i(TAG, "++ onProviderEnabled ++ : " + provider);
		}// ==========================================================

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			Log.i(TAG, "++ onStatusChanged ++ : " + provider + " " + status + " " + extras);
		}// ==========================================================

		/**
		 * Determines whether one Location reading is better than the current
		 * Location fix
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
