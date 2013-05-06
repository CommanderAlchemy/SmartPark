package com.smartpark.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.smartpark.R;

/**
 * SmartParkFragment, this holds the general page of our application
 * @author commander
 *
 */
public class SmartParkFragment extends Fragment {

	// Debug Information, the boolean should be stored in a common settings file in the future!
	private static boolean D = true;
	private static final String TAG = "SmartParkFragment";
	
	// This needs some fixing for code cleanup
	public static final String ARG_SECTION_NUMBER = "section_number";

	public SmartParkFragment() {
		if (D)
			Log.e(TAG, "Fragment: " + this.toString() + " Loaded");
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_sp_view,
				container, false);
		TextView dummyTextView = (TextView) rootView
				.findViewById(R.id.section_label);
		TextView upperRow = new TextView(getActivity());
		
		
		dummyTextView.setText(Integer.toString(getArguments().getInt(
				ARG_SECTION_NUMBER)));
		return rootView;
	}
}