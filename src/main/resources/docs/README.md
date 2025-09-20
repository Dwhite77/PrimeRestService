# PrimeAPI

PrimeAPI is a modular Java application for generating prime numbers using multiple algorithms. It exposes a RESTful API, supports parallel execution, and includes a built-in documentation viewer powered by Markdown and HTML rendering.

---

## Features

- Multiple algorithms: Trial Division, Sieve of Eratosthenes, Sieve of Atkin, Miller–Rabin
- Parallel execution with thread pooling
- REST API with JSON and XML support
- Swagger/OpenAPI documentation
- Markdown-based documentation portal
- Structured error handling
- Benchmarking and performance logging
- Full test coverage with JaCoCo

---

## Algorithms

Each algorithm implements the `PrimeAlgorithm` interface and supports multithreaded execution via `AbstractPrimeAlgorithm`.

- **Trial Division** – Simple, slow; good for small inputs or teaching
- **Sieve of Eratosthenes** – Fast for generating all primes up to n
- **Sieve of Atkin** – Complex but efficient for large ranges
- **Miller–Rabin** – Probabilistic; efficient for testing individual numbers

---

## API Endpoints

- `/api/primes` – Generate primes with query parameters: `limit`, `algorithm`, `threads`
- `/api/info` – Landing page with links to documentation
- `/docs` – Lists available Markdown documentation files
- `/docs/view/{filename}` – Renders Markdown file as styled HTML
- `/api/trigger-runtime-exception` – Simulates a runtime error
- `/api/trigger-response-status` – Simulates a custom status error
- `/api/trigger-illegal-argument` – Simulates an illegal argument error

---

## Error Handling

All exceptions are handled by `GlobalExceptionHandler`, returning structured `APIResponse` objects with:

- `status` – HTTP code
- `error` – Label (e.g. Bad Request)
- `message` – Explanation
- `path` – Triggering endpoint
- `timestamp` – Formatted as `dd-MM-yyyy HH:mm:ss`

`ErrorResponseBuilder` provides reusable methods for generating these payloads.

---

## Documentation Portal

Markdown files are rendered via `/docs/view/{filename}` with sidebar navigation and backlinks. Caching is enabled via `CacheConfig` to improve performance.

---

## Testing Strategy

Testing includes:

- Unit tests for algorithm logic and models
- Integration tests for controllers and error handling
- Benchmarking for performance across thread counts

Coverage is tracked via JaCoCo:

- Instruction coverage – lines executed
- Branch coverage – decision paths tested
- Cyclomatic complexity – number of independent logic paths
- Method/class visibility – which components are exercised

---

## Deployment Notes

- Built with Spring Boot
- Configurable via environment variables:
    - `MAXLIMIT` (default: 1,000,000,000)
    - `MAXTHREADS` (default: 128)
- Compatible with Render and other cloud platforms

---

## Contributor Guide

- Implement new algorithms via `PrimeAlgorithm` and `AbstractPrimeAlgorithm`
- Register with `@Component`
- Document in `/docs` and link via `HtmlHelper`
- Use `PrimeAlgorithmTestSupport` for consistent testing
- Validate both JSON and XML serialization
- Cover all endpoints with integration tests

---

## Documentation Index

- [Prime Algorithms Overview](/docs/view/Prime-Algorithms.md)
- [Trial Division](/docs/view/Trial.md)
- [Sieve of Eratosthenes](/docs/view/Sieve.md)
- [Sieve of Atkin](/docs/view/Atkin.md)
- [Miller-Rabin Test](/docs/view/Miller.md)
- [Error Handling](/docs/view/Error-Handling.md)
- [Jacoco Report](/docs/view/jacoco/index.html)

---

## Technologies Used

- Java 17+
- Spring Boot
- JUnit 5
- RestAssured
- Swagger/OpenAPI
- Markdown + HTML rendering
- JaCoCo
- ConcurrentMapCacheManager

---

## Contact

Built by Dan. For questions, contributions, or feedback, open an issue or reach out directly.