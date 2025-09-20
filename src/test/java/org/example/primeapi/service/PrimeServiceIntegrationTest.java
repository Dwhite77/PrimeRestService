package org.example.primeapi.service;

import lombok.extern.slf4j.Slf4j;


import org.example.primeapi.helper.TestHelperMethods;
import org.example.primeapi.model.PrimePayload;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;


import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(
        classes = org.example.primeapi.PrimeApiApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)@Slf4j
public class PrimeServiceIntegrationTest {

    private TestHelperMethods THM = new TestHelperMethods();

    @Autowired
    private PrimeService service;

    private final List<PrimePayload> benchmarkPayloads = new ArrayList<>();

    private final List<String> algorithmNames = List.of("trial","sieve", "miller", "atkin");

    private final List<Integer> largePrimes = List.of(
            104729,      // 10,000th prime
            1299709,     // 100,000th prime
            15485863,    // 1,000,000th prime
            32452843,    // 2,000,000th prime
            982451653    // largest known 9-digit prime
    );

/*
    @BeforeEach
    void setUp() {
        service = new PrimeService(List.of(
                new SieveAlgorithm(),
                new MillerRabinAlgorithm(),
                new TrialAlgorithm(),
                new AtkinAlgorithm()
        ));
    }*/


    @Test
    void benchmarkTest() {
        THM.skipTest();
        List<Integer> threads = List.of(
                1, 2, 4, 8
        );

        threads.forEach(thread -> {
            largePrimes.forEach(limit -> {
                algorithmNames.forEach(algoName -> {
                    log.info("-------------Test Start---------------");
                    log.info("Testing '{}' with upper limit = {} and threads = {}", algoName, limit, thread);

                    List<Integer> result = service.findPrimes(algoName, limit, thread, false);

                    log.info("Completed '{}' in {} ms", algoName, service.getDurationMs());
                    log.info("Last 5 primes from '{}': {}", algoName,
                            result.subList(Math.max(0, result.size() - 5), result.size()));

                    assertTrue(result.contains(limit),
                            "Algorithm '" + algoName + "' failed to include upper bound prime: " + limit);

                    benchmarkPayloads.add(new PrimePayload(algoName, limit, thread, null, result.size(), service.getDurationMs()));
                });
            });
        });

        log.info(formatBenchmarkTable(benchmarkPayloads));
    }

    public static String formatBenchmarkTable(List<PrimePayload> payloads) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n================ Benchmark Summary ================\n");
        sb.append(String.format("%-10s | %-10s | %-8s | %-12s\n", "Algorithm", "Limit", "Threads", "Duration (ms)"));
        sb.append("---------------------------------------------------\n");

        for (PrimePayload payload : payloads) {
            sb.append(String.format(
                    "%-10s | %-10d | %-8d | %-12d\n",
                    payload.getAlgorithm(),
                    payload.getLimit(),
                    payload.getThreads(),
                    payload.getDurationMs()
            ));
        }

