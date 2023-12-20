package bstmap;

import java.util.Iterator;
import java.util.Set;

public class BSTMap<K extends Comparable<K>, V> implements Map61B<K, V> {
    private BSTNode root;
    private int size;
    private class BSTNode {
        K key;
        V value;
        BSTNode left;
        BSTNode right;

        public BSTNode(K key, V value) {
            this.key = key;
            this.value = value;
            this.left = null;
            this.right = null;
        }
    }


    public void clear() {
        root = null;
        size = 0;
    }

    @Override
    public boolean containsKey(K key) {
        return containsKey(root, key) != null;
    }

    private K containsKey(BSTNode Node, K Key) {
        // empty tree, search miss
        if (Node == null) {
            return null;
        }

        int cmp = Key.compareTo(Node.key);

        if (cmp > 0) {
            return containsKey(Node.right, Key);
        } else if (cmp < 0) {
            return containsKey(Node.left, Key);
        } else {
            return Node.key;
        }
    }

    public V get(K key) {
        return get(root, key);

    }

    private V get(BSTNode Node, K Key) {
        // empty tree, search miss
        if (Node == null) {
            return null;
        }

        int cmp = Key.compareTo(Node.key);

        if (cmp > 0) {
            return get(Node.right, Key);
        } else if (cmp < 0) {
            return get(Node.left, Key);
        } else {
            return Node.value;
        }
    }

    public int size() {
        return size;
    }

    @Override
    public void put(K key, V value) {
        root = put(root, key, value);
    }

    private BSTNode put(BSTNode Node, K key, V val) {
        // Change key’s value to val if key in subtree rooted at x.
        // Otherwise, add new node to subtree associating key with val.
        if (Node == null){
            size += 1;
            return new BSTNode(key, val);
        }
        int cmp = key.compareTo(Node.key);

        if (cmp < 0) {
            Node.left = put(Node.left, key, val);
        } else if (cmp > 0) {
            Node.right = put(Node.right, key, val);
        } else {
            // Update the value for an existing key.
            Node.value = val;
            return Node; // No need to increment size here.
        }
        return Node;
    }

    public Set<K> keySet() {
        throw new UnsupportedOperationException("This operation is not supported");
    }

    @Override
    public V remove(K key) {
        return null;
    }

    @Override
    public V remove(K key, V value) {
        return null;
    }
    public void printInOrder() {
        throw new UnsupportedOperationException("This operation is not supported");
    }

    public Iterator<K> iterator() {
        throw new UnsupportedOperationException("This operation is not supported");
    }

}
