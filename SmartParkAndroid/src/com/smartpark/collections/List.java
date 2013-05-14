package com.smartpark.collections;
import java.util.Iterator;

public interface List<E> {
	
	/**
     * Appends the specified element to the end of this list 
     * @param element element to be appended to this list
     */	
	public void add(E element);

    /**
     * Inserts the specified element at the specified position in this list.
     * Shifts the element currently at that position (if any) and any
     * subsequent elements to the right (adds one to their indices).
     * @param index index at which the specified element is to be inserted
     * @param element element to be inserted
     */
	public void add(int index, E element);
	
	/**
     * Inserts the specified element at the beginning of this list 
     * @param element element to be inserted at the beginning of this list
     */	
	public void addFirst(E element);
	
	/**
     * Appends the specified element at the end of this list 
     * @param element element to be appended at the end of this list
     */	
	public void addLast(E element);

    /**
     * Removes the element at the specified position in this list. Shifts
     * any subsequent elements to the left (subtracts one from their 
     * indices).  Returns the element that was removed from the list.
     * @param index the index of the element to be removed
     * @return the element previously at the specified position
     */
	public E remove(int index);

    /**
     * Removes and returns the first element from this list.
     * @return the first element from this list
     */
	public E removeFirst();
	
    /**
     * Removes and returns the last element from this list.
     * @return the last element from this list
     */
	public E removeLast();

    /**
     * Removes all of the elements from this list. The list will be
     * empty after this call returns.
     */
	public void clear();
	
    /**
     * Returns the element at the specified position in this list.
     * @param index index of the element to return
     * @return the element at the specified position in this list
     */
	public E get(int index);
	
    /**
     * Replaces the element at the specified position in this list with the
     * specified element
     * @param index index of the element to replace
     * @param element element to be stored at the specified position
     * @return the element previously at the specified position
     */
	public E set(int index, E element);
	
    /**
     * Returns the index of the first occurrence of the specified element
     * in this list, or -1 if this list does not contain the element.
     * @param element element to search for
     * @return the index of the first occurrence of the specified element in
     *         this list, or -1 if this list does not contain the element
     */
	public int indexOf(E element);
	
    /**
     * Returns the index of the first occurrence of the specified element
     * in this list, or -1 if this list does not contain the element. The
     * search begins at startIndex in the list.
     * @param startIndex the search starts at position startIndex in the list
     * @param element element to search for
     * @return the index of the first occurrence of the specified element in
     *         this list, or -1 if this list does not contain the element
     */
	public int indexOf(int startIndex, E element);
	
    /**
     * Returns an iterator over the elements in this list in proper sequence.
     * @return an iterator over the elements in this list in proper sequence
     */
	public Iterator<E> iterator();
	
    /**
     * Returns the number of elements in this list.  
     * @return the number of elements in this list
     */
	public int size();
}
