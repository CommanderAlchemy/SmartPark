package com.smartpark.fragments;

import java.util.Calendar;
import java.util.Locale;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.smartpark.R;
import com.smartpark.background.Ref;

/**
 * GPSFragment, this holds the GPS side of the project and will show GPS
 * specific information
 * 
 * @author commander
 * 
 */
public class HistoryFragment extends Fragment {

	// Debug Information, the boolean should be stored in a common settings file
	// in the future!
	private static boolean D = Ref.D;
	private static final String TAG = "GPSFragment";
	private String date = null;
	private int day, year;
	private String month;
	Button btnFromDate;
	// This needs some fixing for code cleanup
	public HistoryFragment() {
		if (D)
			Log.i(TAG, "++ Fragment: " + this.toString() + " Loaded ++");

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.frag_history_view, container,
				false);
		
		
		
		final Calendar cal = Calendar.getInstance();
		day = cal.get(Calendar.DAY_OF_MONTH);
		year = cal.get(Calendar.YEAR);
		
		/* @formatter:off */
		switch (cal.get(Calendar.MONTH)) {
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
		date = day + " " + month + " " + year;
		
		
		((Button) rootView.findViewById(R.id.btnFromDate)).setText(date);
		((Button) rootView.findViewById(R.id.btnToDate)).setText(date);

		// DELETE THIS LATER
		// TextView dummyTextView = (TextView) rootView
		// .findViewById(R.id.section_label);
		// dummyTextView.setText(Integer.toString(getArguments().getInt(
		// ARG_SECTION_NUMBER)));

		return rootView;
	}

}
