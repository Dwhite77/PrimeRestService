package org.example.primeapi.algo;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Implements the Sieve of Atkin algorithm for prime number generation.
 *
 * <p>The Sieve of Atkin is an advanced, mathematically optimized algorithm for finding all prime numbers
 * up to a given limit. Unlike the Sieve of Eratosthenes, which marks off multiples of primes,
 * Atkin uses modular arithmetic and quadratic forms to identify prime candidates more efficiently.
 *
 * <p>This implementation uses a segmented approach:
 * <ul>
 *   <li>First, it computes base primes up to √limit using the core Atkin sieve.</li>
 *   <li>Then, it applies the segmented sieve across the remaining range using those base primes.</li>
 *   <li>Parallel execution is supported via chunked threading for scalability.</li>
 * </ul>
 *
 * <p>Key mathematical steps:
 * <ul>
 *   <li>Marks numbers n where:
 *     <ul>
 *       <li>n = 4x² + y² and n mod 12 = 1 or 5</li>
 *       <li>n = 3x² + y² and n mod 12 = 7</li>
 *       <li>n = 3x² − y² and n mod 12 = 11 (when x > y)</li>
 *     </ul>
 *   </li>
 *   <li>Eliminates squares of primes to remove false positives.</li>
 * </ul>
 *
 * <p>Advantages:
 * <ul>
 *   <li>More efficient than Eratosthenes for large ranges.</li>
 *   <li>Reduces redundant work by skipping even numbers and using modular filters.</li>
 *   <li>Thread-safe and scalable for multi-core execution.</li>
 * </ul>
 *
 * <p>Limitations:
 * <ul>
 *   <li>More complex to implement and debug than simpler sieves.</li>
 *   <li>Requires careful handling of modular conditions and segment boundaries.</li>
 * </ul>
 *
 * <p>Usage:
 * <pre>{@code
 * List<Integer> primes = atkinAlgorithm.generate(1_000_000, 4);
 * }</pre>
 *
 * @author Dan
 * @see org.example.primeapi.algo.PrimeAlgorithm
 */
@Component
public class AtkinAlgorithm extends AbstractPrimeAlgorithm {

    @Override
    public String name() {
        return "atkin";
    }

    @Override
    public List<Integer> generate(int upperLimit, int threads) {
        if (shouldSkip("Segmented Atkin", upperLimit, threads)) return List.of();

        int sqrtLimit = (int) Math.sqrt(upperLimit);
        List<Integer> basePrimes = findAtkinChunk(2, sqrtLimit);

        if (threads <= 1 || upperLimit <= sqrtLimit + 1) {
            List<Integer> segmented = findSegmentedChunk(sqrtLimit + 1, upperLimit, basePrimes);
            List<Integer> allPrimes = new ArrayList<>(basePrimes);
            allPrimes.addAll(segmented);
            Collections.sort(allPrimes);
            return allPrimes;
        }

        List<Integer> segmentedPrimes = runThreaded("Segmented Atkin", sqrtLimit + 1, upperLimit, threads,
                (start, end) -> findSegmentedChunk(start, end, basePrimes));

        List<Integer> allPrimes = new ArrayList<>(basePrimes);
        allPrimes.addAll(segmentedPrimes);
        Collections.sort(allPrimes);
        return allPrimes;
    }

    private List<Integer> findAtkinChunk(int start, int end) {
        boolean[] isPrime = new boolean[end + 1];
        int sqrt = (int) Math.sqrt(end);

        for (int x = 1; x <= sqrt; x++) {
            for (int y = 1; y <= sqrt; y++) {
                int n = 4 * x * x + y * y;
                if (n <= end && (n % 12 == 1 || n % 12 == 5)) isPrime[n] ^= true;

                n = 3 * x * x + y * y;
                if (n <= end && n % 12 == 7) isPrime[n] ^= true;

                if (x > y) {
                    n = 3 * x * x - y * y;
                    if (n <= end && n % 12 == 11) isPrime[n] ^= true;
                }
            }
        }

        for (int i = 5; i <= sqrt; i++) {
            if (isPrime[i]) {
                int square = i * i;
                for (int j = square; j <= end; j += square) {
                    isPrime[j] = false;
                }
            }
        }

        List<Integer> primes = new ArrayList<>();
        if (start <= 2 && end >= 2) primes.add(2);
        if (start <= 3 && end >= 3) primes.add(3);

        for (int i = Math.max(5, start); i <= end; i++) {
            if (isPrime[i]) primes.add(i);
        }

        return primes;
    }

    private List<Integer> findSegmentedChunk(int start, int end, List<Integer> basePrimes) {
        int segmentSize = end - start + 1;
        boolean[] isPrime = new boolean[segmentSize];
        int sqrtEnd = (int) Math.sqrt(end);

        for (int x = 1; x <= sqrtEnd; x++) {
            for (int y = 1; y <= sqrtEnd; y++) {
                int n = 4 * x * x + y * y;
                if (n >= start && n <= end && (n % 12 == 1 || n % 12 == 5)) isPrime[n - start] ^= true;

                n = 3 * x * x + y * y;
                if (n >= start && n <= end && n % 12 == 7) isPrime[n - start] ^= true;

                if (x > y) {
                    n = 3 * x * x - y * y;
                    if (n >= start && n <= end && n % 12 == 11) isPrime[n - start] ^= true;
                }
            }
        }

        for (int prime : basePrimes) {
            int square = prime * prime;
            int firstMultiple = ((start + square - 1) / square) * square;
            for (int i = firstMultiple; i <= end; i += square) {
                isPrime[i - start] = false;
            }
        }

        List<Integer> primes = new ArrayList<>();
        for (int i = 0; i < segmentSize; i++) {
            int candidate = start + i;
            if (candidate >= 2 && isPrime[i]) primes.add(candidate);
        }

        return primes;
    }
}