        sb.append("===================================================\n");
        return sb.toString();
    }

    @Test
    void allAlgorithmsGenerateExpectedPrimesUpTo30() {
        List<Integer> expectedPrimes = List.of(2, 3, 5, 7, 11, 13, 17, 19, 23, 29);
        algorithmNames.forEach(algoName -> {
            log.info("Testing algorithm '{}'", algoName);
            List<Integer> result = service.findPrimes(algoName, 30, 1, false);
            log.info("Output from '{}': {}", algoName, result);
            assertEquals(expectedPrimes, result, "Mismatch in output for algorithm: " + algoName);

        });
    }


    @Test
    void allAlgorithmsGenerateExpectedPrimesUpTo1000UsingMultiThreading() {
        algorithmNames.forEach(algoName -> {
            log.info("Testing algorithm '{}'", algoName);
            List<Integer> primes = service.findPrimes(algoName, 1000, 4, false);
            log.info("Output from '{}': {} primes, sample: {}", algoName, primes.size(), primes.subList(0, Math.min(10, primes.size()))+"...");
            log.info("Last Prime gotten: {}", primes.get(primes.size()-1));
            assertEquals(primes.get(primes.size()-1),997, "Mismatch in final prime");
        });
    }


    @Test
    void allAlgorithmsReturnEmptyForLimit0() {
        algorithmNames.forEach(algoName -> {
            log.info("Testing algorithm '{}' with limit=0", algoName);
            List<Integer> result = service.findPrimes(algoName, 0, 1, false);
            log.info("Result: {}", result);
            assertTrue(result.isEmpty(), "Expected empty result for '" + algoName + "' with limit=0");
        });
    }

    @Test
    void allAlgorithmsReturn1ForLimit1() {
        algorithmNames.forEach(algoName -> {
            log.info("Testing algorithm '{}' with limit=1", algoName);
            List<Integer> result = service.findPrimes(algoName, 1, 1, false);
            log.info("Result: {}", result);
            assertTrue(result.equals(List.of()), "Expected result for '" + algoName + "' with limit=1 is 1");
        });
    }

    @Test
    void allAlgorithmsReturn2ForLimit2() {
        algorithmNames.forEach(algoName -> {
            log.info("Testing algorithm '{}' with limit=2", algoName);
            List<Integer> result = service.findPrimes(algoName, 2, 1, false);
            log.info("Result: {}", result);
            assertTrue(result.equals(List.of(2)), "Expected 2 result for '" + algoName + "' with limit=2");
        });
    }

    @Test
    void allAlgorithmsIncludeUpperBoundIfPrime() {
        List<Integer> primeLimits = List.of(29, 97, 997); // known primes

        primeLimits.forEach(limit -> {
            algorithmNames.forEach(algoName -> {
                log.info("Testing upper bound inclusion for '{}', limit={}", algoName, limit);
                List<Integer> result = service.findPrimes(algoName, limit, 2, false);
                log.debug("Output from '{}': {}", algoName, result);
                assertTrue(result.contains(limit), "Algorithm '" + algoName + "' failed to include prime upper bound: " + limit);
            });
        });
    }




    @Test
    void allAlgorithmsExcludeUpperBoundIfNotPrime() {
        List<Integer> nonPrimeLimits = List.of(30, 100, 1000); // known non-primes

        nonPrimeLimits.forEach(limit -> {
            algorithmNames.forEach(algoName -> {
                log.info("Testing upper bound exclusion for '{}', limit={}", algoName, limit);
                List<Integer> result = service.findPrimes(algoName, limit, 2, false);
                log.debug("Output from '{}': {}", algoName, result);
                assertFalse(result.contains(limit), "Algorithm '" + algoName + "' incorrectly included non-prime upper bound: " + limit);
            });
        });
    }



    @Test
    void allAlgorithmsIncludeLargePrimeUpperBoundAndLogExecutionTime1Threads() {
        THM.skipTest();

        int threads = 1;

        largePrimes.forEach(limit -> {
            algorithmNames.forEach(algoName -> {
                log.info("-------------Test Start---------------");
                log.info("Testing '{}' with upper limit = {} and threads = {}", algoName, limit, threads);


                List<Integer> result = service.findPrimes(algoName, limit, threads, false);


                log.info("Completed '{}' in {} ms", algoName, service.getDurationMs());
                log.info("Last 5 primes from '{}': {}", algoName,
                        result.subList(Math.max(0, result.size() - 5), result.size()));

                assertTrue(result.contains(limit),
                        "Algorithm '" + algoName + "' failed to include upper bound prime: " + limit);
            });
        });

    }

    @Test
    void allAlgorithmsIncludeLargePrimeUpperBoundAndLogExecutionTime2Threads() {
        THM.skipTest();

        int threads = 2;

        largePrimes.forEach(limit -> {
            algorithmNames.forEach(algoName -> {
                log.info("-------------Test Start---------------");
                log.info("Testing '{}' with upper limit = {} and threads = {}", algoName, limit, threads);

                List<Integer> result = service.findPrimes(algoName, limit, threads, false);

                log.info("Completed '{}' in {} ms", algoName, service.getDurationMs());
                log.info("Last 5 primes from '{}': {}", algoName,
                        result.subList(Math.max(0, result.size() - 5), result.size()));

                assertTrue(result.contains(limit),
                        "Algorithm '" + algoName + "' failed to include upper bound prime: " + limit);
            });
        });

    }



    @Test
    void allAlgorithmsIncludeLargePrimeUpperBoundAndLogExecutionTime4Threads() {
        THM.skipTest();

        int threads = 4;

        largePrimes.forEach(limit -> {
            algorithmNames.forEach(algoName -> {
                log.info("-------------Test Start---------------");
                log.info("Testing '{}' with upper limit = {} and threads = {}", algoName, limit, threads);

                List<Integer> result = service.findPrimes(algoName, limit, threads, false);

                log.info("Completed '{}' in {} ms", algoName, service.getDurationMs());
                log.info("Last 5 primes from '{}': {}", algoName,
                        result.subList(Math.max(0, result.size() - 5), result.size()));

                assertTrue(result.contains(limit),
                        "Algorithm '" + algoName + "' failed to include upper bound prime: " + limit);
            });
        });

    }




}
