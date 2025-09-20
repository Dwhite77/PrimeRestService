package org.example.primeapi.util;

import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class PrimeResultWrapper {

    private final List<Integer> fullList;
    private final int batchSize;

    public PrimeResultWrapper(List<Integer> fullList, int batchSize) {
        this.fullList = fullList;
        this.batchSize = batchSize;
    }

    public List<Integer> getFirstBatch() {
        return fullList.subList(0, Math.min(batchSize, fullList.size()));
    }

    public Stream<List<Integer>> streamBatches() {
        int total = fullList.size();
        return IntStream.range(0, (total + batchSize - 1) / batchSize)
                .mapToObj(i -> fullList.subList(i * batchSize, Math.min((i + 1) * batchSize, total)));
    }

    public int getTotalCount() {
        return fullList.size();
    }

    public List<Integer> getFullList() {
        return fullList;
    }
}