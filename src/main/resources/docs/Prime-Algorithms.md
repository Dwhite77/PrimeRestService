# 🧠 Prime Generation Algorithms Overview

This document provides a high-level index of the prime generation algorithms implemented in this project. Each algorithm serves a distinct purpose and offers different trade-offs in terms of performance and complexity.

## Available Algorithms

- [Trial Division](/docs/view/Trial.md)
- [Sieve of Eratosthenes](/docs/view/Sieve.md)
- [Miller-Rabin Primality Test](/docs/view/Miller.md)
- [Sieve of Atkin](/docs/view/Atkin.md)

---

## 🔢 Prime Generation API Examples

These endpoints return prime numbers using different algorithms and thread counts:
```
| Description                                   | Example URL                                                                   |
|-----------------------------------------------|-------------------------------------------------------------------------------|
| Trial division with default thread            | `http://localhost:8080/api/primes?limit=100&algorithm=trial`                  |
| Sieve of Eratosthenes with 4 threads          | `http://localhost:8080/api/primes?limit=1000&algorithm=sieve&threads=4`       |
| Segmented sieve with 8 threads                | `http://localhost:8080/api/primes?limit=50000&algorithm=segmented&threads=8`  |
| Invalid algorithm (triggers error response)   | `http://localhost:8080/api/primes?limit=100&algorithm=banana`                 |
| Negative limit (triggers error response)      | `http://localhost:8080/api/primes?limit=-10&algorithm=trial`                  |
```
---

---

## 🧭 Landing Page
```
| Description                    | Example URL                      |
|--------------------------------|----------------------------------|
| View the raw HTML landing page | `http://localhost:8080/api/info` |
```

## 📊 Benchmark Dashboard Examples

- [View dashboard](http://localhost:8080/dashboard)
- [Run sieve benchmark with 4 threads](http://localhost:8080/api/primes?algorithm=sieve&limit=100000&threads=4)
- [Run atkin benchmark with custom tag](http://localhost:8080/api/primes?algorithm=atkin&limit=50000&threads=2&tag=atkin-fast)
- [Run trial benchmark with 1 thread](http://localhost:8080/api/primes?algorithm=trial&limit=10000&threads=1)

Each algorithm is accessible via the API and can be benchmarked independently. For implementation details, refer to the linked documentation files.

---