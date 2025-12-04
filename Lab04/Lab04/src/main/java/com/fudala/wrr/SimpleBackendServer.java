package com.fudala.wrr;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

public final class SimpleBackendServer implements BackendServer {

    private final String id;
    private final AtomicLong servedCount;
    private volatile int weight;

    public SimpleBackendServer(String id, int weight) {
        this.id = Objects.requireNonNull(id);
        if (weight <= 0) {
            throw new IllegalArgumentException("Weight must be positive");
        }
        this.weight = weight;
        this.servedCount = new AtomicLong();
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public int getWeight() {
        return weight;
    }

    @Override
    public void setWeight(int weight) {
        if (weight <= 0) {
            throw new IllegalArgumentException("Weight must be positive");
        }
        this.weight = weight;
    }

    @Override
    public long getServedCount() {
        return servedCount.get();
    }

    @Override
    public void incrementServedCount() {
        servedCount.incrementAndGet();
    }
}
