package com.smartpark;

/**
 * Use this class for casting to get to method that are not implementet in
 * Thread-class. Implementing this class is safer. Casting a thread that does
 * not implement all methods in this interface means trouble if care is not
 * taken.
 * 
 * @author Saeed
 * @author Artur
 * 
 */
public interface CustomThread {

	/**
	 * Makes possible to terminate thread softly. Recommended on Internet.
	 */
	public void stopThread();

	/**
	 * Makes possible to check the thread for its state.
	 * 
	 * @return running If true then the thread is still running.
	 */
	public boolean isRunning();

}
