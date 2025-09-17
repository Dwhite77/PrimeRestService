package org.example.primeapi.algo.Algorithms;

import org.example.primeapi.algo.AbstractPrimeAlgorithm;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
public class TrialAlgorithm extends AbstractPrimeAlgorithm {

    @Override
    public String name() {
        return "trial";
    }

    @Override
    public List<Integer> generate(int upperLimit, int threads) {
        return runThreaded("Trial", 2, upperLimit, threads, this::trialChunk);
    }



    private List<Integer> trialChunk(int start, int end) {
        return IntStream.rangeClosed(start, end)
                .filter(this::isPrime)
                .boxed()
                .collect(Collectors.toList());
    }

    private boolean isPrime(int n) {
        if (n < 2) return false;
        for (int i = 2; i <= Math.sqrt(n); i++) {
            if (n % i == 0) return false;
        }
        return true;
    }
}
