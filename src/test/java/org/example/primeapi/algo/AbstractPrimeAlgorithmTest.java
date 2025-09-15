package org.example.primeapi.algo;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
public class AbstractPrimeAlgorithmTest {

    private final AbstractPrimeAlgorithm algo = new AbstractPrimeAlgorithm() {
        @Override
        public String name() { return "dummy"; }

        @Override
        public List<Integer> generate(int upperLimit, int threads) {
            return List.of(); // not needed for these tests
        }
    };

    @BeforeAll
    static void initSuite() {
        log.info("Starting PrimeService integration test suite");
    }

    @BeforeEach
    void initTest() {
        log.debug("Setting up test instance");
    }

    @AfterEach
    void tearDown(){
        log.info("Test Completed");
    }

    @Test
    void shouldSkipWhenUpperLimitTooLow() {
        assertTrue(algo.shouldSkip("test", 1, 1));
    }

    @Test
    void shouldSkipWhenThreadsTooHigh() {
        assertTrue(algo.shouldSkip("test", 100, 200));
    }

    @Test
    void shouldSkipWhenUpperLimitTooHigh() {
        assertTrue(algo.shouldSkip("test", AbstractPrimeAlgorithm.MAX_LIMIT + 1, 1));
    }
}
