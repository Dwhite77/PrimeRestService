package org.example.primeapi.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/primes")
@Tag(name = "Prime API",
        description = """
        Returns prime numbers using selectable algorithms: trial, sieve, segmented.
        Benchmarked across input sizes from 10 to 1,000,000 using 4 threads.
        Trial is simplest but slowest; Sieve is faster; Segmented Sieve scales best.
        See full benchmark results in documentation or logs.
        """)
public class PrimeController {

}
