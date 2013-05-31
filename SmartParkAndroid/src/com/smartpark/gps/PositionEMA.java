package com.smartpark.gps;

import java.util.Iterator;
import java.util.LinkedList;

import android.location.Location;
import android.util.Log;

import com.smartpark.activities.MainActivity;

/**
 * The purpose of this class is to calculate more precisely the position based
 * on GPS locations only. This is done by creating an Exponential moving average
 * over 10 locations. The exponentiation arises from the fact that this method
 * monitors for a deviations from a mean value to determine when to weigh the
 * most reason locations more that the older ones.
 * 
 * This class needs a GPS-module that can measure speed and bearing of the
 * device beside position. (However, it is possible to update this class to only
 * use latitude and longitude to accomplish its task.)
 * 
 * @author Saeed
 * 
 */
public class PositionEMA {

	private LinkedList<Location> locationList;
	private int maxMeasurements;
	private float maxInaccuracy = 10.0f;

	private boolean D = MainActivity.D;
	private String TAG = "positionEMA";

	public PositionEMA(int size) {
		locationList = new LinkedList<Location>();
		maxMeasurements = size;
	}

	public void put(Location location) {
		Log.e(TAG, "++ put ++");
		System.out.println(location.getLatitude() + " " +location.getLongitude());
		locationList.addLast(location);
		if (locationList.size() > maxMeasurements) {
			locationList.removeFirst();
		}
	}

	/**
	 * This method resets the
	 */
	public void clear() {
		locationList.clear();
	}

	/**
	 * This method calculates an average over the last few measurements og
	 * position.
	 * 
	 * @param maxSamplesToUse
	 *            Use -1 to average over all measurements or pass an int lower
	 *            than maxMeasurements to average over a portion of the
	 *            measurements.
	 * @return location Returns an object of type Location that contains the
	 *         latitude and longitude calculated.
	 */
	private Location calculateAverage(int maxSamplesToUse) {
		float factor = 0f, accuracySum = 0f, totalLatitude = 0f, totalLongitude = 0f;
		Iterator<Location> latIter = locationList.iterator();
		Location location;
		int interations = 0;

		while (latIter.hasNext() && maxSamplesToUse != interations) {
			interations++;
			location = latIter.next();
			if (location.getAccuracy() < maxInaccuracy) {
				if (location.getAccuracy() != 0) {
					factor = (1 / (location.getAccuracy() + 1));
				} else {
					factor = 1;
				}
				accuracySum += factor;
				totalLatitude += location.getLatitude() * factor;
				totalLongitude += location.getLongitude() * factor;
			} else {
				// exclude location with lower accuracy than maxInaccuracy
			}
			totalLatitude /= accuracySum;
			totalLongitude /= accuracySum;
		}
		Location newLocation = new Location("Average Provider");
		newLocation.setLatitude(totalLatitude);
		newLocation.setLongitude(totalLongitude);
		return newLocation;
	}

