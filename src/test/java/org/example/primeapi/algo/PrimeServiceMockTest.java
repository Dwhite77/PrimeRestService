package org.example.primeapi.algo;

import org.example.primeapi.algo.PrimeAlgorithm;
import org.example.primeapi.service.PrimeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PrimeServiceMockTest {

    private PrimeAlgorithm sieveMock;
    private PrimeAlgorithm millerMock;
    private PrimeService service;

    @BeforeEach
    void setUp() {
        sieveMock = mock(PrimeAlgorithm.class);
        when(sieveMock.name()).thenReturn("sieve");
        when(sieveMock.generate(100, 2)).thenReturn(List.of(2, 3, 5, 7));

        millerMock = mock(PrimeAlgorithm.class);
        when(millerMock.name()).thenReturn("miller");
        when(millerMock.generate(100, 2)).thenReturn(List.of(2, 3, 5, 7, 11));

        service = new PrimeService(List.of(sieveMock, millerMock));
    }

    @Test
    void delegatesToCorrectAlgorithm() {
        List<Integer> result = service.findPrimes("sieve", 100, 2);
        assertEquals(List.of(2, 3, 5, 7), result);
        verify(sieveMock).generate(100, 2);
        verifyNoInteractions(millerMock);
    }

    @Test
    void handlesCaseInsensitiveAlgorithmNames() {
        List<Integer> result = service.findPrimes("MILLER", 100, 2);
        assertEquals(List.of(2, 3, 5, 7, 11), result);
        verify(millerMock).generate(100, 2);
    }

    @Test
    void throwsExceptionForUnknownAlgorithm() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                service.findPrimes("unknown", 100, 2));
        assertTrue(ex.getMessage().contains("Unsupported algorithm"));
    }
}