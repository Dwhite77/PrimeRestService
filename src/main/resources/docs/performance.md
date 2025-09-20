# ⚡ Performance Optimization Notes

This project includes several strategies to improve the performance of prime number generation and API responsiveness. Below is a summary of what's been done and what could be explored next.

---

## ✅ Current Optimizations

### 🔀 Algorithm Selection
- Supports multiple algorithms: `trial`, `sieve`, `miller`, `atkin`.
- Each algorithm is benchmarked across input sizes and thread counts.
- Segmented sieve is preferred for large inputs due to better cache locality and parallelism.

### 🧵 Multi-threading
- All algorithms support configurable thread counts via the `threads` query parameter.
- Benchmarks show significant speedup for large inputs when using 2–4 threads.

### 🧪 Benchmark Logging
- Each `/api/primes` request logs:
    - Algorithm used
    - Input limit
    - Thread count
    - Prime count
    - Duration in milliseconds
- Recent requests are rendered in the documentation portal for quick inspection.

### 🧰 Integration Testing
- Parameterized test suites validate performance and correctness across edge cases.
- JaCoCo coverage ensures all exception paths and algorithm branches are exercised.

  [Jacoco](jacoco/index.html)
### 🧼 Exception Handling
- Global exception handler avoids stack traces and ensures structured error payloads.
- Invalid parameters, unsupported algorithms, and malformed requests are handled gracefully.

---

## 🚀 Future Improvements

### 🧠 Algorithm-Level Enhancements
- Implement wheel factorization to reduce unnecessary checks in trial and sieve methods.
- Explore segmented sieve with block preallocation and SIMD-friendly data structures.

### 📦 Caching & Memoization
- Cache previously computed prime lists for repeated inputs.
- Consider LRU or size-based eviction strategies.

### 🧮 Native Performance
- Offload heavy computation to native code via JNI or GraalVM for ultra-fast execution.
- Profile hotspots using JFR or async-profiler.

### 🌐 Async API Responses
- Use `CompletableFuture` or reactive streams to return primes asynchronously.
- Improves responsiveness for large inputs and high concurrency.

### 📊 Metrics & Monitoring
- Integrate Micrometer + Prometheus for real-time performance metrics.
- Track request latency, thread utilization, and algorithm throughput.

### 🧪 Load Testing
- Use tools like Gatling or k6 to simulate concurrent requests and stress test the API.
- Identify bottlenecks under load and tune thread pool sizes accordingly.

---

## 🧭 Contributor Notes

Performance tuning is an ongoing process. All benchmarks, logs, and diagrams are documented in the portal. Contributors are encouraged to:
- Profile before optimizing
- Benchmark across realistic input sizes
- Document trade-offs and fallback strategies
