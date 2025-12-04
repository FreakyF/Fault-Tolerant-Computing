package com.fudala.wrr;

import java.util.List;

public interface LoadBalancer {
    BackendServer chooseServer();

    void updateWeight(String backendId, int newWeight);

    List<BackendServer> getBackends();
}