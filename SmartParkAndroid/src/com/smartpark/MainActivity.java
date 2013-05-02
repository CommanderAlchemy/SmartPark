package com.smartpark;

import java.util.Locale;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.smartpark.tcp.TCPClient;

public class MainActivity extends FragmentActivity implements
		ActionBar.TabListener {

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a
	 * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
	 * will keep every loaded fragment in memory. If this becomes too memory
	 * intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;

	/**
	 * Fragments, different views in application that you swype, instead of
	 * activities.
	 */
	Fragment fragment;

	private TCPClient mTcpClient;
	// Debugging and stuff
	private static final String TAG = "MainActivityDebug";
	private static final boolean D = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Set up the action bar.
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		// When swiping between different sections, select the corresponding
		// tab. We can also use ActionBar.Tab#select() to do this if we have
		// a reference to the Tab.
		mViewPager
				.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						actionBar.setSelectedNavigationItem(position);
					}
				});

		// For each of the sections in the app, add a tab to the action bar.
		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			// Create a tab with text corresponding to the page title defined by
			// the adapter. Also specify this Activity object, which implements
			// the TabListener interface, as the callback (listener) for when
			// this tab is selected.
			actionBar.addTab(actionBar.newTab()
					.setText(mSectionsPagerAdapter.getPageTitle(i))
					.setTabListener(this));
		}
	}

	/**
	 * Create ActionMenu with settings and add our own menu itmes.
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		CreateMenu(menu);
		return true;
	}

	/**
	 * On ActionMenu Select
	 * Do something when that get selected in the ActionMenu
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Debug stuff
		if (D) {
			Log.d(TAG, "Item: " + item.toString() + "\nID: " + item.getItemId()
					+ "\nIntent: " + item.getIntent());
		}

		// On select
		switch (item.getItemId()) {
		case 0:
			Toast.makeText(this, "You clicked on Login", Toast.LENGTH_SHORT)
					.show();
			startActivity(new Intent(this, LoginActivity.class));
			return true;

		case 1:
			Toast.makeText(this, "You clicked on Item 2", Toast.LENGTH_SHORT)
					.show();
			return true;

		case 2:
			Toast.makeText(this, "You clicked on Item 3", Toast.LENGTH_SHORT)
					.show();
			return true;

		case 3:
			Toast.makeText(this, "You clicked on Item 4", Toast.LENGTH_SHORT)
					.show();
			return true;

		default:
			Toast.makeText(this, "You clicked on Settings", Toast.LENGTH_SHORT)
					.show();
			startActivity(new Intent(this, SettingsActivity.class));
		}
		return false;
	}

	/**
	 * Create Menu Create Action Menu that the application will have to start
	 * other activities.
	 * 
	 * @param menu
	 */
	private void CreateMenu(Menu menu) {
		menu.setQwertyMode(true);
		MenuItem aMenu1 = menu.add(0, 0, 0, "Login");
		{
			aMenu1.setAlphabeticShortcut('a');
		}
		MenuItem aMenu2 = menu.add(0, 1, 1, "Item 2");
		{
			aMenu2.setAlphabeticShortcut('b');
		}
		MenuItem aMenu3 = menu.add(0, 2, 2, "Item 3");
		{
			aMenu3.setAlphabeticShortcut('c');
		}
		MenuItem aMenu4 = menu.add(0, 3, 3, "Item 4");
		{
			aMenu4.setAlphabeticShortcut('d');
		}
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
		// When the given tab is selected, switch to the corresponding page in
		// the ViewPager.
		mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		/**
		 * {@link Fragment} This sets the different fragment views.
		 */
		@Override
		public Fragment getItem(int position) {
			// getItem is called to instantiate the fragment for the given page.
			// Return a DummySectionFragment (defined as a static inner class
			// below) with the page number as its lone argument.
			Bundle args = new Bundle();

			switch (position) {

			case 0:
				fragment = new spFragment();
				args.putInt(debugFragment.ARG_SECTION_NUMBER, position + 1);
				fragment.setArguments(args);
				break;

			case 1:
				fragment = new gpsFragment();
				args.putInt(debugFragment.ARG_SECTION_NUMBER, position + 1);
				fragment.setArguments(args);
				break;

			case 2:
				fragment = new bluetoothFragment();
				args.putInt(debugFragment.ARG_SECTION_NUMBER, position + 1);
				fragment.setArguments(args);
				break;

			case 3:
				fragment = new debugFragment();
				args.putInt(debugFragment.ARG_SECTION_NUMBER, position + 1);
				fragment.setArguments(args);
				break;

			case 4:
				fragment = new DummySectionFragment();
				args.putInt(DummySectionFragment.ARG_SECTION_NUMBER,
						position + 1);
				fragment.setArguments(args);
				break;

			}
			return fragment;
		}

		/**
		 * Getcount This sets how many swipe pages you want to have in the
		 * application.
		 */
		@Override
		public int getCount() {
			// Show 5 total pages.
			return 5;
		}

		/**
		 * {@link Character} This sets the name on the different sections of the
		 * fragments.
		 */
		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				return getString(R.string.title_section1).toUpperCase(l);
			case 1:
				return getString(R.string.title_section2).toUpperCase(l);
			case 2:
				return getString(R.string.title_section3).toUpperCase(l);
			case 3:
				return getString(R.string.title_section4).toUpperCase(l);
			case 4:
				return getString(R.string.title_section5).toUpperCase(l);
			}
			return null;
		}
	}

	/**
	 * {@link DummySectionFragment} Each fragment contains a view in the
	 * mainactivity that you can slide to. Dummy fragment just for developer
	 * lulz
	 * 
	 * @author commander
	 * 
	 */
	public static class DummySectionFragment extends Fragment {
		public static final String ARG_SECTION_NUMBER = "section_number";

		public DummySectionFragment() {
			if (D)
				Log.e(TAG, "DummySectionFragment Loaded");
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main_dummy,
					container, false);
			TextView dummyTextView = (TextView) rootView
					.findViewById(R.id.section_label);
			dummyTextView.setText(Integer.toString(getArguments().getInt(
					ARG_SECTION_NUMBER)));
			return rootView;
		}
	}

	/**
	 * {@link spFragment} This will contain general SmartPark overal view
	 * 
	 * @author commander
	 */
	public static class spFragment extends Fragment {

		public static final String ARG_SECTION_NUMBER = "section_number";

		public spFragment() {
			if (D)
				Log.e(TAG, "spFragment Loaded");
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_sp_view,
					container, false);
			TextView dummyTextView = (TextView) rootView
					.findViewById(R.id.section_label);
			dummyTextView.setText(Integer.toString(getArguments().getInt(
					ARG_SECTION_NUMBER)));
			return rootView;
		}
	}

	/**
	 * {@link debugFragment} This will contain debug fragment where the debug
	 * information will show on the phone if needed.
	 * 
	 * @author commander
	 * 
	 */
	public static class debugFragment extends Fragment {

		public static final String ARG_SECTION_NUMBER = "section_number";

		public debugFragment() {
			if (D)
				Log.e(TAG, "debugFragment Loaded");
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_debug_view,
					container, false);
			TextView dummyTextView = (TextView) rootView
					.findViewById(R.id.section_label);
			dummyTextView.setText(Integer.toString(getArguments().getInt(
					ARG_SECTION_NUMBER)));
			return rootView;
		}
	}

	/*
	 * debugFragment Button Events
	 */
	public void connect(View view) {
		Toast.makeText(this, "connecting...", Toast.LENGTH_LONG).show();
		new connectTask().execute("");
	}

	public void disconnect(View view) {
		if (mTcpClient != null) {
			Toast.makeText(this, "disconnecting...", Toast.LENGTH_LONG).show();
			mTcpClient.stopClient();
		}
	}

	public class connectTask extends AsyncTask<String, String, TCPClient> {

		@Override
		protected TCPClient doInBackground(String... message) {

			// we create a TCPClient object and
			mTcpClient = new TCPClient(new TCPClient.OnMessageReceived() {
				@Override
				// here the messageReceived method is implemented
				public void messageReceived(String message) {
					Log.e(TAG, message);
					// this method calls the onProgressUpdate
					publishProgress(message);
				}
			});
			mTcpClient.run();

			return null;
		}

		@Override
		protected void onProgressUpdate(String... values) {
			super.onProgressUpdate(values);

			// in the arrayList we add the messaged received from server
			// arrayList.add(values[0]);
			// notify the adapter that the data set has changed. This means that
			// new message received
			// from server was added to the list
			// mAdapter.notifyDataSetChanged();
		}
	}

	/**
	 * {@link gpsFragment} This will contain the GPS information and if we get
	 * to it a google map on the current location
	 * 
	 * @author commander
	 * 
	 */
	public static class gpsFragment extends Fragment {

		public static final String ARG_SECTION_NUMBER = "section_number";

		public gpsFragment() {
			if (D)
				Log.e(TAG, "gpsFragment Loaded");
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_gps_view,
					container, false);
			TextView dummyTextView = (TextView) rootView
					.findViewById(R.id.section_label);
			dummyTextView.setText(Integer.toString(getArguments().getInt(
					ARG_SECTION_NUMBER)));
			return rootView;
		}
	}

	/**
	 * {@link bluetoothFragment} This will contin bluetooth information, like
	 * arduino state etc.
	 * 
	 * @author commander
	 * 
	 */
	public static class bluetoothFragment extends Fragment {
		public static final String ARG_SECTION_NUMBER = "section_number";

		public bluetoothFragment() {
			if (D)
				Log.e(TAG, "bluetoothFragment Loaded");
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_bluetooth_view,
					container, false);
			TextView dummyTextView = (TextView) rootView
					.findViewById(R.id.section_label);
			dummyTextView.setText(Integer.toString(getArguments().getInt(
					ARG_SECTION_NUMBER)));
			return rootView;
		}
	}
}
