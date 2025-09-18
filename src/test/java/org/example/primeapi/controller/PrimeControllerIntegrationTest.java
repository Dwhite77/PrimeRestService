package org.example.primeapi.controller;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.example.primeapi.helper.TestHelperMethods;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
@SpringBootTest(
        classes = org.example.primeapi.PrimeApiApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PrimeControllerIntegrationTest {

    private TestHelperMethods THM = new TestHelperMethods();

    @LocalServerPort
    private int port;

    Map<String, Map<Integer, Long>> results = new LinkedHashMap<>();



    @BeforeAll
    void setup() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port; // or whatever port your app runs on
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();

    }

    @Test
    void testValidPrimeRequest() {
        Response response = sendPrimeRequest(30, "sieve", 2, "application/json");
        logResponse(response);
        assertSuccess(response, 30, "sieve", 2);
    }

    @Test
    void testInvalidAlgorithm() {
        Response response = sendPrimeRequest(30, "invalid", 1, "application/json");
        logResponse(response);
        response.then().statusCode(400).body("error.message", containsString("Unsupported algorithm"));
    }


    @Test
    void testXmlAcceptHeaderWithTrial() {
        Response response = sendPrimeRequest(20, "trial", 1, "application/xml");
        logResponse(response);
        response.then()
                .statusCode(200)
                .contentType("application/xml");
    }


    @Test
    void testTrialAlgorithmWithDefaultThreads() {
        Response response = sendPrimeRequest(25, "trial", null, null);
        assertSuccess(response, 25, "trial", 1);
    }

    @Test
    void testTrialAlgorithm() {
        Response response = sendPrimeRequest(25, "trial", 1, null);
        assertSuccess(response, 25, "trial", 1);
    }

    @Test
    void testSieveAlgorithmWithMultipleThreads() {
        Response response = sendPrimeRequest(100, "sieve", 4, null);
        assertSuccess(response, 100, "sieve", 4);
    }

    @Test
    void testAtkinAlgorithm() {
        Response response = sendPrimeRequest(50, "atkin", 2, null);
        assertSuccess(response, 50, "atkin", 2);
    }

    @Test
    void testMillerAlgorithm() {
        Response response = sendPrimeRequest(30, "miller", 1, null);
        assertSuccess(response, 30, "miller", 1);
    }


    @Test
    void testNegativeThreads() {
        Response response = sendPrimeRequest(50, "sieve", -3, null);
        assertError(response, 400, "threads must be >= 1");
    }

    @Test
    void testLargeLimitTrialAlgorithm() {
        Response response = sendPrimeRequest(10_000, "trial", 1, null);
        assertSuccess(response, 10_000, "trial", 1);
    }

    @Test
    void testXmlAcceptHeaderWithSieve() {
        Response response = sendPrimeRequest(50, "sieve", 2, "application/xml");
        logResponse(response);
        response.then()
                .statusCode(200)
                .contentType("application/xml");
    }



    @Test
    void testInvalidAlgorithmName() {
        Response response = sendPrimeRequest(30, "invalidAlgo", 2, null);
        assertError(response, 400, "Unsupported algorithm");
    }

    @Test
    void testNegativeLimit() {
        Response response = sendPrimeRequest(-10, "sieve", 2, null);
        assertError(response, 400, "Limit must be non-negative");
    }

    @Test
    void testZeroThreads() {
        Response response = sendPrimeRequest(50, "sieve", 0, null);
        assertError(response, 400, "threads must be >= 1");
    }

    @Test
    void testMissingLimitParameter() {
        Response response = sendPrimeRequest(null, "sieve", 2, null);
        logResponse(response);
        response.then().statusCode(400);
    }

    @Test
    void testLargeLimitWithSieve() {
        Response response = sendPrimeRequest(1_000_000, "sieve", 4, null);
        assertSuccess(response, 1_000_000, "sieve", 4);
    }

    @Test
    void testLargeLimitWithAtkin() {
        Response response = sendPrimeRequest(500_000, "atkin", 2, null);
        assertSuccess(response, 500_000, "atkin", 2);
    }

    @Test
    void testLargeLimitWithTrial() {
        Response response = sendPrimeRequest(100_000, "trial", 1, null);
        assertSuccess(response, 100_000, "trial", 1);
    }

    @Test
    void testLargeLimitWithMiller() {
        Response response = sendPrimeRequest(10_000, "miller", 1, null);
        assertSuccess(response, 10_000, "miller", 1);
    }

    @Test
    void testSieveWithHighThreadCount() {
        Response response = sendPrimeRequest(1_000_000, "sieve", 16, null);
        assertSuccess(response, 1_000_000, "sieve", 16);
    }

    @Test
    void testSieveWithLargeLimitAndLastPrimeCheck() {
        Response response = sendPrimeRequest(1_000_000, "sieve", 4, null);
        assertSuccess(response, 1_000_000, "sieve", 4);

        List<Integer> primes = response.jsonPath().getList("data.primes", Integer.class);
        assertEquals(999983, primes.get(primes.size() - 1), "Last prime should be 999983 for limit 1,000,000");
    }



    @Test
    void benchmarkSieveAcrossThreads() {
        THM.skipTest();
        int limit = 1_000_000;
        for (int threads : List.of(1, 2, 4, 8, 16)) {
            long duration = benchmark("sieve", limit, threads);
            log.info("Sieve with {} thread(s): {} ms", threads, duration);
        }
    }

    @Test
    void benchmarkAllAlgorithmsSingleThread() {
        THM.skipTest();
        int limit = 500_000;
        for (String algo : List.of("trial", "sieve", "atkin", "miller")) {
            long duration = benchmark(algo, limit, 1);
            log.info("{} (1 thread): {} ms", algo, duration);
        }
    }


    @Test
    void benchmarkMatrix() {
        THM.skipTest();
        int limit = 100_000_000;
        for (String algo : List.of("sieve", "atkin", "miller", "trial")) {
            Map<Integer, Long> threadResults = new LinkedHashMap<>();
            for (int threads : List.of(1, 2, 4, 8)) {
                long duration = benchmark(algo, limit, threads);
                threadResults.put(threads, duration);
            }
            results.put(algo, threadResults);
        }

        results.forEach((algo, threadMap) -> {
            log.info("Results for {}:", algo);
            threadMap.forEach((threads, duration) ->
                    log.info("  {} thread(s): {} ms", threads, duration));
        });
    }



    //-----------------------------------------------

    @Test
    void missingLimitParameterReturns400() {
        Response response = given()
                .accept("application/json")
                .queryParam("algorithm", "sieve")
                .queryParam("threads", 2)
                .get("/api/primes");

        response.then()
                .statusCode(400)
                .body("error.message", containsString("Missing required parameter"));
    }

    @Test
    void invalidLimitTypeReturns400() {
        Response response = given()
                .accept("application/json")
                .queryParam("limit", "abc")
                .queryParam("algorithm", "sieve")
                .queryParam("threads", 2)
                .get("/api/primes");

        response.then()
                .statusCode(400)
                .body("error.message", containsString("Invalid value for parameter"));
    }

    @Test
    void unsupportedAlgorithmReturns400() {
        Response response = sendPrimeRequest(100, "unknown", 2, "application/json");

        response.then()
                .statusCode(400)
                .body("error.message", containsString("Unsupported algorithm"));
    }

    @Test
    void unknownPathReturns404() {
        Response response = given()
                .accept("application/json")
                .get("/api/does-not-exist");

        response.then()
                .statusCode(404)
                .body("error.message", containsString("Unknown path"));
    }

    @Test
    void postToGetEndpointReturns405() {
        Response response = given()
                .accept("application/json")
                .post("/api/primes");

        response.then()
                .statusCode(405)
                .body("error.message", containsString("Method"));
    }



    @Test
    void swaggerUiIsAccessible() {
        Response response = given()
                .when()
                .get("/swagger-ui/index.html");

        response.then()
                .statusCode(200)
                .contentType(containsString("text/html"));
    }

    @Test
    void rootPathRedirectsToLandingPage() {
        Response response = given()
                .redirects().follow(false) // prevent auto-follow so we can inspect the redirect
                .when()
                .get("/");

        response.then()
                .statusCode(302) // Spring issues a 302 redirect
                .header("Location", containsString("/api/info"));
    }

    @Test
    void landingPageIsAccessibleAndContainsReadmeLink() {
        Response response = given()
                .accept("text/html")
                .when()
                .get("/api/info");

        response.then()
                .statusCode(200)
                .contentType(containsString("text/html"))
                .body(containsString("Welcome to the Prime API"))
                .body(containsString("View README"));
    }
    @Test
    void rootPathRedirectsAndServesLandingPage() {
        Response response = given()
                .accept("text/html")
                .redirects().follow(true)
                .when()
                .get("/");

        response.then()
                .statusCode(200)
                .contentType(containsString("text/html"))
                .body(containsString("Welcome to the Prime API"))
                .body(containsString("View README"));
    }




    @Test
    void concurrentRequestsAreHandledSafely() {
        int limit = 10_000;
        int threads = 4;

        Runnable task = () -> {
            Response response = sendPrimeRequest(limit, "sieve", threads, null);
            response.then().statusCode(200);
        };

        List<Thread> threadPool = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            threadPool.add(new Thread(task));
        }

        threadPool.forEach(Thread::start);
        threadPool.forEach(t -> {
            try { t.join(); } catch (InterruptedException ignored) {}
        });
    }

    @Test
    void shouldSkipTriggersCorrectlyForInvalidInputs() {
        List.of(
                Map.of("limit", 1, "threads", 1),
                Map.of("limit", 100, "threads", 129),
                Map.of("limit", 1_000_000_001, "threads", 4),
                Map.of("limit", 5, "threads", 10)
        ).forEach(params -> {
            Response response = sendPrimeRequest(
                    (Integer) params.get("limit"),
                    "sieve",
                    (Integer) params.get("threads"),
                    null
            );
            response.then().statusCode(200);
            List<Integer> primes = response.jsonPath().getList("data.primes", Integer.class);
            assertTrue(primes.isEmpty(), "Expected empty result due to skip logic");
        });
    }

    @Test
    void exportBenchmarkResultsToCsvFormat() {
        THM.skipTest();
        int limit = 100_000;
        StringBuilder csv = new StringBuilder("Algorithm,Threads,DurationMs\n");

        for (String algo : List.of("trial", "sieve", "atkin", "miller")) {
            for (int threads : List.of(1, 2, 4, 8)) {
                long duration = benchmark(algo, limit, threads);
                csv.append(String.format("%s,%d,%d\n", algo, threads, duration));
            }
        }

        log.info("\nBenchmark CSV:\n{}", csv);
    }

    @Test
    void printBenchmarkSummaryTable() {
        THM.skipTest();
        int limit = 100_000;
        Map<String, Map<Integer, Long>> matrix = new LinkedHashMap<>();

        for (String algo : List.of("trial", "sieve", "atkin", "miller")) {
            Map<Integer, Long> threadMap = new LinkedHashMap<>();
            for (int threads : List.of(1, 2, 4, 8)) {
                long duration = benchmark(algo, limit, threads);
                threadMap.put(threads, duration);
            }
            matrix.put(algo, threadMap);
        }

        StringBuilder table = new StringBuilder();
        table.append(String.format("%-10s | %-8s | %-12s\n", "Algorithm", "Threads", "Duration (ms)"));
        table.append("----------------------------------------\n");

        matrix.forEach((algo, threadMap) -> {
            threadMap.forEach((threads, duration) -> {
                table.append(String.format("%-10s | %-8d | %-12d\n", algo, threads, duration));
            });
        });

        log.info("\nBenchmark Summary Table:\n{}", table);
    }



    @Test
    void errorPayloadStructureIsValidForMissingLimit() {
        Response response = given()
                .accept("application/json")
                .queryParam("algorithm", "sieve")
                .queryParam("threads", 2)
                .get("/api/primes");

        response.then()
                .statusCode(400)
                .body("error.status", equalTo(400))
                .body("error.error", equalTo("Bad Request"))
                .body("error.message", containsString("Missing required parameter"))
                .body("error.path", equalTo("/api/primes"))
                .body("timestamp", matchesRegex("\\d{2}-\\d{2}-\\d{4} \\d{2}:\\d{2}:\\d{2}"))
                .body("successful", equalTo(false));

    }

    //-----------Helper Methods----------



    private Response sendPrimeRequest(Integer limit, String algorithm, Integer threads, String acceptHeader) {
        var request = given();
        if (limit != null) request.queryParam("limit", limit);
        if (algorithm != null) request.queryParam("algorithm", algorithm);
        if (threads != null) request.queryParam("threads", threads);
        if (acceptHeader != null) request.accept(acceptHeader);

        return request.when().get("/api/primes");
    }

    private void assertSuccess(Response response, int expectedLimit, String expectedAlgorithm, int expectedThreads) {
        logResponse(response);
        response.then()
                .statusCode(200)
                .body("data.limit", equalTo(expectedLimit))
                .body("data.algorithm", equalTo(expectedAlgorithm))
                .body("data.threads", equalTo(expectedThreads))
                .body("data.total", greaterThan(0))
                .body("data.primes.size()", greaterThan(0));


    }

    private void logDuration(Response response) {
        long duration = response.jsonPath().getLong("data.durationMs");
        log.info("Execution time: {} ms", duration);
    }

    private void assertError(Response response, int expectedStatus, String expectedMessageFragment) {
        logResponse(response);
        response.then()
                .statusCode(expectedStatus)
                .body("error.message", containsString(expectedMessageFragment));
    }

    private void logResponse(Response response){
        //log.info("Response Body: {}", response.getBody().asPrettyString());
        log.info("Headers: {}",response.getHeaders().toString());
        log.info("Status code: {}", response.getStatusCode());
    }

    private long benchmark(String algorithm, int limit, int threads) {
        Response response = sendPrimeRequest(limit, algorithm, threads, null);
        logResponse(response);

        response.then().statusCode(200);
        long duration = response.jsonPath().getLong("data.durationMs");
        log.info("Benchmark → Algorithm: {}, Threads: {}, Limit: {}, Duration: {} ms",
                algorithm, threads, limit, duration);
        return duration;
    }
}
