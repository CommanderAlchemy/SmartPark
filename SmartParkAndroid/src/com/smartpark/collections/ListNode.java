package com.smartpark.collections;

/**
 * This class is used by LinkedList to create nodes and store the reference
 * of both the next link in the collection and the reference to the object
 * containing the data.
 * 
 * @author Saeed Mir Ghasemi
 * @author Truls Olof Haraldsson
 * @author Artur Kim Olech
 * @param <E>
 */

public class ListNode<E> {
    private E data;
    private ListNode<E> next;
    
    /**
     * The constructor sets the reference-variables of data with generic type E, which
     * holds the reference to an object the data, and next which holds the reference
     * for the next link in the list.
     * 
     * @param data
     * @param next
     */
   
    /**
     * Constructor
     * @param data
     * @param next
     */
    public ListNode( E data, ListNode<E> next ) {
        this.data = data;
        this.next = next;
    }
    
    /**
     * Get Data
     * @return
     */
    public E getData() {
        return this.data;
    }
    
    /**
     * Set Data
     * @param data
     */
    public void setData( E data ) {
        this.data = data;
    }
    
    /**
     * Get Next
     * @return
     */
    public ListNode<E> getNext() {
        return this.next;
    }
    
    /**
     * Set Next
     * @param next
     */
    public void setNext( ListNode<E> next ) {
        this.next = next;
    }
    
    /**
     * toString Method
     */
    public String toString() {
    	StringBuilder str = new StringBuilder("[ ");
    	str.append(data.toString());
    	ListNode<E> node = next;
        while( node != null ) {
        	str.append( "; ");
            str.append( node.getData().toString() );
            node = node.getNext();
        }
        str.append( " ]");
        return str.toString();
    }
}