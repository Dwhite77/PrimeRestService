package org.example.primeapi.algo.Algorithms;

import org.example.primeapi.algo.AbstractPrimeAlgorithm;
import org.example.primeapi.algo.BasePrimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
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


    @Autowired
    private BasePrimeService basePrimeService;


    @Override
    public String name() {
        return "sieve";
    }

    @Override
    public List<Integer> generate(int upperLimit, int threads) {
        int sqrtLimit = (int) Math.sqrt(upperLimit);
        List<Integer> basePrimes = basePrimeService.generateSieveBasePrimes(sqrtLimit);

        if (upperLimit <= sqrtLimit) return basePrimes;

        List<Integer> segmentedPrimes = (threads <= 1)
                ? sieveSegment(sqrtLimit + 1, upperLimit, basePrimes)
                : runThreaded("Sieve", sqrtLimit + 1, upperLimit, threads,
                (segmentStart, segmentEnd) -> sieveSegment(segmentStart, segmentEnd, basePrimes));

        List<Integer> allPrimes = new ArrayList<>(basePrimes);
        allPrimes.addAll(segmentedPrimes);
        Collections.sort(allPrimes);
        return allPrimes;
    }



    public List<Integer> generateBasePrimesForTesting(int limit) {
        return basePrimeService.generateSieveBasePrimes(limit);
    }


    /**
     * Applies segmented sieve logic to eliminate composites in the range [segmentStart, segmentEnd].
     */
    private List<Integer> sieveSegment(int segmentStart, int segmentEnd, List<Integer> basePrimes) {
        int segmentSize = segmentEnd - segmentStart + 1;
        boolean[] isPrimeCandidate = new boolean[segmentSize];
        Arrays.fill(isPrimeCandidate, true);

        for (int basePrime : basePrimes) {
            int primeSquared = basePrime * basePrime;
            int firstMultipleInSegment = Math.max(primeSquared,
                    ((segmentStart + basePrime - 1) / basePrime) * basePrime);

            for (int multiple = firstMultipleInSegment; multiple <= segmentEnd; multiple += basePrime) {
                isPrimeCandidate[multiple - segmentStart] = false;
            }
        }

        List<Integer> confirmedPrimes = new ArrayList<>();
        for (int offset = 0; offset < segmentSize; offset++) {
            int candidate = segmentStart + offset;
            if (isPrimeCandidate[offset]) confirmedPrimes.add(candidate);
        }

        return confirmedPrimes;
    }
}