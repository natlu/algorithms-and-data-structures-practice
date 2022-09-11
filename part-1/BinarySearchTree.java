/*
princeton notes
*/

import java.util.LinkedList;
import java.util.Queue;

public class BinarySearchTree<Key extends Comparable<Key>, Value> {
    private Node root;

    private class Node {
        private Key key;
        private Value value;
        private Node left, right;
        private int count;

        public Node(Key key, Value value) {
            this.key = key;
            this.value = value;
            this.count = 1;
        }
    }

    public void put(Key key, Value val) {
        root = put(root, key, val);
    }

    public Node put(Node node, Key key, Value val) {
        if (node == null) return new Node(key, val);
        int compareVal = key.compareTo(node.key);
        if (compareVal < 0) node.left = put(node.left, key, val);
        else if (compareVal > 0) node.right = put(node.right, key, val);
        else node.value = val;
        node.count = 1 + size(node.right) + size(node.left);
        return node;
    }

    public int size() {
        return size(root);
    }

    public int size(Node node) {
        if (node == null) return 0;
        return node.count;
    }


    public Value get(Key key) {
        Node node = root;
        while (node != null) {
            int compareVal = key.compareTo(node.key);
            if (compareVal < 0) node = node.left;
            else if (compareVal > 0) node = node.right;
            else return node.value;
        }
        return null;
    }

    public Key floor(Key key) {
        Node ans = floor(root, key);
        if (ans == null) return null;
        return ans.key;
    }

    public Node floor(Node node, Key key) {
        if (node == null) return null;
        int compareVal = key.compareTo(node.key);
        if (compareVal == 0) return node;
        else if (compareVal < 0) return floor(node.left, key);
        else { // if compareVal > 0
            Node res = floor(node.right, key);

            // if the next looked at node is null,
            // then the floor must be the current node
            if (res == null) return node;

            return res;
        }
    }

    public Key ceiling(Key key) {
        Node ans = ceiling(root, key);
        if (ans == null) return null;
        return ans.key;
    }

    public Node ceiling(Node node, Key key) {
        if (node == null) return null;
        int compareVal = key.compareTo(node.key);
        if (compareVal == 0) return node;
        else if (compareVal > 0) return ceiling(node.right, key);
        else { // if compareVal < 0
            Node res = ceiling(node.left, key);
            if (res == null) return node;
            return res;
        }
    }

    public void deleteMin() {
        root = deleteMin(root);
    }

    private Node deleteMin(Node x) {
        if (x.left == null) return x.right;
        x.left = deleteMin(x.left);
        x.count = 1 + size(x.left) + size(x.right);
        return x;
    }

    public void delete(Key key) {
        root = delete(root, key);
    }

    public Node delete(Node node, Key key) {
        if (node == null) return null;

        int compareVal = key.compareTo(node.key);
        if (compareVal < 0) node.left = delete(node.left, key);
        else if (compareVal > 0) node.right = delete(node.right, key);
        else {
            // takes care of 0 and 1 children
            if (node.left == null) return node.right;
            if (node.right == null) return node.left;

            Node toReplace = node;
            Node successor = min(toReplace);
            successor.right = deleteMin(toReplace.right); // delete min from the right subtree
            successor.left = toReplace.left;
        }
        node.count = 1 + size(node.left) + size(node.right);
        return node;
    }

    public Node min(Node node) {
        if (node == null) return null;
        Node res = min(node.left);
        if (res == null) return node;
        return res;
    }

    public Iterable<Key> keys() {
        Queue<Key> q = new LinkedList<>();
        inorder(root, q);
        return q;
    }

    private void inorder(Node x, Queue<Key> q) {
        if (x == null) return;
        inorder(x.left, q);
        q.add(x.key);
        inorder(x.right, q);
    }


    public static void main(String[] args) {

        BinarySearchTree<Integer, String> bst = new BinarySearchTree<>();
        bst.put(5, "55");
        bst.put(3, "33");
        bst.put(8, "88");
        bst.put(6, "66");
        bst.put(9, "99");
        bst.put(10, "00");

        System.out.println(bst.ceiling(7));

        System.out.println(bst.size());

//        System.out.println("min ------------------");

        System.out.println("------------------");
        for (Integer k : bst.keys()) {
            System.out.println(k);
        }
    }
}
