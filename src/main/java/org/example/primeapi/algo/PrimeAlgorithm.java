package org.example.primeapi.algo;

import java.util.List;

public interface PrimeAlgorithm {
    String name();
    List<Integer> generate(int upperLimit, int threads);
}