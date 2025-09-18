package org.example.primeapi.view;

import org.example.primeapi.model.PrimePayload;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class PrimeRequestLog {
    private static final Queue<PrimePayload> recentRequests = new ConcurrentLinkedQueue<>();

    public static void log(PrimePayload payload) {
        if (recentRequests.size() >= 10) recentRequests.poll(); // keep last 10
        recentRequests.offer(payload);
    }

    public static List<PrimePayload> getRecent() {
        return List.copyOf(recentRequests);
    }
}
