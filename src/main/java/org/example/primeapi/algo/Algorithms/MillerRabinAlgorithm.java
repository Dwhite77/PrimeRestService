package org.example.primeapi.algo.Algorithms;

import org.example.primeapi.algo.AbstractPrimeAlgorithm;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


/**
 * Implements the Miller–Rabin primality test for generating prime candidates.
 *
 * <p>The Miller–Rabin test is a probabilistic algorithm used to determine whether a number is likely prime.
 * It is based on properties of modular arithmetic and is particularly efficient for large integers.
 * While it does not guarantee primality, it can be tuned to achieve extremely high confidence.
 *
 * <p>This implementation uses a fixed set of deterministic bases ({2, 3, 5, 7, 11}) which are sufficient
 * to guarantee correctness for integers less than 2³². For larger ranges, the algorithm remains probabilistic.
 *
 * <p>Algorithm steps:
 * <ul>
 *   <li>Express n − 1 as 2ʳ·d where d is odd</li>
 *   <li>For each base a:
 *     <ul>
 *       <li>Compute aᵈ mod n</li>
 *       <li>If result is 1 or n − 1, continue</li>
 *       <li>Otherwise, square repeatedly up to r − 1 times and check for n − 1</li>
 *       <li>If none match, n is composite</li>
 *     </ul>
 *   </li>
 * </ul>
 *
 * <p>Parallelization:
 * <ul>
 *   <li>Input range is divided into chunks and processed concurrently</li>
 *   <li>Each thread applies the Miller–Rabin test independently</li>
 * </ul>
 *
 * <p>Advantages:
 * <ul>
 *   <li>Extremely fast for large numbers</li>
 *   <li>Works well for cryptographic applications and probabilistic filtering</li>
 *   <li>Thread-safe and scalable</li>
 * </ul>
 *
 * <p>Limitations:
 * <ul>
 *   <li>Not a true sieve—does not eliminate composites systematically</li>
 *   <li>Probabilistic nature means false positives are possible for very large n</li>
 * </ul>
 *
 * <p>Usage:
 * <pre>{@code
 * List<Integer> primes = millerRabinAlgorithm.generate(1_000_000, 8);
 * }</pre>
 *
 * @author Dan
 * @see org.example.primeapi.algo.PrimeAlgorithm
 */
@Component
public class MillerRabinAlgorithm extends AbstractPrimeAlgorithm {

    @Override
    public String name() {
        return "miller";
    }

    @Override
    public List<Integer> generate(int upperLimit, int threads) {
        return runThreaded("Miller-Rabin", 2, upperLimit, threads, this::chunk);
    }

    private List<Integer> chunk(int start, int end) {
        return IntStream.rangeClosed(start, end)
                .filter(this::isProbablyPrime)
                .boxed()
                .collect(Collectors.toList());
    }

    private boolean isProbablyPrime(int n) {
        if (n < 2) return false;
        if (n == 2 || n == 3) return true;
        if (n % 2 == 0) return false;

        int r = 0, d = n - 1;
        while (d % 2 == 0) {
            d /= 2;
            r++;
        }

        int[] bases = {2, 3, 5, 7, 11};
        for (int a : bases) {
            if (a >= n) continue;
            int x = modPow(a, d, n);
            if (x == 1 || x == n - 1) continue;

            boolean passed = false;
            for (int i = 0; i < r - 1; i++) {
                x = modPow(x, 2, n);
                if (x == n - 1) {
                    passed = true;
                    break;
                }
            }
            if (!passed) return false;
        }
        return true;
    }

    private int modPow(int base, int exp, int mod) {
        long result = 1, b = base;
        while (exp > 0) {
            if ((exp & 1) == 1) result = (result * b) % mod;
            b = (b * b) % mod;
            exp >>= 1;
        }
        return (int) result;
    }
}
