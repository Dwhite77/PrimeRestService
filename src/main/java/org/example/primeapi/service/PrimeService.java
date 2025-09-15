package org.example.primeapi.service;

import org.example.primeapi.algo.PrimeAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PrimeService {

    private final Map<String, PrimeAlgorithm> algorithmMap;

    @Autowired
    public PrimeService(List<PrimeAlgorithm> algorithms) {
        this.algorithmMap = algorithms.stream()
                .collect(Collectors.toMap(
                        alg -> alg.name().toLowerCase(),
                        alg -> alg
                ));
    }

    public List<Integer> findPrimes(String algorithm, int limit, int threads) {
        PrimeAlgorithm selected = algorithmMap.get(algorithm.toLowerCase());
        if (selected == null) {
            throw new IllegalArgumentException("Unsupported algorithm: " + algorithm);
        }
        return selected.generate(limit, threads);
    }
}