package org.example.primeapi.algo;

import org.example.primeapi.algo.Algorithms.AtkinAlgorithm;
import org.example.primeapi.algo.Algorithms.SieveAlgorithm;
import org.example.primeapi.config.CacheConfig;
import org.example.primeapi.service.PrimeService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;


import java.util.List;
import java.util.concurrent.TimeUnit;
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Import(CacheConfig.class)
@ActiveProfiles("test")
class CachingIntegrationTest {

    @Autowired
    private SieveAlgorithm sieveAlgorithm;

    @Autowired
    private AtkinAlgorithm atkinAlgorithm;

    @Autowired
    private PrimeService primeService;

    @Autowired
    private CacheManager cacheManager;

    // ✅ Sieve base prime caching
    @Test
    void sieveBasePrimes_shouldBeCachedAfterFirstCall() {
        int limit = 10_000;
        primeService.clearBasePrimeCache();

        long firstStart = System.nanoTime();
        List<Integer> firstCall = sieveAlgorithm.generateBasePrimesForTesting(limit);
        long firstDuration = System.nanoTime() - firstStart;

        long secondStart = System.nanoTime();
        List<Integer> secondCall = sieveAlgorithm.generateBasePrimesForTesting(limit);
        long secondDuration = System.nanoTime() - secondStart;

        long firstMs = TimeUnit.NANOSECONDS.toMillis(firstDuration);
        long secondMs = TimeUnit.NANOSECONDS.toMillis(secondDuration);
        System.out.printf("Sieve base primes timing — First: %dms, Second: %dms%n", firstMs, secondMs);

        Assertions.assertEquals(firstCall, secondCall, "Cached result should match original");
        Assertions.assertTrue(secondMs <= firstMs / 2 + 1, "Second call should be faster due to caching");

        Cache baseCache = cacheManager.getCache("basePrimes");
        Assertions.assertNotNull(baseCache.get(limit), "Sieve base prime cache should contain entry for limit " + limit);
    }

    @Test
    void atkinBasePrimes_shouldBeCachedAfterFirstCall() {
        int start = 2;
        int end = 10_000;

        primeService.clearBasePrimeCache();

        long firstStart = System.nanoTime();
        List<Integer> firstCall = atkinAlgorithm.generateBasePrimesForTesting(start, end);
        long firstDuration = System.nanoTime() - firstStart;

        long secondStart = System.nanoTime();
        List<Integer> secondCall = atkinAlgorithm.generateBasePrimesForTesting(start, end);
        long secondDuration = System.nanoTime() - secondStart;

        long firstMs = TimeUnit.NANOSECONDS.toMillis(firstDuration);
        long secondMs = TimeUnit.NANOSECONDS.toMillis(secondDuration);
        System.out.printf("Atkin base primes timing — First: %dms, Second: %dms%n", firstMs, secondMs);

        Assertions.assertEquals(firstCall, secondCall, "Cached result should match original");
        Assertions.assertTrue(secondMs <= firstMs / 2 + 1, "Second call should be faster due to caching");

        Cache baseCache = cacheManager.getCache("basePrimes");
        Assertions.assertNotNull(baseCache.get(end), "Atkin base prime cache should contain entry for end " + end);
    }

    @Test
    void sievePrimeResults_shouldBeCachedIfEnabled() {
        String algorithm = "sieve";
        int limit = 5000;
        int threads = 1;
        primeService.clearPrimeCache();

        List<Integer> firstCall = primeService.findPrimes(algorithm, limit, threads, true);
        List<Integer> secondCall = primeService.findPrimes(algorithm, limit, threads, true);

        Assertions.assertEquals(firstCall, secondCall, "Cached prime result should match original");

        String cacheKey = algorithm + "-" + limit + "-" + threads;
        Cache primeCache = cacheManager.getCache("primes");
        Assertions.assertNotNull(primeCache.get(cacheKey), "Sieve prime result cache should contain entry for key: " + cacheKey);
    }

    @Test
    void atkinPrimeResults_shouldBeCachedIfEnabled() {
        String algorithm = "atkin";
        int limit = 5000;
        int threads = 1;
        primeService.clearPrimeCache();

        List<Integer> firstCall = primeService.findPrimes(algorithm, limit, threads, true);
        List<Integer> secondCall = primeService.findPrimes(algorithm, limit, threads, true);

        Assertions.assertEquals(firstCall, secondCall, "Cached prime result should match original");

        String cacheKey = algorithm + "-" + limit + "-" + threads;
        Cache primeCache = cacheManager.getCache("primes");
        Assertions.assertNotNull(primeCache.get(cacheKey), "Atkin prime result cache should contain entry for key: " + cacheKey);
    }

    @Test
    void basePrimeCache_shouldBeClearedViaService() {
        int limit = 1000;
        sieveAlgorithm.generateBasePrimesForTesting(limit);
        Assertions.assertNotNull(cacheManager.getCache("basePrimes").get(limit), "Base prime cache should be populated");

        primeService.clearBasePrimeCache();
        Assertions.assertNull(cacheManager.getCache("basePrimes").get(limit), "Base prime cache should be cleared");
    }

    @Test
    void atkinBasePrimeCache_shouldBeClearedViaService() {
        int start = 2;
        int end = 1000;

        atkinAlgorithm.generateBasePrimesForTesting(start, end);
        Assertions.assertNotNull(cacheManager.getCache("basePrimes").get(end), "Base prime cache should be populated");

        primeService.clearBasePrimeCache();
        Assertions.assertNull(cacheManager.getCache("basePrimes").get(end), "Base prime cache should be cleared");
    }



    @Test
    void primeResultCache_shouldBeClearedViaService() {
        String algorithm = "atkin";
        int limit = 3000;
        int threads = 2;

        primeService.findPrimes(algorithm, limit, threads, true);
        String cacheKey = algorithm + "-" + limit + "-" + threads;
        Assertions.assertNotNull(cacheManager.getCache("primes").get(cacheKey), "Prime result cache should be populated");

        primeService.clearPrimeCache();
        Assertions.assertNull(cacheManager.getCache("primes").get(cacheKey), "Prime result cache should be cleared");
    }

    // ✅ Optional: Cross-algorithm cache isolation
    @Test
    void sieveAndAtkinBasePrimes_shouldNotInterfere() {
        int limit = 2000;
        primeService.clearBasePrimeCache();

        List<Integer> sievePrimes = sieveAlgorithm.generateBasePrimesForTesting(limit);
        List<Integer> atkinPrimes = atkinAlgorithm.generateBasePrimesForTesting(2, limit);

        Assertions.assertEquals(sievePrimes, atkinPrimes, "Both algorithms should produce same base primes for small limit");
        Assertions.assertNotNull(cacheManager.getCache("basePrimes").get(limit), "Shared cache should contain entry for limit " + limit);
    }
}