package com.smartpark.collections;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * This class is able to create and manage a resizable array
 * of generic type E and give the user precise control over
 * the elements stored in the array. A programmer can add an
 * element into an index at any time. It is possible to replace
 * an element or remove it. The class will allocate more space for
 * storage when needed and unallocate all memory when cleared.
 * 
 * @author Saeed Mir Ghasemi
 * @author Truls Olof Haraldsson
 * @author Artur Kim Olech
 * @param <E> 
 */
public class ArrayList<E> implements List<E> {
	private E[] elements;
	private int lastOccupiedIndex;
	private int allocatedSpace;

	/**
	 * The default constructor constructs an arrayList of initial
	 * capacity of 10 by invoking another constructor that can
	 * create the array.
	 * 
	 */
	public ArrayList() {
		this(10);
	}

	/**
	 * This constructor takes a singles value as the initial capacity
	 * that it has to create the ArrayList with. Then it populates the
	 * other class-variables with their initial values.
	 * 
	 * @param initialCapacity
	 */
	public ArrayList(int initialCapacity) {
		initialCapacity = Math.max(1, initialCapacity);
		elements = (E[]) new Object[initialCapacity];
		allocatedSpace = elements.length;
		lastOccupiedIndex = -1;
	}

	/**
	 * CheckBounds
	 * @param index	, for index
	 */
	private void checkBounds(int index) {
		if (index < 0 || index > allocatedSpace - 1) {
			throw new IndexOutOfBoundsException();
		}
	}

	private void grow() {
		// Adds times 2 to the size of the array but first, stores
		// the size of the whole array into a separate integer
		allocatedSpace = (int) (allocatedSpace * 2);
		E[] temp = (E[]) new Object[allocatedSpace];

		// Stores the content of elements into the new temp-array
		for (int i = 0; i <= lastOccupiedIndex; i++) {
			temp[i] = elements[i];
		}

		/*
		 * Stores the reference of temp into elements, thereby have
		 * changed the elements-array.
		 */
		elements = temp;
	}

	/**
	 * This will grow the size of the ArrayList if it is full.
	 * 
	 * @author Saeed
	 * @param index
	 */
	private void growSizeIfFull() {
		if (lastOccupiedIndex == allocatedSpace - 1) {
			grow();
		}
	}

	/**
	 * This will make room for one element at inIndex, by incrementing
	 * the index-value of all elements places after inIndex by 1.
	 * 
	 * @param inIndex makes room at this index in the array.
	 * @author Saeed
	 */
	private void IncIndexOfAllElem(int inIndex) {
		growSizeIfFull();
		for (int i = lastOccupiedIndex; i >= inIndex; i--) {
			elements[i + 1] = elements[i];
		}
	}

	/**
	 * This method adds an element of type <E> into the desired position of
	 * index.
	 * 
	 * @param inIndex the index-value in which the element has to be added to
	 * @param element the element to be added to the array
	 */
	public void add(int inIndex, E inElement) {
		/* This if-statement will make sure that all chosen indexes higher than the
		 * last occupied index in the array will be repositioned to be added last
		 * in the array. */
		if (inIndex > lastOccupiedIndex + 1){
			inIndex = lastOccupiedIndex + 1;
		}
		// This will grow the size of the array if it is full already
		growSizeIfFull();
		/*
		 * This will test the index-value to be inside the boundaries of the
		 * ArrayList.
		 */
		checkBounds(inIndex);
		if (inIndex < 0 || inIndex > allocatedSpace + 1) {
			throw new IndexOutOfBoundsException();
		}

		/*
		 * This will move every element that is places after the inIndex-value
		 * one index-value down to make room for the addition at inIndex-value.
		 */
		IncIndexOfAllElem(inIndex);

		// replace the, now moved, element at index with inElement
		elements[inIndex] = inElement;
		lastOccupiedIndex++;
	}

	/**
	 * This method adds an element to the array by invoking another add-method
	 * that can take in an index-value to. This method places the new element at
	 * the end of the array.
	 * 
	 * @param inElement
	 *            , the element to be added to the end of the array
	 * @author Saeed
	 */
	public void add(E inElement) {
		add(lastOccupiedIndex + 1, inElement);
	}

	/**
	 * Adds an element into the first position of the array by moving the other
	 * element to make room for this one. It only moves the portion of the array
	 * that is occupied to make room for the element to be inserted.
	 * 
	 * @param E
	 *            element
	 * @author Saeed
	 */
	public void addFirst(E inElement) {
		add(0, inElement);
	}

