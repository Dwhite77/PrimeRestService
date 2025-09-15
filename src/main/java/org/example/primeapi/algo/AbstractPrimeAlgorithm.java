package org.example.primeapi.algo;

import org.example.primeapi.util.ThreadPoolManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Abstract base class for prime generation algorithms with support for parallel execution.
 *
 * <p>This class provides shared utilities for implementing scalable prime algorithms,
 * including input validation and multithreaded chunk processing. Subclasses define
 * specific generation strategies (e.g., sieving, probabilistic testing) while inheriting
 * thread-safe execution patterns.
 *
 * <p>Features:
 * <ul>
 *   <li>Validates input bounds and thread constraints via {@code shouldSkip}</li>
 *   <li>Distributes work across threads using {@code runThreaded}</li>
 *   <li>Aggregates results deterministically with deduplication and sorting</li>
 * </ul>
 *
 * <p>Parallelization:
 * <ul>
 *   <li>Input range is divided into evenly sized chunks</li>
 *   <li>Each chunk is processed concurrently via a {@code BiFunction}</li>
 *   <li>Thread pool is managed by {@code ThreadPoolManager}</li>
 * </ul>
 *
 * <p>Usage:
 * <pre>{@code
 * List<Integer> primes = runThreaded("Sieve", 10_000, 100_000, 8, (start, end) -> {
 *     return sieveSegment(start, end, basePrimes);
 * });
 * }</pre>
 *
 * <p>Limitations:
 * <ul>
 *   <li>Thread count capped at {@code MAX_THREADS}</li>
 *   <li>Upper limit capped at {@code MAX_LIMIT}</li>
 *   <li>Caller must ensure chunk processor is thread-safe</li>
 * </ul>
 *
 * <p>Designed for extension by deterministic and probabilistic prime algorithms.
 */
public abstract class AbstractPrimeAlgorithm implements PrimeAlgorithm {

    protected static final int MAX_LIMIT = 1_000_000_000;
    protected static final int MAX_THREADS = 128;

    protected boolean shouldSkip(String label, int upperLimit, int threads) {
        return upperLimit < 2 || threads > upperLimit || upperLimit > MAX_LIMIT || threads > MAX_THREADS;
    }

    protected List<Integer> runThreaded(String label, int lowerLimit, int upperLimit, int threads,
                                        BiFunction<Integer, Integer, List<Integer>> chunkProcessor) {
        ExecutorService executor = ThreadPoolManager.createFixedPool(threads, label);
        List<Future<List<Integer>>> futures = new ArrayList<>();

        int totalRange = upperLimit - lowerLimit + 1;
        int chunkSize = (int) Math.ceil((double) totalRange / threads);

        for (int i = 0; i < threads; i++) {
            int start = lowerLimit + i * chunkSize;
            int end = Math.min(lowerLimit + (i + 1) * chunkSize - 1, upperLimit);
            futures.add(executor.submit(() -> chunkProcessor.apply(start, end)));
        }

        List<Integer> allPrimes = futures.stream()
                .flatMap(future -> {
                    try {
                        return future.get().stream();
                    } catch (InterruptedException | ExecutionException e) {
                        Thread.currentThread().interrupt();
                        return Stream.empty();
                    }
                })
                .distinct()
                .sorted()
                .collect(Collectors.toList());

        executor.shutdown();
        return allPrimes;
    }
}