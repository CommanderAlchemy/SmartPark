package com.smartpark.background;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.smartpark.activities.MainActivity;
import com.smartpark.bluetooth.BlueController;
import com.smartpark.broadcastReceivers.BTAdapterStateReceiver;
import com.smartpark.broadcastReceivers.BTFoundDeviceReceiver;
import com.smartpark.gps.GPSService;
import com.smartpark.tcp.TCPController;

public class BackOperationService extends Service {

	// Create a BroadcastReceiver for ACTION_FOUND
	private static BTFoundDeviceReceiver btFoundDeviceReceiver;
	private static BTAdapterStateReceiver btAdapterStateReceiver;

	/*
	 * Create a filter so that we only receive intent for events that we are
	 * interested in.
	 */
	private static IntentFilter btFoundDeviceFilter = new IntentFilter(
			BluetoothDevice.ACTION_FOUND);
	private static IntentFilter btAdapterStateFilter = new IntentFilter(
			BluetoothAdapter.ACTION_STATE_CHANGED);
	private static IntentFilter btConnectionStateFilter = new IntentFilter(
			BluetoothDevice.ACTION_ACL_DISCONNECTED);

	// GLOBAL APPLICATION STATE FLAGS
	private static boolean btFindIntentIsRegistered = false;
	private static boolean btStateIntentIsRegistered = false;
	private static boolean btConnectionStateIntentIsRegistered = false;

	private Context applicationContext;

	private String TAG = "BackOperationService";
	private boolean D = MainActivity.D;

	private BlueController btController;
	private TCPController tcpController;
	private Handler handler;
	private BackgroundOperationThread bgThread;
	private SharedPreferences mainPreference;

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
		if (D)
			Log.e(TAG, "++ onCreate ++");

		startService(new Intent(getBaseContext(), GPSService.class));

		mainPreference = getSharedPreferences("MainPreference", MODE_PRIVATE);

		applicationContext = getApplicationContext();

		// -----------
		tcpController = new TCPController();
		btController = new BlueController(applicationContext);
		handler = new Handler(btController, tcpController, mainPreference);
		bgThread = new BackgroundOperationThread(applicationContext, this,
				btController, tcpController, handler, mainPreference);
		handler.setBackgroundThread(bgThread);

		btFoundDeviceReceiver = new BTFoundDeviceReceiver();
		btAdapterStateReceiver = new BTAdapterStateReceiver(this);

		// -----------
	}

	@Override
	public void onDestroy() {
		super.onDestroy(); // this is not needed in service
		if (D)
			Log.e(TAG, "++ onDestroy ++");

		// We first stop the thread
		// We invoke all the cleanUp()-method of the different classes if they
		// exist
		// We then clean this Service-resources if any
		// We set everything in Ref.java to null

		bgThread.powerDown();

		// Unregister all BroadcastReceivers that are registered
		if (btFindIntentIsRegistered) {
			unregisterReceiver(btFoundDeviceReceiver);
			btFindIntentIsRegistered = false;
		}
		if (btStateIntentIsRegistered || btConnectionStateIntentIsRegistered) {
			unregisterReceiver(btAdapterStateReceiver);
			btStateIntentIsRegistered = false;
			btConnectionStateIntentIsRegistered = false;
		}

		stopService(new Intent(getBaseContext(), GPSService.class));
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);
		if (D)
			Log.e(TAG, "++ onStartCommand ++");

		if (!btFindIntentIsRegistered) {
			// These are all unregistered in onDestroy
			// Register a receiver for found BT-devices
			registerReceiver(btFoundDeviceReceiver, btFoundDeviceFilter);
			btFindIntentIsRegistered = true;
		}
		if (!btStateIntentIsRegistered) {
			// Register a receiver for BT-adapter state changes
			registerReceiver(btAdapterStateReceiver, btAdapterStateFilter);
			btStateIntentIsRegistered = true;
		}
		if (!btConnectionStateIntentIsRegistered) {
			// Register a receiver for BT-connection changes
			registerReceiver(btAdapterStateReceiver, btConnectionStateFilter);
			btConnectionStateIntentIsRegistered = true;
		}

		if (bgThread == null) {
			bgThread = new BackgroundOperationThread(applicationContext, this,
					btController, tcpController, handler, mainPreference);

			bgThread.start();
		} else {
			if (!bgThread.isRunning()) {
				try {
					Thread.currentThread();
					Thread.sleep(1000);
				} catch (InterruptedException e1) {
				}
				if (!bgThread.isRunning()) {
					bgThread.start();
				}
			}
		}

		if (D)
			Log.i(TAG, "--- Service action= " + intent.getAction());
		
		boolean restart = false;
		if (intent != null) {
			restart = intent.getBooleanExtra("Restart", false);
		}
		if (restart) {
			Ref.activeActivity.finish();

			Intent i = new Intent(getApplicationContext(), MainActivity.class);
			i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(i);
		}
		return START_STICKY;
	}
}