	/**
	 * This method adds an element to the array by invoking another add-method
	 * that can take in an index-value too. This method places the new element
	 * at the end of the array.
	 * 
	 * @param inElement
	 *            , the element to be added to the end of the array
	 * @author Saeed
	 */
	public void addLast(E inElement) {
		add(lastOccupiedIndex + 1, inElement);
	}

	/**
	 * This method removes an element from the array and returns it. This method
	 * also decrements the value of lastOccupiedIndeex which is a direct
	 * indication of the amount of data in the array.
	 * 
	 * @param index
	 *            the index to be returned and removed
	 * @return E the returned element
	 * @author Saeed
	 */
	public E remove(int index) {
		checkBounds(index);

		E elem = elements[index];
		for (int i = index; i < allocatedSpace - 1; i++) {
			elements[i] = elements[i + 1];
		}
		lastOccupiedIndex--;
		return elem;
	}

	/**
	 * This method removes the first element in the array (at index 0) and
	 * returns it.
	 * 
	 * @return E the removed element
	 */
	public E removeFirst() {
		return remove(0);
	}

	/**
	 * This method removes an element from the lasOccupiedIndex in the array,
	 * and there by the last element.
	 * 
	 * @return E the removed element
	 */
	public E removeLast() {
		return remove(lastOccupiedIndex);
	}

	/**
	 * This method empties the content of the ArrayList and reduces the
	 * allocated size down to 10 elements.
	 */
	public void clear() {
		lastOccupiedIndex = -1;
		// Reduces the size of the array to 10 elements but first, stores the 
		// new size of the whole array into a separate integer.
		/* Stores the reference of temp into elements, thereby have changed the
		 * elements-array. */
		allocatedSpace = 10;
		elements = (E[]) new Object[allocatedSpace];
	}

	/**
	 * Returns the element at index without removing it from the ArrayList.
	 * 
	 * @return E element
	 * @param index
	 */
	public E get(int index) {
		checkBounds(index);
		return elements[index];
	}

	/**
	 * Replaces the element at the given value with the new one and returns the
	 * old element.
	 * 
	 * @return E element
	 * @param index
	 * @param E
	 *            inElement
	 */
	public E set(int index, E inElement) {
		checkBounds(index);
		E replacedElement = elements[index];
		elements[index] = inElement;
		return replacedElement;
	}

	/**
	 * This will return the index of the first element that is equal to the
	 * element it is looking up in the array. This method uses the implemented
	 * equal()-method of the object E. If not found it returns -1.
	 * 
	 * @return int index
	 * @param E
	 *            element
	 */
	public int indexOf(E element) {
		for (int i = 0; i <= lastOccupiedIndex; i++) {
			if (elements[i].equals(element)) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * This will return the index of the first element that is equal to the
	 * element it is looking up in the array after a given startIndex. This
	 * method uses the implemented equal()-method of the object E. If not found
	 * it returns -1.
	 * 
	 * @return index
	 * @param element
	 */
	public int indexOf(int startIndex, E element) {
		if (startIndex >= 0 && startIndex <= lastOccupiedIndex) {
			for (int i = startIndex; i <= lastOccupiedIndex; i++) {
				if (elements[i].equals(element)) {
					return i;
				}
			}
		}
		return -1;
	}

	/**
	 * This method returns the value of lastOccupiedIndex + 1 as the size of the
	 * array.
	 * 
	 * @return lastOccupiedIndex +1
	 */
	public int size() {
		return this.lastOccupiedIndex + 1;
	}

	/**
	 * This method goes through the whole list and appends their
	 * toString-methods outputs to a variable, and it then later prints it all.
	 * 
	 * @return String toString outputs of all elements
	 */
	public String toString() {
		StringBuilder res = new StringBuilder("[ ");
		for (int i = 0; i <= lastOccupiedIndex; i++) {
			res.append(elements[i]);
			if (i <= lastOccupiedIndex - 1)
				res.append("; ");
		}
		res.append(" ]");
		return res.toString();
	}

	/**
	 * This method creates an object of the internal Iterator-class and passes
	 * it out to the calling method.
	 * 
	 * @return Iterator<E> the returned reference to the Iterator
	 */
	public Iterator<E> iterator() {
		return new Iter();
	}

	/**
	 * This is an internal class that implements the Iterator-interface.
	 * 
	 */
	private class Iter implements Iterator<E> {
		private int index = 0;

		public boolean hasNext() {
			return index <= lastOccupiedIndex;
		}

		public E next() {
			if (index == lastOccupiedIndex + 1){
				throw new NoSuchElementException();
			}
			return elements[index++];
		}

		public void remove() {
			throw new UnsupportedOperationException();
		}
	}
}
