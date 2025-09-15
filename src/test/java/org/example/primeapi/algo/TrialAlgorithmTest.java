package org.example.primeapi.algo;

public class TrialAlgorithmTest extends PrimeAlgorithmTestSupport{
    @Override
    protected PrimeAlgorithm getAlgorithm() {
        return new TrialAlgorithm();
    }
}
