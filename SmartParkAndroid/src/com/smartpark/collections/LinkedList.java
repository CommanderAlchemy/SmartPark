package com.smartpark.collections;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * This class creates a list of object of generic type
 * E and links them together by reference. This gives
 * the user flexible access to all elements of the list.
 * A programmer can add an element anywhere in the list,
 * and can access them by means of integer index. The
 * user can ask for the index of a particular element in
 * the list. A linked list can grow as needed and shrink
 * as elements are removed. a linked list is good to be
 * used for queues.
 * 
 * @param <E>
 * @author Saeed
 */

public class LinkedList<E> implements List<E>, Iterable<E> {
	// This is the reference (pointer) to the entire list.
	private ListNode<E> list;
	// This is the size of the list
	private int size;

	// Constructor
	/**
	 * This constructor initiates the class-variables with
	 * values that creates a empty LinkedList. The size of
	 * the list is set to 0 and the class refers to no list,
	 * by setting the list-variable to 'null'.
	 */
	public LinkedList() {
		this.list = null;
		this.size = 0;
	}

	private ListNode<E> locate(int index) {
		ListNode<E> node = list;
		for (int i = 0; i < index; i++)
			node = node.getNext();
		return node;
	}

	/**
	 * This returns the size of the LinkedList by returning
	 * an integer that must be properly maintained by the 
	 * LinkedList-class.
	 * 
	 * @return size, The size of the list.
	 */
	public int size() {
		return size;
	}

	// public int size() {
	// int n = 0;
	// ListNode<E> node = list;
	// while( node != null ) {
	// node = node.getNext();
	// n++;
	// }
	// return n;
	// }

	/**
	 * With this method the data-reference-variable of an
	 * element can be retrieved.
	 * 
	 * @param index The index for witch it has to return the data-variable
	 * @return E the returned data element og generic type E
	 */
	public E get(int index) {
		if ((index < 0) || (index >= size())) {
			throw new IndexOutOfBoundsException(
				"size=" + size() + ", index=" + index);
		}
		ListNode<E> node = locate(index);
		return node.getData();
	}

	/**
	 * This method insert new data into an element in the array.
	 * This method replaces the old data at the specified index
	 * with the new, and returns the replaced data.
	 * 
	 * @param index The index in which the data must be inserted
	 * @param data The data to be inserted
	 * @return E the replace data is returned
	 */
	public E set(int index, E data) {
		if ((index < 0) || (index >= size())) {
			throw new IndexOutOfBoundsException(
				"size=" + size() + ", index=" + index);
		}
		ListNode<E> node = locate(index);
		E oldData = node.getData();
		node.setData(data);
		return oldData;
	}

	/**
	 * This adds a new data to the list. It does that at the
	 * end of the list.
	 * 
	 * @param data The data to be added.
	 */
	public void add(E data) {
		add(size(), data);
	}

	/**
	 * This stores a given data at the beginning of the list.
	 * 
	 * @param data The data to be added.
	 */
	public void addFirst(E data) {
		add(0, data);
	}

	/**
	 * This inserts the given data at the end of the list.
	 * 
	 * @param data The data to be added.
	 */
	public void addLast(E data) {
		add(size(), data);
	}

	/**
	 * This adds the data at the index specified. The data
	 * placed at the index value and all other elements with
	 * higher index value is moved one index value higher to 
	 * make room for the data element.
	 * 
	 * @param index the index to insert the new data into.
	 * @param data the data to be stored in the list.
	 */
	public void add(int index, E data) {
		if ((index < 0) || (index > size())) {
			throw new IndexOutOfBoundsException("size=" + size() + ", index="
					+ index);
		}
		ListNode<E> nodeBefore, nodeAfter, newNode;

		if (index == 0) {
			// Creating a new node and passing the pointer of nodeAfter
			// that is to follow the new, to the new node created.
			list = new ListNode<E>(data, list);
		} else {
			// Locating the node at the specified index in the LinkedList
			nodeBefore = locate(index - 1);

			// Getting the pointer for the node following nodeBefore
			nodeAfter = nodeBefore.getNext();

			// Creating a new node and passing the pointer of nodeAfter
			// that is to follow the new, to the new node created.
			newNode = new ListNode<E>(data, nodeAfter);

			// The pointer to the newNode is passed to the node before it
			// this is called nodeBefore
			nodeBefore.setNext(newNode);
		}
		// Incrementing the integer that states the size of the LinkedList
		size++;
	}

	/**
	 * This will remove the first element from the list and return the
	 * pointer/reference to the elements data-object.
	 * 
	 * @return E element
	 */
	public E removeFirst() {
		return remove(0);
	}

	/**
	 * This removes an element from the end of the list and returns its
	 * data-pointer.
	 * 
	 * @return E data The reference-value to the data of the removed node
	 */
	public E removeLast() {
		return remove(size()-1);
	}

