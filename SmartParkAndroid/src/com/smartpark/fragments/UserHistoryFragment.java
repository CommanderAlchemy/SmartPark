package com.smartpark.fragments;

import java.util.ArrayList;
import java.util.Calendar;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.smartpark.R;
import com.smartpark.activities.MainActivity;
import com.smartpark.background.BackgroundOperationThread;
import com.smartpark.background.HistoryThread;

/**
 * GPSFragment, this holds the GPS side of the project and will show GPS
 * specific information
 * 
 * @author commander
 * 
 */
public class UserHistoryFragment extends Fragment {

	/*
	 * Debug Information, the boolean should be stored in a common settings file
	 * in the future!
	 */
	private static boolean D = MainActivity.D;
	private static final String TAG = "HistoryFragment";

	private static HashMap<String, View> viewReferences = new HashMap<String, View>(
			20);

	private Vibrator myVib;

	private static DatePickerFragment datePickerFromDate;
	private static DatePickerFragment datePickerToDate;

	// CODES
	public static final int BUTTON_FROM_DATE = 1;
	public static final int BUTTON_TO_DATE = 2;

	// Initialize the array for listview
	static ArrayList<String> list = new ArrayList<String>();

	// String[] monthsArray = { "JAN", "FEB", "MAR", "APR", "MAY", "JUNE",
	// "JULY",
	// "AUG", "SEPT", "OCT", "NOV", "DEC" };

	// Declare the UI components
	private ListView historyListView;

	private static ArrayAdapter<String> arrayAdapter;

	private View.OnClickListener onClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			Log.e(TAG, "++ onClick ++");
			if (v instanceof Button) {
				switch (v.getId()) {
				case R.id.btnFromDate:
					myVib.vibrate(50);
					datePickerFromDate.show(getActivity().getFragmentManager(),
							"From Date");
					break;
				case R.id.btnToDate:
					myVib.vibrate(50);
					datePickerToDate.show(getActivity().getFragmentManager(),
							"To Date");
					break;
				default:
					break;
				}
			}

		}
	};
	private HistoryThread screenThread;
	private SharedPreferences mainPreference;
