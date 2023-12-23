package hashmap;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;
import java.util.HashSet;

/**
 *  A hash table-backed Map implementation. Provides amortized constant time
 *  access to elements via get(), remove(), and put() in the best case.
 *
 *  Assumes null keys will never be inserted, and does not resize down upon remove().
 *  @author YOUR NAME HERE
 */
public class MyHashMap<K, V> implements Map61B<K, V> {

    /**
     * Protected helper class to store key/value pairs
     * The protected qualifier allows subclass access
     */
    protected class Node {
        K key;
        V value;

        Node(K k, V v) {
            key = k;
            value = v;
        }
    }

    /* Instance Variables */
    private Collection<Node>[] buckets;
    private int size;
    private int load;
    private double loadFactor;
    private Set<K> keys;
    // You should probably define some more!

    /** Constructors */
    public MyHashMap() {
        this.loadFactor = 0.75;
        this.size = 16;
        this.load = 0;
        this.buckets = new Collection[this.size];
        this.keys = new HashSet<>();
    }

    public MyHashMap(int initialSize) {
        this.loadFactor = 0.75;
        this.size = initialSize;
        this.load = 0;
        this.buckets = new Collection[this.size];
        this.keys = new HashSet<>();
    }

    /**
     * MyHashMap constructor that creates a backing array of initialSize.
     * The load factor (# items / # buckets) should always be <= loadFactor
     *
     * @param initialSize initial size of backing array
     * @param maxLoad maximum load factor
     */
    public MyHashMap(int initialSize, double maxLoad ) {
        this.loadFactor = maxLoad;
        this.size = initialSize;
        this.load = 0;
        this.buckets = new Collection[this.size];
        this.keys = new HashSet<>();
    }

    /**
     * Returns a new node to be placed in a hash table bucket
     */
    private Node createNode(K key, V value) {
        return null;
    }

    /**
     * Returns a data structure to be a hash table bucket
     *
     * The only requirements of a hash table bucket are that we can:
     *  1. Insert items (`add` method)
     *  2. Remove items (`remove` method)
     *  3. Iterate through items (`iterator` method)
     *
     * Each of these methods is supported by java.util.Collection,
     * Most data structures in Java inherit from Collection, so we
     * can use almost any data structure as our buckets.
     *
     * Override this method to use different data structures as
     * the underlying bucket type
     *
     * BE SURE TO CALL THIS FACTORY METHOD INSTEAD OF CREATING YOUR
     * OWN BUCKET DATA STRUCTURES WITH THE NEW OPERATOR!
     */
    protected Collection<Node> createBucket() {
        return new LinkedList<>();
    }

    /**
     * Returns a table to back our hash table. As per the comment
     * above, this table can be an array of Collection objects
     *
     * BE SURE TO CALL THIS FACTORY METHOD WHEN CREATING A TABLE SO
     * THAT ALL BUCKET TYPES ARE OF JAVA.UTIL.COLLECTION
     *
     * @param tableSize the size of the table to create
     */
    private Collection<Node>[] createTable(int tableSize) {
        return null;
    }

    // TODO: Implement the methods of the Map61B Interface below
    // Your code won't compile until you do so!

    @Override
    public void clear() {
        this.buckets = new Collection[16];
        this.loadFactor = 0.75;
        this.size = 16;
        this.load = 0;
        this.keys = new HashSet<>();

    }

    @Override
    public boolean containsKey(K key) {
        return this.keys.contains(key);
    }

    @Override
    public V get(K key) {
        if (!keys.contains(key)) {return null;}

        int index = Math.floorMod(key.hashCode(),this.size);
        for (Node item:this.buckets[index]) {
            if (item.key.equals(key)) {
                return item.value;
            }
        }
        return null;
    }

    @Override
    public int size() {
        return this.load;
    }

    @Override
    public void put(K key, V value) {
        // check if already in
        if (containsKey(key)) {
            int index = Math.floorMod(key.hashCode(),this.size);
            for (Node item:this.buckets[index]) {
                if (item.key.equals(key)) {
                    item.value = value;
                }
            }
            return;
        }

        // resize
        if (((double) this.load / this.size) >= this.loadFactor) {
            resize();
        }

        int index = Math.floorMod(key.hashCode(), this.size);
        if (this.buckets[index] == null) {
            this.buckets[index] = createBucket();
        }

        this.buckets[index].add(new Node(key, value));
        this.load++;
        this.keys.add(key);
    }

    private void resize() {
        MyHashMap<K, V> newHashMap = new MyHashMap<>(this.size * 2);
        for (K k : this.keySet()) {
            newHashMap.put(k, this.get(k));
        }

        // Update the current map with the resized one
        this.buckets = newHashMap.buckets;
        this.size = newHashMap.size;
        this.load = newHashMap.load;
        this.keys = newHashMap.keys;
    }


    @Override
    public Set<K> keySet() {
       return this.keys;
    }

    @Override
    public V remove(K key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public V remove(K key, V value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterator<K> iterator() {
        return new HashMapIterator();
    }

    private class HashMapIterator implements Iterator <K> {
        private Iterator<K> keysIterator = keys.iterator();

        @Override
        public boolean hasNext() {
            return keysIterator.hasNext();
        }

        @Override
        public K next() {
            return keysIterator.next();
        }
    }

}
