package com.smartpark.trash;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.util.Log;

public class CopyPasteCode extends Activity {
	
	private long HALF_MINUTES;


	public void onClickGoHome(){
		Intent setIntent = new Intent(Intent.ACTION_MAIN);
		setIntent.addCategory(Intent.CATEGORY_HOME);
		setIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(setIntent);
	}
	
//    android:onClick="onClickBtnFromDate"

	
//	TextView dummyTextView = (TextView) rootView.findViewById(R.id.section_label);
//	dummyTextView.setText(Integer.toString(getArguments().getInt(ARG_SECTION_NUMBER)));
//	
//	try {
//		Thread.currentThread();
//		Thread.sleep(100);
//	} catch (Exception e) {
//		Log.e("Therad sleep", "--> Sleep didn't work");
//	}
	
	
	public void weNeedThisToCopyPasteWhereWeWantToHaveAButton() {
		//@formatter:off
		//@formatter:on
		// ======== ALERTDIALOG START =========================
		AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
		builder1.setTitle("Title");
		builder1.setMessage("my message");
		builder1.setCancelable(true);
		builder1.setNegativeButton(android.R.string.no,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						// TODO Add your code for the button here.
					}
				});
		builder1.setNeutralButton("neutral",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						// TODO Add your code for the button here.
					}
				});
		builder1.setPositiveButton(android.R.string.ok,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						// TODO Add your code for the button here.
					}
				});
		AlertDialog alert = builder1.create();
		alert.show();
		// ======== ALERTDIALOG END =========================
	}
	

//	LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//	View firstView = inflater.inflate(R.layout.frag_history_view, null); 
//	this.btnFromDate = (Button) firstView.findViewById(R.id.btnFromDate);
//	this.btnToDate = (Button) firstView.findViewById(R.id.btnToDate);

	
	
	
	// == START ====================================================
//	View v = Inflater.inflate(R.layout.****);
//	View innerView = v.findViewById(id_number_of_view_inside_v);
	// == END ======================================================
	
	
	// ===============================
	// STUFF WE NEED TO TAKE A LOOK AT
	// ===============================

	/**
	 * Artur: Only for inspection, removed later.
	 */
	/**
	 * Saeed: Yeah!!!
	 */
	// public class ConnectTask extends AsyncTask<String, String, TCPClient> {
	//
	// @Override
	// protected TCPClient doInBackground(String... message) {
	// // Debug stuff
	// if (D) {
	// Log.d(TAG, "class ConnectTask doInBackground");
	// }
	//
	// // we create a TCPClient object and
	// References.client = new TCPClient(new OnMessageReceived() {
	// @Override
	// // here the messageReceived method is implemented
	// public void messageReceived(String message) {
	// Log.e(TAG, message);
	// // this method calls the onProgressUpdate
	// publishProgress(message);
	// }
	// });
	// References.client.run();
	//
	// return null;
	// }
	//
	// @Override
	// protected void onProgressUpdate(String... values) {
	// // Debug stuff
	// if (D) {
	// Log.d(TAG, "onProgressUpdate");
	// }
	// super.onProgressUpdate(values);
	//
	// // in the arrayList we add the messaged received from server
	// // arrayList.add(values[0]);
	// // notify the adapter that the data set has changed. This means that
	// // new message received
	// // from server was added to the list
	// // mAdapter.notifyDataSetChanged();
	// }
	// }
	
	/**
	 * Determines whether the newly received location is better than the
	 * current location fix.
	 * 
	 * @param location
	 *            The new Location that you want to evaluate
	 * @param currentBestLocation
	 *            The current Location fix, to which you want to compare the
	 *            new one
	 */
	protected boolean isBetterLocation(Location location,
			Location currentBestLocation) {
		if (currentBestLocation == null) {
			// A new location is always better than no location
			return true;
		}

		// Check whether the new location fix is newer or older
		long timeDelta = location.getTime() - currentBestLocation.getTime();
		boolean isSignificantlyNewer = timeDelta > HALF_MINUTES;
		boolean isSignificantlyOlder = timeDelta < -HALF_MINUTES;
		boolean isNewer = timeDelta > 0;

		// If it's been more than two minutes since the current location,
		// use the new location because the user has likely moved.
		if (isSignificantlyNewer) {
			return true;
			// If the new location is more than two minutes older, it must
			// be worse
		} else if (isSignificantlyOlder) {
			return false;
		}

		// Check whether the new location fix is more or less accurate
		int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation
				.getAccuracy());
		boolean isLessAccurate = accuracyDelta > 0;
		boolean isMoreAccurate = accuracyDelta < 0;
		boolean isSignificantlyLessAccurate = accuracyDelta > 200;

		// Check if the old and new location are from the same provider
		boolean isFromSameProvider = isSameProvider(location.getProvider(),
				currentBestLocation.getProvider());

		// Determine location quality using a combination of timeliness and
		// accuracy
		if (isMoreAccurate) {
			return true;
		} else if (isNewer && !isLessAccurate) {
			return true;
		} else if (isNewer && !isSignificantlyLessAccurate
				&& isFromSameProvider) {
			return true;
		}
		return false;
	}// ==========================================================

	/** Checks whether two providers are the same */
	private boolean isSameProvider(String provider1, String provider2) {
		if (provider1 == null) {
			return provider2 == null;
		}
		return provider1.equals(provider2);
	}// ==========================================================
}
