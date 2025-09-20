package org.example.primeapi.helper;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import static org.junit.jupiter.api.Assumptions.assumeTrue;

@Slf4j
public class TestHelperMethods {

    @Setter
    @Value("${BENCHMARK:false}")
    private boolean runBenchmark;



    public void skipTest(){
        log.info("Skipping this test due to BENCHMARK being set to false");
        assumeTrue(runBenchmark); // currently just a flag that allows us to skip a test if its set to false
    }
}
