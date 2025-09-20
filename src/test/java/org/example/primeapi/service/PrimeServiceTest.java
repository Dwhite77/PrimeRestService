package org.example.primeapi.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for verifying correctness of prime number algorithms in PrimeService.
 * Covers both single-threaded and multi-threaded implementations.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Slf4j
@SpringBootTest
public class PrimeServiceTest {

    @Autowired
    private PrimeService primeService;

    record TestCase(int limit, int threads, AssertionType type, List<Integer> expected) {}
    record SkipCase(String label, int limit, int threads, boolean shouldSkipExpected) {}

    enum AssertionType {
        FULL_LIST,
        LAST_PRIME,
        PRIME_COUNT,
        EMPTY
    }


    static List<String> algorithms() {
        return List.of("Trial", "Sieve", "Atkin", "Miller");
    }

    @ParameterizedTest
    @MethodSource("algorithmTestCases")
    void testAlgorithmsReturnExpectedPrimes(String algorithm, int limit, int threads, AssertionType type, List<Integer> expected) {
        List<Integer> primes = primeService.findPrimes(algorithm, limit, threads, false);

        switch (type) {
            case EMPTY -> assertTrue(primes.isEmpty(), algorithm + " should return empty list for limit " + limit);
            case FULL_LIST -> assertEquals(expected, primes, algorithm + " failed for limit " + limit);
            case LAST_PRIME -> assertEquals(expected.get(expected.size() - 1), primes.get(primes.size() - 1),
                    algorithm + " last prime mismatch");
            case PRIME_COUNT -> assertEquals(expected.get(0), primes.size(), algorithm + " prime count mismatch");
        }
    }

    @ParameterizedTest
    @MethodSource("dynamicSkipTestCases")
    void testShouldSkipConditions(String label, int limit, int threads, boolean expected) {
        boolean result = primeService.shouldSkip(label, limit, threads);
        assertEquals(expected, result, String.format("Expected shouldSkip=%s for label='%s', limit=%d, threads=%d", expected, label, limit, threads));
    }

    Stream<Arguments> dynamicSkipTestCases() {
        return skipTestCases(primeService);
    }



    static Stream<Arguments> algorithmTestCases() {
        return algorithms().stream()
                .flatMap(algo -> baseTestCases().stream()
                        .map(tc -> Arguments.of(algo, tc.limit(), tc.threads(), tc.type(), tc.expected()))
                );
    }

    static Stream<Arguments> skipTestCases(PrimeService primeService) {
        int maxLimit = primeService.getMaxLimit();
        int maxThreads = primeService.getMaxThreads();

        return Stream.of(
                Arguments.of("TooLowLimit", 1, 1, true),
                Arguments.of("NegativeLimit", -10, 1, true),
                Arguments.of("ThreadsExceedLimit", 5, 10, true),
                Arguments.of("LimitExceedsMax", maxLimit + 1, 4, true),
                Arguments.of("ThreadsExceedMax", 100, maxThreads + 1, true),
                Arguments.of("ValidCase", 1000, 4, false),
                Arguments.of("EdgeCaseLimitEqualsMax", maxLimit, 1, false),
                Arguments.of("EdgeCaseThreadsEqualsMax", 1000, maxThreads, false),
                Arguments.of("ThreadsEqualLimit", 10, 10, false)
        );
    }


    static List<TestCase> baseTestCases() {
        return List.of(
                new TestCase(10, 1, AssertionType.FULL_LIST, List.of(2, 3, 5, 7)),
                new TestCase(10, 4, AssertionType.FULL_LIST, List.of(2, 3, 5, 7)),
                new TestCase(25, 4, AssertionType.FULL_LIST, List.of(2, 3, 5, 7, 11, 13, 17, 19, 23)), // full range for chunk
                new TestCase(0, 1, AssertionType.EMPTY, List.of()),
                new TestCase(1, 1, AssertionType.EMPTY, List.of()),
                new TestCase(-10, 1, AssertionType.EMPTY, List.of()),
                new TestCase(5000, 1, AssertionType.LAST_PRIME, List.of(4999)),
                new TestCase(10_000, 1, AssertionType.PRIME_COUNT, List.of(1229)),
                new TestCase(10_000, 4, AssertionType.PRIME_COUNT, List.of(1229)),
                new TestCase(5, 10, AssertionType.EMPTY, List.of()),
                new TestCase(10_000, 64, AssertionType.LAST_PRIME, List.of(1229, 9973)),
                new TestCase(100, 129, AssertionType.EMPTY, List.of()),
                new TestCase(1_000_000_001, 4, AssertionType.EMPTY, List.of())
        );
    }

}
