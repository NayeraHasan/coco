import java.util.concurrent.locks.ReentrantLock;

class Node {
    int value;
    Node left, right;

    Node(int value) {
        this.value = value;
        left = right = null;
    }
}

public class ConcurrentBST {
    private Node root;
    private final ReentrantLock lock = new ReentrantLock(true);  // Fair mode

    // Intentional corruption method as required
    public void corruptNode(int targetValue, int newValue) {
        lock.lock();
        try {
            Node node = findNode(root, targetValue);
            if (node != null) {
                // Directly change the value without maintaining BST property
                node.value = newValue;
            }
        } finally {
            lock.unlock();
        }
    }

    // Helper method to find a node with a specific value
    private Node findNode(Node node, int value) {
        if (node == null) return null;
        if (node.value == value) return node;
        if (value < node.value) return findNode(node.left, value);
        return findNode(node.right, value);
    }

    public void add(int value) {
        lock.lock();
        try {
            root = insert(root, value);
        } finally {
            lock.unlock();
        }
    }

    private Node insert(Node node, int value) {
        if (node == null) return new Node(value);
        if (value < node.value) node.left = insert(node.left, value);
        else if (value > node.value) node.right = insert(node.right, value);
        return node;
    }

    public boolean contains(int value) {
        lock.lock();
        try {
            return search(root, value);
        } finally {
            lock.unlock();
        }
    }

    private boolean search(Node node, int value) {
        if (node == null) return false;
        if (value == node.value) return true;
        return value < node.value ? search(node.left, value) : search(node.right, value);
    }

    public void remove(int value) {
        lock.lock();
        try {
            root = delete(root, value);
        } finally {
            lock.unlock();
        }
    }

    private Node delete(Node node, int value) {
        if (node == null) return null;
        if (value < node.value) node.left = delete(node.left, value);
        else if (value > node.value) node.right = delete(node.right, value);
        else {
            if (node.left == null) return node.right;
            if (node.right == null) return node.left;
            Node successor = minValueNode(node.right);
            node.value = successor.value;
            node.right = delete(node.right, successor.value);
        }
        return node;
    }

    private Node minValueNode(Node node) {
        while (node.left != null) node = node.left;
        return node;
    }

    public void printInOrder() {
        lock.lock();
        try {
            inOrder(root);
            System.out.println();
        } finally {
            lock.unlock();
        }
    }

    private void inOrder(Node node) {
        if (node == null) return;
        inOrder(node.left);
        System.out.print(node.value + " ");
        inOrder(node.right);
    }

    public boolean checkRep() {
        lock.lock();
        try {
            return isBST(root, Integer.MIN_VALUE, Integer.MAX_VALUE);
        } finally {
            lock.unlock();
        }
    }

    private boolean isBST(Node node, int min, int max) {
        if (node == null) return true;
        if (node.value <= min || node.value >= max) return false;
        return isBST(node.left, min, node.value) && isBST(node.right, node.value, max);
    }
}
