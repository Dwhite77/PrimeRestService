# 🧠 Sieve of Atkin

The Sieve of Atkin is a mathematically optimized algorithm for generating prime numbers up to a given limit. Unlike the Sieve of Eratosthenes, which eliminates multiples of known primes, Atkin uses modular arithmetic and quadratic forms to identify prime candidates more efficiently.

---

## 📘 Core Algorithm

### How It Works

1. Initialize a boolean array `isPrime[0..n]` with all entries set to `false`.
2. For each pair `(x, y)` within bounds:
    - Flip `isPrime[n]` if:
        - `n = 4x² + y²` and `n mod 12 == 1 or 5`
        - `n = 3x² + y²` and `n mod 12 == 7`
        - `n = 3x² − y²` and `n mod 12 == 11` (only if `x > y`)
3. Eliminate false positives by marking all multiples of squares of primes as `false`.
4. Add 2 and 3 manually, then collect all remaining `true` entries as primes.

This approach filters candidates using number-theoretic properties before confirming primality.

---

## ⏱️ Time Complexity

The Sieve of Atkin runs in **O(n)** time, rather than **O(n log log n)**, because each of its three main phases scales linearly with the input size.

- Modular filtering over all integer pairs (x, y) up to √n performs O(n) toggles in total.
- Eliminating multiples of prime squares requires  
  ∑ₚ≤√n O(n/p²) = O(n) operations.
- Scanning the boolean array of length n to collect confirmed primes takes O(n).

By focusing only on modular constraints and square multiples—rather than marking every multiple of each prime—Atkin removes the log log n factor present in Eratosthenes’ method, yielding a true linear bound.

---

## 🧩 Segmented Sieve

To handle large values of `n`, the algorithm uses a segmented approach.

### Phase 1: Base Prime Generation

- Compute all primes ≤ √n using the core Atkin sieve.
- These primes are used to eliminate square multiples in higher segments.

### Phase 2: Chunked Sieving

- Divide the range `[√n + 1, n]` into chunks.
- For each chunk:
    - Apply the same modular filters as in the base sieve.
    - Use base primes to eliminate square multiples.
    - Collect remaining candidates as primes.

This keeps memory usage bounded and avoids recomputation across segments.

---

## 🧵 Multi-threaded Execution

When `threads > 1`, the algorithm distributes chunks across multiple threads. Each thread processes its segment independently using the shared base primes.

### Parallel Speedup

With `t` threads, the ideal time complexity becomes:

    O(n log log n / t)

### Thread Behavior

- A thread pool manages concurrent execution.
- Each thread applies modular filters and eliminates square multiples in its chunk.
- Base primes are shared read-only—no locking required.
- Once all threads complete, results are merged and sorted.

This design is cache-friendly, thread-safe, and scales well across cores.

---

## ✅ Benefits

- Efficient for large upper limits (e.g. 10⁷+)
- Skips even numbers and uses modular filters to reduce work
- Thread-safe and cache-aware
- Modular structure supports future optimizations

---

## ⚠️ Limitations

- More complex to implement and debug than simpler sieves
- Requires careful handling of modular conditions and segment boundaries

---

## 🧠 Execution Modes

- **Single-threaded Mode**  
  Processes chunks sequentially using modular filters.  
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

- [Wikipedia: Sieve of Atkin](https://en.wikipedia.org/wiki/Sieve_of_Atkin)
- [Prime Number Algorithms – GeeksforGeeks](https://www.geeksforgeeks.org/prime-number-algorithms/)
