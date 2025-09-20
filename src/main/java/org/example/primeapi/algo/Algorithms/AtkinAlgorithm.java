package org.example.primeapi.algo.Algorithms;

import org.example.primeapi.algo.AbstractPrimeAlgorithm;
import org.example.primeapi.algo.BasePrimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
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
 *       <li>n = 4x² + y² and n mod 12 = 1 or 5 (mod means n/12 remainder = 1 or 5)</li>
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

    @Autowired
    private BasePrimeService basePrimeService;

    @Override
    public String name() {
        return "atkin";
    }

    @Override
    public List<Integer> generate(int upperLimit, int threads) {

        int sqrtLimit = (int) Math.sqrt(upperLimit);
        List<Integer> basePrimes = basePrimeService.generateAtkinBasePrimes(2, sqrtLimit);

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


    public List<Integer> generateBasePrimesForTesting(int start, int end) {
        return basePrimeService.generateAtkinBasePrimes(start, end);
    }


    private List<Integer> findSegmentedChunk(int lowerBound, int upperBound, List<Integer> basePrimes) {
        int segmentSize = upperBound - lowerBound + 1;
        boolean[] isPrimeCandidate = new boolean[segmentSize];
        int sqrtUpperBound = (int) Math.sqrt(upperBound);

        applyAtkinFiltersToSegment(isPrimeCandidate, lowerBound, upperBound, sqrtUpperBound);
        eliminateMultiplesOfPrimeSquaresInSegment(isPrimeCandidate, lowerBound, upperBound, basePrimes);

        return collectConfirmedPrimesFromSegment(isPrimeCandidate, lowerBound, upperBound);
    }



    /**
     * Applies Atkin's modular filters to identify prime candidates within the segment.
     */
    private void applyAtkinFiltersToSegment(boolean[] isPrimeCandidate, int lowerBound, int upperBound, int sqrtLimit) {
        for (int x = 1; x <= sqrtLimit; x++) {
            for (int y = 1; y <= sqrtLimit; y++) {

                int candidate1 = 4 * x * x + y * y;
                if (candidate1 >= lowerBound && candidate1 <= upperBound && (candidate1 % 12 == 1 || candidate1 % 12 == 5)) {
                    isPrimeCandidate[candidate1 - lowerBound] ^= true;
                }

                int candidate2 = 3 * x * x + y * y;
                if (candidate2 >= lowerBound && candidate2 <= upperBound && candidate2 % 12 == 7) {
                    isPrimeCandidate[candidate2 - lowerBound] ^= true;
                }

                if (x > y) {
                    int candidate3 = 3 * x * x - y * y;
                    if (candidate3 >= lowerBound && candidate3 <= upperBound && candidate3 % 12 == 11) {
                        isPrimeCandidate[candidate3 - lowerBound] ^= true;
                    }
                }
            }
        }
    }

    /**
     * Removes false positives by marking multiples of prime squares as non-prime within the segment.
     */
    private void eliminateMultiplesOfPrimeSquaresInSegment(boolean[] isPrimeCandidate, int lowerBound, int upperBound, List<Integer> basePrimes) {
        for (int basePrime : basePrimes) {
            int primeSquared = basePrime * basePrime;
            int firstMultipleInSegment = ((lowerBound + primeSquared - 1) / primeSquared) * primeSquared;

            for (int multiple = firstMultipleInSegment; multiple <= upperBound; multiple += primeSquared) {
                isPrimeCandidate[multiple - lowerBound] = false;
            }
        }
    }

    /**
     * Collects confirmed primes from the segment after filtering.
     */
    private List<Integer> collectConfirmedPrimesFromSegment(boolean[] isPrimeCandidate, int lowerBound, int upperBound) {
        List<Integer> confirmedPrimes = new ArrayList<>();

        for (int offset = 0; offset < isPrimeCandidate.length; offset++) {
            int candidate = lowerBound + offset;
            if (candidate >= 2 && isPrimeCandidate[offset]) {
                confirmedPrimes.add(candidate);
            }
        }

        return confirmedPrimes;
    }
}
