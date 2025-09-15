package org.example.primeapi.algo;

import org.example.primeapi.algo.Algorithms.TrialAlgorithm;

public class TrialAlgorithmTest extends PrimeAlgorithmTestSupport{
    @Override
    protected PrimeAlgorithm getAlgorithm() {
        return new TrialAlgorithm();
    }
}
