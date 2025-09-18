# 🧠 Trial Division Primality Test

Trial division is the most straightforward method for determining whether a number is prime. It checks each candidate by dividing it by all integers up to its square root. While simple and easy to implement, it’s not the most efficient for large ranges—but it’s reliable and deterministic.

---

## 📘 Core Algorithm

### How It Works

1. For each number `n` in the range:
    - If `n < 2`, it’s not prime.
    - Check divisibility from `2` up to `√n`.
    - If any divisor divides `n` evenly, it’s composite.
    - Otherwise, `n` is prime.

This brute-force approach guarantees correctness and is ideal for small ranges or educational use.

---

## ⏱️ Time Complexity

Trial division runs in **O(√n)** time per number.

- For a range `[2, N]`, total time is roughly:

  O(N√N)

- No precomputation or memory allocation is required.
- Each number is tested independently.

This makes it inefficient for large-scale prime generation but useful for isolated primality checks.

---

## 🧩 Range-Based Execution

To generate primes up to `n`, the algorithm applies trial division to each number in the range.

- No sieving or marking is performed.
- Each number is tested in isolation.
- Results are collected into a final list of primes.

This approach is memory-light and easy to parallelize.

---

## 🧵 Multi-threaded Execution

When `threads > 1`, the input range is divided into chunks and processed concurrently.

### Parallel Speedup

With `t` threads, the ideal time complexity becomes:

    O(N√N / t)

### Thread Behavior

- A thread pool manages concurrent execution.
- Each thread applies trial division to its assigned chunk.
- No shared state or locking is required.
- Results are merged after all threads complete.

This design is embarrassingly parallel and scales well with CPU cores.

---

## ✅ Benefits

- Simple and deterministic
- Easy to implement and debug
- Thread-safe and memory-efficient
- Suitable for small ranges or educational use

---

## ⚠️ Limitations

- Poor performance for large upper limits
- Not suitable for cryptographic or high-throughput applications
- Redundant work across candidates

---

## 🧠 Execution Modes

- **Single-threaded Mode**  
  Applies trial division sequentially across the range.  
  Best for small ranges or environments with limited concurrency.  
  Performance: *Slow*

- **Multi-threaded Mode**  
  Splits the range into chunks and tests in parallel.  
  No shared state, no locking.  
  Performance: *Faster*

---

This implementation is minimal, predictable, and well-suited for deterministic primality testing in constrained environments.

---

## 📚 Further Reading

- [Wikipedia: Trial Division](https://en.wikipedia.org/wiki/Trial_division)
- [Prime Testing – GeeksforGeeks](https://www.geeksforgeeks.org/prime-number/)