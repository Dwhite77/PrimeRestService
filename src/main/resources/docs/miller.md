# 🧠 Miller–Rabin Primality Test

The Miller–Rabin test is a probabilistic algorithm for determining whether a number is likely prime. It’s based on modular arithmetic and is especially efficient for large integers. While it doesn’t guarantee primality, it can be tuned to achieve extremely high confidence—making it ideal for cryptographic applications and fast filtering.

---

## 📘 Core Algorithm

### How It Works

1. Express `n − 1` as `2ʳ·d` where `d` is odd.
2. For each base `a`:
    - Compute `aᵈ mod n`.
    - If the result is `1` or `n − 1`, continue.
    - Otherwise, square repeatedly up to `r − 1` times and check for `n − 1`.
    - If none match, `n` is composite.

This process is repeated for a fixed set of bases. If a number passes all checks, it is considered probably prime.

---

## ⏱️ Time Complexity

The Miller–Rabin test runs in **O(k log³ n)** time per number, where `k` is the number of bases tested.

- For small `n`, a fixed set of bases (e.g. `{2, 3, 5, 7, 11}`) is sufficient to guarantee correctness.
- For large `n`, the algorithm remains probabilistic but can be tuned for arbitrarily low error rates.

Unlike sieves, this test doesn’t eliminate composites systematically—it checks each number individually.

---

## 🧩 Range-Based Execution

To generate primes within a range, the algorithm applies the test to each number from `2` to `n`.

- Each number is tested independently.
- Only numbers that pass all base checks are retained.
- No memory-intensive sieving or marking is required.

This makes it ideal for sparse prime detection or cryptographic key generation.

---

## 🧵 Multi-threaded Execution

When `threads > 1`, the input range is divided into chunks and processed concurrently.

### Parallel Speedup

With `t` threads, the ideal time complexity becomes:

    O(k log³ n × (n / t))

### Thread Behavior

- A thread pool manages concurrent execution.
- Each thread applies the Miller–Rabin test to its assigned chunk.
- No shared state or locking is required.
- Results are collected and merged after all threads complete.

This design is thread-safe, embarrassingly parallel, and scales well with CPU cores.

---

## ✅ Benefits

- Extremely fast for large numbers
- Ideal for cryptographic applications and probabilistic filtering
- Thread-safe and scalable
- Requires minimal memory

---

## ⚠️ Limitations

- Not a true sieve—doesn’t eliminate composites in bulk
- Probabilistic nature means false positives are possible for very large `n`
- Less efficient than sieves for dense prime generation

---

## 🧠 Execution Modes

- **Single-threaded Mode**  
  Applies the test sequentially across the range.  
  Best for small ranges or environments with limited concurrency.  
  Performance: *Fast*

- **Multi-threaded Mode**  
  Splits the range into chunks and tests in parallel.  
  No shared state, no locking.  
  Performance: *Faster*

---

This implementation is lightweight, scalable, and well-suited for high-confidence primality testing across large ranges.

---

## 📚 Further Reading

- [Wikipedia: Miller–Rabin Primality Test](https://en.wikipedia.org/wiki/Miller%E2%80%93Rabin_primality_test)
- [Cryptographic Applications – GeeksforGeeks](https://www.geeksforgeeks.org/primality-test-set-3-miller-rabin/)