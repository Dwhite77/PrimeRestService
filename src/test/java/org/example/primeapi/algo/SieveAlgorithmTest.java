package org.example.primeapi.algo;

import org.example.primeapi.algo.Algorithms.SieveAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class SieveAlgorithmTest extends PrimeAlgorithmTestSupport {

    @Autowired
    private SieveAlgorithm sieveAlgorithm;

    @Override
    protected PrimeAlgorithm getAlgorithm() {
        return sieveAlgorithm;
    }
}
