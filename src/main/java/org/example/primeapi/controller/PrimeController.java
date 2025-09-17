package org.example.primeapi.controller;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.example.primeapi.algo.Algos;
import org.example.primeapi.model.APIResponse;
import org.example.primeapi.model.ErrorPayload;
import org.example.primeapi.model.PrimePayload;
import org.example.primeapi.service.PrimeService;
import org.example.primeapi.util.ErrorResponseBuilder;
import org.example.primeapi.view.LandingPageBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

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


    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful operation", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = PrimePayload.class)),
            }),
            @ApiResponse(responseCode = "400", description = "Invalid input or algorithm", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorPayload.class))
            })
    })

    @GetMapping(produces = { "application/json", "application/xml" })
    public ResponseEntity<APIResponse> getPrimes(
            @RequestParam int limit,
            @RequestParam(defaultValue = "trial") String algorithm,
            @RequestParam(defaultValue = "1") int threads,
            HttpServletRequest request
    ) {
        log.info("Algorithm '{}' requested for limit {} with {} thread(s)", algorithm, limit, threads);

        if (limit < 0 || threads < 1) {
            ErrorPayload error = ErrorResponseBuilder.badRequest("Limit must be non-negative and threads must be >= 1", request);
            return ResponseEntity.status(400).body(APIResponse.error(error, 400));
        }

        if (!Algos.isValidAlgo(algorithm)) {
            ErrorPayload error = ErrorResponseBuilder.badRequest("Unsupported algorithm: " + algorithm, request);
            return ResponseEntity.status(400).body(APIResponse.error(error, 400));
        }

        List<Integer> primes = primeService.findPrimes(algorithm.toLowerCase(), limit, threads);

        PrimePayload payload = new PrimePayload(
                algorithm, limit, threads, primes, primes.size(), primeService.getDurationMs()
        );

        return ResponseEntity.ok(APIResponse.success(payload, 200));
    }
    @GetMapping(path = "/", produces = "text/html")
    public String landingPage() {
        return LandingPageBuilder.getHtml();
    }
}