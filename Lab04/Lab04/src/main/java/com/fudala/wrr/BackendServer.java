package com.fudala.wrr;

public interface BackendServer {
    String getId();

    int getWeight();

    void setWeight(int weight);

    long getServedCount();

    void incrementServedCount();
}