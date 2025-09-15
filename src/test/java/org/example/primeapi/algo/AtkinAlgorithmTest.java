package org.example.primeapi.algo;

import org.example.primeapi.algo.Algorithms.AtkinAlgorithm;

public class AtkinAlgorithmTest extends PrimeAlgorithmTestSupport{
    @Override
    protected PrimeAlgorithm getAlgorithm(){
        return new AtkinAlgorithm();
    }
}



