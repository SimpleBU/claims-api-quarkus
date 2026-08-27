package com.example.claims.model;

import java.util.concurrent.atomic.AtomicLong;

public class IdSequence {

    private final String prefix;
    private final AtomicLong counter;

    public IdSequence(String prefix, long start) {
        this.prefix = prefix;
        this.counter = new AtomicLong(start);
    }

    public String next() {
        return prefix + "-" + counter.incrementAndGet();
    }

    public long current() {
        return counter.get();
    }
}
