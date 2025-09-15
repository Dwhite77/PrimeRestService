package org.example.primeapi.service;

import lombok.Getter;
import org.example.primeapi.algo.PrimeAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PrimeService {

    private final Map<String, PrimeAlgorithm> algorithmMap;

    @Getter
    private long durationMs;

    @Autowired
    public PrimeService(List<PrimeAlgorithm> algorithms) {
        this.algorithmMap = algorithms.stream()
                .collect(Collectors.toMap(
                        alg -> alg.name().toLowerCase(),
                        alg -> alg
                ));
    }

    public List<Integer> findPrimes(String algorithm, int limit, int threads) {

        if(limit==1){return List.of(1);}
        if(limit==2){return List.of(1,2);}

        PrimeAlgorithm selected = algorithmMap.get(algorithm.toLowerCase());

        if (selected == null) {
            throw new IllegalArgumentException("Unsupported algorithm: " + algorithm);
        }


        long start = System.nanoTime();
        List<Integer> results = selected.generate(limit, threads);
        durationMs = (System.nanoTime() - start) / 1_000_000;

        return results;
    }


}