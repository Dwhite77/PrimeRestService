package org.example.primeapi.algo;

import org.example.primeapi.algo.MillerRabinAlgorithm;
import org.example.primeapi.algo.SieveAlgorithm;
import org.example.primeapi.service.PrimeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PrimeServiceIntegrationTest {

    private PrimeService service;

    @BeforeEach
    void setUp() {
        service = new PrimeService(List.of(
                new SieveAlgorithm(),
                new MillerRabinAlgorithm(),
                new TrialAlgorithm(),
                new AtkinAlgorithm()
        ));
    }

    @Test
    void sieveGeneratesCorrectPrimesUpTo30() {
        List<Integer> primes = service.findPrimes("sieve", 30, 1);
        assertEquals(List.of(2, 3, 5, 7, 11, 13, 17, 19, 23, 29), primes);
    }

    @Test
    void millerGeneratesLikelyPrimesUpTo30() {
        List<Integer> primes = service.findPrimes("miller", 30, 1);
        assertTrue(primes.containsAll(List.of(2, 3, 5, 7, 11, 13, 17, 19, 23, 29)));
    }

    @Test
    void sieveHandlesMultithreading() {
        List<Integer> primes = service.findPrimes("sieve", 1_000, 4);
        assertTrue(primes.contains(997));
        assertFalse(primes.contains(1000));
    }

    @Test
    void millerHandlesMultithreading() {
        List<Integer> primes = service.findPrimes("miller", 1_000, 4);
        assertTrue(primes.contains(997));
    }
}
