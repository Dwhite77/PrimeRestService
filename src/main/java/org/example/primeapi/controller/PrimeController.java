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
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.view.RedirectView;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/")
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



    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful operation", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = PrimePayload.class))
            }),
            @ApiResponse(responseCode = "400", description = "Bad request due to invalid parameters or unsupported algorithm", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorPayload.class))
            }),
            @ApiResponse(responseCode = "404", description = "Path not found", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorPayload.class))
            }),
            @ApiResponse(responseCode = "405", description = "HTTP method not supported", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorPayload.class))
            }),
            @ApiResponse(responseCode = "500", description = "Unexpected server error", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorPayload.class))
            })
    })

    @GetMapping(path="/api/primes", produces = { "application/json", "application/xml" })
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


    //----------------Documentation Controller--------------

    @GetMapping("/docs")
    public String listMarkdownFiles() {
        File docsDir = new File("docs");
        File[] files = docsDir.listFiles((dir, name) -> name.endsWith(".md"));

        StringBuilder html = new StringBuilder("<html><body><h1>📚 Documentation Index</h1><ul>");
        if (files != null) {
            Arrays.stream(files)
                    .sorted(Comparator.comparing(File::getName))
                    .forEach(file -> {
                        String name = file.getName();
                        html.append(String.format("<li><a href=\"/docs/view/%s\">%s</a></li>", name, name));
                    });
        } else {
            html.append("<li>No documentation files found.</li>");
        }
        html.append("</ul></body></html>");
        return html.toString();
    }

    @GetMapping("/docs/view/{filename}")
    public ResponseEntity<String> viewMarkdownFile(@PathVariable String filename) {
        File file = new File("docs", filename);
        if (!file.exists() || !filename.endsWith(".md")) {
            return ResponseEntity.status(404).body("File not found: " + filename);
        }

        try {
            String content = Files.readString(file.toPath());
            return ResponseEntity.ok()
                    .header("Content-Type", "text/plain; charset=UTF-8")
                    .body(content);
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Error reading file: " + filename);
        }
    }



    //-----------------Testing Endpoints----------

    @GetMapping("/api/trigger-runtime-exception")
    public APIResponse triggerException() {
        throw new RuntimeException("Boom");
    }

    @GetMapping("/api/trigger-response-status")
    public void triggerResponseStatus() {
        throw new ResponseStatusException(HttpStatus.I_AM_A_TEAPOT, "I'm a teapot");
    }
    @GetMapping("/api/trigger-illegal-argument")
    public void triggerIllegalArgument() {
        throw new IllegalArgumentException("Illegal argument");
    }



}