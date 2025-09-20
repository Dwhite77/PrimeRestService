package org.example.primeapi.service;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.example.primeapi.algo.PrimeAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@Slf4j
@Service
public class PrimeService {

    private final Map<String, PrimeAlgorithm> algorithmMap;

    @Value("${MAXLIMIT:1000000000}")
    private int maxLimit;

    @Value("${MAXTHREADS:128}")
    private int maxThreads;

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

    @Cacheable(value = "primes",
            key = "#algorithm + '-' + #limit + '-' + #threads",
            unless = "!#useCache"
    )
    public List<Integer> findPrimes(String algorithm, int limit, int threads, boolean useCache) {

        if(limit==2){return List.of(2);}
        if (shouldSkip(algorithm, limit, threads)) return List.of();

        PrimeAlgorithm selected = algorithmMap.get(algorithm.toLowerCase());

        if (selected == null) {
            throw new IllegalArgumentException("Unsupported algorithm: " + algorithm);
        }


        long start = System.nanoTime();
        List<Integer> results = selected.generate(limit, threads);
        durationMs = (System.nanoTime() - start) / 1_000_000;

        return results;
    }

    public boolean shouldSkip(String label, int limit, int threads) {
        if (limit < 2) {
            log.warn("Skipping test [{}]: limit {} is below minimum threshold (must be ≥ 2)", label, limit);
            return true;
        }
        if(limit==1){
            log.warn("Skipping test [{}] limit is 1", label);
            return true;
        }

        if (threads > limit) {
            log.warn("Skipping test [{}]: thread count {} exceeds upper limit {} (threads must be ≤ limit)", label, threads, limit);
            return true;
        }

        if (limit > maxLimit) {
            log.warn("Skipping test [{}]: limit {} exceeds MAX_LIMIT ({})", label, limit, maxLimit);
            return true;
        }

        if (threads > maxThreads) {
            log.warn("Skipping test [{}]: thread count {} exceeds MAX_THREADS ({})", label, threads, maxThreads);
            return true;
        }

        return false;
    }

    @CacheEvict(value = "primes", allEntries = true)
    public void clearPrimeCache() {
        log.info("✅ Prime cache cleared manually");
    }

    @CacheEvict(value = "basePrimes", allEntries = true)
    public void clearBasePrimeCache(){
        log.info("✅ Base Prime cache cleared manually");
    }


}