package org.example.primeapi.algo;

import org.example.primeapi.algo.Algorithms.MillerRabinAlgorithm;

public class MillerRabinAlgorithmTest extends PrimeAlgorithmTestSupport {
    @Override
    protected PrimeAlgorithm getAlgorithm() {
        return new MillerRabinAlgorithm();
    }
}

