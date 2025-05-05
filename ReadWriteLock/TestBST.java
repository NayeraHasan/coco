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


// import java.util.Random;

// public class TestBST {
//     public static void main(String[] args) {
//         ConcurrentBST tree = new ConcurrentBST();
//         int numThreads = 4;
//         int operationsPerThread = 1000;

//         Thread[] threads = new Thread[numThreads];

//         // Create threads
//         for (int i = 0; i < numThreads; i++) {
//             int threadId = i;
//             threads[i] = new Thread(() -> {
//                 Random rand = new Random();
//                 for (int j = 0; j < operationsPerThread; j++) {
//                     int val = rand.nextInt(100);
//                     if (rand.nextBoolean()) {
//                         tree.add(val);
//                         System.out.println("Thread " + threadId + " added " + val);
//                     } else {
//                         tree.remove(val);
//                         System.out.println("Thread " + threadId + " removed " + val);
//                     }
//                 }
//             });
//         }

//         // Start all threads
//         for (Thread t : threads) t.start();

//         // Wait for all threads to finish
//         for (Thread t : threads) {
//             try {
//                 t.join();
//             } catch (InterruptedException e) {
//                 e.printStackTrace();
//             }
//         }

//         // Final check
//         System.out.println("\nFinal In-order Traversal:");
//         tree.printInOrder();

//         System.out.println("Tree is valid BST? " + tree.checkRep());
//     }
// }


import java.util.Random;

public class TestBST {
    public static void main(String[] args) {
        ConcurrentBST tree = new ConcurrentBST();

        // Create reader threads
        Thread reader1 = new Thread(() -> {
            Random rand = new Random();
            for (int i = 0; i < 10; i++) {
                int val = rand.nextInt(50);
                tree.contains(val);
                try {
                    Thread.sleep(rand.nextInt(100));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "Reader-1");

        Thread reader2 = new Thread(() -> {
            Random rand = new Random();
            for (int i = 0; i < 10; i++) {
                int val = rand.nextInt(50);
                tree.checkRep();
                try {
                    Thread.sleep(rand.nextInt(100));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "Reader-2");

        // Create writer threads
        Thread writer1 = new Thread(() -> {
            Random rand = new Random();
            for (int i = 0; i < 10; i++) {
                int val = rand.nextInt(50);
                tree.add(val);
                try {
                    Thread.sleep(rand.nextInt(150));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "Writer-1");

        Thread writer2 = new Thread(() -> {
            Random rand = new Random();
            for (int i = 0; i < 10; i++) {
                int val = rand.nextInt(50);
                tree.remove(val);
                try {
                    Thread.sleep(rand.nextInt(150));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
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

        // Final in-order print
        System.out.println("\nFinal in-order tree:");
        tree.printInOrder();
        System.out.println("Final structure valid? " + tree.checkRep());
    }
}
