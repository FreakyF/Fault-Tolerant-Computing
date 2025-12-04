package com.fudala.wrr;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class LoadBalancerServer {

    private final HttpServer httpServer;
    private final LoadBalancer loadBalancer;
    private final ExecutorService executor;

    public LoadBalancerServer(InetSocketAddress address, List<BackendServer> backends) throws IOException {
        this.loadBalancer = new WeightedRoundRobinLoadBalancer(backends);
        this.httpServer = HttpServer.create(address, 0);
        this.executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2);
        this.httpServer.createContext("/", new HttpLoadBalancingHandler(loadBalancer));
        this.httpServer.createContext("/metrics", new ServerMetricsHandler(loadBalancer));
        this.httpServer.setExecutor(executor);
    }

    public void start() {
        httpServer.start();
    }

    public void stop() {
        httpServer.stop(0);
        executor.shutdownNow();
    }

    public LoadBalancer getLoadBalancer() {
        return loadBalancer;
    }
}