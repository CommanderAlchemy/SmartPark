package com.smartpark.fragments;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.smartpark.R;
import com.smartpark.activities.MainActivity;
import com.smartpark.background.BackgroundOperationThread;

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

	// Initialize the array for listview
	static ArrayList<String> list = new ArrayList<String>();
	private ListView controllerList;
	private Button btnRefresh;
	private static ArrayAdapter<String> arrayAdapter;

	private View.OnClickListener onClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			Log.e(TAG, "sdfsdf");
			Log.e(TAG, "++ onClick ++");

			BackgroundOperationThread.sendByTCP("CheckPark;0:0");

		}
	};

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
		btnRefresh = (Button) rootView.findViewById(R.id.btnRefresh);
		controllerList = (ListView) rootView.findViewById(R.id.controllerList);

		arrayAdapter = new ArrayAdapter<String>(getActivity(),
				android.R.layout.simple_list_item_1, list);
		controllerList.setAdapter(arrayAdapter);

		list.add("Hello there controller");
		arrayAdapter.notifyDataSetChanged();
		btnRefresh.setOnClickListener(onClickListener);
		return rootView;
	}

	@Override
	public void onResume() {
		super.onResume();
		if (arrayAdapter == null) {

			arrayAdapter = new ArrayAdapter<String>(getActivity(),
					android.R.layout.simple_list_item_1, list);
			controllerList.setAdapter(arrayAdapter);
			btnRefresh.setOnClickListener(onClickListener);
		}
	}

	public static void setControllerList(String str) {
		Log.e(TAG, "++ requestParkedCars ++");
		// TODO remove old cars from list.
		list.add(str);
		arrayAdapter.notifyDataSetChanged();
	}

}
