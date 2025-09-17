package org.example.primeapi.util;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.*;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class ThreadPoolManagerTest {

    @Test
    void testThreadNamingAndExecution() throws InterruptedException {
        int threadCount = 4;
        String label = "prime";
        ExecutorService pool = ThreadPoolManager.createFixedPool(threadCount, label);

        CountDownLatch latch = new CountDownLatch(threadCount);
        ConcurrentLinkedQueue<String> threadNames = new ConcurrentLinkedQueue<>();

        IntStream.range(0, threadCount).forEach(i -> pool.submit(() -> {
            String name = Thread.currentThread().getName();
            log.info("Executing task on {}", name);
            threadNames.add(name);
            latch.countDown();
        }));

        assertTrue(latch.await(2, TimeUnit.SECONDS), "Tasks did not complete in time");
        assertEquals(threadCount, threadNames.size(), "Incorrect number of threads used");

        threadNames.forEach(name -> {
            assertTrue(name.startsWith(label + "-pool-"), "Thread name does not start with expected label");
            assertTrue(name.contains("-thread-"), "Thread name does not contain expected suffix");
        });

        pool.shutdownNow();
    }

    @Test
    void testZeroThreadsShouldThrowException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                ThreadPoolManager.createFixedPool(0, "zero")
        );
        log.warn("Expected exception for zero threads: {}", exception.getMessage());
    }

    @Test
    void testNegativeThreadsShouldThrowException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                ThreadPoolManager.createFixedPool(-3, "negative")
        );
        log.warn("Expected exception for negative threads: {}", exception.getMessage());
    }

    @Test
    void testNullLabelStillCreatesPool() throws InterruptedException {
        ExecutorService pool = ThreadPoolManager.createFixedPool(2, null);
        CountDownLatch latch = new CountDownLatch(2);
        ConcurrentLinkedQueue<String> threadNames = new ConcurrentLinkedQueue<>();

        IntStream.range(0, 2).forEach(i -> pool.submit(() -> {
            String name = Thread.currentThread().getName();
            log.info("Thread name with null label: {}", name);
            threadNames.add(name);
            latch.countDown();
        }));

        assertTrue(latch.await(2, TimeUnit.SECONDS));
        threadNames.forEach(name -> assertTrue(name.contains("pool-") && name.contains("-thread-")));

        pool.shutdownNow();
    }

    @Test
    void testEmptyLabelStillCreatesPool() throws InterruptedException {
        ExecutorService pool = ThreadPoolManager.createFixedPool(2, "");
        CountDownLatch latch = new CountDownLatch(2);
        ConcurrentLinkedQueue<String> threadNames = new ConcurrentLinkedQueue<>();

        IntStream.range(0, 2).forEach(i -> pool.submit(() -> {
            String name = Thread.currentThread().getName();
            log.info("Thread name with empty label: {}", name);
            threadNames.add(name);
            latch.countDown();
        }));

        assertTrue(latch.await(2, TimeUnit.SECONDS));
        threadNames.forEach(name -> assertTrue(name.startsWith("pool-") || name.contains("-thread-")));

        pool.shutdownNow();
    }

    @Test
    void testShutdownBehavior() {
        ExecutorService pool = ThreadPoolManager.createFixedPool(2, "shutdown");
        pool.shutdown();
        assertTrue(pool.isShutdown(), "Pool should be shut down");
        log.info("Pool shutdown confirmed");
    }
}