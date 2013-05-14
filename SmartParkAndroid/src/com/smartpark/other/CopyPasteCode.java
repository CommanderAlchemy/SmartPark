package com.smartpark.other;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

public class CopyPasteCode extends Activity {
	

	public void weNeedThisToCopyPasteWhereWeWantToHaveAButton() {
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
	}

}
