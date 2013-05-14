package com.smartpark.collections;

/**
* The class represents a queue were you can add and remove elements.
* @author Saeed Mir Ghasemi
* @author Truls Olof Haraldsson
* @author Artur Kim Olech
*/

public class ArrayQueue<E> implements Queue<E> {
    private E[] elements;
    private int size = 0;
    private int front = 0;
    
    
    /**
     * Creates an ArrayQueue with the capacity 10
     */
    public ArrayQueue(){
    	this(10);
    }
    
    /**
     * Creates an ArrayQueue with the capacity <tt> capacity
     * @param capacity
     */
    public ArrayQueue(int capacity) {
        elements = (E[])new Object[ capacity ];
    }
    
    /**
     * Sets the capacity of the ArrayQueue to <tt> capacity
     * @param capacity
     */
    private void setCapacity(int capacity){
    	capacity = (capacity < size)? size : capacity;
    	E[] temp = (E[])new Object[ capacity ];
    	int elementAt = front;
    	while (!(elementAt == front + size)){
    		temp[elementAt] = elements[elementAt];
    		elementAt++;
    	}
    	elements = temp;
    }
    
    public void enqueue( E elem ) {
        if( size == elements.length) {
        	throw new QueueException("enqueue: Queue is full");
        	
        }else if(front + size >= elements.length){
        	elements[front + size - elements.length] = elem;
        	
        }else{
            elements[ front + size++ ] = elem;
        }
        
        System.out.println(size + " size " + front + " front");
        
        
    }
    
    
    public E dequeue() {
        if(size==0) {
            throw new QueueException("dequeue: Queue is empty");
        }
        E elem = elements[front++];
        size--;
        if(front == elements.length){
        	front = 0;
        }
        System.out.println(size + " size " + front + " front");
        return elem;
    }
    
    public E peek() {
        if( size==0 ) {
            throw new QueueException("peek: Queue is empty");
        }
        return elements[ front ];
    }
    
    public boolean empty() {
        return (size==0);
    }
    
    public int size() {
        return size;
    }
    
    /**
	 * This method goes through the whole list and appends their
	 * toString-methods outputs to a variable, and it then later prints it all.
	 * 
	 * @return String toString outputs of all elements
	 */
	public String toString() {
		StringBuilder res = new StringBuilder("[ ");
		for (int i = front; i < front + size; i++) {
			res.append(elements[i]);
			if (i <= front + size - 2)
				res.append("; ");
		}
		res.append(" ]");
		return res.toString();
	}

	
	
	
    
    
    
    public static void main(String[] args){
    	ArrayQueue<Integer> e = new ArrayQueue<Integer>();
    	System.out.println(e.empty());
    	System.out.println(e.size() + " size");
    	e.enqueue(2);
    	e.enqueue(3);
    	System.out.println(e);
    	System.out.println(e.size() + " size");

    	e.enqueue(4);
    	e.enqueue(5);
    	e.enqueue(6);
    	e.enqueue(7);
    	System.out.println(e);
    	System.out.println();
    	
    	System.out.println(e.dequeue());
    	System.out.println(e);
    	System.out.println(e.dequeue());
    	System.out.println(e);
    	System.out.println(e.peek());
    	System.out.println(e);
    	System.out.println(e.empty());
    	System.out.println(e.size());
    	
    }
}
