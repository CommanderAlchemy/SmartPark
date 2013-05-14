package com.smartpark.collections;
import java.util.Iterator;

/** 
 * @author Saeed Mir Ghasemi
 * @author Truls Olof Haraldsson
 * @author Artur Kim Olech
 */

public interface SearchTree<K,V> {
	
	/**
	 * Add an element with the key <tt> key <tt> and value <t> value <t> to the search tree
	 * @param key 
	 * @param value 
	 */
    public void put(K key, V value);
    
    /**
     * Remove the element with the specific key
     * @param key
     * @return returns the removed element
     */
    public V remove(K key);
    
    /**
     * Removes the value with the specific <tt> key from the searchTree
     * @param key
     * @return the previous value
     */
    public V get(K key);
    
    /**
     * Search the searchTree if it contains an element with the key <tt> key
     * @param key
     * @return trus if elements exsist else false
     */
    public boolean contains(K key);
    
    /**
     * Tells you the height of the searchTree
     * @return the hight of the tree 
     */
    public int height();
    
    /**
     * Iterates through the searchtree
     * @return
     */
    public Iterator<V> iterator();
    
    /**
     * 
     * @return the amount of elements storded in the searchtree
     */
    public int size();
    
    /**
     * 
     * @return A list of all the keys mapped in the searchtree
     */
    public List<K> keys();
    
    /**
     *
     * @return A list of all the values mapped in the searchtree
     */
    public List<V> values();
    
    /** 
     * 
     * @return the first element from the search tree (the lowest)
     */
    public V first();
    
    /** 
     * 
     * @return the last element from the search tree (the highest)
     */
    public V last();
}
