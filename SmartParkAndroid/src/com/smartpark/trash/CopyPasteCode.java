package com.smartpark.trash;

import java.util.zip.Inflater;

import com.smartpark.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.View;

public class CopyPasteCode extends Activity {
	

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
	
}
