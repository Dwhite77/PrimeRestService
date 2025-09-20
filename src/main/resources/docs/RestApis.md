## 🌐 What Is a REST API?

A **REST API** (Representational State Transfer Application Programming Interface) is a standardized way for systems to communicate over HTTP. 
It allows clients (like browsers, mobile apps, or other services) to interact with a server by sending requests and receiving structured responses—usually in JSON or XML.


## ✅ Why REST Works Perfectly for This App

This app’s all about running prime benchmarks with different algorithms, limits, and thread counts—and REST is the cleanest way to expose that. 
You hit an endpoint with a few query params, get back structured data, and move on. No session state, no weird payload formats—just simple HTTP. 
It works locally, it works on Render, and it’s easy for contributors to test, automate, or plug into dashboards. The JSON responses are easy to parse, 
and the whole thing stays modular and predictable. REST keeps the interface lean, the logic focused, and the contributor experience frictionless.

### 🔧 Core Concepts

- **Resources**: Everything in a REST API is treated as a resource—users, posts, products, etc. Each resource is accessible via a unique URL.
- **HTTP Methods**:
    - `GET`: Retrieve data
    - `POST`: Create new data
    - `PUT`: Update existing data
    - `DELETE`: Remove data
- **Statelessness**: Each request is independent. The server doesn’t store client context between requests.
- **Structured Responses**: Data is typically returned in JSON format, making it easy to parse and use in frontend applications.
- **Uniform Interface**: REST APIs follow predictable patterns, making them intuitive to use and easy to document.

---

### 📦 Example Request

```http
GET /api/products/42
Host: example.com
Accept: application/json
```

**Response:**

```json
{
  "id": 42,
  "name": "Wireless Keyboard",
  "price": 49.99,
  "inStock": true
}
```

---

### 🚀 Why Use REST APIs?

- Language-agnostic: Any client that can make HTTP requests can use a REST API.
- Scalable: Stateless design makes it easier to scale horizontally.
- Modular: Each endpoint serves a specific purpose, making systems easier to maintain and extend.
- Developer-friendly: Clear structure and widespread adoption make REST APIs ideal for onboarding and collaboration.

---

### 🧠 REST vs Other API Styles
```
| Style     | Protocol | Format     | Statefulness | Common Use Case                   |
|-----------|----------|------------|--------------|-----------------------------------|
| REST      | HTTP     | JSON/XML   | Stateless    | Web services, mobile apps         |
| GraphQL   | HTTP     | JSON       | Stateless    | Flexible queries, single endpoint |
| gRPC      | HTTP/2   | Protobuf   | Stateless    | High-performance microservices    |

```
---

REST APIs are the backbone of modern web architecture—simple, powerful, and built for interoperability.


