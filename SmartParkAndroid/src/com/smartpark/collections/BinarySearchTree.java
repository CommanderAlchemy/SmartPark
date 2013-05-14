package com.smartpark.collections;

import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;

import com.smartpark.other.Action;

/**
 * This class represent a searchTree were every node in the tree can only have max 2 childs and 1 parent.
 * The elements are sorted in order too.
 * @author Saeed Mir Ghasemi
 * @author Truls Olof Haraldsson
 * @author Artur Kim Olech
 *
 * @param <K> Key for the root node
 * @param <V> Value for the root node
 */

public class BinarySearchTree<K, V> implements SearchTree<K, V> {
	private Comparator<K> comparator;
	private BSTNode<K, V> tree;
	private int size;

	/** 
	 * Initiate a binarySearchTree with a default comparator
	 */
	public BinarySearchTree() {
		comparator = new Comp();
	}
	
	/**
	 * Initiate a binarySearchTree with a specific comparator <tt> comp
	 * @param comp
	 */
	public BinarySearchTree(Comparator<K> comp) {
		comparator = comp;
	}

	/**
	 * @return The node that acts like the root in the bindarySearchTree
	 */
	public BSTNode<K, V> root() {
		return tree;
	}
	
	public V get(K key) {
		BSTNode<K, V> node = find(key);
		if (node != null)
			return node.value;
		return null;
	}

	public void put(K key, V value) {
		tree = put(tree, key, value);
		size++;
	}

	public V remove(K key) {
		V value = get(key);
		if (value != null) {
			tree = remove(tree, key);
			size--;
		}
		return value;
	}

	public boolean contains(K key) {
		return find(key) != null;
	}

	public int height() {
		return height(tree);
	}

	public Iterator<V> iterator() {
		return new Iter();
	}

	/** 
	 * Finds the node that is mapped with the specific <tt> key
	 * @param key
	 * @return the node 
	 */
	private BSTNode<K, V> find(K key) {
		int res;
		BSTNode<K, V> node = tree;
		while ((node != null)
				&& ((res = comparator.compare(key, node.key)) != 0)) {
			if (res < 0)
				node = node.left;
			else
				node = node.right;
		}
		return node;
	}
	/**
	 * Add an element with the key <tt> key <tt> and value <tt> value <tt> to the specifc node <tt> node <tt>
	 * @param key 
	 * @param value 
	 * @param node
	 */
	private BSTNode<K, V> put(BSTNode<K, V> node, K key, V value) {
		if (node == null) {
			node = new BSTNode<K, V>(key, value, null, null);
		} else {
			if (comparator.compare(key, node.key) < 0) {
				node.left = put(node.left, key, value);
			} else if (comparator.compare(key, node.key) > 0) {
				node.right = put(node.right, key, value);
			}
		}
		return node;
	}
	/**
     * Removes the value with the specific <tt> key from the subtree
     * @param key
     * @param subtree
     * @return the previous value
     */
	private BSTNode<K, V> remove(BSTNode<K, V> subTree, K key) {
		int compare = comparator.compare(key, subTree.key);
		if (compare == 0) {
			if (subTree.left == null && subTree.right == null)
				subTree = null;
			else if (subTree.left != null && subTree.right == null)
				subTree = subTree.left;
			else if (subTree.left == null && subTree.right != null)
				subTree = subTree.right;
			else {
				BSTNode<K, V> min = getSuccessor(subTree.right);
				min.right = remove(subTree.right, min.key);
				min.left = subTree.left;
				subTree = min;
			}
		} else if (compare < 0) {
			subTree.left = remove(subTree.left, key);
		} else {
			subTree.right = remove(subTree.right, key);
		}
		return subTree;
	}

	private BSTNode<K, V> getSuccessor(BSTNode<K, V> successor) {
		while (successor.left != null)
			successor = successor.left;
		return successor;
	}
	
	/**
     * Tells you the height of the subTree
     * @param node there the subtree start
     * @return the hight of the tree 
     */
	private int height(BSTNode<K, V> node) {
		if (node == null)
			return -1;
		return 1 + Math.max(height(node.left), height(node.right));
	}

	public int size() {
		return size;
	}

	public int size1() {
		if (tree == null)
			return 0;
		return tree.size();
	}

	public int size2() {
		return size2(tree);
	}

	public int size2(BSTNode<K, V> node) {
		if (node == null)
			return 0;
		return 1 + size2(node.left) + size2(node.right);
	}

	public List<K> keys() {
		ArrayList<K> list = new ArrayList<K>();
		keys(tree, list);
		return list;
	}
	
	/** 
	 * The method goes through the binarySearchTree and store the keys in an ArrayList <tt> list
	 * @param node
	 * @param list
	 */
	private void keys(BSTNode<K, V> node, ArrayList<K> list) {
		if (node != null) {
			keys(node.left, list);
			list.add(list.size(), node.key); 
			keys(node.right, list);
		}
	}

	public List<V> values() {
		LinkedList<V> list = new LinkedList<V>();
		values(tree, list);
		return list;
	}

