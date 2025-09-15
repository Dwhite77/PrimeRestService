package org.example.primeapi.algo.Algorithms;

import org.example.primeapi.algo.AbstractPrimeAlgorithm;
import org.springframework.stereotype.Component;

import java.util.*;


/**
 * Implements the segmented Sieve of Eratosthenes for prime generation.
 *
 * <p>This algorithm efficiently computes all prime numbers up to a given upper limit
 * using a two-phase approach: base prime generation followed by segmented sieving.
 * It is deterministic, cache-friendly, and well-suited for parallel execution.
 *
 * <p>Base primes are computed up to √n using the classic sieve. These primes are then
 * used to eliminate composites in higher segments, enabling scalable prime generation
 * for large ranges.
 *
 * <p>Algorithm steps:
 * <ul>
 *   <li>Compute all primes ≤ √n using the standard sieve</li>
 *   <li>Divide the range [√n + 1, n] into chunks</li>
 *   <li>For each chunk:
 *     <ul>
 *       <li>Mark all numbers as prime</li>
 *       <li>For each base prime p:
 *         <ul>
 *           <li>Find the first multiple of p in the chunk</li>
 *           <li>Mark all multiples of p as composite</li>
 *         </ul>
 *       </li>
 *     </ul>
 *   </li>
 * </ul>
 *
 * <p>Parallelization:
 * <ul>
 *   <li>Chunks are processed concurrently using a thread pool</li>
 *   <li>Each thread performs independent sieving using shared base primes</li>
 * </ul>
 *
 * <p>Advantages:
 * <ul>
 *   <li>Deterministic and accurate for all ranges</li>
 *   <li>Highly efficient for large upper limits</li>
 *   <li>Thread-safe and cache-friendly</li>
 *   <li>Modular design supports future optimizations</li>
 * </ul>
 *
 * <p>Limitations:
 * <ul>
 *   <li>Memory usage grows with segment size</li>
 *   <li>Not suitable for primality testing of individual large numbers</li>
 * </ul>
 *
 * <p>Usage:
 * <pre>{@code
 * List<Integer> primes = sieveAlgorithm.generate(1_000_000, 4);
 * }</pre>
 */
@Component
public class SieveAlgorithm extends AbstractPrimeAlgorithm {

    @Override
    public String name() {
        return "sieve";
    }

    @Override
    public List<Integer> generate(int upperLimit, int threads) {
        if (shouldSkip("Sieve", upperLimit, threads)) return List.of();

        int sqrtLimit = (int) Math.sqrt(upperLimit);
        List<Integer> basePrimes = generateBasePrimes(sqrtLimit);

        if (upperLimit <= sqrtLimit) return basePrimes;

        List<Integer> segmentedPrimes = (threads <= 1)
                ? segmentedChunk(sqrtLimit + 1, upperLimit, basePrimes) // if threads is smaller than 1 true
                : runThreaded("Sieve", sqrtLimit + 1, upperLimit, threads, (start, end) -> segmentedChunk(start, end, basePrimes)); // if false

        List<Integer> allPrimes = new ArrayList<>(basePrimes);
        allPrimes.addAll(segmentedPrimes);
        Collections.sort(allPrimes);
        return allPrimes;
    }

    private List<Integer> generateBasePrimes(int limit) {
        boolean[] isPrime = new boolean[limit + 1];
        Arrays.fill(isPrime, true);

        for (int p = 2; p * p <= limit; p++) {
            if (!isPrime[p]) continue;
            for (int i = p * p; i <= limit; i += p) {
                isPrime[i] = false;
            }
        }

        List<Integer> primes = new ArrayList<>();
        for (int i = 2; i <= limit; i++) {
            if (isPrime[i]) primes.add(i);
        }

        return primes;
    }

    private List<Integer> segmentedChunk(int start, int end, List<Integer> basePrimes) {
        if (start < 2) start = 2;
        boolean[] isPrime = new boolean[end - start + 1];
        Arrays.fill(isPrime, true);

        for (int prime : basePrimes) {
            int firstMultiple = Math.max(prime * prime, ((start + prime - 1) / prime) * prime);
            for (int i = firstMultiple; i <= end; i += prime) {
                isPrime[i - start] = false;
            }
        }

        List<Integer> primes = new ArrayList<>();
        for (int i = 0; i < isPrime.length; i++) {
            int candidate = start + i;
            if (isPrime[i]) primes.add(candidate);
        }

        return primes;
    }
}