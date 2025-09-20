package org.example.primeapi.algo;

import org.example.primeapi.algo.Algorithms.AtkinAlgorithm;
import org.example.primeapi.algo.Algorithms.SieveAlgorithm;
import org.example.primeapi.service.PrimeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
class PrimePerformanceTest {

    @Autowired
    private PrimeService primeService;

    @Autowired
    private AtkinAlgorithm atkinAlgorithm;

    @Autowired
    private SieveAlgorithm sieveAlgorithm;

    @Autowired
    private CacheManager cacheManager;

    private final int limit = 1_000_000;

    @BeforeEach
    void clearCaches() {
        primeService.clearBasePrimeCache();
        primeService.clearPrimeCache();
    }

    @Test
    void benchmarkFullPrimeCaching_acrossAlgorithmsAndThreads() {
        for (String algorithm : List.of("sieve", "atkin")) {
            for (int threads : List.of(1, 4)) {
                String label = algorithm + " [" + threads + " threads]";

                long uncachedStart = System.nanoTime();
                List<Integer> uncached = primeService.findPrimes(algorithm, limit, threads, true);
                long uncachedDuration = System.nanoTime() - uncachedStart;

                long cachedStart = System.nanoTime();
                List<Integer> cached = primeService.findPrimes(algorithm, limit, threads, true);
                long cachedDuration = System.nanoTime() - cachedStart;

                long uncachedMs = TimeUnit.NANOSECONDS.toMillis(uncachedDuration);
                long cachedMs = TimeUnit.NANOSECONDS.toMillis(cachedDuration);

                System.out.printf("Full primes — %s: Uncached = %dms, Cached = %dms%n", label, uncachedMs, cachedMs);

                assertEquals(uncached, cached, "Cached result should match original for " + label);
                assertTrue(cachedMs <= uncachedMs / 2 + 1, "Cached call should be faster for " + label);
            }
        }
    }

    @Test
    void benchmarkBasePrimeCaching_forSieveAndAtkin() {
        for (String algorithm : List.of("sieve", "atkin")) {
            String label = algorithm + " base primes";

            long uncachedStart, uncachedDuration, cachedStart, cachedDuration;
            List<Integer> uncached, cached;

            if (algorithm.equals("sieve")) {
                uncachedStart = System.nanoTime();
                uncached = sieveAlgorithm.generateBasePrimesForTesting(limit);
                uncachedDuration = System.nanoTime() - uncachedStart;

                cachedStart = System.nanoTime();
                cached = sieveAlgorithm.generateBasePrimesForTesting(limit);
                cachedDuration = System.nanoTime() - cachedStart;
            } else {
                uncachedStart = System.nanoTime();
                uncached = atkinAlgorithm.generateBasePrimesForTesting(2, limit);
                uncachedDuration = System.nanoTime() - uncachedStart;

                cachedStart = System.nanoTime();
                cached = atkinAlgorithm.generateBasePrimesForTesting(2, limit);
                cachedDuration = System.nanoTime() - cachedStart;
            }

            long uncachedMs = TimeUnit.NANOSECONDS.toMillis(uncachedDuration);
            long cachedMs = TimeUnit.NANOSECONDS.toMillis(cachedDuration);

            System.out.printf("Base primes — %s: Uncached = %dms, Cached = %dms%n", label, uncachedMs, cachedMs);

            assertEquals(uncached, cached, "Cached base primes should match original for " + label);
            assertTrue(cachedMs <= uncachedMs / 2 + 1, "Cached base primes should be faster for " + label);
        }
    }

    @Test
    void benchmarkAlgorithmOnlyExecution() {
        for (PrimeAlgorithm algo : List.of(sieveAlgorithm, atkinAlgorithm)) {
            String label = algo.name();

            long startSingle = System.nanoTime();
            List<Integer> singleThreaded = algo.generate(limit, 1);
            long durationSingle = System.nanoTime() - startSingle;

            long startMulti = System.nanoTime();
            List<Integer> multiThreaded = algo.generate(limit, 4);
            long durationMulti = System.nanoTime() - startMulti;

            long singleMs = TimeUnit.NANOSECONDS.toMillis(durationSingle);
            long multiMs = TimeUnit.NANOSECONDS.toMillis(durationMulti);

            System.out.printf("Algorithm-only — %s: Single = %dms, Multi = %dms%n", label, singleMs, multiMs);

            assertEquals(singleThreaded, multiThreaded, "Multi-threaded result should match single-threaded for " + label);
        }
    }






    @Test
    void trialAlgorithm_shouldBenefitFromPrimesCaching_singleThreaded() {
        testPrimeCachingPerformance("trial", limit, 1);
    }

    @Test
    void trialAlgorithm_shouldBenefitFromPrimesCaching_multiThreaded() {
        testPrimeCachingPerformance("trial", limit, 4);
    }

    @Test
    void millerAlgorithm_shouldBenefitFromPrimesCaching_singleThreaded() {
        testPrimeCachingPerformance("miller", limit, 1);
    }

    @Test
    void millerAlgorithm_shouldBenefitFromPrimesCaching_multiThreaded() {
        testPrimeCachingPerformance("miller", limit, 4);
    }

    private void testPrimeCachingPerformance(String algorithm, int limit, int threads) {
        primeService.clearPrimeCache();

        long uncachedStart = System.nanoTime();
        List<Integer> uncached = primeService.findPrimes(algorithm, limit, threads, true);
        long uncachedDuration = System.nanoTime() - uncachedStart;

        long cachedStart = System.nanoTime();
        List<Integer> cached = primeService.findPrimes(algorithm, limit, threads, true);
        long cachedDuration = System.nanoTime() - cachedStart;

        long uncachedMs = TimeUnit.NANOSECONDS.toMillis(uncachedDuration);
        long cachedMs = TimeUnit.NANOSECONDS.toMillis(cachedDuration);

        System.out.printf("Primes caching — %s [%d threads]: Uncached = %dms, Cached = %dms%n",
                algorithm, threads, uncachedMs, cachedMs);

        assertEquals(uncached, cached, "Cached result should match original for " + algorithm);
        assertTrue(cachedMs <= uncachedMs / 2 + 1, "Cached call should be faster for " + algorithm);
    }






}