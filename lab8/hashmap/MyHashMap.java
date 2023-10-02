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

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            Node otherNode = (Node) obj;

            return key.equals(otherNode.key);
        }

        @Override
        public int hashCode() {
            return Objects.hash(key);
        }
    }



    /* Instance Variables */
    private Collection<Node>[] buckets;
    // You should probably define some more!
    private double maxLoadFactor = 0.75;
    private double loadFactorToResizeDown = 0.125;
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
        maxLoadFactor = maxLoad;
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

    private boolean isBucketEmptyOrNull(Collection<Node> bucket) {
        return bucket == null || bucket.isEmpty();
    }

    @Override
    public boolean containsKey(K key) {
        int index = getIndexFromHashCode(key.hashCode());
        Collection<Node> bucket = buckets[index];
        if (isBucketEmptyOrNull(bucket)) {
            return false;
        }
        return bucket.contains(createNode(key, null));
    }

    @Override
    public V get(K key) {
        int index = getIndexFromHashCode(key.hashCode());
        Collection<Node> bucket = buckets[index];
        if (isBucketEmptyOrNull(bucket)) {
            return null;
        }
        for (Node node : bucket) {
            if (node.key.equals(key)) {
                return node.value;
            }
        }
        return null;
    }

    private void resize(float resizeFactor) {
        size = 0;
        Collection<Node>[] oldTable = buckets;
        buckets = new Collection[(int) Math.ceil(oldTable.length * resizeFactor)];
        for (Collection<Node> bucket : oldTable) {
            if (isBucketEmptyOrNull(bucket)) {
                continue;
            }
            for (Node node : bucket) {
                put(node.key, node.value);
            }
        }
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

        if (currentLoadFactor() >= maxLoadFactor) {
            resize(2);
        }
    }

    private double currentLoadFactor() {
        return (double) size / buckets.length;
    }


    @Override
    public V remove(K key) {
        int index = getIndexFromHashCode(key.hashCode());
        Collection<Node> bucket = buckets[index];
        V val = get(key);
        bucket.remove(createNode(key, null));
        if (currentLoadFactor() <= loadFactorToResizeDown) {
            resize(0.5F);
        }
        return val;
    }

    @Override
    public V remove(K key, V value) {
        int index = getIndexFromHashCode(key.hashCode());
        Collection<Node> bucket = buckets[index];
        V val = get(key);
        if (val.equals(value)) {
            bucket.remove(createNode(key, value));
        }
        if (currentLoadFactor() <= loadFactorToResizeDown) {
            resize(0.5F);
        }
        return val;
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
            while(isBucketEmptyOrNull(bucket) && bucketIndex < buckets.length - 1) {
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
        MyHashMap<String, Integer> hashMap = new MyHashMap<>(4);
        hashMap.put("Bla", 12);
        hashMap.put("Bleh", 13);
        hashMap.put("Richa", 13);
        System.out.println(hashMap.containsKey("Bla"));
        System.out.println(hashMap.containsKey("bla"));
        System.out.println(hashMap.containsKey("Bleh"));
        System.out.println(hashMap.containsKey("Richa"));


        System.out.println(hashMap.size);
        for (String s : hashMap) {
            System.out.println(s);
        }

    }

}
