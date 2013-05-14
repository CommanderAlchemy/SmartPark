package com.smartpark.collections;

/**
 * This class provides two static method for searching lists of generic type E.
 * The search algorithm are Binary Search and Liner Search.
 * 
 * 
 * @author Saeed
 * 
 */

public class Searching {

	public static <E> int binarySearch(ArrayList<E> list, E element) {
		int first = 0, last = list.size() - 1, middle, pos = -1;

		while (pos == -1 && first <= last) {
			middle = (first + last) / 2;

			if (((Comparable<E>) list.get(middle)).compareTo(element) == 0) {
				pos = middle;
			} else {
				if (((Comparable<E>) list.get(middle)).compareTo(element) > 0) {
					last = middle - 1;
				} else {
					first = middle + 1;
				}
			}
		}
		return pos;
	}

	
	
	
	
	public static <E> int linearSearch(List<E> list, E element) {
		int index = 0, pos = -1;

		while (pos == -1 && index < list.size()) {
			if (list.get(index).equals(element)) {
				pos = index;
			}
			index++;
		}
		return pos;
	}

}
