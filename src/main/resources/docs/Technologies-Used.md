# 🛠️ Technologies Used

This project combines a lean, testable backend with contributor-friendly documentation and deployment. Here's a breakdown of the tools and why they were chosen:

---

## ⚙️ Build & Language

### Maven
Used as the build tool to manage dependencies, plugins, and lifecycle phases. It keeps the project modular and reproducible across environments.

### Java 17+
Modern language features, better performance, and long-term support. Enables cleaner syntax and improved concurrency handling.

---

## 🚀 Frameworks & Runtime

### Spring Boot
Handles dependency injection, web routing, and configuration. It simplifies REST API development and integrates cleanly with testing, docs, and deployment.

### Docker
Containerizes the app for consistent deployment across local and cloud environments. Makes onboarding and scaling frictionless.

### Render
Cloud platform used to deploy the app. It supports automatic builds from Git and exposes the REST API with minimal setup.

---

## 🧪 Testing & Coverage

### JUnit 5
Core testing framework for unit and integration tests. Supports parameterized tests and clean lifecycle hooks.

### RestAssured
Used for expressive HTTP-level integration tests. Validates endpoint behavior and response structure.

### AssertJ, Hamcrest, Mockito
Pulled in via `spring-boot-starter-test`. These libraries support fluent assertions, matcher-based validation, and mocking for isolated unit tests.

### JaCoCo
Generates code coverage reports. Integrated into the Maven build to track test completeness and expose results via static resources.

---

## 📦 Data & Serialization

### Jackson Databind
Handles JSON serialization and deserialization. Powers the API’s request/response payloads.

### Jackson XML & JSR310
Adds support for XML responses and Java time types. Enables flexible formatting for clients that prefer XML over JSON.

---

## 📚 Documentation & Rendering

### Swagger/OpenAPI (springdoc)
Auto-generates interactive API documentation. Makes it easy for contributors to explore endpoints and test requests.

### Markdown + HTML Rendering (CommonMark)
Used to render contributor docs with tables, headings, and inline formatting. Supports GitHub-flavored markdown and custom HTML output.

---

## 🧰 Developer Experience

### Lombok
Reduces boilerplate with annotations like `@Getter`, `@Builder`, and `@AllArgsConstructor`. Keeps models clean and focused.

---

This stack was chosen to balance clarity, performance, and contributor usability. 
Every tool serves a distinct purpose—from endpoint design to test coverage to deployment.