	/**
	 * This method uses the calculateAverage()-method to deliver a calculated
	 * position even while in motion. This method will simply strip more and
	 * more measurements the faster the speed is and lays a calculated mount of
	 * distance to the position based on the bearings of the device. By doing so
	 * without advanced geometric calculation for position and distance
	 * calculation on a globes. The impact is negligible in short distances like
	 * 20 meters.
	 * 
	 * @return location Returns an object of type Location that contains the
	 *         latitude and longitude calculated.
	 * 
	 */
	public Location getPosition() {
//		Location averageLocation, lastLocation, secondToLastLocation;
//		
//		System.out.println(locationList.getLast().getLatitude());
//		System.out.println(locationList.getLast().getLongitude());
//		
//		System.out.println(locationList.size());
//		
//		if (locationList.size() > 1) {
//			lastLocation = locationList.getLast();
//			secondToLastLocation = locationList.get(locationList.size() - 2);
//			// Slightly averaging speed and bearings
//			float lastSpeed = (lastLocation.getSpeed() + secondToLastLocation
//					.getSpeed()) / 2;
//			System.out.println(lastSpeed);
//			// float bearing = (lastLocation.getBearing() + secondToLastLocation
//			// .getBearing()) / 2;
//
////			if (lastSpeed == 0.0f) {
////				if (D)
////					Log.w(TAG, "Device does not support speed measurement");
////				return null;
////			} else {
//				/*
//				 * At 0 km/h the entire array is used to average the position,
//				 * but at speeds higher than 34,2 km/h, (9,5m/s *
//				 * 3.6(s*km)/(h*m)), no averaging is performed.
//				 */
//				int samples = (int) (maxMeasurements - lastSpeed);
//				averageLocation = calculateAverage((samples < 1) ? 1 : samples);
//				System.out.println("samples" + samples);
//				System.out.println("lat " + averageLocation.getLatitude());
//				// 90 degrees is East, 180 South, and 270 West and 0 in North
//				// Math.cos(bearing); // work on this
//				// Math.sin(bearing);
//				
//				Log.e(TAG, "fsf: " + averageLocation.getLatitude());
//
//				return averageLocation;
////			}
//		}
//		double latitute = 0, longitute = 0;
//		for(int i = 0 ; i < locationList.size() ; i++){
//			latitute += locationList.get(i).getLatitude();
//			longitute += locationList.get(i).getLongitude();
//		}
//		Location location = null;
//		location.setLatitude(latitute/locationList.size());
//		location.setLatitude(longitute/locationList.size());
		
		return locationList.getLast();
	}

	/**
	 * This function returns the distance between two locations
	 * 
	 * @param lat1
	 * @param lon1
	 * @param lat2
	 * @param lon2
	 * @return
	 */
	public double calculateDistance(Location locationT1, Location locationT2) {
		double latA = Math.toRadians(locationT1.getLatitude());
		double lonA = Math.toRadians(locationT1.getLongitude());
		double latB = Math.toRadians(locationT2.getLatitude());
		double lonB = Math.toRadians(locationT2.getLongitude());
		double cosAng = (Math.cos(latA) * Math.cos(latB) * Math
				.cos(lonB - lonA)) + (Math.sin(latA) * Math.sin(latB));
		double ang = Math.acos(cosAng);
		double dist = ang * 6371; // 6371 is radius of the earth in km
		return dist; // Distance in km
	}

	public float aha(Location user_location, double lat, double lng) {
		Location destination = new Location("gps");
		destination.setLatitude(lat);
		destination.setLongitude(lng);
		float dist = user_location.distanceTo(destination);
		return dist;
	}

	/**
	 * This script [in Javascript] calculates great-circle distances between the
	 * two points – that is, the shortest distance over the earth’s surface –
	 * using the ‘Haversine’ formula.
	 * 
	 * @return
	 */

	// public void getDistanceFromLatLonInKm(lat1,lon1,lat2,lon2) {
	// var R = 6371; // Radius of the earth in km
	// var dLat = deg2rad(lat2-lat1); // deg2rad below
	// var dLon = deg2rad(lon2-lon1);
	// var a =
	// Math.sin(dLat/2) * Math.sin(dLat/2) +
	// Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) *
	// Math.sin(dLon/2) * Math.sin(dLon/2)
	// ;
	// var c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
	// var d = R * c; // Distance in km
	// return d;
	// }
	//
	// function deg2rad(deg) {
	// return deg * (Math.PI/180)
	// }
	//
	// /// <summary>
	// /// Calculate the distance between two places.
	// /// </summary>
	// /// <param name="lon1"></param>
	// /// <param name="lat1"></param>
	// /// <param name="lon2"></param>
	// /// <param name="lat2"></param>
	// /// <returns></returns>
	// public static double DistanceBetweenPlaces(
	// double lon1,
	// double lat1,
	// double lon2,
	// double lat2)
	// {
	// double dlon = Radians(lon2 - lon1);
	// double dlat = Radians(lat2 - lat1);
	//
	// double a = (Math.Sin(dlat / 2) * Math.Sin(dlat / 2)) +
	// Math.Cos(Radians(lat1)) * Math.Cos(Radians(lat2)) * (Math.Sin(dlon / 2) *
	// Math.Sin(dlon / 2));
	// double angle = 2 * Math.Atan2(Math.Sqrt(a), Math.Sqrt(1 - a));
	// return angle * RADIUS;
	// }
	//
}