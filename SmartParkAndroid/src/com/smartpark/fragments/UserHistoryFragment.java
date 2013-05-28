package com.smartpark.fragments;

import java.util.Calendar;
import java.util.HashMap;

import android.app.Activity;
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
import com.smartpark.background.Ref;

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
	private static boolean D = Ref.D;
	private static final String TAG = "HistoryFragment";

	private HashMap<String, View> viewReferences = new HashMap<String, View>(20);

	private Vibrator myVib;

	private DatePickerFragment datePickerFromDate;
	private DatePickerFragment datePickerToDate;

	// CODES
	public static final int BUTTON_FROM_DATE = 1;
	public static final int BUTTON_TO_DATE = 2;

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

		// === CREATE REFERENCE FOR ALL VIEWS IN FRAGMENT ===========
		//@formatter:off
		int[] viewIds = new int[]
				{R.id.btnFromDate,R.id.btnToDate};
		String[] viewKeys = new String[]
				{"btnFromDate","btnToDate"};
		//@formatter:on
		View view;
		Log.w(TAG, "------------------- 1");
		for (int i = 0; i < viewIds.length; i++) {

			view = rootView.findViewById(viewIds[i]);

			if (view instanceof Button) {
				view.setOnClickListener(onClickListener);
			}
			viewReferences.put(viewKeys[i], view);
		}
		// === REFERENCES CREATED =======================================
		Log.w(TAG, "------------------- 2");
		if (datePickerFromDate == null) {
			Log.e(TAG, "++ onStart ++ datePickerFromDate == null");
			datePickerFromDate = new DatePickerFragment(this);
			datePickerToDate = new DatePickerFragment(this);

			Calendar cal = Calendar.getInstance();
			cal.get(Calendar.YEAR);
			cal.get(Calendar.MONTH);
			cal.get(Calendar.DAY_OF_MONTH);
			int[] date = { cal.get(Calendar.DAY_OF_MONTH),
					cal.get(Calendar.MONTH), cal.get(Calendar.YEAR) };
			datePickerFromDate.setDate(date);
			datePickerToDate.setDate(date);

		} else {
			if (savedInstanceState != null) {
				OnClickBtnDateEvent(savedInstanceState.getIntArray("FromDate"),
						BUTTON_FROM_DATE);
				OnClickBtnDateEvent(savedInstanceState.getIntArray("ToDate"),
						BUTTON_TO_DATE);
			} else {
				OnClickBtnDateEvent(datePickerFromDate.getDate(),
						BUTTON_FROM_DATE);
				OnClickBtnDateEvent(datePickerToDate.getDate(), BUTTON_TO_DATE);
			}
		}

		Log.w(TAG, "------------------- 3");

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
		Log.i(TAG, "++ OnClickBtnDateEvent ++");
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
		Log.w(TAG, "------------------- 4");

		Log.w(TAG, "-----" + tag);

		switch (tag) {
		case BUTTON_FROM_DATE:
			int[] toDate = datePickerToDate.getDate();
			Log.w(TAG, "------------------- x");
			otherDate.set(toDate[2], toDate[1], toDate[0]);
			Log.w(TAG, "------------------- 5");

			switch (newCal.compareTo(otherDate)) {
			case 1:
				break;
			default:
				((Button) viewReferences.get("btnFromDate"))
						.setText(pickedDate);
				error = false;
			}
			break;
		case BUTTON_TO_DATE:
			int[] fromDate = datePickerFromDate.getDate();
			otherDate.set(fromDate[2], fromDate[1], fromDate[0]);
			Log.w(TAG, "------------------- 6");

			switch (newCal.compareTo(otherDate)) {
			case -1:
				break;
			default:
				((Button) viewReferences.get("btnToDate")).setText(pickedDate);
				error = false;
			}
			break;
		}
		if (error)
			Toast.makeText(getActivity(), "From date is newer than To date",
					Toast.LENGTH_LONG).show();

	}

	private void requestParkedCars() {
		Log.e(TAG, "++ requestParkedCars ++");

		/*
		 * TODO implement Query method from the database. Parking Data!
		 * 
		 * Query;date:date
		 */
	}

	@Override
	public void onStart() {
		super.onStart();
		Log.e(TAG, "++ onStart ++");

	}

	@Override
	public void onResume() {
		super.onResume();
		Log.e(TAG, "++ onResume ++");

//		if (datePickerFromDate == null) {
//			datePickerFromDate = new DatePickerFragment(this);
//			datePickerToDate = new DatePickerFragment(this);
//
//			Calendar cal = Calendar.getInstance();
//			cal.get(Calendar.YEAR);
//			cal.get(Calendar.MONTH);
//			cal.get(Calendar.DAY_OF_MONTH);
//			int[] date = { cal.get(Calendar.DAY_OF_MONTH),
//					cal.get(Calendar.MONTH), cal.get(Calendar.YEAR) };
//			datePickerFromDate.setDate(date);
//			datePickerToDate.setDate(date);
//
//		} else {
//			OnClickBtnDateEvent(datePickerFromDate.getDate(), BUTTON_FROM_DATE);
//			OnClickBtnDateEvent(datePickerToDate.getDate(), BUTTON_TO_DATE);
//		}
	}

	public void onDestroy() {
		super.onDestroy();
		Log.w(TAG, "++ onDestroy ++");
	}

}
