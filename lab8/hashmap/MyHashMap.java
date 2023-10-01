package hashmap;

import java.util.*;

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
    // You should probably define some more!
    private double loadFactor = 0.75;
    private int size;  // actual total item inside.

    /** Constructors */
    public MyHashMap() {
        int initialSize = 16;
        buckets = createTable(initialSize);
    }

    public MyHashMap(int initialSize) {
        buckets = createTable(initialSize);
    }

    /**
     * MyHashMap constructor that creates a backing array of initialSize.
     * The load factor (# items / # buckets) should always be <= loadFactor
     *
     * @param initialSize initial size of backing array
     * @param maxLoad maximum load factor
     */
    public MyHashMap(int initialSize, double maxLoad) {
        loadFactor = maxLoad;
        buckets = createTable(initialSize);
    }

    /**
     * Returns a new node to be placed in a hash table bucket
     */
    private Node createNode(K key, V value) {
        return new Node(key, value);
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
        return new Collection[tableSize];
    }

    // TODO: Implement the methods of the Map61B Interface below
    // Your code won't compile until you do so!

    private int getIndexFromHashCode(int hashCode) {
        // use floorMod instead of % to avoid weird case like negative mod.
        // ex : we want -1 % 4 to be 3 instead of -1, that doesn't fit index.
        return Math.floorMod(hashCode, buckets.length);
    }

    @Override
    public void clear() {
        size = 0;
        for (int i = 0; i < buckets.length; i++) {
            buckets[i] = null;
        }
    }

    private boolean isBucketEmpty(Collection<Node> bucket) {
        return bucket == null || bucket.isEmpty();
    }

    @Override
    public boolean containsKey(K key) {
        int index = getIndexFromHashCode(key.hashCode());
        Collection<Node> bucket = buckets[index];
        if (isBucketEmpty(bucket)) {
            return false;
        }
        for (Node node : bucket) {
            if (node.key.equals(key)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public V get(K key) {
        int index = getIndexFromHashCode(key.hashCode());
        Collection<Node> bucket = buckets[index];
        if (isBucketEmpty(bucket)) {
            return null;
        }
        for (Node node : bucket) {
            if (node.key.equals(key)) {
                return node.value;
            }
        }
        return null;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void put(K key, V value) {
        int index = getIndexFromHashCode(key.hashCode());
        Collection<Node> bucket = buckets[index];
        if (bucket == null) {
            bucket = createBucket();
            buckets[index] = bucket;
        }
        for (Node node : bucket) {
            if (node.key.equals(key)) {
                node.value = value;
                return;
            }
        }
        bucket.add(createNode(key, value));
        size++;
    }

    @Override
    public Set<K> keySet() {
        Set<K> keySet = new HashSet<>();
        for (Collection<Node> currBucket : buckets) {
            if (currBucket == null) {
                continue;
            }
            for (Node node : currBucket) {
                keySet.add(node.key);
            }
        }
        return keySet;
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
        return new MyHashMapIterator();
    }



    private class MyHashMapIterator implements Iterator<K> {
        private int cursor;
        private int bucketIndex;
        private Iterator<Node> bucketIterator;

        public MyHashMapIterator() {
            cursor = 0;
            bucketIndex = -1;
            bucketIterator = nextNonEmptyIterator();
        }

        @Override
        public boolean hasNext() {
            return cursor < size;
        }

        @Override
        public K next() {
            if (bucketIterator.hasNext()) {
                Node node = bucketIterator.next();
                cursor++;
                return node.key;
            } else {
                bucketIterator = nextNonEmptyIterator();
                return next();
            }
        }

        /** Return the next possible non-empty iterator.
         * if there's no non-empty iterator left, return null. */
        private Iterator<Node> nextNonEmptyIterator(){
            bucketIndex++;
            Collection<Node> bucket = buckets[bucketIndex];
            while(isBucketEmpty(bucket) && bucketIndex < buckets.length - 1) {
                bucketIndex++;
                bucket = buckets[bucketIndex];
            }
            if (bucket != null && bucket.iterator().hasNext()) {
                return bucket.iterator();
            }
            return null;
        }




    }
    public static void main(String[] args) {
        MyHashMap<String, Integer> hashMap = new MyHashMap<>();
        hashMap.put("Bla", 12);
        hashMap.put("Bleh", 13);
        System.out.println(hashMap.size);
        for (String s : hashMap) {
            System.out.println(s);
        }

    }

}
