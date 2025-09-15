package org.example.primeapi.algo;

public class MillerRabinAlgorithmTest extends PrimeAlgorithmTestSupport {
    @Override
    protected PrimeAlgorithm getAlgorithm() {
        return new MillerRabinAlgorithm();
    }
}

