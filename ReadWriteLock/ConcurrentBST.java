import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

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
    private final ReadWriteLock rwLock = new ReentrantReadWriteLock();

    // WRITE: Add
    public void add(int value) {
        String thread = Thread.currentThread().getName();
        Logger.log(thread, "WRITE", "add", "trying", String.valueOf(value));
        rwLock.writeLock().lock();
        try {
            Logger.log(thread, "WRITE", "add", "acquired", String.valueOf(value));
            root = insert(root, value);
        } finally {
            rwLock.writeLock().unlock();
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
        rwLock.writeLock().lock();
        try {
            Logger.log(thread, "WRITE", "remove", "acquired", String.valueOf(value));
            root = delete(root, value);
        } finally {
            rwLock.writeLock().unlock();
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
        while (node.left != null) node = node.left;
        return node;
    }

    // READ: Contains
    public boolean contains(int value) {
        String thread = Thread.currentThread().getName();
        Logger.log(thread, "READ", "contains", "trying", String.valueOf(value));
        rwLock.readLock().lock();
        try {
            Logger.log(thread, "READ", "contains", "acquired", String.valueOf(value));
            return search(root, value);
        } finally {
            rwLock.readLock().unlock();
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
        rwLock.readLock().lock();
        try {
            Logger.log(thread, "READ", "printInOrder", "acquired", "-");
            inOrder(root);
            System.out.println();
        } finally {
            rwLock.readLock().unlock();
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
        rwLock.readLock().lock();
        try {
            Logger.log(thread, "READ", "checkRep", "acquired", "-");
            return isBST(root, Integer.MIN_VALUE, Integer.MAX_VALUE);
        } finally {
            rwLock.readLock().unlock();
            Logger.log(thread, "READ", "checkRep", "released", "-");
        }
    }

    private boolean isBST(Node node, int min, int max) {
        if (node == null) return true;
        if (node.value <= min || node.value >= max) return false;
        return isBST(node.left, min, node.value) && isBST(node.right, node.value, max);
    }
}
