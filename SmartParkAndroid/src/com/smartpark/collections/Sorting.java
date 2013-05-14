package com.smartpark.collections;

import java.util.Random;

/**
 * This class provides two static sorting methods for sorting a double array and
 * an array list og generic type E. double arrays are sorted by
 * QuickSort-algorithm and lists are soorted with MargeSort-algorithm.
 * 
 * @author Saeed Mir Ghasemi
 * @author Truls Olof Haraldsson
 * @author Artur Kim Olech

 * 
 */
public class Sorting {

	/**
	 * This method will start a sorting process that is based on
	 * QuickSort-algorithm. The process is recursive.
	 * 
	 * @param array
	 *            the double array to be sorted ascending
	 */
	public static void sort(double[] array) {
		quickSort(array, 0, array.length - 1);
	}
	
	/**
	 * QuickSort
	 * @param array	, Array
	 * @param min	, Min
	 * @param max	, Max
	 */
	private static void quickSort(double[] array, int min, int max) {
		int right;
		if (min < max) {
			right = partition(array, min, max);
			quickSort(array, min, right - 1);
			quickSort(array, right + 1, max);
		}
	} 
	
	/**
	 * Partition
	 * @param array
	 * @param min
	 * @param max
	 * @return right
	 */
	private static int partition(double[] array, int min, int max) {
		double partitionValue = array[min];

		// At first, the min and max will encompass the whole array
		int left = min;
		int right = max;

		while (left < right) {
			// The next two while loops will make the array field narrower and
			// and work from the outer sides of the whole array towards the
			// partitionValue.
			while (array[left] <= partitionValue && left < right) {
				// If the value in array is smaller than the partitionValue then
				// point to the next value in the left partition.
				left++;
			}
			while (array[right] > partitionValue) {
				// If the value in array is greater than the partitionValue then
				// point to the next value in the right partition.
				right--;
			}

			// At this point the while loops have stopped because they have
			// encountered a value that didn't pass the test.
			// Or all values did pass and left became equal to right.
			if (left < right) {
				// This will swap the values at index left and right
				// which should be bigger than partitionValue but on the left
				// side, and smaller than partitionValue but on the right side.
				swap(array, left, right);
				// Sometimes the swap will occur on the same side and only on
				// the right side. look at the note above.
			}
			// this will swap the partitionValue over to the newly swapped
			// value that was smaller than partitionValue.
			swap(array, min, right);
		}
		return right;
	} 

	/**
	 * Swap
	 * @param array
	 * @param left
	 * @param right
	 */
	private static void swap(double[] array, int left, int right) {
		double tmp = array[left];
		array[left] = array[right];
		array[right] = tmp;
	} 

	/**
	 * Sort
	 * @param list
	 */
	public static <E> void sort(ArrayList<E> list) {
		ArrayList<E> temp = new ArrayList<E>(list.size());
		mergeSort(list, 0, list.size() - 1, temp);
	}

	/**
	 * Merge
	 * @param list
	 * @param min
	 * @param max
	 * @param temp
	 */
	private static <E> void mergeSort(ArrayList<E> list, int min, int max,
			ArrayList<E> temp) {
		if (min < max) {
			int mid = (min + max) / 2;
			mergeSort(list, min, mid, temp);
			mergeSort(list, mid + 1, max, temp);
			merge(list, min, mid, max, temp);
		}
	}

	/**
	 * This sorts the specified ArrayList of generic type E, while using the
	 * MergeSort-algorithm.
	 * 
	 * @param list
	 *            , the list to be sorted
	 * @param first
	 *            , the first index of the sublist
	 * @param mid
	 *            , the midpoint of the sublist
	 * @param last
	 *            , the last index of the sublist
	 */
	private static <E> void merge(ArrayList<E> list, int first, int mid,
			int last, ArrayList<E> temp) {
		int first1 = first, last1 = mid;
		// endpoints of first sublist
		int first2 = mid + 1, last2 = last;
		// endpoints of second sublist
		int index = first1;
		// next index open in temp array
		// Copy smaller item from each sublist into temp until one
		// of the sublist is exhausted
		while (first1 <= last1 && first2 <= last2) {
			if ((((Comparable) list.get(first1)).compareTo(list.get(first2))) < 0) {
				temp.set(index, list.get(first1));
				first1++;
			} else {
				temp.set(index, list.get(first2));
				first2++;
			}
			index++;
		}

		// When one of the sublists gets exhausted, this code will flush the
		// content of the other sublist into the temp-list.
		while (first1 <= last1) {
			temp.set(index, list.get(first1));
			first1++;
			index++;
		}
		while (first2 <= last2) {
			temp.set(index, list.get(first2));
			first2++;
			index++;
		}

		// The temp-list will get emptied into the actual list
		for (index = first; index <= last; index++) {
			list.set(index, temp.get(index));
		}
	}

	public static void main(String[] args) {
		ArrayList<Integer> e = new ArrayList<Integer>();
		Random rand = new Random();
		for (int i = 0; i < 3000; i++) {
			// e.add(i);
			e.add(i, rand.nextInt(10000));
		}
		System.out.println(e);
		Sorting.sort(e);
		System.out.println(e);
	}

}
