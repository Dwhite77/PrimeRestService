package org.example.primeapi.util;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Centralized thread pool manager for prime computation tasks.
 * Provides named thread pools for better logging and diagnostics.
 */
@Slf4j
public class ThreadPoolManager {

    private static final AtomicInteger poolCounter = new AtomicInteger(1);

    public static ExecutorService createFixedPool(int threads, String label) {
        if (threads <= 0) {
            throw new IllegalArgumentException("Thread count must be positive: " + threads);
        }

        int poolId = poolCounter.getAndIncrement();
        String safeLabel = (label == null || label.isBlank()) ? "default" : label;

        ThreadFactory factory = runnable -> {
            Thread t = new Thread(runnable);
            t.setName(safeLabel + "-pool-" + poolId + "-thread-" + t.getId());
            log.debug("Created thread: {}", t.getName());
            return t;
        };

        log.info("Creating fixed thread pool with {} threads and label '{}'", threads, safeLabel);
        return Executors.newFixedThreadPool(threads, factory);
    }
}