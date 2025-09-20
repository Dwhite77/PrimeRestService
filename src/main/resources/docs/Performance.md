# ⚡ Performance Optimization Notes

This project includes several strategies to improve the performance of prime number generation and API responsiveness. Below is a summary of what's been done and what could be explored next.

---

## ✅ Current Optimizations

### 🔀 Algorithm Selection
- Supports multiple algorithms: `trial`, `sieve`, `miller`, `atkin`.
- Each algorithm is benchmarked across input sizes and thread counts.
- Segmented sieve is preferred for large inputs due to better cache locality and parallelism.
- `PrimeService` dynamically selects the algorithm via a registry map and validates inputs before execution.

### 🧵 Multi-threading
- All algorithms support configurable thread counts via the `threads` query parameter.
- Parallel execution is handled by `AbstractPrimeAlgorithm.runThreaded(...)`, which:
  - Divides the input range into evenly sized chunks
  - Submits each chunk to a named thread pool via `ThreadPoolManager`
  - Aggregates results with deduplication and sorting
- Thread names follow the format: `{label}-pool-{id}-thread-{threadId}` for easier diagnostics.
- Benchmarks show significant speedup for large inputs when using 2–4 threads.

### 🧪 Benchmark Logging
- Each `/api/primes` request logs:
  - Algorithm used
  - Input limit
  - Thread count
  - Prime count
  - Duration in milliseconds
- `PrimeRequestLog` stores recent requests, which are rendered in the documentation portal for quick inspection.
This allows contributors to benchmark raw algorithm performance or force fresh computation.


### 🧠 Prime Result Caching

To reduce redundant computation and improve response times, the system implements two layers of caching:

#### 🔹 Base Prime Caching (`basePrimes`)
Used internally by algorithms like **Atkin** and **Sieve** to avoid recomputing primes up to √N. These primes are reused across segmented and multi-threaded executions.

- **Cache Name**: `basePrimes`
- **Key Format**: `end` (upper bound of base primes)
- **Usage**: `BasePrimeService.generateAtkinBasePrimes(...)`, `generateSieveBasePrimes(...)`
- **Impact**: Speeds up base prime filtering and reduces memory churn

#### 🔹 Full Prime Result Caching (`primes`)
Caches the final list of primes returned by any algorithm for a given input configuration. This includes `trial`, `sieve`, `miller`, and `atkin`.

- **Cache Name**: `primes`
- **Key Format**: `algorithm-limit-threads` (e.g. `atkin-1000000-4`)
- **Usage**: `PrimeService.findPrimes(...)`
- **Impact**: Dramatically improves performance for repeated queries

#### 🔹 Cache Toggle Support
Clients can enable caching by setting `useCache=true` in the query string:

```
GET /api/primes?algorithm=atkin&limit=1000000&threads=4&useCache=true
```


#### 📊 Observed Performance Gains
Performance improvements vary by algorithm, input size, and thread count. Integration tests have shown significant speedups for repeated requests with caching enabled.

> Placeholder: Insert benchmark results here once finalized (e.g. uncached vs cached durations for each algorithm at 1M limit)

#### 🧪 Integration Test Coverage
Caching behavior is validated through full integration tests that:
- Measure uncached vs cached execution time
- Assert result consistency across calls
- Verify cache population and keying
- Support toggling cache usage via `useCache` flag



### 🧰 Integration Testing
- Parameterized test suites validate performance and correctness across edge cases.
- JaCoCo coverage ensures all exception paths and algorithm branches are exercised.

  [Jacoco](/jacoco/index.html)

### 🧼 Exception Handling
- Global exception handler avoids stack traces and ensures structured error payloads.
- Invalid parameters, unsupported algorithms, and malformed requests are handled gracefully.
- `ErrorResponseBuilder` centralizes error formatting for consistency.

### 🗂 Documentation Caching
- Markdown rendering is cached using `ConcurrentMapCacheManager` under the `"docs"` cache.
- Improves performance when rendering frequently accessed documentation files.

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
- Use `durationMs` and recent request logs to guide tuning efforts
- Validate changes with integration tests and coverage reports