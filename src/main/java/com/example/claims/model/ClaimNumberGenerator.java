package com.example.claims.model;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Produces claim numbers in the CLM-XXXXXXXX shape used by the public claim lookup.
 */
public class ClaimNumberGenerator {

    private final AtomicInteger counter;

    public ClaimNumberGenerator(int start) {
        this.counter = new AtomicInteger(start);
    }

    public String next() {
        return String.format("CLM-%08d", counter.incrementAndGet());
    }
}
