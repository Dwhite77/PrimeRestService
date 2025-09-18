# Error Handling Overview

This document outlines all handled errors in the Prime API, including their causes, HTTP status codes, and expected error messages. Each error is returned as a structured `ErrorPayload` object for consistent client-side handling and debugging.

---

## Error Format

All errors follow this structure:

```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Detailed explanation of the error",
  "path": "/requested/endpoint"
}
```

---

## Handled Errors
```
| Error Name                            | When It Occurs                                                                 | Status Code | Error Label           | Example Message                                                                 |
|-------------------------------------------|----------------------------------------------------------------------------|-------------|-----------------------|---------------------------------------------------------------------------------|
| `MethodArgumentTypeMismatchException`     | A query/path parameter cannot be converted to the expected type            | `400`       | Bad Request           | `Invalid value for parameter 'limit': expected 'Integer', but received 'abc'`   |
| `MissingServletRequestParameterException` | A required query parameter is missing                                      | `400`       | Bad Request           | `Missing required parameter 'limit' of type 'Integer'`                          |
| `MissingPathVariableException`            | A required path variable is missing                                        | `400`       | Bad Request           | `Missing path variable 'algorithm'`                                             |
| `HttpMessageNotReadableException`         | The request body is malformed or unreadable                                | `400`       | Bad Request           | `Malformed request body: Unexpected character ('}'...)`                         |
| `IllegalArgumentException`                | A method receives an invalid argument                                      | `400`       | Bad Request           | `Illegal argument: limit must be positive`                                      |
| `ResponseStatusException`                 | A controller throws a manual status exception                              | `4xx/5xx`   | Bad Request           | `Unsupported algorithm: segmented-foo`                                          |
| `HttpRequestMethodNotSupportedException`  | An endpoint is called with an unsupported HTTP method                      | `405`       | Method Not Allowed    | `Method 'POST' not supported. Supported methods: GET`                           |
| `NoHandlerFoundException`                 | The requested path does not match any controller                           | `404`       | Not Found             | `Unknown path: /docs/view/unknown.md`                                           |
| `AlgorithmNotSupportedException`          | A requested algorithm is not implemented                                   | `400`       | Bad Request           | `Unsupported algorithm: fibonacci`                                              |
| `Exception` (generic fallback)            | Any unhandled exception                                                    | `500`       | Internal Server Error | `Unexpected error occurred`                                                     |
```


### Detailed Errors

Each error is described with its trigger condition, HTTP status code, label, and an example message.

---

### ⚠️ `MethodArgumentTypeMismatchException`
- **Occurs When**: A query or path parameter cannot be converted to the expected type
- **Status Code**: `400`
- **Label**: Bad Request
- **Example**:  
  `Invalid value for parameter 'limit': expected 'Integer', but received 'abc'`

---

### ⚠️ `MissingServletRequestParameterException`
- **Occurs When**: A required query parameter is missing
- **Status Code**: `400`
- **Label**: Bad Request
- **Example**:  
  `Missing required parameter 'limit' of type 'Integer'`

---

### ⚠️ `MissingPathVariableException`
- **Occurs When**: A required path variable is missing
- **Status Code**: `400`
- **Label**: Bad Request
- **Example**:  
  `Missing path variable 'algorithm'`

---

### ⚠️ `HttpMessageNotReadableException`
- **Occurs When**: The request body is malformed or unreadable
- **Status Code**: `400`
- **Label**: Bad Request
- **Example**:  
  `Malformed request body: Unexpected character ('}'...)`

---

### ⚠️ `IllegalArgumentException`
- **Occurs When**: A method receives an invalid argument
- **Status Code**: `400`
- **Label**: Bad Request
- **Example**:  
  `Illegal argument: limit must be positive`

---

### ⚠️ `ResponseStatusException`
- **Occurs When**: A controller throws a manual status exception
- **Status Code**: `4xx/5xx`
- **Label**: Bad Request
- **Example**:  
  `Unsupported algorithm: segmented-foo`

---

### ⚠️ `HttpRequestMethodNotSupportedException`
- **Occurs When**: An endpoint is called with an unsupported HTTP method
- **Status Code**: `405`
- **Label**: Method Not Allowed
- **Example**:  
  `Method 'POST' not supported. Supported methods: GET`

---

### ⚠️ `NoHandlerFoundException`
- **Occurs When**: The requested path does not match any controller
- **Status Code**: `404`
- **Label**: Not Found
- **Example**:  
  `Unknown path: /docs/view/unknown.md`

---

### ⚠️ `AlgorithmNotSupportedException`
- **Occurs When**: A requested algorithm is not implemented
- **Status Code**: `400`
- **Label**: Bad Request
- **Example**:  
  `Unsupported algorithm: fibonacci`

---

### ⚠️ `Exception` (Generic Fallback)
- **Occurs When**: Any unhandled exception
- **Status Code**: `500`
- **Label**: Internal Server Error
- **Example**:  
  `Unexpected error occurred`

---
## ErrorPayload Builder Methods

These are the static methods used to construct structured error responses:

- `badRequest(message, request)` → `400 Bad Request`
- `unauthorized(message, request)` → `401 Unauthorized`
- `forbidden(message, request)` → `403 Forbidden`
- `notFound(message, request)` → `404 Not Found`
- `methodNotAllowed(message, request)` → `405 Method Not Allowed`
- `conflict(message, request)` → `409 Conflict`
- `unprocessableEntity(message, request)` → `422 Unprocessable Entity`
- `internalServerError(message, request)` → `500 Internal Server Error`
- `serviceUnavailable(message, request)` → `503 Service Unavailable`

---

## 🧩 Example Error Response (Wrapped in `APIResponse`)

This is how a handled error is returned using your `APIResponse` model. The `error` field contains an `ErrorPayload`, while `data` is omitted. The `timestamp` is formatted as `dd-MM-yyyy HH:mm:ss`.

```json
{
  "httpStatus": 400,
  "error": {
    "status": 400,
    "label": "Bad Request",
    "message": "Missing required parameter 'limit' of type 'Integer'",
    "path": "/api/primes"
  },
  "timestamp": "18-09-2025 14:30:00"
}
```

> ✅ Note: `data` is omitted when an error occurs. The `isSuccessful()` method will return `false` in this case.

---

## 🔙 Navigation

- [← Back to Prime Algorithms Overview](/docs/view/prime-algorithms.md)
- [← Back to Documentation Index](/docs)