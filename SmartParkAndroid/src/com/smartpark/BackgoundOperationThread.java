package com.smartpark;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;

import android.bluetooth.BluetoothDevice;
import android.util.Log;
import android.widget.Toast;

public class BackgoundOperationThread extends Thread {

	/*
	 * keeps a list of booleans to determine if all activities have been
	 * destroyed so that the thread wont continue running for ever.
	 */
	public static boolean mainActivity = true;
	public static boolean settingsActivity = true;
	public static boolean loginActivity = true;

	private static long shutdownTime = 0; // 0 = never

	// TRANSMITBUFFERS
	private LinkedList<String> btTransmitBuffer = new LinkedList<String>();
	private LinkedList<String> tcpTransmitBuffer = new LinkedList<String>();

	// Debugging and stuff
	private static final String TAG = "bgThread";
	private static final boolean D = Ref.D;

	private BufferedReader bufferedReader;

	public BackgoundOperationThread() {
		// TODO
	}

	@Override
	public void run() {
		Log.w(TAG, "Started");
		/*
		 * Check the connection states Handle the states
		 * 
		 * See if there is anything to be send Send till there is nothing bo be
		 * send
		 * 
		 * See if anything has been received Handle the incoming data If engine
		 * is of, then start parking-methods
		 * 
		 * 
		 * Calculate an average of the position
		 */

		if (Ref.btDevice == null) {
			if (Ref.btController == null)
				Log.e(TAG, "FUCK YOU");
			BluetoothDevice device = Ref.btController.getPairedDeviceByName("Speed");
			if (device != null) {
				Ref.btDevice = device;
				Ref.btController.connectAsynchroniouslyTo();
				Log.w(TAG, "problem " + device.getAddress());
			} else {
				Log.w(TAG, "device is null, bluetooth not found");
			}

		}

		String inData = null;

		try {
			Thread.sleep((long) (Math.random() * 3000));
		} catch (InterruptedException e2) {
			// TODO Auto-generated catch block
			Log.e(TAG, "Thread sleep failed at Math.random");
		}
		while (Ref.btInStream == null) {
			if (Ref.btInStream != null) {
				
				bufferedReader = new BufferedReader(new InputStreamReader(Ref.btInStream));
				Log.d(TAG, "reader created !");
			}
		}

		inData = btRead();
		if (inData != null) {
			Log.d(TAG, "Received data");
			Integer t = Integer.parseInt(inData) + 1;
			sendBT(t.toString());
		}

		while (true) {
			if (Ref.btSocket != null) {
				if (Ref.btState == Ref.STATE_NOT_CONNECTED) {
					Log.d(TAG, "not connected to bt");
				}

				while (btTransmitBuffer.size() > 0
						&& Ref.getbtState() == Ref.STATE_CONNECTED) {
					btWrite();
				}

				// while (tcpTransmitBuffer.size() > 0 && Ref.gettcpState() ==
				// Ref.STATE_CONNECTED) {
				// byte[] data = btTransmitBuffer.removeFirst().getBytes();
				// try {
				// Ref.tcpOutStream.write(data); TODO
				// } catch (IOException e1) {
				// Log.e(TAG, "Sending of data with BT failed" + e1);
				// }
				// }

			}

			inData = btRead();

			if (inData != null) {
				Integer t = Integer.parseInt(inData) + 1;
				sendBT(t.toString());
			}

			// Check to see if the connections are OK

			// -----------------------------------------
			Log.d(TAG, "thread igang 2");
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}

			if (mainActivity || settingsActivity || loginActivity) {
				shutdownTime = 0;
				Log.d(TAG, "thread igang");
				if (false)
					Log.d("BackThread", "Application is frontmost");
			} else {
				if (shutdownTime == 0) {
					shutdownTime = System.currentTimeMillis();
				} else if (System.currentTimeMillis() - shutdownTime > 5000) {
					shutdownThread();
				}
				if (D)
					Log.d("BackThread",
							"thread is shutting down"
									+ (System.currentTimeMillis() - shutdownTime));
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
				}
			}
			Log.e(TAG, "The thread died");
		}
		
	}

	private String btRead() {
		String inData = null;
		if (Ref.btInStream != null) {
			try {
				bufferedReader = new BufferedReader(new InputStreamReader(Ref.btInStream));
				if(bufferedReader == null){
					bufferedReader = new BufferedReader(new InputStreamReader(Ref.btInStream));
					Log.e(TAG, "bufferedReader = null");					
				}
				if (bufferedReader.ready()) {
					inData = bufferedReader.readLine();
					Log.d(TAG, "DATA= " + bufferedReader.readLine());
				}
			} catch (IOException e1) {

				 if (!Ref.btSocket.isConnected()) {
					Ref.setbtState(Ref.STATE_NOT_CONNECTED);
					Log.e(TAG, "btSocket not connected");
				}
			}
		}
		return inData;
	}

	private void btWrite() {
		byte[] data = btTransmitBuffer.removeFirst().getBytes();
		try {
			Ref.btOutStream.write(data);
		} catch (IOException e1) {
			Log.e(TAG, "Sending of data with bt failed" + e1);
		}
	}

	private void shutdownThread() {
		// TODO Auto-generated method stub

		try {
			Ref.btSocket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
		}

		Ref.bgThread = null;

	}

	public void sendBT(String data) {
		btTransmitBuffer.addLast(data);
	}

	public void sendTCP(String data) {
		tcpTransmitBuffer.addLast(data);
	}

}