	private void values(BSTNode<K, V> node, LinkedList<V> list) {
		if (node != null) {
			values(node.left, list);
			values(node.right, list);
			list.add(list.size(), node.value);
		}
	}

	public V first() {
		BSTNode<K, V> node = tree;
		if (node == null)
			return null;
		while (node.left != null) {
			node = node.left;
		}
		return node.value;
	}

	public V last() {
		BSTNode<K, V> node = tree;
		if (node == null)
			return null;
		while (node.right != null) {
			node = node.right;
		}
		return node.value;
	}

	/** 
	 * Prints out the BinarySearchTree
	 */
	public void print() {
		print(tree);
	}
	
	/**
	 * Prints out the BinarySearchTree with the start at <<tt>> node
	 * @param node
	 */
	private void print(BSTNode<K, V> node) {
		if (node != null) {
			print(node.left);
			System.out.println(node.key + ", " + node.value);
			print(node.right);
		}
	}

	/**
	 * Prints out the BindarySearchTree in preorder
	 */
	public void printPreorder() {
		printPreorder(tree);
	}
	
	/**
	 * Prints out the BindarySearchTree with the start at <<tt>> node in preorder
	 */
	private void printPreorder(BSTNode<K, V> node) {
		if (node != null) {
			System.out.println(node.key + ", " + node.value);
			printPreorder(node.left);
			printPreorder(node.right);
		}
	}
	
	/**
	 * Prints out the BindarySearchTree in postorder
	 */
	public void printPostorder() {
		printPostorder(tree);
	}
	
	/**
	 * Prints out the BindarySearchTree in postorder with the start at <<tt>> node
	 */
	private void printPostorder(BSTNode<K, V> node) {
		if (node != null) {
			printPostorder(node.left);
			printPostorder(node.right);
			System.out.println(node.key + ", " + node.value);
		}
	}

	public void printLevelOrder() {
		ArrayQueue<BSTNode<K, V>> queue = new ArrayQueue<BSTNode<K, V>>();
		BSTNode<K, V> node;
		queue.enqueue(tree);
		while (!queue.empty()) {
			node = queue.dequeue();
			System.out.print(node.value + " ");
			if (node.left != null)
				queue.enqueue(node.left);
			if (node.right != null)
				queue.enqueue(node.right);
		}
	}

	private class Comp implements Comparator<K> {
		public int compare(K key1, K key2) {
			Comparable<K> k1 = (Comparable<K>) key1;
			return k1.compareTo(key2);
		}
	}
	
	/**
	 * The Class lets you iterate through the binarySearchTree in order
	 * implements Iterator
	 */
	private class Iter implements Iterator<V> {
		ArrayList<V> list = new ArrayList<V>();
		int index = -1;

		public Iter() {
			inOrder(tree);
		}

		private void inOrder(BSTNode<K, V> node) {
			if (node != null) {
				inOrder(node.left);
				list.add(node.value);
				inOrder(node.right);
			}
		}

		public boolean hasNext() {
			return index < list.size() - 1;
		}

		public V next() {
			if (!hasNext())
				throw new NoSuchElementException();
			index++;
			return list.get(index);
		}

		public void remove() {
			throw new UnsupportedOperationException();
		}
	}

	/**
	 * The method lets you go through your subTree starting at <<tt>> node
	 * @param node
	 * @param action
	 */
	private void traverse(BSTNode<K, V> node, Action<V> action) {
		if (node != null) {
			traverse(node.left, action);
			action.action(node.value);
			traverse(node.right, action);
		}
	}

	/**
	 * The method lets you go through the whole BinarySearchTree
	 * @param action
	 */
	public void traverse(Action<V> action) {
		traverse(tree, action);
	}

	/**
	 * The class collects all the values in the binarySearchTree and add them to a linkedList.
	 * The class implements Action
	 */
	public class CollectValues implements Action<V> {
		private LinkedList<V> values = new LinkedList<V>();

		public List<V> getValues() {
			return values;
		}

		public void action(V value) {
			values.add(values.size(), value);
		}
	}
	
	/**
	 * The Class collects all the even values in the BinarySearchTree and add them to a linkedList.
	 * The class extends CollectValues
	 */
	public class CollectEvenValues<T> extends CollectValues {
		public void action(V obj) {
			if (obj instanceof Integer && ((Integer) obj) % 2 == 0)
				super.action(obj);
		}
	}

//	public static void main(String[] args) {
//		BinarySearchTree<String, String> tree = new BinarySearchTree<String, String>();
//		tree.put("karta", "map");
//		tree.put("vacker", "beautiful");
//		tree.put("svart", "black");
//		tree.put("lärare", "teacher");
//		tree.put("boll", "ball");
//		tree.put("vit", "white");
//		tree.put("hus", "house");
//		tree.put("vänster", "left");
//		tree.put("höger", "right");
//		tree.root().showTree();
//		String res = (String) tree.get("lärare");
//		System.out.println(res);
//		System.out.println(tree.get("LÄRARE"));
//		System.out.println("---------------------");
//		Iterator<String> elements = tree.iterator();
//		while (elements.hasNext()) {
//			System.out.println(elements.next());
//		}
//	}
}
