# 🧪 Testing Strategy

This project uses a layered testing approach to ensure correctness, performance, and resilience across all components—from algorithm logic to API endpoints and error handling. All tests are written using **JUnit 5** (a modern Java testing framework), with support from **Mockito** (a library for mocking dependencies), **RestAssured** (a fluent API for testing REST services), and **Spring Boot Test** (Spring’s integration testing support).

---

## ✅ Unit Testing

Unit tests validate individual components in isolation (without relying on other parts of the system), ensuring deterministic behavior and fast feedback during development.

### 🔍 Algorithm Tests
Each prime generation algorithm (`Trial`, `Sieve`, `Atkin`, `Miller-Rabin`) extends a shared abstract test class:

- `PrimeAlgorithmTestSupport` verifies:
    - Prime generation up to 100
    - Empty results for invalid limits
    - Sorted, distinct output

### 🧠 Service Logic
- `AbstractPrimeAlgorithmTest` validates `PrimeService.shouldSkip(...)` logic for:
    - Excessive limits
    - Invalid thread counts
    - Edge-case inputs (unusual or boundary values that might break logic)

### 🧱 Exception Classes
- `AlgorithmNotSupportedExceptionTest` confirms message formatting and exception chaining (linking one exception to another as its cause).

### 📦 Model Validation
- `APIResponseTest`, `ErrorPayloadTest`, and `PrimePayloadTest` verify:
    - Field population
    - JSON/XML serialization (converting objects to structured text formats)
    - Null handling and field exclusion
    - Timestamp formatting

### 🧰 Utility Classes
- `ErrorResponseBuilderTest` validates structured error payloads across all HTTP status codes.
- `ThreadPoolManagerTest` confirms:
    - Thread naming conventions
    - Pool creation with null/empty labels
    - Shutdown behavior
    - Exception handling for invalid thread counts

---

## 🔗 Integration Testing

Integration tests validate how components interact within the full Spring Boot context (i.e. with real wiring, configuration, and dependencies).

### 🌐 Controller Layer
- `PrimeControllerIntegrationTest` covers:
    - Valid requests across all algorithms
    - Accept headers (`application/json`, `application/xml`) (used to specify desired response format)
    - Large input limits and thread variations
    - Error scenarios (invalid inputs, unsupported algorithms, missing parameters)
    - Concurrency handling via thread pools (running multiple tasks in parallel)
    - Root path redirect and landing page content
    - Swagger UI accessibility (auto-generated API documentation interface)

### ❌ Error Handling
- `GlobalExceptionHandlerIntegrationTest` verifies structured error responses for:
    - `400`, `404`, `405`, `500`, and custom status codes
    - Timestamp formatting
    - Path and message clarity

---

## 📊 Benchmarking

Benchmarking is built into the integration layer to measure algorithm performance across thread counts and input sizes.

- `PrimeControllerIntegrationTest` includes:
    - Single-thread and multi-thread benchmarks
    - Matrix-style comparisons (tabular format showing performance across combinations)
    - CSV export and summary table generation

## 🧪 Benchmark Results

The following benchmarks were run using the `/api/primes` endpoint with a limit of `100,000,000`. Each algorithm was tested across multiple thread counts to evaluate performance scaling.

---

### 🔍 Summary

- **Trial** is the slowest but scales well with threads.
- **Sieve** and **Atkin** show strong multithreaded performance.
- **Miller** benefits significantly from parallelism.
- All results were captured via integration tests using `RestAssured`.

---

### 📊 Benchmark Table
```
| Algorithm | Threads | Duration (ms) |
|-----------|---------|---------------|
| **Trial** | 1       | 54647         |
|           | 2       | 34830         |
|           | 4       | 19137         |
|           | 8       | 11042         |
| **Sieve** | 1       | 1023          |
|           | 2       | 1459          |
|           | 4       | 1156          |
|           | 8       | 952           |
| **Atkin** | 1       | 1152          |
|           | 2       | 1095          |
|           | 4       | 744           |
|           | 8       | 868           |
| **Miller**| 1       | 10168         |
|           | 2       | 5196          |
|           | 4       | 2784          |
|           | 8       | 2032          |
```
As you can see from the table above all of the algorithms beat just running through using trial in terms of time.
The most optimal Algorithm appears to be the Sieve of Atkin, with its time being the best in this benchmark, interestingly the 4 thread variant was faster than the 8 thread

These results validate that multithreading improves performance across all algorithms, with diminishing returns beyond 4 threads in some cases. Trial division remains the least performant but is included for completeness and comparison.

### 📊 Benchmark Table (Cached Execution)

The following results were captured using the cached versions of the **sieve** and **atkin** algorithms, again with a limit of `100,000,000`. This test demonstrates how caching improves performance under high-load conditions.
```
| Algorithm           | Threads | Duration (ms) |
|---------------------|---------|---------------|
| **Sieve** (cached)  | 2       | 1796          |
|                     | 4       | 1194          |
|                     | 6       | 896           |
|                     | 8       | 986           |
| **Atkin** (cached)  | 2       | 1227          |
|                     | 4       | 979           |
|                     | 6       | 1186          |
|                     | 8       | 879           |
```
These results confirm that caching enables large-limit benchmarks to run reliably and efficiently. While Atkin remains the fastest overall, Sieve shows strong performance gains with increased thread count. Interestingly, the 6-thread variant of Sieve outperformed the 8-thread run, suggesting a sweet spot in parallelism for this workload.

---

## 📈 Code Coverage

We use **JaCoCo** to measure test coverage across all layers. The report includes:

- Instruction coverage (how many lines of code were executed)
- Branch coverage (how many decision paths were tested)
- Cyclomatic complexity (a measure of how many independent paths exist through the code—higher values mean more logic branches and potential risk)
- Method and class-level visibility (which methods and classes were touched by tests)

👉 [View JaCoCo Coverage Report](/jacoco/index.html)

Coverage is generated during the build process and bundled into the deployed app for transparency and contributor insight.

## 🚫 Excluding the `view` Package from Coverage

- This project is built by and for backend developers. The core focus is on exposing clean, benchmarkable REST endpoints—not rendering HTML. The `view` package contains helper classes for Markdown rendering, sidebar generation, and HTML wrapping. While functional, it's not central to the API’s purpose or reliability.

- Due to time constraints and the fact that this extension is arguably overkill for a backend-focused system, I’ve made the decision to exclude `org.example.primeapi.view` from JaCoCo coverage. This lets us concentrate testing efforts where they matter: algorithm correctness, endpoint behavior, error handling, and caching logic.

- None of the functionality exposed via `/api/primes` or `/api/cache/clear` depends on the view layer. Even if the HTML rendering fails, the API still does exactly what it’s supposed to—generate primes, return structured responses, and log requests. So for now, I’m accepting the risk and skipping tests for this branch.

- If contributors want to extend or refactor the view layer later, they’re welcome to add coverage—but it’s not a priority for this release.

---

## 🎯 Testing Goals

- Achieve full JaCoCo coverage for all algorithm, service, and exception classes
- Validate `ErrorPayload` and `PrimePayload` structure across all response formats
- Ensure thread safety (no race conditions or shared state issues) and performance consistency in multi-threaded execution
- Maintain contributor-friendly test scaffolding (reusable test setup and helpers) with expressive logging and modular helpers

---

## 🧭 Contributor Notes

- Use `PrimeAlgorithmTestSupport` when adding new algorithms
- Use `TestHelperMethods.skipTest()` to conditionally skip benchmarks (useful for long-running tests)
- Validate both JSON and XML serialization for new payloads
- Ensure all new endpoints are covered by integration tests and fallback logic

---