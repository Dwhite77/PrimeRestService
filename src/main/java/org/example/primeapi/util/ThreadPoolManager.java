package org.example.primeapi.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Centralized thread pool manager for prime computation tasks.
 * Provides named thread pools for better logging and diagnostics.
 */
public class ThreadPoolManager {

    private static final AtomicInteger poolCounter = new AtomicInteger(1);

    public static ExecutorService createFixedPool(int threads, String label) {
        int poolId = poolCounter.getAndIncrement();
        ThreadFactory factory = runnable -> {
            Thread t = new Thread(runnable);
            t.setName(label + "-pool-" + poolId + "-thread-" + t.getId());
            return t;
        };
        return Executors.newFixedThreadPool(threads, factory);
    }
}