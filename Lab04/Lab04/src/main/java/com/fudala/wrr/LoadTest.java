package com.fudala.wrr;

import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

public final class LoadTest {

    private static final Logger LOGGER = Logger.getLogger(LoadTest.class.getName());

    private LoadTest() {
    }

    public static void runWithLoadBalancer(LoadBalancer loadBalancer) {
        int requests = 10000;
        for (int i = 0; i < requests; i++) {
            BackendServer backendServer = loadBalancer.chooseServer();
            backendServer.incrementServedCount();
        }
        for (BackendServer backendServer : loadBalancer.getBackends()) {
            long served = backendServer.getServedCount();
            double percentage = served * 100.0 / requests;
            String percentageText = String.format(Locale.US, "%.2f", percentage);
            LOGGER.info(() -> backendServer.getId()
                    + " weight=" + backendServer.getWeight()
                    + " served=" + served
                    + " (" + percentageText + "%)");
        }
    }

    @SuppressWarnings("unused")
    static void main(String[] args) {
        BackendServer backendA = new SimpleBackendServer("Backend A", 5);
        BackendServer backendB = new SimpleBackendServer("Backend B", 3);
        BackendServer backendC = new SimpleBackendServer("Backend C", 2);
        LoadBalancer loadBalancer = new WeightedRoundRobinLoadBalancer(List.of(backendA, backendB, backendC));
        runWithLoadBalancer(loadBalancer);
    }
}