//	private ListView listViewHistory;
	//TODO check if crash^ 
	private Button btnToDate;
	private Button btnFromDate;

	// ----------------------------------------------------------------

	public UserHistoryFragment() {
		if (D)
			Log.e(TAG, "++ Fragment: " + this.toString() + " Loaded ++");
	}

	// ----------------------------------------------------------------
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.e(TAG, "++ onCreateView ++");

		View rootView = inflater.inflate(R.layout.frag_history_view, container,
				false);

		myVib = (Vibrator) getActivity().getSystemService(
				Activity.VIBRATOR_SERVICE);

		mainPreference = getActivity().getSharedPreferences("MainPreference",
				Activity.MODE_PRIVATE);

		// === CREATE REFERENCE FOR ALL VIEWS IN FRAGMENT ===========
		historyListView = (ListView) rootView.findViewById(R.id.listViewHistory);
		btnToDate = (Button) rootView.findViewById(R.id.btnToDate);
		btnToDate.setOnClickListener(onClickListener);
		btnFromDate = (Button) rootView.findViewById(R.id.btnFromDate);
		btnFromDate.setOnClickListener(onClickListener);
		// === REFERENCES CREATED =======================================
		
		//Arraylist
		arrayAdapter = new ArrayAdapter<String>(getActivity(),
				android.R.layout.simple_list_item_1, list);
		historyListView.setAdapter(arrayAdapter);
		
		
		if (datePickerFromDate == null) {
			datePickerFromDate = new DatePickerFragment(this);
			datePickerToDate = new DatePickerFragment(this);
		}
		if (savedInstanceState != null) {
			OnClickBtnDateEvent(savedInstanceState.getIntArray("FromDate"),
					BUTTON_FROM_DATE);
			OnClickBtnDateEvent(savedInstanceState.getIntArray("ToDate"),
					BUTTON_TO_DATE);
		} else {
			Calendar cal = Calendar.getInstance();
			cal.get(Calendar.YEAR);
			cal.get(Calendar.MONTH);
			cal.get(Calendar.DAY_OF_MONTH);
			int[] date = { cal.get(Calendar.DAY_OF_MONTH),
					cal.get(Calendar.MONTH), cal.get(Calendar.YEAR) };
			datePickerFromDate.setDate(date);
			datePickerToDate.setDate(date);

			OnClickBtnDateEvent(datePickerFromDate.getDate(), BUTTON_FROM_DATE);
			OnClickBtnDateEvent(datePickerToDate.getDate(), BUTTON_TO_DATE);
		}
		Log.e(TAG, "onCreateView ended");

		screenThread = new HistoryThread(viewReferences, this, mainPreference);
		screenThread.start();

		return rootView;
	}

	// ----------------------------------------------------------

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		Log.e(TAG, "++ onSaveInstanceState ++");
		outState.putIntArray("FromDate", datePickerFromDate.getDate());
		outState.putIntArray("ToDate", datePickerToDate.getDate());
		System.out.println(outState.getIntArray("FromDate").toString());
	};

	// ----------------------------------------------------------

	public void OnClickBtnDateEvent(int[] newDate, int tag) {
		Log.i(TAG, "++ OnClickBtnDateEvent ++" + newDate[1] + "tag" + tag);
		String month = null, pickedDate;
		boolean error = true;

		/* @formatter:off */
		switch (newDate[1]) {
		case 0:		month = "Jan"; 	break;
		case 1:		month = "Feb";	break;
		case 2:		month = "Mar";	break;
		case 3:		month = "Apr";	break;
		case 4:		month = "Maj";	break;
		case 5:		month = "Jun";	break;
		case 6:		month = "Jul";	break;
		case 7: 	month = "Aug";	break;
		case 8: 	month = "Sep";	break;
		case 9:		month = "Okt";	break;
		case 10:	month = "Nov";	break;
		case 11:	month = "Dec";	break;
		}
		/* @formatter:on */
		pickedDate = newDate[0] + " " + month + " " + newDate[2];
		Log.e(TAG, "" + pickedDate);

		Calendar newCal = Calendar.getInstance();
		newCal.set(newDate[2], newDate[1], newDate[0], 0, 0, 0);

		Calendar otherDate = Calendar.getInstance();
		otherDate.set(0, 0, 0, 0, 0, 0);
		switch (tag) {
		case BUTTON_FROM_DATE:
			int[] toDate = datePickerToDate.getDate();
			otherDate.set(toDate[2], toDate[1], toDate[0]);

			switch (newCal.compareTo(otherDate)) {
			case 1:
				break;
			default:
				btnFromDate.setText(pickedDate);
				error = false;
			}
			break;
		case BUTTON_TO_DATE:
			int[] fromDate = datePickerFromDate.getDate();
			otherDate.set(fromDate[2], fromDate[1], fromDate[0]);

			switch (newCal.compareTo(otherDate)) {
			case -1:
				break;
			default:
				btnToDate.setText(pickedDate);
				error = false;
			}
			break;
		}
		if (error)
			Toast.makeText(getActivity(), "From date is newer than To date",
					Toast.LENGTH_LONG).show();

	}
	
	
	
	
	
	
	public static void setHistory(String str){
		String[] message = str.split(":");
		
		
		list.add(str);
		arrayAdapter.notifyDataSetChanged();
	}

	public void getHistory() {
		BackgroundOperationThread.getHistory(
				datePickerFromDate.getDateInMillis(),
				datePickerToDate.getDateInMillis());
		arrayAdapter.notifyDataSetChanged();
	}

	public static void receptionBegon() {
		((ProgressBar) viewReferences.get(R.id.progressBarHistory))
		.setVisibility(View.VISIBLE);
	}
	
	public static void receiveDone() {
		((ProgressBar) viewReferences.get(R.id.progressBarHistory))
				.setVisibility(View.INVISIBLE);
	}

	private void setItemInListView(String parking) {
		// ((ListView)viewReferences.get(R.id.listViewHistory)).addI; TODO
	}

	@Override
	public void onStart() {
		super.onStart();
		Log.e(TAG, "++ onStart ++");
		// Do not use this. This won't run on orientation change
	}

	@Override
	public void onResume() {
		super.onResume();
		Log.e(TAG, "++ onResume ++");

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.w(TAG, "++ onDestroy ++");
	}

}
