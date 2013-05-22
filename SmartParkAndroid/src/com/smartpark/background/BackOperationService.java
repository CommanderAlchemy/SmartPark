package com.smartpark.background;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.preference.PreferenceManager.OnActivityResultListener;
import android.util.Log;
import android.widget.Toast;

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
	private boolean D = Ref.D;
	
	BlueController btController;
	TCPController tcpController;
	
	

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

		startService(new Intent(getBaseContext(), GPSService.class));

		applicationContext = getApplicationContext();
		Toast.makeText(applicationContext, "Service started",
				Toast.LENGTH_SHORT).show();
		// -----------
		BlueController btController = new BlueController(applicationContext);
		TCPController tcpController = new TCPController();

		btFoundDeviceReceiver = new BTFoundDeviceReceiver(btController);
		btAdapterStateReceiver = new BTAdapterStateReceiver(btController);

		
		
		
		// -----------
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

		Ref.bgThread.powerDown();

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
		Log.i(TAG, "++ onStartCommand ++");

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
		
		
		if(Ref.bgThread == null){
			Ref.bgThread = new BackgroundOperationThread(applicationContext,
					btController, tcpController);
			Ref.bgThread.start();
			}else{
				if(!Ref.bgThread.isAlive()){
					Ref.bgThread.start();
				}
			}
			
		Log.i(TAG, "Service action= " + intent.getAction());
		
		String message = intent.getStringExtra("message to Service");
		
		// TODO do something with the message

		return START_STICKY;
	}
}
