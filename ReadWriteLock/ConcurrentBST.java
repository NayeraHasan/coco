import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

class Node {
    int value;
    Node left, right;
    // Lock for fine-grained concurrency at node level
    final ReadWriteLock lock;

    Node(int value) {
        this.value = value;
        left = right = null;
        // Use a fair lock to prevent starvation
        this.lock = new ReentrantReadWriteLock(true);
    }
}

public class ConcurrentBST {
    private Node root;
    // Tree-level lock for operations that need to lock the whole tree
    private final ReadWriteLock treeLock = new ReentrantReadWriteLock(true); // Fair lock to prevent starvation

    // Intentional corruption method as required
    public void corruptNode(int targetValue, int newValue) {
        String thread = Thread.currentThread().getName();
        Logger.log(thread, "WRITE", "corrupt", "trying", targetValue + "->" + newValue);
        treeLock.writeLock().lock();
        try {
            Logger.log(thread, "WRITE", "corrupt", "acquired", targetValue + "->" + newValue);
            Node node = findNode(root, targetValue);
            if (node != null) {
                // Directly change the value without maintaining BST property
                node.value = newValue;
                Logger.log(thread, "WRITE", "corrupt", "corrupted", targetValue + "->" + newValue);
            }
        } finally {
            treeLock.writeLock().unlock();
            Logger.log(thread, "WRITE", "corrupt", "released", targetValue + "->" + newValue);
        }
    }

    // Helper method to find a node with a specific value
    private Node findNode(Node node, int value) {
        if (node == null) return null;
        if (node.value == value) return node;
        if (value < node.value) return findNode(node.left, value);
        return findNode(node.right, value);
    }

    // WRITE: Add
    public void add(int value) {
        String thread = Thread.currentThread().getName();
        Logger.log(thread, "WRITE", "add", "trying", String.valueOf(value));
        
        // Use simple tree-level locking for now - can be optimized later
        treeLock.writeLock().lock();
        try {
            Logger.log(thread, "WRITE", "add", "acquired", String.valueOf(value));
            root = insert(root, value);
        } finally {
            treeLock.writeLock().unlock();
            Logger.log(thread, "WRITE", "add", "released", String.valueOf(value));
        }
    }

    private Node insert(Node node, int value) {
        if (node == null) return new Node(value);
        if (value < node.value) node.left = insert(node.left, value);
        else if (value > node.value) node.right = insert(node.right, value);
        return node;
    }

    // WRITE: Remove
    public void remove(int value) {
        String thread = Thread.currentThread().getName();
        Logger.log(thread, "WRITE", "remove", "trying", String.valueOf(value));
        
        treeLock.writeLock().lock();
        try {
            Logger.log(thread, "WRITE", "remove", "acquired", String.valueOf(value));
            root = delete(root, value);
        } finally {
            treeLock.writeLock().unlock();
            Logger.log(thread, "WRITE", "remove", "released", String.valueOf(value));
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
        Node current = node;
        while (current.left != null) current = current.left;
        return current;
    }

    // READ: Contains
    public boolean contains(int value) {
        String thread = Thread.currentThread().getName();
        Logger.log(thread, "READ", "contains", "trying", String.valueOf(value));
        
        treeLock.readLock().lock();
        try {
            Logger.log(thread, "READ", "contains", "acquired", String.valueOf(value));
            return search(root, value);
        } finally {
            treeLock.readLock().unlock();
            Logger.log(thread, "READ", "contains", "released", String.valueOf(value));
        }
    }

    private boolean search(Node node, int value) {
        if (node == null) return false;
        if (value == node.value) return true;
        return value < node.value ? search(node.left, value) : search(node.right, value);
    }

    // READ: In-order print
    public void printInOrder() {
        String thread = Thread.currentThread().getName();
        Logger.log(thread, "READ", "printInOrder", "trying", "-");
        treeLock.readLock().lock();
        try {
            Logger.log(thread, "READ", "printInOrder", "acquired", "-");
            inOrder(root);
            System.out.println();
        } finally {
            treeLock.readLock().unlock();
            Logger.log(thread, "READ", "printInOrder", "released", "-");
        }
    }

    private void inOrder(Node node) {
        if (node == null) return;
        inOrder(node.left);
        System.out.print(node.value + " ");
        inOrder(node.right);
    }

    // READ: Check representation invariant
    public boolean checkRep() {
        String thread = Thread.currentThread().getName();
        Logger.log(thread, "READ", "checkRep", "trying", "-");
        treeLock.readLock().lock();
        try {
            Logger.log(thread, "READ", "checkRep", "acquired", "-");
            return isBST(root, Integer.MIN_VALUE, Integer.MAX_VALUE);
        } finally {
            treeLock.readLock().unlock();
            Logger.log(thread, "READ", "checkRep", "released", "-");
        }
    }

    private boolean isBST(Node node, int min, int max) {
        if (node == null) return true;
        if (node.value <= min || node.value >= max) return false;
        return isBST(node.left, min, node.value) && isBST(node.right, node.value, max);
    }
}