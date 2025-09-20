package org.example.primeapi.controller;

import io.swagger.v3.oas.annotations.Operation;
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
import org.example.primeapi.view.HtmlHelper;
import org.example.primeapi.view.LandingPageBuilder;
import org.example.primeapi.view.PrimeRequestLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.cache.CacheManager;
import org.springframework.cache.Cache;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Slf4j
@RestController
@RequestMapping("/")
public class PrimeController {

    @Autowired
    private PrimeService primeService;

    @Autowired
    private CacheManager cacheManager;

    @Tag(name = "Prime API", description = "Endpoints for prime number generation and benchmarking")
    @Operation(
            summary = "Generate prime numbers",
            description = """
        Returns a list of prime numbers using the specified algorithm.
        Supports trial division, sieve of Eratosthenes, and segmented sieve.
        Results include count and duration in milliseconds.
        """,
            tags = { "Prime API" }
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful prime generation", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = PrimePayload.class))
            }),
            @ApiResponse(responseCode = "400", description = """
        Bad request due to:
        - Missing required parameters
        - Invalid parameter types
        - Unsupported algorithm
        - Illegal argument
        - Malformed request body
        """, content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorPayload.class))
            }),
            @ApiResponse(responseCode = "404", description = "Path not found or missing path variable", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorPayload.class))
            }),
            @ApiResponse(responseCode = "405", description = "HTTP method not supported for this endpoint", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorPayload.class))
            }),
            @ApiResponse(responseCode = "500", description = """
        Internal server error due to:
        - Unhandled exceptions
        - Runtime failures
        - ResponseStatusException
        """, content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorPayload.class))
            })
    })
    @GetMapping(path="/api/primes", produces = { "application/json", "application/xml" })
    public ResponseEntity<APIResponse> getPrimes(
            @RequestParam int limit,
            @RequestParam(defaultValue = "trial") String algorithm,
            @RequestParam(defaultValue = "1") int threads,
            @RequestParam(defaultValue = "false") boolean useCache,
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

        List<Integer> primes = primeService.findPrimes(algorithm.toLowerCase(), limit, threads, useCache);

        PrimePayload payload = new PrimePayload(
                algorithm, limit, threads, primes, primes.size(), primeService.getDurationMs()
        );
        PrimeRequestLog.log(payload);
        return ResponseEntity.ok(APIResponse.success(payload, 200));
    }


    @GetMapping
    public RedirectView redirectToInfo(){
        return new RedirectView("/api/info");
    }

    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Returns landing page HTML"),
            @ApiResponse(responseCode = "404", description = "Page not found")
    })
    @GetMapping(path = "/api/info", produces = "text/html")
    public String landingPage() {
        return LandingPageBuilder.getHtml();
    }



    //----------------Misc-----------------------------------


    @Tag(name = "Cache", description = "Endpoints for managing cached prime results")
    @Operation(
            summary = "Clear prime cache",
            description = "Evicts cached prime results from memory. Clears all caches by default, or a specific cache if 'target' is provided.",
            tags = { "Cache" }
    )
    @GetMapping("/api/cache/clear")
    public ResponseEntity<PrimePayload> clearCache(@RequestParam(required = false) String target) {
        Set<String> validCaches = Set.of("primes", "basePrimes");
        List<Integer> clearedCodes = new ArrayList<>();

        if (target == null) {
            primeService.clearPrimeCache();
            primeService.clearBasePrimeCache();
            clearedCodes.addAll(List.of(1, 2)); // 1 = primes, 2 = basePrimes
            log.info("✅ All caches cleared via PrimeService");
        } else {
            switch (target) {
                case "primes" -> {
                    primeService.clearPrimeCache();
                    clearedCodes.add(1);
                    log.info("✅ Prime cache cleared via PrimeService");
                }
                case "basePrimes" -> {
                    primeService.clearBasePrimeCache();
                    clearedCodes.add(2);
                    log.info("✅ Base prime cache cleared via PrimeService");
                }
                default -> {
                    log.warn("❌ Invalid cache target: {}", target);
                    return ResponseEntity.badRequest().body(new PrimePayload("invalid-cache", 0, 0, List.of(), 0, 0));
                }
            }
        }

        PrimePayload payload = new PrimePayload("cache-cleared", 0, 0, clearedCodes, 0, 0);
        return ResponseEntity.ok(payload);
    }

    //----------------Documentation Controller--------------


    @Tag(name = "Documentation", description = "Endpoints for viewing Markdown-based documentation")
    @Operation(
            summary = "List available documentation files",
            description = "Returns an HTML index of all Markdown documentation files.",
            tags = { "Documentation" }
    )
    @GetMapping("/docs")
    public String listMarkdownFiles() {
        List<String> filenames = HtmlHelper.getMarkdownFiles();
        String sidebar = HtmlHelper.buildSidebar(filenames);
        String content = HtmlHelper.buildIndexContent(filenames);
        return HtmlHelper.wrapHtml(sidebar, content, false);
    }



    @Tag(name = "Documentation", description = "Endpoints for viewing Markdown-based documentation")
    @Operation(
            summary = "View Markdown file as HTML",
            description = "Renders the specified Markdown file as styled HTML with sidebar and backlinks.",
            tags = { "Documentation" }
    )
    @GetMapping("/docs/view/{filename}")
    public ResponseEntity<String> viewMarkdownAsHtml(@PathVariable String filename) {
        if (!HtmlHelper.isValidMarkdownFile(filename)) {
            return ResponseEntity.status(404).body("<h2>File not found: " + filename + "</h2>");
        }

        try {
            String markdown = HtmlHelper.readMarkdown(filename);
            String html = HtmlHelper.convertMarkdownToHtml(filename, markdown);
            String sidebar = HtmlHelper.buildSidebar(HtmlHelper.getMarkdownFiles());
            String fullHtml = HtmlHelper.wrapHtml(sidebar, html, true);

            return ResponseEntity.ok()
                    .header("Content-Type", "text/html; charset=UTF-8")
                    .body(fullHtml);

        } catch (IOException e) {
            return ResponseEntity.status(500).body("<h2>Error reading file: " + filename + "</h2>");
        }
    }




    //-----------------Testing Endpoints----------

    @Tag(name = "Error Simulation", description = "Endpoints that trigger various exceptions for testing")
    @Operation(
            summary = "Trigger runtime exception",
            description = "Throws a RuntimeException to test global error handling.",
            tags = { "Error Simulation" }
    )
    @GetMapping("/api/trigger-runtime-exception")
    public APIResponse triggerException() {
        throw new RuntimeException("Boom");
    }
    @Tag(name = "Error Simulation", description = "Endpoints that trigger various exceptions for testing")
    @Operation(
            summary = "Trigger response status exception",
            description = "Throws a ResponseStatusException with HTTP 418 status.",
            tags = { "Error Simulation" }
    )
    @GetMapping("/api/trigger-response-status")
    public void triggerResponseStatus() {
        throw new ResponseStatusException(HttpStatus.I_AM_A_TEAPOT, "I'm a teapot");
    }
    @Tag(name = "Error Simulation", description = "Endpoints that trigger various exceptions for testing")
    @Operation(
            summary = "Trigger illegal argument exception",
            description = "Throws an IllegalArgumentException to test validation errors.",
            tags = { "Error Simulation" }
    )
    @GetMapping("/api/trigger-illegal-argument")
    public void triggerIllegalArgument() {
        throw new IllegalArgumentException("Illegal argument");
    }



}