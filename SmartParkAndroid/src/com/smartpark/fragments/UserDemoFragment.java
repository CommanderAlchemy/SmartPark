package com.smartpark.fragments;

import java.util.Calendar;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.smartpark.R;
import com.smartpark.activities.MainActivity;
import com.smartpark.background.BackgroundOperationThread;
import com.smartpark.background.Ref;
import com.smartpark.gps.GPSReceiver;

/**
 * BluetoothFragment, this holds the bluetooth side of the project and will show
 * bluetooth specific information
 * 
 * @author commander
 * 
 */
public class UserDemoFragment extends Fragment {

	// Debug Information, the boolean should be stored in a common settings file
	// in the future!
	private static boolean D = MainActivity.D;
	private static SharedPreferences mainPreference;
	private static final String TAG = "DemoFragment";

	private View.OnClickListener onClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			Log.e(TAG, "++ onClick ++");
			if (v instanceof Button) {
				switch (v.getId()) {
				case R.id.btnTogglePark:
					myVib.vibrate(50);
					
					
					break;
				default:
					break;
				}
			}
		}
	};
	private Vibrator myVib;
	
	public UserDemoFragment() {
		if (D)
			Log.d(TAG, "Fragment: " + this.toString() + " Loaded");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.i(TAG, "++ onCreateView ++");
		View rootView = inflater.inflate(R.layout.frag_demo_view, container,
				false);
		
		myVib = (Vibrator) getActivity().getSystemService(
				Activity.VIBRATOR_SERVICE);
		
		mainPreference = getActivity().getSharedPreferences("MainPreference", Context.MODE_PRIVATE);
		
		return rootView;
	}
	
	
	
	public static boolean startParkDemo(String licensePlate, String carModel, String deviceID) {
		Log.e(TAG, "++ startPark ++");

		String demoPark = "DemoPark;";
		String ssNbr = mainPreference.getString("ssNbr", "error");
		if (!ssNbr.equals("error")) {
			Location location = GPSReceiver.getLocation();
			if (location == null) {
				return false;
			}
			
			Log.e(TAG, "location: " + location.getLatitude() + " " + location.getLongitude());
			
			Calendar cal = Calendar.getInstance();
			long startTimestamp = cal.getTimeInMillis();
			
			// DemoPark;ssNbr:55.3452324:26.3423423:2342133424:0:ADT-435:Renault:0:0:deviceID
			// param-size = 9
			demoPark += ssNbr + ":" + location.getLatitude() + ":"
					+ location.getLongitude() + ":" + startTimestamp + ":0:"
					+ licensePlate + ":" + carModel + ":0:0:" + deviceID;

			Log.e(TAG, "--> Send parking request:\n" + demoPark);
			
			BackgroundOperationThread.startParkString = demoPark.split(";")[1].split(":");

			mainPreference.edit().putString("StartPark", demoPark).apply();

			BackgroundOperationThread.sendByTCP(demoPark);
			return true;
		} else {
			Toast.makeText(Ref.activeActivity, "Error,  please login again",
					Toast.LENGTH_LONG).show();
			return false;
		}
	}
	


}// ============================================================================
