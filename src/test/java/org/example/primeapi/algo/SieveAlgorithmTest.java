package org.example.primeapi.algo;

import org.example.primeapi.algo.Algorithms.SieveAlgorithm;

public class SieveAlgorithmTest extends PrimeAlgorithmTestSupport {
    @Override
    protected PrimeAlgorithm getAlgorithm() {
        return new SieveAlgorithm();
    }
}

