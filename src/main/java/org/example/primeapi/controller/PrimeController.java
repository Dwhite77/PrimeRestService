package org.example.primeapi.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.example.primeapi.algo.Algos;
import org.example.primeapi.model.ApiResponse;
import org.example.primeapi.model.ErrorPayload;
import org.example.primeapi.model.PrimePayload;
import org.example.primeapi.service.PrimeService;
import org.example.primeapi.util.ErrorResponseBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/primes")
@Tag(name = "Prime API",
        description = """
        Returns prime numbers using selectable algorithms: trial, sieve, segmented.
        Benchmarked across input sizes from 10 to 1,000,000 using 4 threads.
        Trial is simplest but slowest; Sieve is faster; Segmented Sieve scales best.
        See full benchmark results in documentation or logs.
        """
)
public class PrimeController {

    @Autowired
    private PrimeService primeService;

    @GetMapping(produces = { "application/json", "application/xml" })
    public ApiResponse getPrimes(
            @RequestParam int limit,
            @RequestParam(defaultValue = "trial") String algorithm,
            @RequestParam(defaultValue = "1") int threads,
            HttpServletRequest request
    ) {
        log.info("Algorithm '{}' requested for limit {} with {} thread(s)", algorithm, limit, threads);

        if (limit < 0 || threads < 1) {ErrorPayload error = ErrorResponseBuilder.badRequest("Limit must be non-negative and threads must be >= 1", request);
            return ApiResponse.error(error, 400);
        }

        if (!Algos.isValidAlgo(algorithm)) {
            ErrorPayload error = ErrorResponseBuilder.badRequest("Unsupported algorithm: " + algorithm, request);
            return ApiResponse.error(error, 400);
        }

        List<Integer> primes = primeService.findPrimes(algorithm.toLowerCase(), limit, threads);

        PrimePayload payload = new PrimePayload(algorithm, limit, threads, primes, primes.size(), primeService.getDurationMs());

        return ApiResponse.success(payload, 200);
    }
}