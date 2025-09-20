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

---

## 📈 Code Coverage

We use **JaCoCo** to measure test coverage across all layers. The report includes:

- Instruction coverage (how many lines of code were executed)
- Branch coverage (how many decision paths were tested)
- Cyclomatic complexity (a measure of how many independent paths exist through the code—higher values mean more logic branches and potential risk)
- Method and class-level visibility (which methods and classes were touched by tests)

👉 [View JaCoCo Coverage Report](/jacoco/index.html)

Coverage is generated during the build process and bundled into the deployed app for transparency and contributor insight.

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