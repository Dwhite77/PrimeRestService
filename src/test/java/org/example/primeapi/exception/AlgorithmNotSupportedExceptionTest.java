package org.example.primeapi.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AlgorithmNotSupportedExceptionTest {

    @Test
    void constructorWithMessageSetsCorrectly() {
        AlgorithmNotSupportedException ex = new AlgorithmNotSupportedException("trial");
        assertEquals("Unsupported algorithm: trial", ex.getMessage());
    }

    @Test
    void constructorWithMessageAndCauseSetsCorrectly() {
        Throwable cause = new IllegalArgumentException("Invalid input");
        AlgorithmNotSupportedException ex = new AlgorithmNotSupportedException("sieve", cause);

        assertEquals("Unsupported algorithm: sieve", ex.getMessage());
        assertEquals(cause, ex.getCause());
    }
}