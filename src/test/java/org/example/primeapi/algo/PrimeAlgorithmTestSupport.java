package org.example.primeapi.algo;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

@SpringBootTest
@Slf4j
public abstract class PrimeAlgorithmTestSupport {


    protected abstract PrimeAlgorithm getAlgorithm();

    @Test
    void generatesPrimesUpTo100() {
        List<Integer> primes = getAlgorithm().generate(100, 1);
        assertTrue(primes.containsAll(List.of(2, 3, 5, 7, 97)));
        assertFalse(primes.contains(100));
    }

    @Test
    void returnsEmptyForUpperLimitLessThan2() {
        List<Integer> primes = getAlgorithm().generate(1, 1);
        assertTrue(primes.isEmpty());
    }

    //THis is no longer handled at this level so will not pass
    /*@Test
    void handlesThreadCountGreaterThanLimit() {
        List<Integer> primes = getAlgorithm().generate(10, 20);
        assertTrue(primes.isEmpty());
    }*/

    @Test
    void returnsSortedDistinctPrimes() {
        List<Integer> primes = getAlgorithm().generate(200, 4);
        assertEquals(primes.stream().distinct().sorted().toList(), primes);
    }
}
