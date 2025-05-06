# Concurrent Binary Search Tree Implementation

This project implements a thread-safe Binary Search Tree (BST) using Read-Write Locks to handle concurrent operations efficiently.

## Binary Search Tree Overview
A Binary Search Tree is a hierarchical data structure where each node contains a value and has up to two children. The BST property ensures that for any node:
- All values in its left subtree are less than the node's value
- All values in its right subtree are greater than the node's value
This property is maintained through careful locking during modifications and verified using the `checkRep()` method.

## Concurrency Concepts

### Read-Write Locks vs Reentrant Locks
Read-Write locks were chosen over simple Reentrant locks because:
- They allow multiple concurrent readers, improving performance for read-heavy workloads
- They maintain data consistency by ensuring exclusive access during writes
- They provide better scalability for concurrent applications

### Read-Write Locks
- **Read Locks**: Multiple threads can hold read locks simultaneously
- **Write Locks**: Only one thread can hold a write lock at a time
- **Lock Fairness**: The implementation uses fair locks to prevent starvation

### Concurrency Issues Handled

1. **Deadlocks**
   - Prevented by using a single tree-level lock for write operations
   - No nested lock acquisition that could cause circular wait conditions

2. **Livelocks**
   - Avoided by using fair locks (ReentrantReadWriteLock(true))
   - Threads are granted locks in FIFO order

3. **Starvation**
   - Prevented through fair locking mechanism
   - Read and write operations are queued fairly

4. **Race Conditions**
   - Protected against using appropriate lock granularity
   - Tree-level locking for structural modifications
   - Node-level locking for fine-grained operations

## Implementation Details

### Lock Structure
- **Tree-Level Lock**: Controls access to the entire tree structure
- **Node-Level Locks**: Each node has its own ReadWriteLock for fine-grained control

### Operations

1. **Read Operations** (use read locks)
   - `contains()`: Search for a value
   - `printInOrder()`: Traverse the tree
   - `checkRep()`: Verify BST properties

2. **Write Operations** (use write locks)
   - `add()`: Insert new values
   - `remove()`: Delete values
   - `corruptNode()`: Intentionally modify node values

### Lock Implementation
- Uses Java's `ReentrantReadWriteLock`
- Fair locking enabled to prevent starvation
- Proper lock release in finally blocks to prevent deadlocks

## Testing Strategy

### Random Operations Testing
The implementation includes comprehensive random testing in two scenarios:

1. **Multi-Threaded Random Testing**
   - Uses fixed random seeds for reproducibility
   - Random values between 0-99
   - Random sleep times (0-49ms for readers, 0-99ms for writers)
   - Mix of concurrent read and write operations
   - Separate reader and writer threads
   - Periodic BST property verification

2. **High Contention Testing**
   - Focuses on a small set of values (42, 17, 50, 35, 60)
   - 5 writer threads and 3 reader threads
   - Random choice between add and remove operations
   - Very short sleep times (0-9ms) to increase contention
   - Frequent BST property checks
   - Tests lock fairness under high contention

### Testing Scenarios

1. **Single-Threaded Tests**
   - Basic BST operations
   - Property verification
   - Edge cases

2. **Corruption Tests**
   - Tests BST property violations
   - Verifies detection of invalid states
   - Tests recovery mechanisms

3. **Multi-Threaded Tests**
   - Concurrent read operations
   - Concurrent write operations
   - Mixed read-write operations
   - Property maintenance under concurrency

4. **High Contention Tests**
   - Multiple threads on same values
   - Lock contention scenarios
   - Fairness verification
   - Performance under stress

## Example Operations from Log

Here's a sequence of operations showing concurrent access patterns:

```
1. Reader-1 tries to check if value 11 exists
2. Reader-2 tries to verify BST properties
3. Writer-2 attempts to remove value 15
4. Writer-1 attempts to add value 11
5. Reader-2 acquires read lock and verifies properties
6. Reader-1 acquires read lock and checks for value
7. Writer-2 acquires write lock and removes value
8. Writer-1 acquires write lock and adds value
```

This sequence demonstrates:
- Multiple readers can access the tree simultaneously
- Write operations are serialized
- Fair lock ordering prevents starvation
- Proper lock release after operations

## Performance Considerations
- Read operations can proceed concurrently
- Write operations are serialized
- Fair locking may impact performance but ensures fairness
- Tree-level locking provides safety at the cost of some concurrency

## Testing
The implementation includes comprehensive testing through `TestBST.java` that verifies:
- Concurrent operations
- Lock behavior
- BST properties
- Thread safety 