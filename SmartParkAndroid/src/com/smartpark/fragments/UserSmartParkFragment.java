package com.smartpark.fragments;

import java.util.HashMap;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.smartpark.R;
import com.smartpark.activities.MainActivity;

/**
 * SmartParkFragment, this holds the general page of our application
 * 
 * @author commander
 * 
 */
public class UserSmartParkFragment extends Fragment {

	// Debug Information, the boolean should be stored in a common settings file
	// in the future!
	private static boolean D = MainActivity.D;
	private static final String TAG = "SmartParkFragment";

	// This needs some fixing for code cleanup
	public static final String ARG_SECTION_NUMBER = "section_number";

	public UserSmartParkFragment() {
		if (D)
			Log.d(TAG, "Fragment: " + this.toString() + " Loaded");
	}

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
	private HashMap<String, View> viewReferences = new HashMap<String, View>(20);
	boolean isParking = false;
	String[] screenStrings = new String[8];
	String[] viewKeys;
	SharedPreferences mainPreference;
	protected boolean run = true;
	private Thread screenThread;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.i(TAG, "++ onCreateView ++");

		View rootView = inflater.inflate(R.layout.frag_smartpark_view,
				container, false);

		// === CREATE REFERENCE FOR ALL VIEWS IN FRAGMENT ===========
		//@formatter:off
				int[] viewIds = new int[]
						{R.id.lblCurrentTime,R.id.lblGPS,R.id.lblBT,R.id.lblParkedSinceShow,
						R.id.lblDurationShow,R.id.lblPriceNowShow,R.id.lblFreeTimeShow,
						R.id.lblHoursShow,R.id.lblPriceShow,R.id.lblTotalPriceShow};
				String[] viewKeys = new String[]
						{"lblCurrentTime","lblGPS","lblBT","lblParkedSinceShow","lblDurationShow",
						"lblPriceNowShow","lblFreeTimeShow","lblHoursShow","lblPriceShow",
						"lblTotalPriceShow"};
				//@formatter:on
		View view;
		for (int i = 0; i < viewIds.length; i++) {

			view = rootView.findViewById(viewIds[i]);

			if (view instanceof Button) {
				view.setOnClickListener(onClickListener);
			}
			System.out.println();
			viewReferences.put(viewKeys[i], view);
		}
		// === REFERENCES CREATED =======================================

		myVib = (Vibrator) getActivity().getSystemService(
				Activity.VIBRATOR_SERVICE);

		mainPreference = getActivity().getSharedPreferences("MainPreference",
				Activity.MODE_PRIVATE);

		isParking = mainPreference.getBoolean("isParking", false);
		screenStrings[0] = mainPreference.getString("price", "---");
		screenStrings[1] = mainPreference.getString("company", "---");
		screenStrings[2] = mainPreference.getString("smsQuery", "");
		screenStrings[3] = mainPreference.getString("ticketHours", "---");
		screenStrings[4] = mainPreference.getString("freeHours", "---");
		screenStrings[5] = mainPreference.getString("longtitude", "0");
		screenStrings[6] = mainPreference.getString("latitude", "0");
		screenStrings[7] = mainPreference.getString("parkID", "-1");

		return rootView;
	}

	@Override
	public void onResume() {
		super.onResume();

		screenThread = new Thread() {
			private boolean run = true;

			@Override
			public void run() {
				while (run) {

					if (isParking) {
						
//						((TextView) viewReferences.get()
//								.setText(screenStrings[i]);

					} else {
						((TextView) viewReferences.get("lblTotalPriceShow"))
								.setText(mainPreference.getString(
										"MonthlyTotal", "0"));
					}

				}
			}

			public void stopThread() {
				run = false;
			}
		};
		screenThread.start();
	}

	public void onDestroy() {
		super.onDestroy();
//		((ScreenThread) screenThread).stopThread();
	}

	private int convertMilisToMinut(String millis) {
		int minuts = -1;
		try {
			minuts = (int) (Long.parseLong(millis) / 60000);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return minuts;
	}
}
