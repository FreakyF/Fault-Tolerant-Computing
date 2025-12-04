package com.fudala.wrr;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class WeightedRoundRobinLoadBalancer implements LoadBalancer {

    private final List<Node> nodes;
    private int totalWeight;

    public WeightedRoundRobinLoadBalancer(List<BackendServer> backends) {
        if (backends == null || backends.isEmpty()) {
            throw new IllegalArgumentException("Backends list must not be empty");
        }
        this.nodes = new ArrayList<>();
        for (BackendServer backend : backends) {
            this.nodes.add(new Node(backend));
        }
        recalculateTotalWeight();
    }

    private void recalculateTotalWeight() {
        int sum = 0;
        for (Node node : nodes) {
            sum += node.weight;
        }
        this.totalWeight = sum;
    }

    @Override
    public synchronized BackendServer chooseServer() {
        Node selected = null;
        for (Node node : nodes) {
            node.currentWeight += node.weight;
            if (selected == null || node.currentWeight > selected.currentWeight) {
                selected = node;
            }
        }
        if (selected == null) {
            throw new IllegalStateException("No backend nodes configured");
        }
        selected.currentWeight -= totalWeight;
        return selected.backend;
    }

    @Override
    public synchronized void updateWeight(String backendId, int newWeight) {
        if (backendId == null || backendId.isBlank()) {
            throw new IllegalArgumentException("Backend id must not be blank");
        }
        if (newWeight <= 0) {
            throw new IllegalArgumentException("Weight must be positive");
        }
        boolean found = false;
        for (Node node : nodes) {
            if (node.backend.getId().equals(backendId)) {
                node.weight = newWeight;
                node.backend.setWeight(newWeight);
                found = true;
            }
        }
        if (!found) {
            throw new IllegalArgumentException("Unknown backend id: " + backendId);
        }
        recalculateTotalWeight();
        resetCurrentWeights();
    }

    @Override
    public synchronized List<BackendServer> getBackends() {
        List<BackendServer> result = new ArrayList<>();
        for (Node node : nodes) {
            result.add(node.backend);
        }
        return Collections.unmodifiableList(result);
    }

    private void resetCurrentWeights() {
        for (Node node : nodes) {
            node.currentWeight = 0;
        }
    }

    private static final class Node {
        private final BackendServer backend;
        private int weight;
        private int currentWeight;

        private Node(BackendServer backend) {
            this.backend = Objects.requireNonNull(backend);
            this.weight = backend.getWeight();
            this.currentWeight = 0;
        }
    }
}