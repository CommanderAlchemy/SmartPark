package com.smartpark.fragments;

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

import com.smartpark.R;
import com.smartpark.activities.MainActivity;
import com.smartpark.background.BackgroundOperationThread;
import com.smartpark.background.Ref;
import com.smartpark.background.SmartParkThread;

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
					if (BackgroundOperationThread.isParking()) {
						Toast.makeText(Ref.activeActivity,
								"Stopped parking...", Toast.LENGTH_SHORT)
								.show();
						boolean parking = BackgroundOperationThread.stopPark(
								"ADT-435", "Renault");

					} else {
						Toast.makeText(Ref.activeActivity,
								"Initiating Parking...", Toast.LENGTH_SHORT)
								.show();
						boolean parking = BackgroundOperationThread.startPark(
								"ADT-435", "Renault");
						if (!parking) {
							Toast.makeText(Ref.activeActivity,
									"Has no location!", Toast.LENGTH_SHORT)
									.show();
						}
					}
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
	private SmartParkThread screenThread;
	private Button btnPark;
	private TextView lblCurrentTime;
	private TextView lblGPS;
	private TextView lblBT;
	private TextView lblParkedSinceShow;
	private TextView lblDurationShow;
	private TextView lblPriceTillNowShow;
	private TextView lblFreeHoursShow;
	private TextView lblTicketHoursShow;
	private TextView lblPriceShow;
	private TextView lblTotalPriceShow;

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
						R.id.lblDurationShow,R.id.lblPriceTillNowShow,R.id.lblFreeHoursShow,
						R.id.lblTicketHoursShow,R.id.lblPriceShow,R.id.lblTotalPriceShow,R.id.btnTogglePark};
				String[] viewKeys = new String[]
						{"lblCurrentTime","lblGPS","lblBT","lblParkedSinceShow","lblDurationShow",
						"lblPriceTillNowShow","lblFreeHoursShow","lblTicketHoursShow","lblPriceShow",
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

		btnPark = (Button) viewReferences.get("btnTogglePark");
		lblCurrentTime = (TextView) viewReferences.get("lblCurrentTime");
		lblGPS = (TextView) viewReferences.get("lblGPS");
		lblBT = (TextView) viewReferences.get("lblBT");
		lblParkedSinceShow = (TextView) viewReferences
				.get("lblParkedSinceShow");
		lblDurationShow = (TextView) viewReferences.get("lblDurationShow");
		lblPriceTillNowShow = (TextView) viewReferences
				.get("lblPriceTillNowShow");
		lblFreeHoursShow = (TextView) viewReferences.get("lblFreeHoursShow");
		lblTicketHoursShow = (TextView) viewReferences
				.get("lblTicketHoursShow");
		lblPriceShow = (TextView) viewReferences.get("lblPriceShow");
		lblTotalPriceShow = (TextView) viewReferences.get("lblTotalPriceShow");

		myVib = (Vibrator) getActivity().getSystemService(
				Activity.VIBRATOR_SERVICE);

		mainPreference = getActivity().getSharedPreferences("MainPreference",
				Activity.MODE_PRIVATE);

		// isParking = mainPreference.getBoolean("isParking", false);
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

		screenThread = new SmartParkThread(viewReferences, this, mainPreference);
		screenThread.start();
	}

	public void onDestroy() {
		super.onDestroy();
		screenThread.stopThread();
	}

	public String convertMilisToTime(String millisString) {
		String time = "00:00";
		try {
			long millis = Long.parseLong(millisString);
			time = TimeUnit.MINUTES.toHours(TimeUnit.MINUTES.toMinutes(millis))
					+ ":" + TimeUnit.MILLISECONDS.toMinutes(millis);
			System.out.println(time);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return time;
	}

	public String convertMilisToTime(long millis) {
		String time = "00:00";
		try {
			time = TimeUnit.MINUTES.toHours(TimeUnit.MINUTES.toMinutes(millis))
					+ ":" + TimeUnit.MILLISECONDS.toMinutes(millis);
			System.out.println(time);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return time;
	}

	public void setbtnParkText(String string) {
		if (!btnPark.getText().equals(string)) {
			btnPark.setText(string);
		}
	}

	public void setLblGPSText(String string) {
		if (!lblGPS.getText().equals(string)) {
			lblGPS.setText(string);
		}
	}

	public void setLblBTText(String string) {
		if (!lblBT.getText().equals(string)) {
			lblBT.setText(string);
		}
	}

	public void setLblParkedSinceShowText(String string) {
		if (!lblParkedSinceShow.getText().equals(string)) {
			lblParkedSinceShow.setText(string);
		}
	}

	public void setLblDurationShowText(String string) {
		if (!lblDurationShow.getText().equals(string)) {
			lblDurationShow.setText(string);
		}
	}

	public void setlblPriceTillNowShowText(String string) {
		if (!lblPriceTillNowShow.getText().equals(string)) {
			lblPriceTillNowShow.setText(string);
		}
	}

	public void setlblFreeHoursShowText(String string) {
		if (!lblFreeHoursShow.getText().equals(string)) {
			lblFreeHoursShow.setText(string);
		}
	}

	public void setlblTicketHoursShowText(String string) {
		if (!lblTicketHoursShow.getText().equals(string)) {
			lblTicketHoursShow.setText(string);
		}
	}

	public void setlblPriceShowText(String string) {
		if (!lblPriceShow.getText().equals(string)) {
			lblPriceShow.setText(string);
		}
	}

	public void setlblTotalPriceShowText(String string) {
		if (!lblTotalPriceShow.getText().equals(string)) {
			lblTotalPriceShow.setText(string);
		}
	}

}
