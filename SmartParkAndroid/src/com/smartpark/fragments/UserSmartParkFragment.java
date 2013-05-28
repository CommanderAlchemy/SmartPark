package com.smartpark.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.smartpark.R;
import com.smartpark.activities.MainActivity;

/**
 * SmartParkFragment, this holds the general page of our application
 * @author commander
 *
 */
public class UserSmartParkFragment extends Fragment  {
	
	// Debug Information, the boolean should be stored in a common settings file in the future!
	private static boolean D = MainActivity.D;
	private static final String TAG = "SmartParkFragment";
	
	// This needs some fixing for code cleanup
	public static final String ARG_SECTION_NUMBER = "section_number";

	public UserSmartParkFragment() {
		if (D)
			Log.d(TAG, "Fragment: " + this.toString() + " Loaded");
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.i(TAG, "++ onCreateView ++");

		View rootView = inflater.inflate(R.layout.frag_smartpark_view,container, false);
		return rootView;
	}

	@Override
	public void onResume() {
		super.onResume();
	}
	
	
	
}
