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

## 📊 Example Requests

### Render Deployed

- [Run sieve benchmark with 4 threads](https://primerestservice.onrender.com/api/primes?algorithm=sieve&limit=100000&threads=4)
- [Run atkin benchmark with 2 threads](https://primerestservice.onrender.com/api/primes?algorithm=atkin&limit=50000&threads=2)
- [Run trial benchmark with 1 thread](https://primerestservice.onrender.com/api/primes?algorithm=trial&limit=10000&threads=1)
- [Run miller benchmark with 8 threads](https://primerestservice.onrender.com/api/primes?algorithm=miller&limit=250000&threads=8)
- [Run sieve benchmark with cache disabled](https://primerestservice.onrender.com/api/primes?algorithm=sieve&limit=50000&threads=2&useCache=false)
- [Run atkin benchmark with XML response format](https://primerestservice.onrender.com/api/primes?algorithm=atkin&limit=20000&threads=1)
- [Run trial benchmark with cache enabled](https://primerestservice.onrender.com/api/primes?algorithm=trial&limit=15000&threads=2&useCache=true)
- [Run sieve benchmark with high thread count](https://primerestservice.onrender.com/api/primes?algorithm=sieve&limit=1000000&threads=16)
- [Run miller benchmark with small limit and JSON response](https://primerestservice.onrender.com/api/primes?algorithm=miller&limit=100&threads=1)
- [Run atkin benchmark with large limit](https://primerestservice.onrender.com/api/primes?algorithm=atkin&limit=75000&threads=4)

### Local host

- [Run sieve benchmark with 4 threads](http://localhost:8080/api/primes?algorithm=sieve&limit=100000&threads=4)
- [Run atkin benchmark with 2 threads](http://localhost:8080/api/primes?algorithm=atkin&limit=50000&threads=2)
- [Run trial benchmark with 1 thread](http://localhost:8080/api/primes?algorithm=trial&limit=10000&threads=1)
- [Run miller benchmark with 8 threads](http://localhost:8080/api/primes?algorithm=miller&limit=250000&threads=8)
- [Run sieve benchmark with cache disabled](http://localhost:8080/api/primes?algorithm=sieve&limit=50000&threads=2&useCache=false)
- [Run atkin benchmark with XML response format](http://localhost:8080/api/primes?algorithm=atkin&limit=20000&threads=1)
- [Run trial benchmark with cache enabled](http://localhost:8080/api/primes?algorithm=trial&limit=15000&threads=2&useCache=true)
- [Run sieve benchmark with high thread count](http://localhost:8080/api/primes?algorithm=sieve&limit=1000000&threads=16)
- [Run miller benchmark with small limit and JSON response](http://localhost:8080/api/primes?algorithm=miller&limit=100&threads=1)
- [Run atkin benchmark with large limit](http://localhost:8080/api/primes?algorithm=atkin&limit=75000&threads=4)



---

## 🧭 Landing Page


[View the raw HTML landing page](http://localhost:8080/api/info)

[View the raw HTML landing page on Render](https://primerestservice.onrender.com/api/info)




---