# 🧮 PrimeAPI

### 🔗 [Main Render Deployment Page](https://primerestservice.onrender.com/) : https://primerestservice.onrender.com/
### 🔗 [Swagger Link](https://primerestservice.onrender.com/swagger-ui/index.html)
PrimeAPI is a modular, high-performance Java application for generating prime numbers using multiple algorithms. It exposes a RESTful API, supports parallel execution, and includes a built-in documentation viewer powered by Markdown and HTML rendering.

---

## Project Set-up
```
git clone https://github.com/Dwhite77/PrimeRestService.git

cd PrimeRestService

mvn clean install

mvn spring-boot:run
```

---
## 🚀 Features

- Multiple prime generation algorithms: Trial Division, Sieve of Eratosthenes, Sieve of Atkin, Miller–Rabin
- Parallel execution with thread pooling
- REST API with JSON and XML support
- Swagger/OpenAPI documentation
- Markdown-based documentation portal with sidebar navigation
- Structured error handling and fallback responses
- Benchmarking and performance logging
- Test coverage with JaCoCo (92%)

---

## 🧠 Algorithms


Each algorithm implements the `PrimeAlgorithm` interface and is registered via Spring’s dependency injection. Parallelization is handled by `AbstractPrimeAlgorithm` using `ThreadPoolManager`.

| Algorithm              | Time Complexity        | Use Case Notes                                      |
|------------------------|------------------------|-----------------------------------------------------|
| Trial Division         | O(N * √N)              | Simple, slow; good for small inputs                 |
| Sieve of Eratosthenes  | O(n*log(log(n)))       | Fast for generating all primes up to n              |
| Sieve of Atkin         | O(n) (theoretical)     | Complex; faster than Eratosthenes for large n       |
| Miller–Rabin           | O(N * k * log³ N)      | Probabilistic; efficient for testing one number     |

---

## 📦 API Endpoints

### `/api/primes`
- https://primerestservice.onrender.com/api/primes?limit=100&algorithm=sieve&threads=2 Headers: Accept: application/xml
```xml
<APIResponse>
  <httpStatus>200</httpStatus>
  <data>
    <algorithm>sieve</algorithm>
    <limit>100</limit>
    <threads>2</threads>
    <primes>
      <prime>2</prime>
      <prime>3</prime>
      <prime>5</prime>
      <prime>7</prime>
      <prime>11</prime>
      <prime>13</prime>
      <prime>17</prime>
      <prime>19</prime>
      <prime>23</prime>
      <prime>29</prime>
      <prime>31</prime>
      <prime>37</prime>
      <prime>41</prime>
      <prime>43</prime>
      <prime>47</prime>
      <prime>53</prime>
      <prime>59</prime>
      <prime>61</prime>
      <prime>67</prime>
      <prime>71</prime>
      <prime>73</prime>
      <prime>79</prime>
      <prime>83</prime>
      <prime>89</prime>
      <prime>97</prime>
    </primes>
    <total>25</total>
    <durationMs>1</durationMs>
  </data>
  <timestamp>21-09-2025 11:37:57</timestamp>
  <successful>true</successful>
</APIResponse>
```

Or Headers: Accept: application/json

```json
{
  "httpStatus": 200,
  "data": {
    "algorithm": "sieve",
    "limit": 100,
    "threads": 2,
    "primes": [2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 53, 59, 61, 67, 71, 73, 79, 83, 89, 97],
    "total": 25,
    "durationMs": 3
  },
  "timestamp": "21-09-2025 12:45:24",
  "successful": true
}

```
- Generates prime numbers using the specified algorithm.

**Query Parameters:**
- `limit` (int): Upper bound for prime generation (This is Currently limited to 1000000000)
- `algorithm` (string): One of `trial`, `sieve`, `atkin`, `miller`
- `threads` (int): Number of threads to use (This is Currently limited to 128)

**Response:**  
Returns a `PrimePayload` with algorithm name, limit, thread count, prime list, total count, and duration.

### `/api/info`
- https://primerestservice.onrender.com/api/info
- Returns the landing page HTML with links to documentation. (No longer the landing page)

### `/docs`
- https://primerestservice.onrender.com/docs
- Lists available Markdown documentation files. (This is the new landing page)

### `/docs/view/{filename}`
- https://primerestservice.onrender.com/docs/view/Atkin.md
- Renders a Markdown file as styled HTML with sidebar and backlinks.

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
- Caching via `CacheConfig` for performance

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


👉 [View JaCoCo Coverage Report](https://primerestservice.onrender.com/jacoco/index.html)


---

## 🛠 Deployment Notes

- Built with Spring Boot
- Configurable via environment variables:
  - `MAXLIMIT` (default: 1,000,000,000)
  - `MAXTHREADS` (default: 128)
  - You can alter these but the application has not been tested beyond these limits so may be unstable
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

- [Prime Algorithms Overview](https://primerestservice.onrender.com/docs/view/Prime-Algorithms.md)
- [Trial Division](https://primerestservice.onrender.com/docs/view/Trial.md)
- [Sieve of Eratosthenes](https://primerestservice.onrender.com/docs/view/Sieve.md)
- [Sieve of Atkin](https://primerestservice.onrender.com/docs/view/Atkin.md)
- [Miller-Rabin Test](https://primerestservice.onrender.com/docs/view/Miller.md)
- [Error Handling](https://primerestservice.onrender.com/docs/view/Error-Handling.md)
- [Jacoco Report](https://primerestservice.onrender.com/jacoco/index.html)

---

## 🧩 Technologies Used

- Java 17+
- Spring Boot
- Microsoft CoPilot (To speed up Code generation, Documentation generation, Test generation and Refactoring)
- GitLab Duo (To speed up Code generation, Documentation generation, Test generation and Refactoring)
- JUnit 5
- RestAssured
- [Swagger](https://primerestservice.onrender.com/swagger-ui/index.html)/OpenAPI
- Markdown + HTML rendering
- JaCoCo
- ConcurrentMapCacheManager
- Docker
- Lombok
- Jackson Databind

---

## 📬 Contact

Built by Dan. For questions, contributions, or feedback, feel free to open an issue or reach out directly.
