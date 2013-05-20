package com.smartpark.background;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.preference.PreferenceManager.OnActivityResultListener;
import android.util.Log;
import android.widget.Toast;

import com.smartpark.broadcastReceivers.BTAdapterStateReceiver;
import com.smartpark.broadcastReceivers.BTFoundDeviceReceiver;

public class BackOperationService extends Service {

	// Create a BroadcastReceiver for ACTION_FOUND
	private static BTFoundDeviceReceiver btFoundDeviceReceiver = new BTFoundDeviceReceiver();
	private static BTAdapterStateReceiver btAdapterStateReceiver = new BTAdapterStateReceiver();
	/*
	 * Create a filter so that we only receive intent for events that we are
	 * interested in.
	 */
	private static IntentFilter btFoundDeviceFilter = new IntentFilter(
			BluetoothDevice.ACTION_FOUND);
	private static IntentFilter btAdapterStateFilter = new IntentFilter(
			BluetoothAdapter.ACTION_STATE_CHANGED);
	private static IntentFilter btConnectionStateFilter = new IntentFilter(
			BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED);

	// GLOBAL APPLICATION STATE FLAGS
	private static boolean btFindIntentIsRegistered;
	private static boolean btStateIntentIsRegistered;
	private static boolean btConnectionStateIntentIsRegistered;
	private static boolean gpsReceiverIsRegistered;

	private Context applicationContext;

	private String TAG = "BackOperationService";
	private boolean D = Ref.D;

	// ============ END OF CLASS-VARIABLES ===========================

	/**
	 * We do not wish to enable binding and return a null as an interface to
	 * inhibit interprocess communication with this service.
	 */
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate(); // Not needed
		Log.i(TAG, "++ onCreate ++");
		
		applicationContext = getApplicationContext();
		Toast.makeText(applicationContext, "Service started",
				Toast.LENGTH_SHORT).show();
		Ref.bgThread = new BackgroundOperationThread(applicationContext);
		Ref.bgThread.start();
	}

	@Override
	public void onDestroy() {
		super.onDestroy(); // this is not needed in service
		Log.i(TAG, "++ onDestroy ++");
		// We first stop the thread
		// We invoke all the cleanUp()-method of the different classes if they
		// exist
		// We then clean this Service-resources if any
		// We set everything in Ref.java to null

		// Unregister all BroadcastReceivers that are registered
		if (btFindIntentIsRegistered) {
			unregisterReceiver(btFoundDeviceReceiver);
			btFindIntentIsRegistered = false;
		}
		if (btStateIntentIsRegistered) {
			unregisterReceiver(btAdapterStateReceiver);
			btStateIntentIsRegistered = false;
		}
		if (btConnectionStateIntentIsRegistered) {
			unregisterReceiver(btAdapterStateReceiver);
			btConnectionStateIntentIsRegistered = false;
		}
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);
		Log.i(TAG, "++ onStartCommand ++");

		// These are all unregistered in onDestroy
		// Register a receiver for found BT-devices
		registerReceiver(btFoundDeviceReceiver, btFoundDeviceFilter);
		btFindIntentIsRegistered = true;
		// Register a receiver for BT-adapter state changes
		registerReceiver(btAdapterStateReceiver, btAdapterStateFilter);
		btStateIntentIsRegistered = true;
		// Register a receiver for BT-connection changes
		registerReceiver(btAdapterStateReceiver, btConnectionStateFilter);
		btConnectionStateIntentIsRegistered = true;

		

		return START_STICKY;
	}

	public class ty implements OnActivityResultListener {

		@Override
		public boolean onActivityResult(int requestCode, int resultCode,
				Intent data) {
			Log.e(TAG,
					"%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%\n%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");

			// TODO Auto-generated method stub
			return false;
		}

	}

	public void onActivityResult() {

	}

}
