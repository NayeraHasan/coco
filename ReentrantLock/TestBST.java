// public class TestBST {
//     public static void main(String[] args) {
//         ConcurrentBST tree = new ConcurrentBST();

//         tree.add(10);
//         tree.add(5);
//         tree.add(15);
//         tree.add(7);
//         tree.remove(10);

//         tree.printInOrder();  // should print sorted elements
//         System.out.println("Contains 7? " + tree.contains(7));
//         System.out.println("Valid BST? " + tree.checkRep());
//     }
// }


import java.util.Random;

public class TestBST {
    // Flag to enable/disable sleeps for faster test runs
    private static final boolean ENABLE_SLEEPS = true;
    
    // Sleep helper method
    private static void sleep(int milliseconds) {
        if (!ENABLE_SLEEPS) return;
        
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    // Test single-threaded operations
    private static void testSingleThreaded() {
        System.out.println("\n=== Single-Threaded Test ===");
        ConcurrentBST tree = new ConcurrentBST();
        
        // Add elements
        System.out.println("Adding elements...");
        tree.add(10);
        tree.add(5);
        tree.add(15);
        tree.add(2);
        tree.add(7);
        tree.add(12);
        tree.add(20);
        
        // Print the tree
        System.out.println("Tree contents:");
        tree.printInOrder();
        System.out.println("Is valid BST? " + tree.checkRep());
        
        // Test contains
        System.out.println("Contains 7? " + tree.contains(7));
        System.out.println("Contains 13? " + tree.contains(13));
        
        // Test remove
        System.out.println("Removing element 10...");
        tree.remove(10);
        System.out.println("Tree after removal:");
        tree.printInOrder();
        System.out.println("Is valid BST? " + tree.checkRep());
    }
    
    // Test representation invariant with corruption
    private static void testCorruption() {
        System.out.println("\n=== Corruption Test ===");
        ConcurrentBST tree = new ConcurrentBST();
        
        // Build a tree
        System.out.println("Building tree...");
        tree.add(10);
        tree.add(5);
        tree.add(15);
        tree.add(2);
        tree.add(7);
        
        // Verify initial state
        System.out.println("Initial tree:");
        tree.printInOrder();
        System.out.println("Is valid BST? " + tree.checkRep());
        
        // Test corruption scenarios
        
        // Scenario 1: Corrupt a leaf node to make it larger than its parent
        System.out.println("\nCorruption Scenario 1: Corrupt leaf node (7 -> 20)");
        tree.corruptNode(7, 20);
        System.out.println("Tree after corruption:");
        tree.printInOrder();
        System.out.println("Is valid BST? " + tree.checkRep());
        
        // Reset tree
        tree = new ConcurrentBST();
        tree.add(10);
        tree.add(5);
        tree.add(15);
        tree.add(2);
        tree.add(7);
        
        // Scenario 2: Corrupt a parent node to disrupt order with its children
        System.out.println("\nCorruption Scenario 2: Corrupt parent node (5 -> 6)");
        tree.corruptNode(5, 6);
        System.out.println("Tree after corruption:");
        tree.printInOrder();
        System.out.println("Is valid BST? " + tree.checkRep());
        
        // Reset tree
        tree = new ConcurrentBST();
        tree.add(10);
        tree.add(5);
        tree.add(15);
        tree.add(2);
        tree.add(7);
        
        // Scenario 3: Corrupt the root to make it smaller than left child
        System.out.println("\nCorruption Scenario 3: Corrupt root (10 -> 1)");
        tree.corruptNode(10, 1);
        System.out.println("Tree after corruption:");
        tree.printInOrder();
        System.out.println("Is valid BST? " + tree.checkRep());
    }
    
    // Test multi-threaded operations
    private static void testMultiThreaded() {
        System.out.println("\n=== Multi-Threaded Test ===");
        ConcurrentBST tree = new ConcurrentBST();
        
        // Pre-populate the tree with some values
        for (int i = 0; i < 10; i++) {
            tree.add(i * 10);
        }
        
        System.out.println("Initial tree:");
        tree.printInOrder();
        System.out.println("Is valid BST? " + tree.checkRep());
        
        // Create reader threads
        Thread reader1 = new Thread(() -> {
            Random rand = new Random(1); // Fixed seed for reproducibility
            for (int i = 0; i < 20; i++) {
                int val = rand.nextInt(100);
                boolean result = tree.contains(val);
                System.out.println("Reader-1: Contains " + val + "? " + result);
                sleep(rand.nextInt(50));
            }
        }, "Reader-1");
        
        Thread reader2 = new Thread(() -> {
            Random rand = new Random(2); // Different seed
            for (int i = 0; i < 20; i++) {
                boolean valid = tree.checkRep();
                System.out.println("Reader-2: Tree valid? " + valid);
                sleep(rand.nextInt(50));
            }
        }, "Reader-2");
        
        // Create writer threads
        Thread writer1 = new Thread(() -> {
            Random rand = new Random(3);
            for (int i = 0; i < 10; i++) {
                int val = rand.nextInt(100);
                tree.add(val);
                System.out.println("Writer-1: Added " + val);
                sleep(rand.nextInt(100));
            }
        }, "Writer-1");
        
        Thread writer2 = new Thread(() -> {
            Random rand = new Random(4);
            for (int i = 0; i < 10; i++) {
                int val = rand.nextInt(100);
                tree.remove(val);
                System.out.println("Writer-2: Removed " + val);
                sleep(rand.nextInt(100));
            }
        }, "Writer-2");
        
        // Start all threads
        reader1.start();
        reader2.start();
        writer1.start();
        writer2.start();
        
        // Join all threads
        try {
            reader1.join();
            reader2.join();
            writer1.join();
            writer2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        // Final verification
        System.out.println("\nFinal tree:");
        tree.printInOrder();
        System.out.println("Is valid BST? " + tree.checkRep());
    }
    
    // Test concurrent operations with high contention
    private static void testHighContention() {
        System.out.println("\n=== High Contention Test ===");
        ConcurrentBST tree = new ConcurrentBST();
        
        // All threads operate on same small set of values for high contention
        final int[] values = {42, 17, 50, 35, 60};
        
        // Add initial value
        tree.add(42);
        
        // Create multiple writer threads that add and remove the same values
        Thread[] writers = new Thread[5];
        for (int i = 0; i < writers.length; i++) {
            final int threadId = i;
            writers[i] = new Thread(() -> {
                Random rand = new Random(threadId + 10);
                for (int j = 0; j < 20; j++) {
                    int val = values[rand.nextInt(values.length)];
                    if (rand.nextBoolean()) {
                        tree.add(val);
                        System.out.println("Writer-" + threadId + ": Added " + val);
                    } else {
                        tree.remove(val);
                        System.out.println("Writer-" + threadId + ": Removed " + val);
                    }
                    sleep(rand.nextInt(10)); // Short sleep to increase contention
                }
            }, "Writer-" + i);
        }
        
        // Create reader threads
        Thread[] readers = new Thread[3];
        for (int i = 0; i < readers.length; i++) {
            final int threadId = i;
            readers[i] = new Thread(() -> {
                Random rand = new Random(threadId);
                for (int j = 0; j < 30; j++) {
                    int val = values[rand.nextInt(values.length)];
                    boolean result = tree.contains(val);
                    System.out.println("Reader-" + threadId + ": Contains " + val + "? " + result);
                    
                    // Periodically check tree validity
                    if (j % 5 == 0) {
                        boolean valid = tree.checkRep();
                        System.out.println("Reader-" + threadId + ": Tree valid? " + valid);
                    }
                    
                    sleep(rand.nextInt(5)); // Very short sleep
                }
            }, "Reader-" + i);
        }
        
        // Start all threads
        for (Thread t : readers) t.start();
        for (Thread t : writers) t.start();
        
        // Join all threads
        try {
            for (Thread t : readers) t.join();
            for (Thread t : writers) t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        // Final verification
        System.out.println("\nFinal tree in high contention test:");
        tree.printInOrder();
        System.out.println("Is valid BST? " + tree.checkRep());
    }
    
    // Main method to run all tests
    public static void main(String[] args) {
        System.out.println("Starting concurrent BST tests...");
        
        // Run all tests
        testSingleThreaded();
        testCorruption();
        testMultiThreaded();
        testHighContention();
        
        System.out.println("\nAll tests completed.");
    }
}
