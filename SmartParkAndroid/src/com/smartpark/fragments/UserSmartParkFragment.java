package com.smartpark.fragments;

import java.util.Calendar;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

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
import android.widget.Toast;

import com.smartpark.CustomThread;
import com.smartpark.R;
import com.smartpark.activities.MainActivity;
import com.smartpark.background.BackgroundOperationThread;
import com.smartpark.background.Ref;

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
					if(BackgroundOperationThread.isP)
					BackgroundOperationThread.startPark("ADT-435", "Renault");
					
					Toast.makeText(Ref.activeActivity, "Initiating Parking...", Toast.LENGTH_SHORT).show();
						
						

					Toast.makeText(Ref.activeActivity, "Stopped Parking...", Toast.LENGTH_SHORT).show();
					break;
				default:
					break;
				}
			}
		}
	};

	private Vibrator myVib;
	private HashMap<String, View> viewReferences = new HashMap<String, View>(20);
	
	String[] screenStrings = new String[8];
	String[] viewKeys;
	SharedPreferences mainPreference;
	protected boolean run = true;
	private Thread screenThread;

	// ==================================================
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
						R.id.lblHoursShow,R.id.lblPriceShow,R.id.lblTotalPriceShow,R.id.btnTogglePark};
				String[] viewKeys = new String[]
						{"lblCurrentTime","lblGPS","lblBT","lblParkedSinceShow","lblDurationShow",
						"lblPriceNowShow","lblFreeTimeShow","lblHoursShow","lblPriceShow",
						"lblTotalPriceShow","btnTogglePark"};
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

//		isParking = mainPreference.getBoolean("isParking", false);
		screenStrings[0] = mainPreference.getString("price", "---");
		screenStrings[1] = mainPreference.getString("company", "---");
		screenStrings[2] = mainPreference.getString("smsQuery", "---");
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
			private Button btnPark;
			private TextView lblCurrentTime;
			private TextView lblGPS;
			private TextView lblBT;
			private TextView lblParkedSinceShow;
			private TextView lblDurationShow;
			private TextView lblPriceNowShow;
			private TextView lblFreeTimeShow;
			private TextView lblHoursShow;
			private TextView lblPriceShow;
			private TextView lblTotalPriceShow;
			
			@Override
			public void run() {
				btnPark = (Button)viewReferences.get("btnTogglePark");
				lblCurrentTime = (TextView)viewReferences.get("lblCurrentTime");
				lblGPS = (TextView)viewReferences.get("lblGPS");
				lblBT = (TextView)viewReferences.get("lblBT");
				lblParkedSinceShow = (TextView)viewReferences.get("lblParkedSinceShow");
				lblDurationShow = (TextView)viewReferences.get("lblDurationShow");
				lblPriceNowShow = (TextView)viewReferences.get("lblPriceNowShow");
				lblFreeTimeShow = (TextView)viewReferences.get("lblFreeTimeShow");
				lblHoursShow = (TextView)viewReferences.get("lblHoursShow");
				lblPriceShow = (TextView)viewReferences.get("lblPriceShow");
				lblTotalPriceShow = (TextView)viewReferences.get("lblTotalPriceShow");
				
				while (run) {
					if (BackgroundOperationThread.parkingStarted()) {
						
//						while(!BackgroundOperationThread.parkingStarted()){
//							// Wait till response arrived from server
//							// TODO
//							try {
//								Thread.sleep(1000);
//							} catch (InterruptedException e) {
//								Log.w(TAG, "Error in sleep1: ", e);
//							}
//						}
						
						// TODO check to see if the parking-sequence have been aborted
						
						
						String parking = mainPreference.getString("StartPark", "0");
						String[] current_parking = parking.split(";")[1].split(":");
						// StartPark;xxxxxx:55.3452324:26.3423423:2342133424:0:ADT-435:Renault:0
						
						String parkedSince = convertMilisToTime(current_parking[3]);
						String duration = "dfsfs";
						String price = "dfsfs";
						String ticketTime = "dfsfs";
						String freeTime = "dfsfs";
						String priceTillNow = "";
						String totalThisMonth = mainPreference.getString("totalThisMonth", "0");
						
//						((TextView) viewReferences.get()
//								.setText(screenStrings[i]);

					} else {
						if(!btnPark.getText().equals("Park")){
							btnPark.setText("Park");
						}
						
//						((TextView) viewReferences.get("lblTotalPriceShow"))
//								.setText(mainPreference.getString(
//										"MonthlyTotal", "0"));
						
						
					}
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						Log.w(TAG, "Error in sleep2: ", e);
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
		((CustomThread) screenThread).stopThread();
	}

	protected String convertMilisToTime(String millisString) {
		String time = "00:00";
		try {
			long millis = Long.parseLong(millisString);
			time = TimeUnit.MINUTES.toHours(TimeUnit.MINUTES.toMinutes(millis)) + ":" +
					TimeUnit.MILLISECONDS.toMinutes(millis);
			System.out.println(time);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return time;
	}
}
