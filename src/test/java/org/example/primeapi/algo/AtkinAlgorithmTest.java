package org.example.primeapi.algo;

import org.example.primeapi.algo.Algorithms.AtkinAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class AtkinAlgorithmTest extends PrimeAlgorithmTestSupport {

    @Autowired
    private AtkinAlgorithm atkinAlgorithm;

    @Override
    protected PrimeAlgorithm getAlgorithm() {
        return atkinAlgorithm;
    }
}

