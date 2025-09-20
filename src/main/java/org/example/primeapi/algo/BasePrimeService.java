package org.example.primeapi.algo;

import org.example.primeapi.algo.Algorithms.AtkinUtil;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@Service
public class BasePrimeService {

    @Cacheable(value = "basePrimes", key = "#limit")
    public List<Integer> generateSieveBasePrimes(int limit) {
        boolean[] isPrimeCandidate = new boolean[limit + 1];
        Arrays.fill(isPrimeCandidate, true);

        for (int candidate = 2; candidate * candidate <= limit; candidate++) {
            if (!isPrimeCandidate[candidate]) continue;
            for (int multiple = candidate * candidate; multiple <= limit; multiple += candidate) {
                isPrimeCandidate[multiple] = false;
            }
        }

        List<Integer> confirmedPrimes = new ArrayList<>();
        for (int value = 2; value <= limit; value++) {
            if (isPrimeCandidate[value]) confirmedPrimes.add(value);
        }

        return confirmedPrimes;
    }

    @Cacheable(value = "basePrimes", key = "#end")
    public List<Integer> generateAtkinBasePrimes(int start, int end) {
        boolean[] isPrimeCandidate = new boolean[end + 1];
        int sqrtLimit = (int) Math.sqrt(end);

        AtkinUtil.applyAtkinFilters(isPrimeCandidate, sqrtLimit, end);
        AtkinUtil.eliminateMultiplesOfPrimeSquares(isPrimeCandidate, sqrtLimit, end);

        return AtkinUtil.collectConfirmedPrimes(isPrimeCandidate, start, end);
    }


}
