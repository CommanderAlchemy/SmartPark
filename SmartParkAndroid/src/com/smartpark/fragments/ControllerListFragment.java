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
 * DebugFragment, this will contain some debug information needed during this
 * development.
 * 
 * @author commander
 * 
 */
public class ControllerListFragment extends Fragment {

	// Debug Information, the boolean should be stored in a common settings file
	// in the future!
	private static boolean D = MainActivity.D;
	private static final String TAG = "ListFragment";

	// This needs some fixing for code cleanup
	public static final String ARG_SECTION_NUMBER = "section_number";

	public ControllerListFragment() {
		if (D)
			Log.d(TAG, "Fragment: " + this.toString() + " Loaded");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.i(TAG, "++ onCreateView ++");
		View rootView = inflater.inflate(R.layout.frag_cont_list_view,
				container, false);

		return rootView;
	}
	
	private void requestParkedCars() {
		Log.e(TAG, "++ requestParkedCars ++");

		/*
		 * TODO implement Query method from the database. Parking Data!
		 * 
		 * Query;date:date
		 */
		
	}
	
}
