package bstmap;


import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class BSTMap<K extends Comparable<K>, V> implements Map61B<K, V> {

    private Node root;
    private int size;

    private class Node {
        private K key;
        private V value;
        private Node left, right;

        public Node(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }

    @Override
    public void clear() {
        root = null;
        size = 0;
    }

    @Override
    public boolean containsKey(K key) {
        return containsKey(key, this.root);
    }

    /** Recursive private function that search deep into the tree.
     * if currNode is null, this mean we have fall down from the last leaf of the tree,
     * thus this mean the item searched is not in the map */
    private boolean containsKey(K key, Node currNode) {
        if (currNode == null) {
            return false;
        }
        if (key.compareTo(currNode.key) < 0) {
            return containsKey(key, currNode.left);
        }
        if (key.compareTo(currNode.key) > 0) {
            return containsKey(key, currNode.right);
        }
        // if we are here, this means a match is found and we return true.
        return true;

    }

    @Override
    public V get(K key) {
        return get(key, this.root);
    }

    /** Recursive function to get value from the BSTMap.
    * if value is not found, return null. */
    private V get(K key, Node currNode) {
        if (currNode == null) {
            return null;
        }
        if (key.compareTo(currNode.key) < 0) {
            return get(key, currNode.left);
        }
        if (key.compareTo(currNode.key) > 0) {
            return get(key, currNode.right);
        }
        // if we are here, this means compareTo function return 0 which means we found the node for the key.
        return currNode.value;
    }

    @Override
    public int size() {
        return size;
    }

    private void increaseSize() {
        size += 1;
    }

    @Override
    public void put(K key, V value) {
        root = put(key, value, this.root);
    }

    /** This is the most confusing part for me, since honestly the most intuitive
     * way of doing this recursion in my head is what Josh described as "arms length" recursion.
     * need to do this pattern more.*/
    private Node put(K key, V value, Node currNode) {
        if (currNode == null) {
            increaseSize();
            return new Node(key, value);
        }
        if (key.compareTo(currNode.key) < 0) {
            currNode.left = put(key, value, currNode.left);
        } else if (key.compareTo(currNode.key) > 0) {
            currNode.right = put(key, value, currNode.right);
        }
        return currNode;
    }

    @Override
    public Set<K> keySet() {
        Set<K> set = new HashSet<>();
        fillSetWithDFS(root, set);
        return set;
    }

    /**  Preorder traversal with DFS, root -> left -> right */
    private void fillSetWithDFS(Node currNode, Set<K> set) {
        if (currNode == null) {
            return;
        }
        set.add(currNode.key);
        fillSetWithDFS(currNode.left, set);
        fillSetWithDFS(currNode.right, set);
    }

    @Override
    public V remove(K key) {
        throw new UnsupportedOperationException("Not implememented yet");
    }

    @Override
    public V remove(K key, V value) {
        throw new UnsupportedOperationException("Not implememented yet");
    }

    @Override
    public Iterator<K> iterator() {
        throw new UnsupportedOperationException("Not implememented yet");
    }


}
