# 🧮 PrimeAPI

PrimeAPI is a modular, high-performance Java application for generating prime numbers using multiple algorithms. It exposes a RESTful interface, supports parallel execution, and includes a built-in documentation viewer powered by Markdown and HTML rendering.

---

## 🚀 Features

- Multiple prime generation algorithms: Trial Division, Sieve of Eratosthenes, Sieve of Atkin, Miller–Rabin
- Parallel execution with thread pooling (splitting work across multiple threads)
- REST API with JSON and XML support
- Swagger/OpenAPI documentation (auto-generated API docs)
- Markdown-based documentation portal with sidebar navigation
- Structured error handling and fallback responses
- Benchmarking and performance logging
- Full test coverage with JaCoCo (Java Code Coverage tool)

---

## 🧠 Algorithms

| Algorithm         | Time Complexity (how runtime grows with input size) | Use Case Notes                                      |
|------------------|---------------------------|----------------------------------------------------|
| Trial Division    | O(√n)                     | Simple, slow; good for small inputs or teaching    |
| Sieve of Eratosthenes | O(n log log n)        | Fast for generating all primes up to n             |
| Miller–Rabin      | O(k log³ n)               | Probabilistic; efficient for testing one number    |
| Sieve of Atkin    | O(n) (theoretical)        | Complex; faster than Eratosthenes for large n      |

Each algorithm implements the `PrimeAlgorithm` interface and is registered via Spring’s dependency injection. Parallelization is handled by `AbstractPrimeAlgorithm` using `ThreadPoolManager`, which creates named thread pools for concurrent execution.

---

## 📦 API Endpoints

### `/api/primes`
Generates prime numbers using the specified algorithm.

**Query Parameters:**
- `limit` (int): Upper bound for prime generation
- `algorithm` (string): One of `trial`, `sieve`, `atkin`, `miller`
- `threads` (int): Number of threads to use

**Response:**
- `PrimePayload` with algorithm name, limit, thread count, prime list, total count, and duration

### `/api/info`
Returns the landing page HTML with links to documentation.

### `/docs`
Lists available Markdown documentation files.

### `/docs/view/{filename}`
Renders a Markdown file as styled HTML with sidebar and backlinks.

### Error Simulation Endpoints
Used for testing global exception handling:
- `/api/trigger-runtime-exception`
- `/api/trigger-response-status`
- `/api/trigger-illegal-argument`

---

## 🧰 Error Handling

All exceptions are handled by `GlobalExceptionHandler`, which maps common Spring and runtime errors to structured `APIResponse` objects.

**Handled Cases:**
- Missing parameters
- Type mismatches
- Unsupported HTTP methods
- Malformed request bodies
- Unknown paths
- Runtime exceptions

Responses include:
- `status` (HTTP code)
- `error` (label like "Bad Request")
- `message` (detailed explanation)
- `path` (endpoint that triggered the error)
- `timestamp` (formatted as `dd-MM-yyyy HH:mm:ss`)

`ErrorResponseBuilder` provides reusable methods to construct these payloads for each error type.

---

## 📚 Documentation Portal

Markdown files are stored in `resources/docs` and rendered via `HtmlHelper` and `MarkdownConverter`.

Features:
- Sidebar navigation
- Grouped sections: Algorithms, Informational, Miscellaneous
- Backlinks for intuitive navigation
- Embedded algorithm comparison and recent request tables

Caching is enabled via `CacheConfig` using `ConcurrentMapCacheManager`, which stores parsed documentation under the `"docs"` cache. This improves performance when rendering frequently accessed files.

---

## 🧪 Testing Strategy

Testing is divided into:

- **Unit Tests**: Validate algorithm logic, model serialization, and utility behavior
- **Integration Tests**: Verify controller behavior, error handling, and service logic
- **Benchmarking**: Measure performance across algorithms and thread counts

Coverage is tracked via **JaCoCo**, including:
- Instruction coverage (lines executed)
- Branch coverage (decision paths tested)
- Cyclomatic complexity (number of independent logic paths—higher means more complex logic)
- Method/class visibility (which components are exercised)

👉 [View JaCoCo Coverage Report](/jacoco/index.html)

---

## 🛠 Deployment Notes

- Built with Spring Boot
- Configurable via environment variables:
    - `MAXLIMIT` (default: 1,000,000,000)
    - `MAXTHREADS` (default: 128)
- Compatible with Render and other cloud platforms
- Static resources and documentation are bundled into the JAR

---

## 👥 Contributor Guide

- Add new algorithms by implementing `PrimeAlgorithm` and extending `AbstractPrimeAlgorithm`
- Register via `@Component` for auto-discovery
- Document new algorithms in `docs/` and link them in `buildIndexContent(...)`
- Use `PrimeAlgorithmTestSupport` for consistent test coverage
- Validate both JSON and XML serialization
- Ensure all endpoints are covered by integration tests

---

## 📄 Documentation Index

- [Prime Algorithms Overview](src/main/resources/docs/Prime-Algorithms.md)
- [Trial Division](src/main/resources/docs/Trial.md)
- [Sieve of Eratosthenes](src/main/resources/docs/Sieve.md)
- [Sieve of Atkin](src/main/resources/docs/Atkin.md)
- [Miller-Rabin Test](src/main/resources/docs/Miller.md)
- [Error Handling](src/main/resources/docs/Error-Handling.md)
- [Jacoco Report](src/main/resources/docs/jacoco/index.html)

---

## 🧩 Technologies Used

- Java 17+
- Spring Boot
- JUnit 5
- RestAssured
- Swagger/OpenAPI
- Markdown + HTML rendering
- JaCoCo (test coverage)
- ConcurrentMapCacheManager (in-memory caching)

---

## 📬 Contact

Built by Dan. For questions, contributions, or feedback, feel free to open an issue or reach out directly.