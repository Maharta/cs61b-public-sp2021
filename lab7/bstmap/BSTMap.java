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

    private void increaseSize() {
        size += 1;
    }

    private void decreaseSize() {
        size -= 1;
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
        int cmp = key.compareTo(currNode.key);
        if (cmp < 0) {
            return containsKey(key, currNode.left);
        }
        if (cmp > 0) {
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
        int cmp = key.compareTo(currNode.key);
        if (cmp < 0) {
            return get(key, currNode.left);
        }
        if (cmp > 0) {
            return get(key, currNode.right);
        }
        // if we are here, this means compareTo function return 0 which means we found the node for the key.
        return currNode.value;
    }

    @Override
    public int size() {
        return size;
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
        int cmp = key.compareTo(currNode.key);
        if (cmp < 0) {
            currNode.left = put(key, value, currNode.left);
        } else if (cmp > 0) {
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
        // this dummy node will bring the deleted value of the node, in case a node gets deleted.
        Node removedNodeBringer = new Node(null, null);
        root = remove(key, root, removedNodeBringer);
        return removedNodeBringer.value;
    }

    /** Removal of a node is essentially this: remove the pointer (whether left or right) of the parent
     * that points to the node, then the node will be collected by the garbage collection. */
    private Node remove(K key, Node currNode, Node removedNodeBringer) {
        if (currNode == null) {
            return null;
        }

        int cmp = key.compareTo(currNode.key);
        if (cmp < 0) {
            currNode.left = remove(key, currNode.left, removedNodeBringer);
        } else if (cmp > 0) {
            currNode.right = remove(key, currNode.right, removedNodeBringer);
        } else {
            // found the node to be deleted
            nodeCopy(currNode, removedNodeBringer);
            decreaseSize();

            // check case whether it has 0, 1, or 2 children.
            // these 2 ifs account the case for 1 or 0 children. If it has 1 child, point the parent pointer to its child. if 0 children, this if will point it's parent pointer to null.
            if (currNode.left == null) {
                return currNode.right;
            }
            if (currNode.right == null) {
                return currNode.left;
            }
            // if the node to be deleted has 2 children: copy successor (smallest value) of the right subtree -
            // of the to be deleted node to take place of the deleted node.
            Node min = min(currNode.right);
            // removing min will always be case 1: 0 children or case 2: 1 children. A min key in a tree can't have 2 children (impossible)
            remove(min.key, currNode.right, null);
            // the successor (min from right subtree) will replace the deleted Node now.
            nodeCopy(min, currNode);
        }
        return currNode;
    }

    /** finds the smallest Node of a tree or even subtree */
    private Node min(Node root) {
        if (root.left != null) {
            return min(root.left);
        }
        return root;
    }

    private void nodeCopy(Node from, Node to) {
        if (from == null || to == null) {
            return;
        }
        to.key = from.key;
        to.value = from.value;
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
