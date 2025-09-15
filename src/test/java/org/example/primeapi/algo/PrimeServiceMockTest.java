package org.example.primeapi.algo;

import org.example.primeapi.service.PrimeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PrimeServiceMockTest {

    private PrimeAlgorithm sieveMock;
    private PrimeAlgorithm millerMock;
    private PrimeAlgorithm atkinMock;
    private PrimeAlgorithm trialMock;

    private PrimeService service;

    @BeforeEach
    void setUp() {
        sieveMock = mock(PrimeAlgorithm.class);
        when(sieveMock.name()).thenReturn("sieve");
        when(sieveMock.generate(10, 2)).thenReturn(List.of(2, 3, 5, 7));

        millerMock = mock(PrimeAlgorithm.class);
        when(millerMock.name()).thenReturn("miller");
        when(millerMock.generate(10, 2)).thenReturn(List.of(2, 3, 5, 7));

        atkinMock = mock(PrimeAlgorithm.class);
        when(atkinMock.name()).thenReturn("atkin");
        when(atkinMock.generate(10, 2)).thenReturn(List.of(2, 3, 5, 7));

        trialMock = mock(PrimeAlgorithm.class);
        when(trialMock.name()).thenReturn("trial");
        when(trialMock.generate(10, 2)).thenReturn(List.of(2, 3, 5, 7));

        service = new PrimeService(List.of(sieveMock, millerMock, atkinMock, trialMock));
    }

    @Test
    void delegatesToCorrectAlgorithm() {
        List<Integer> result = service.findPrimes("sieve", 10, 2);
        assertEquals(List.of(2, 3, 5, 7), result);
        verify(sieveMock).generate(10, 2);
    }

    @Test
    void handlesCaseInsensitiveAlgorithmNames() {
        List<Integer> result = service.findPrimes("MILLER", 10, 2);
        assertEquals(List.of(2, 3, 5, 7), result);
        verify(millerMock).generate(10, 2);
    }

    @Test
    void delegatesToCorrectAlgorithmAtkinCaseInsensitive() {
        List<Integer> result = service.findPrimes("Atkin", 10, 2);
        assertEquals(List.of(2, 3, 5, 7), result);
        verify(atkinMock).generate(10, 2);
    }

    @Test
    void delegatesToCorrectAlgorithmTrial() {
        List<Integer> result = service.findPrimes("trial", 10, 2);
        assertEquals(List.of(2, 3, 5, 7), result);
        verify(trialMock).generate(10, 2);
    }

    @Test
    void throwsExceptionForUnknownAlgorithm() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                service.findPrimes("unknown", 100, 2));
        assertTrue(ex.getMessage().contains("Unsupported algorithm"));
    }
}