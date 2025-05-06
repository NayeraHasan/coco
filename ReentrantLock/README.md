# Concurrent Binary Search Tree with ReentrantLock

This implementation demonstrates a thread-safe Binary Search Tree (BST) using Java's `ReentrantLock` for synchronization. The implementation ensures thread safety while maintaining the BST properties and provides comprehensive testing for various concurrent scenarios.

## Implementation Details

### Locking Strategy
- Uses a single `ReentrantLock` for all operations
- Provides mutual exclusion for all tree modifications
- Ensures thread safety but with less concurrency than ReadWriteLock

### Key Features
1. **Thread Safety**
   - All operations (add, remove, contains) are atomic
   - No race conditions in tree modifications
   - Maintains BST properties under concurrent access

2. **Representation Invariant**
   - Implements `checkRep()` to verify BST properties
   - Validates tree structure after each operation
   - Helps detect corruption in concurrent scenarios

3. **Corruption Detection**
   - Can detect invalid BST states
   - Useful for testing and debugging
   - Helps ensure data structure integrity

## Test Suite

The implementation includes a comprehensive test suite that covers:

1. **Single-Threaded Tests**
   - Basic BST operations
   - Tree structure verification
   - Operation correctness

2. **Corruption Tests**
   - Tests tree integrity under invalid states
   - Verifies detection of BST property violations
   - Three scenarios:
     - Corrupting leaf nodes
     - Corrupting parent nodes
     - Corrupting root node

3. **Multi-Threaded Tests**
   - Concurrent readers and writers
   - Random operation sequences
   - Tree validity checks during operations

4. **High Contention Tests**
   - Multiple threads operating on same values
   - Short sleep times to increase contention
   - Continuous validity checks

## Performance Characteristics

### Advantages
- Simple implementation
- Guaranteed thread safety
- No risk of deadlock
- Easy to understand and maintain

### Disadvantages
- Less concurrent than ReadWriteLock
- All operations are mutually exclusive
- May have lower throughput under high read loads

## Usage Example

```java
ConcurrentBST tree = new ConcurrentBST();

// Thread-safe operations
tree.add(10);
tree.add(5);
tree.add(15);

// Concurrent access
boolean contains = tree.contains(10);
boolean isValid = tree.checkRep();
```

## Testing Strategy

The test suite uses a combination of:
1. **Deterministic Tests**
   - Fixed operation sequences
   - Known tree structures
   - Predictable outcomes

2. **Random Tests**
   - Random operation sequences
   - Random values
   - Multiple concurrent threads

3. **Corruption Tests**
   - Deliberate tree structure violations
   - Invalid BST states
   - Property verification

## Comparison with ReadWriteLock

While this implementation is simpler than the ReadWriteLock version, it provides:
- Stronger consistency guarantees
- Simpler code
- No risk of deadlock
- Easier to reason about

However, it may have lower performance under high read loads since all operations require exclusive access. 