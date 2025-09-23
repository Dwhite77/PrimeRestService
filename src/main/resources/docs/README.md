# 🧮 PrimeAPI

PrimeAPI is a modular, high-performance Java application for generating prime numbers using multiple algorithms. It exposes a RESTful API, supports parallel execution, and includes a built-in documentation viewer powered by Markdown and HTML rendering.

---
### 🔗 [Main Render Deployment Page](https://primerestservice.onrender.com/) : https://primerestservice.onrender.com/
## 🚀 Features

- Multiple prime generation algorithms: Trial Division, Sieve of Eratosthenes, Sieve of Atkin, Miller–Rabin
- Parallel execution with thread pooling
- REST API with JSON and XML support
- Swagger/OpenAPI documentation (auto-generated API docs)
- Markdown-based documentation portal with sidebar navigation
- Structured error handling and fallback responses
- Benchmarking and performance logging
- Full test coverage with JaCoCo
- Caching for rendered documentation via `ConcurrentMapCacheManager`

---

## 🧠 Algorithms

Each algorithm implements the `PrimeAlgorithm` interface and is registered via Spring’s dependency injection. Parallelization is handled by `AbstractPrimeAlgorithm` using `ThreadPoolManager`.
```
| Algorithm              | Time Complexity        | Use Case Notes                                      |
|------------------------|------------------------|-----------------------------------------------------|
| Trial Division         | O(N * √N)              | Simple, slow; good for small inputs                 |
| Sieve of Eratosthenes  | O(n*log(log(n)))       | Fast for generating all primes up to n              |
| Sieve of Atkin         | O(n) (theoretical)     | Complex; faster than Eratosthenes for large n       |
| Miller–Rabin           | O(N * k * log³ N)      | Probabilistic; efficient for testing one number     |
```

---

## 📦 API Endpoints

### `/api/primes`
Generates prime numbers using the specified algorithm.

**Query Parameters:**
- `limit` (int): Upper bound for prime generation
- `algorithm` (string): One of `trial`, `sieve`, `atkin`, `miller`
- `threads` (int): Number of threads to use

**Response:**  
Returns a `PrimePayload` with algorithm name, limit, thread count, prime list, total count, and duration.

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

**Response Structure:**
- `status` – HTTP code
- `error` – Label (e.g. Bad Request)
- `message` – Explanation
- `path` – Triggering endpoint
- `timestamp` – Formatted as `dd-MM-yyyy HH:mm:ss`

Reusable payloads are constructed via `ErrorResponseBuilder`.

---

## 📚 Documentation Portal

Markdown files are stored in `resources/docs` and rendered via `HtmlHelper` and `MarkdownConverter`.

**Features:**
- Sidebar navigation
- Grouped sections: Algorithms, Informational, Miscellaneous
- Backlinks for intuitive navigation
- Embedded algorithm comparison and recent request tables

---

## 🧪 Testing Strategy

Testing is divided into:

- **Unit Tests**: Validate algorithm logic, model serialization, and utility behavior
- **Integration Tests**: Verify controller behavior, error handling, and service logic
- **Benchmarking**: Measure performance across algorithms and thread counts

Coverage is tracked via **JaCoCo**, including:
- Instruction coverage (lines executed)
- Branch coverage (decision paths tested)
- Cyclomatic complexity (number of independent logic paths)
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

🔗 [Main Render Deployment Page](https://primerestservice.onrender.com/)

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
- [Main Docs Page](/docs)
- [Prime Algorithms Overview](/docs/view/Prime-Algorithms.md)
- [Trial Division](/docs/view/Trial.md)
- [Sieve of Eratosthenes](/docs/view/Sieve.md)
- [Sieve of Atkin](/docs/view/Atkin.md)
- [Miller-Rabin Test](/docs/view/Miller.md)
- [Error Handling](/docs/view/Error-Handling.md)
- [Jacoco Report](/docs/view/jacoco/index.html)

---

## 🧩 Technologies Used

- Java 17+
- Spring Boot
- Microsoft CoPilot (To speed up Code generation, Documentation generation, Test generation and Refactoring)
- GitLab Duo (To speed up Code generation, Documentation generation, Test generation and Refactoring)
- JUnit 5
- RestAssured
- Swagger/OpenAPI
- Markdown + HTML rendering
- JaCoCo
- ConcurrentMapCacheManager
- Docker
- Lombok
- Jackson Databind

---

## 📬 Contact

Built by Dan. For questions, contributions, or feedback, feel free to open an issue or reach out directly.
