# 🧠 Sieve of Eratosthenes

The Sieve of Eratosthenes is a foundational algorithm for generating all prime numbers up to a given limit. It’s simple, efficient, and forms the basis for more advanced techniques like segmented and parallel sieving.

---

## 📘 Core Algorithm

### How It Works

1. Create a boolean array `isPrime[0..n]` and initialize all entries to `true`.
2. Starting from `p = 2`, mark all multiples of `p` as `false`.
3. Move to the next `p` that is still marked `true` and repeat.
4. Stop when `p * p > n`.

This process leaves only the prime numbers marked as `true`.

---

## ⏱️ Time Complexity

For each prime `p`, the sieve marks all multiples of `p` up to `n`.  
The total number of operations across all primes ≤ `n` is roughly:

    Total work ≈ n × (1/2 + 1/3 + 1/5 + 1/7 + ...)

This sum behaves like `log(log(n))`, giving the algorithm its well-known time complexity:

**O(n log log n)**

---

## 🧩 Segmented Sieve

To scale the sieve for large values of `n`, I use a segmented approach. This breaks the problem into manageable chunks and avoids allocating memory for the entire range.

### Phase 1: Base Prime Generation

- Compute all primes ≤ √n using the classic sieve.
- These primes are used to eliminate composites in higher segments.

### Phase 2: Chunked Sieving

- Divide the range `[√n + 1, n]` into chunks.
- For each chunk:
    - Initialize all values as prime.
    - For each base prime `p`:
        - Find the first multiple of `p` within the chunk.
        - Mark all multiples of `p` as composite.

This keeps memory usage localized and avoids redundant computation.

---

## 🧵 Multi-threaded Execution

When `threads > 1`, the algorithm distributes chunks across multiple threads. Each thread processes its segment independently using the shared base primes.

### Parallel Speedup

With `t` threads, the ideal time complexity becomes:

    O(n log log n / t)

### Thread Behavior

- A thread pool manages concurrent execution.
- Each thread marks composites in its assigned chunk.
- Base primes are shared read-only—no locking required.
- Once all threads complete, results are merged and sorted.

This design is cache-friendly, thread-safe, and scales cleanly across cores.

---

## ✅ Benefits

- Efficient for large upper limits (e.g. 10⁷+)
- Thread-safe and cache-aware
- Modular structure supports future optimizations

---

## ⚠️ Limitations

- Memory usage grows with segment size
- Not suitable for testing primality of individual large numbers

---

## 🧠 Execution Modes

- **Single-threaded Mode**  
  Processes chunks sequentially.  
  Best for small to medium ranges.  
  Performance: *Fast*

- **Multi-threaded Mode**  
  Splits chunks across threads for parallel processing.  
  Shared base primes, no locking.  
  Performance: *Faster*

---

This implementation is deterministic, scalable, and well-suited for benchmarking prime generation across input sizes and thread counts.

---

## 📚 Further Reading

- [Wikipedia: Sieve of Eratosthenes](https://en.wikipedia.org/wiki/Sieve_of_Eratosthenes)
- [Animated Explanation – GeeksforGeeks](https://www.geeksforgeeks.org/sieve-of-eratosthenes/)