package com.smartpark.interfaces;

/**
 * This is an interface used for passing data between classes.
 * 
 * @author Saeed
 */


public interface OnMessageReceived {
	/*
	 * This method must be implemented in a class for which an instance of it is
	 * passed to the TCPclient-class so that it can pass data 
	 */
	public void messageReceived(String message);
	
	
}
