package org.example.primeapi.algo;

import lombok.extern.slf4j.Slf4j;
import org.example.primeapi.service.PrimeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(
        classes = org.example.primeapi.PrimeApiApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)@Slf4j
public class AbstractPrimeAlgorithmTest {

    @Autowired
    private PrimeService primeService;

    @Test
    void shouldSkipWhenUpperLimitTooLow() {
        assertTrue(primeService.shouldSkip("test", 1, 1));
    }

    @Test
    void shouldSkipWhenThreadsTooHigh() {
        int excessiveThreads = primeService.getMaxThreads() + 1;
        assertTrue(primeService.shouldSkip("test", 1000, excessiveThreads));
    }

    @Test
    void shouldSkipWhenUpperLimitTooHigh() {
        int excessiveLimit = primeService.getMaxLimit() + 1;
        assertTrue(primeService.shouldSkip("test", excessiveLimit, 1));
    }

    @Test
    void shouldSkipWhenThreadsExceedLimit() {
        assertTrue(primeService.shouldSkip("test", 5, 10));
    }

    @Test
    void shouldNotSkipForValidInputs() {
        assertTrue(!primeService.shouldSkip("test", 1000, 4));
    }
}