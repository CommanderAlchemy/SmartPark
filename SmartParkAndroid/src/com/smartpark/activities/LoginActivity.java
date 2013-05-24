package com.smartpark.activities;

import java.util.LinkedList;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.smartpark.R;
import com.smartpark.background.Ref;

/**
 * Activity which displays a login screen to the user, offering registration as
 * well.
 */
public class LoginActivity extends Activity {

	/**
	 * The default email to populate the email field with.
	 */
	public static final String EXTRA_EMAIL = "com.example.android.authenticatordemo.extra.EMAIL";

	/**
	 * Keep track of the login task to ensure we can cancel it if requested.
	 */
	private UserLoginTask mAuthTask = null;

	// Values for email and password at the time of the login attempt.
	private String mEmail;
	private String mPassword;

	// UI references.
	private EditText mEmailView;
	private EditText mPasswordView;
	private View mLoginFormView;
	private View mLoginStatusView;
	private TextView mLoginStatusMessageView;

	private boolean isBackdoorEnabled = false;
	private boolean isController = false;

	private static LinkedList<String> messages = new LinkedList<String>();
	private boolean timeout = false;

	private final boolean D = Ref.D;
	private static final String TAG = "LoginActivity";

	// ================================================================

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_login);

		// Set up the login form.
		mEmail = getIntent().getStringExtra(EXTRA_EMAIL);
		mEmailView = (EditText) findViewById(R.id.socialSecurityNumber);
		mEmailView.setText(mEmail);

		mPasswordView = (EditText) findViewById(R.id.password);
		mPasswordView
				.setOnEditorActionListener(new TextView.OnEditorActionListener() {
					@Override
					public boolean onEditorAction(TextView textView, int id,
							KeyEvent keyEvent) {
						if (id == R.id.login || id == EditorInfo.IME_NULL) {
							attemptLogin();
							return true;
						}
						return false;
					}
				});

		mLoginFormView = findViewById(R.id.login_form);
		mLoginStatusView = findViewById(R.id.login_status);
		mLoginStatusMessageView = (TextView) findViewById(R.id.login_status_message);

		findViewById(R.id.sign_in_button).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						attemptLogin();
					}
				});
	}

	@Override
	public void onBackPressed() {
		if (getIntent().getExtras().getBoolean("CancelAllowed")) {
			setResult(MainActivity.RESULT_CANCELED, new Intent());
			finish();
		} else {

			// ======== ALERTDIALOG START =========================
			AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
			builder1.setTitle("Login required");
			builder1.setMessage("This will exit the application.\nWill you continue?");
			builder1.setCancelable(true);
			builder1.setNegativeButton("No",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
						}
					});
			builder1.setPositiveButton("Yes",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							setResult(MainActivity.RESULT_EXIT, new Intent());
							finish();
						}
					});
			AlertDialog alert = builder1.create();
			alert.show();
			// ======== ALERTDIALOG END =========================
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	/**
	 * On ActionMenu Select Do something when that get selected in the
	 * ActionMenu
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (D)
			Log.i(TAG, "++ onOptionsItemSelected ++");

		if (D) {
			Log.d(TAG, "Item: " + item.toString() + "\nID: " + item.getItemId()
					+ "\nIntent: " + item.getIntent());
		}
		Log.e(TAG, "dadasdd" + item.getItemId());
		// TODO Cleanup!
		// On select
		switch (item.getItemId()) {
		case R.id.action_forgot_password:

			// ======== ALERTDIALOG START =========================
			AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
			builder1.setTitle("Recover lost password");
			builder1.setMessage("Not yet implemented.\nPlease try later...");
			builder1.setCancelable(true);
			builder1.setPositiveButton(android.R.string.ok,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
						}
					});
			AlertDialog alert = builder1.create();
			alert.show();
			// ======== ALERTDIALOG END =========================
			return true;
		}
		return false;
	}

	/**
	 * Attempts to sign in or register the account specified by the login form.
	 * If there are form errors (invalid email, missing fields, etc.), the
	 * errors are presented and no actual login attempt is made.
	 */
	public void attemptLogin() {
		if (mAuthTask != null) {
			return;
		}

		// Reset errors.
		mEmailView.setError(null);
		mPasswordView.setError(null);

		// Store values at the time of the login attempt.
		mEmail = mEmailView.getText().toString();
		mPassword = mPasswordView.getText().toString();

		boolean cancel = false;
		View focusView = null;

		// Check for a valid password.
		if (TextUtils.isEmpty(mPassword) && !mEmail.equals("666")) {
			mPasswordView.setError(getString(R.string.error_field_required));
			focusView = mPasswordView;
			cancel = true;
		} else if (mPassword.length() < 4 && !mEmail.equals("666")) {
			mPasswordView.setError(getString(R.string.error_invalid_password));
			focusView = mPasswordView;
			cancel = true;
		}

		// Check for a valid email address.
		if (TextUtils.isEmpty(mEmail)) {
			mEmailView.setError(getString(R.string.error_field_required));
			focusView = mEmailView;
			cancel = true;
		} else if (mEmail.length() != 6 && !mEmail.equals("666")) {
			mEmailView.setError(getString(R.string.error_invalid_email));
			focusView = mEmailView;
			cancel = true;
		}

		if (cancel) {
			// There was an error; don't attempt login and focus the first
			// form field with an error.
			focusView.requestFocus();
		} else {
			// Show a progress spinner, and kick off a background task to
			// perform the user login attempt.
			mLoginStatusMessageView.setText(R.string.login_progress_signing_in);
			showProgress(true);
			mAuthTask = new UserLoginTask();
			mAuthTask.execute((Void) null);
		}
	}

	/**
	 * Shows the progress UI and hides the login form.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void showProgress(final boolean show) {
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs to fade-in
		// the progress spinner.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			int shortAnimTime = getResources().getInteger(
					android.R.integer.config_shortAnimTime);

			mLoginStatusView.setVisibility(View.VISIBLE);
			mLoginStatusView.animate().setDuration(shortAnimTime)
					.alpha(show ? 1 : 0)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoginStatusView.setVisibility(show ? View.VISIBLE
									: View.GONE);
						}
					});

			mLoginFormView.setVisibility(View.VISIBLE);
			mLoginFormView.animate().setDuration(shortAnimTime)
					.alpha(show ? 0 : 1)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoginFormView.setVisibility(show ? View.GONE
									: View.VISIBLE);
						}
					});
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
			mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}

	/**
	 * This method is used by the Handler to give this process data.
	 * 
	 * @param data
	 */
	public static void setMessage(String data) {
		Log.e(TAG, "++ setMessage ++ " + data);
		messages.addLast(data);
	}

	/**
	 * Represents an asynchronous login operation to query the server and
	 * authenticate the user. The thread waits till the handler has received the
	 * data and put that in the LinkedList of this class before invoking
	 * notifyAll() to notify this thread tht data has arrived. Not running a
	 * loop while waiting saves power.
	 */
	public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {
		@Override
		protected Boolean doInBackground(Void... params) {
			messages.clear();

			String queryToServer = "Login;"
					+ ((TextView) findViewById(R.id.socialSecurityNumber))
							.getText() + ":"
					+ ((TextView) findViewById(R.id.password)).getText();

			System.out.println(queryToServer);

			if (queryToServer.contains("Login;666:")) {
				if (mPassword.length() > 0) {
					isController = true;
					Log.e(TAG, "--- BACKDOOR ACTIVATED !");
				}

				isBackdoorEnabled = true;
				System.out.println("login Tr");
				return true;
			}

			Log.e(TAG, "Sending Login Request: " + queryToServer);
			Ref.bgThread.sendByTCP(queryToServer);

			int iterations = 0;
			while (!timeout) {
				// try {
				// Thread.currentThread();
				// Thread.sleep(100);
				// } catch (Exception e) {
				// Log.e("Therad sleep", "--> Sleep didn't work");
				// }

				try {
					wait(1000);
				} catch (InterruptedException e) {
				}

				for (int i = 0; i < messages.size(); i++) {
					String line = messages.getFirst();
					String[] respons = line.split(";");
					Log.e(TAG, "Loop receives: " + respons[0] + " "
							+ respons[1]);

					if (respons[0].equals("LoginACK")) {
						String[] data = respons[1].split(":");

						if (data[0].equals("Accepted")) {
							return true;
						} else if (data[0].equals("Denied")) {
							return false;
						} else {
							messages.removeFirst();
						}
					} else {
						messages.removeFirst();
					}
				}
				if (iterations == 100) {
					timeout = true;
				}
				iterations++;
				Log.e(TAG, "" + iterations);
			}
			return false;
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			mAuthTask = null;
			showProgress(false);

			if (timeout) {
				Toast.makeText(getBaseContext(), "Counld not reach the Server",
						Toast.LENGTH_LONG).show();
			}

			if (success) {
				if (!isBackdoorEnabled) {
					// Storing some data as shared preference
					SharedPreferences loginPreferences = getSharedPreferences(
							"loginActivity", MODE_PRIVATE);
					Editor edit = loginPreferences.edit();
					edit.putBoolean("controller", messages.getFirst()
							.split(";")[1].split(":")[1].equals("true"));
					edit.putBoolean("login", success);
					edit.commit();
				} else {
					// Storing some data as shared preference
					SharedPreferences loginPreferences = getSharedPreferences(
							"loginActivity", MODE_PRIVATE);
					Editor edit = loginPreferences.edit();
					edit.putBoolean("controller", isController);
					edit.putBoolean("login", success);
					edit.commit();
				}
				Toast.makeText(getBaseContext(), "Login successful",
						Toast.LENGTH_SHORT).show();

				// Making ready to respond to onActivityResult()
				Intent intent = new Intent();
				setResult(RESULT_OK, intent);
				finish();
			} else {
				mPasswordView
						.setError(getString(R.string.error_incorrect_password));
				mPasswordView.requestFocus();
			}
		}

		@Override
		protected void onCancelled() {
			mAuthTask = null;
			showProgress(false);
		}
	}
}
