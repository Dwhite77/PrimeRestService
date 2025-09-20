package org.example.primeapi.algo.Algorithms;



import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for core Atkin sieve operations.
 * Provides reusable methods for base prime generation using modular filters and square elimination.
 *
 * <p>Used by BasePrimeService and AtkinAlgorithm to avoid circular dependencies and promote modularity.
 */
public class AtkinUtil {

    /**
     * Applies Atkin's modular arithmetic filters to identify prime candidates.
     *
     * @param isPrimeCandidate boolean array marking potential primes
     * @param sqrtLimit        √upperBound
     * @param upperBound       inclusive upper bound of sieve
     */
    public static void applyAtkinFilters(boolean[] isPrimeCandidate, int sqrtLimit, int upperBound) {
        for (int x = 1; x <= sqrtLimit; x++) {
            for (int y = 1; y <= sqrtLimit; y++) {
                int candidate1 = 4 * x * x + y * y;
                if (candidate1 <= upperBound && (candidate1 % 12 == 1 || candidate1 % 12 == 5)) {
                    isPrimeCandidate[candidate1] ^= true;
                }

                int candidate2 = 3 * x * x + y * y;
                if (candidate2 <= upperBound && candidate2 % 12 == 7) {
                    isPrimeCandidate[candidate2] ^= true;
                }

                if (x > y) {
                    int candidate3 = 3 * x * x - y * y;
                    if (candidate3 <= upperBound && candidate3 % 12 == 11) {
                        isPrimeCandidate[candidate3] ^= true;
                    }
                }
            }
        }
    }

    /**
     * Removes false positives by marking multiples of prime squares as non-prime.
     *
     * @param isPrimeCandidate boolean array marking potential primes
     * @param sqrtLimit        √upperBound
     * @param upperBound       inclusive upper bound of sieve
     */
    public static void eliminateMultiplesOfPrimeSquares(boolean[] isPrimeCandidate, int sqrtLimit, int upperBound) {
        for (int prime = 5; prime <= sqrtLimit; prime++) {
            if (isPrimeCandidate[prime]) {
                int primeSquared = prime * prime;
                for (int multiple = primeSquared; multiple <= upperBound; multiple += primeSquared) {
                    isPrimeCandidate[multiple] = false;
                }
            }
        }
    }

    /**
     * Collects all confirmed primes in the range [lowerBound, upperBound].
     *
     * @param isPrimeCandidate boolean array marking confirmed primes
     * @param lowerBound       inclusive lower bound
     * @param upperBound       inclusive upper bound
     * @return list of confirmed primes
     */
    public static List<Integer> collectConfirmedPrimes(boolean[] isPrimeCandidate, int lowerBound, int upperBound) {
        List<Integer> confirmedPrimes = new ArrayList<>();

        if (lowerBound <= 2 && upperBound >= 2) confirmedPrimes.add(2);
        if (lowerBound <= 3 && upperBound >= 3) confirmedPrimes.add(3);

        for (int candidate = Math.max(5, lowerBound); candidate <= upperBound; candidate++) {
            if (isPrimeCandidate[candidate]) confirmedPrimes.add(candidate);
        }

        return confirmedPrimes;
    }
}