	/**
	 * This removes a node from the list and returning its value.
	 * 
	 * @param index node to be removed
	 * @return E node, the removed node
	 */
	public E remove(int index) {
		if ((index < 0) || (index >= size())) {
			throw new IndexOutOfBoundsException("size=" + size() + ", index=" + index);
		}
		E res;
		if (index == 0) {
			res = list.getData();
			list = list.getNext();
		} else {
			ListNode<E> node = locate(index - 1);
			res = node.getNext().getData();
			node.setNext(node.getNext().getNext());
		}
		size--;
		return res;
	}

	/**
	 * This removes all nodes from the linked list and sets the size of the
	 * array to 0.
	 */

	public void clear() {
		// Removes all nodes from the linked list by setting the linked list
		// reference-value to null and thereby not point to anything.
		list = null;
		size = 0;
	}

	/**
	 * this returns the index of the data in the linked list. If not found the
	 * method returns -1.
	 * 
	 * @param data
	 *            , the data to find the index of.
	 * @return index, the index of the data.
	 */
	public int indexOf(E data) {
		int n = 0;
		ListNode<E> node = list;
		while (node != null) {
			if (node.getData().toString().equals(data.toString())) {
				return n;
			} else {
				node = node.getNext();
				n++;
			}
		}
		return -1;
	}

	/**
	 * This method will return the index at which is finds the specified data.
	 * It will return -1 if the data doesn't exist in the list.
	 * 
	 * @param startIndex
	 *            , The point at which it starts looking for the index.
	 * @param E
	 *            data, The data to find the index for.
	 * @return index, The found index for the data.
	 */
	public int indexOf(int startIndex, E data) {
		int n = 0;
		ListNode<E> node = locate(startIndex);
		while (node != null) {
			if (node.getData().toString().equals(data.toString())) {
				return n + startIndex;
			} else {
				node = node.getNext();
				n++;
			}
		}
		return -1;
	}

	/**
	 * This will return an object of the internal iterator-class inside for
	 * iteration of the LinkedList.
	 * 
	 * @return Iterator<E>
	 */
	public Iterator<E> iterator() {
		return new Iter();
	}

	/**
	 * This class implements the interface Iterator<E> and is used to make the
	 * LinkedLink iterable.
	 */
	private class Iter implements Iterator<E> {
		private ListNode<E> currentNode = list;
		private int index = 0;

		/**
		 * Returns a boolean value after examining that the LinkedList contains
		 * at least one more element to return.
		 * 
		 * @return boolean true if more elements available
		 * @author Saeed
		 */
		public boolean hasNext() {
			return index < size;
		}
		
		/**
		 * Returns the next node in the LinkedList.
		 * 
		 * @return E the data returned at one iteration
		 */
		public E next() {
			if (index == size) {
				throw new NoSuchElementException();
			}
			E data = currentNode.getData();
			currentNode = currentNode.getNext();
			index++;
			return data;
		}

		/**
		 * This method is not implemented and will throw a
		 * UnsupportedOperationException if invoked.
		 * 
		 */
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}

	/**
	 * This method will print the content of all elements in the list and return
	 * this also as a String.
	 * 
	 * @return String all content of the list is returned as a String
	 */
	public String toString() {
		if (list != null)
			return list.toString();
		else
			return "[]";
	}

	public static void main(String[] args) {
		LinkedList<Integer> list = new LinkedList<Integer>();
		// for (int i = 0 ; i < 30 ; i++){
		// list.add(i);
		// }
		list.add(3);
		System.out.println(list);
		list.add(4);
		System.out.println(list);
		list.add(5);
		System.out.println(list);
		list.add(6);
		System.out.println(list);
		list.add(7);
		System.out.println(list);
		list.add(8);
		System.out.println(list);
		list.addLast(9);
		System.out.println(list);
		list.addLast(10);
		System.out.println(list);
		list.addFirst(11);
		System.out.println(list);
		list.add(3,80);
		System.out.println(list);
		System.out.println(list.size());
		list.set(3,70);
		System.out.println(list);
		
		System.out.println(list.get(3));
		
		System.out.println(list.remove(3));
		System.out.println(list);
		System.out.println(list.removeLast());
		System.out.println(list);
		System.out.println(list.removeFirst());
		System.out.println(list);
		list.addLast(100);
		System.out.println(list);
//		list.clear();
		System.out.println(list);
		list.addLast(3);
		System.out.println(list);
//		list.clear();
		System.out.println(list);
		list.addFirst(4);
		System.out.println(list);
//		list.clear();
		System.out.println(list);
		list.add(0,1);
		System.out.println(list);
		list.set(list.size()-1, 44444);
		System.out.println(list);
		list.set(4, 6);
		System.out.println(list);
		System.out.println(list.indexOf(6,6));
		
		Iterator iter = list.iterator();
		System.out.println(iter.hasNext());
		while (iter.hasNext()){
			System.out.println(iter.next());
		}

	}
}
