package org.webserver.httpserver.core;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class LoadBalancer {
    private static LoadBalancer instance;
    private final List<String> servers; // Danh sách các backend servers
    private AtomicInteger currentIndex = new AtomicInteger(0);


    public LoadBalancer(List<String> servers) {
        this.servers = servers;
    }

    public static synchronized LoadBalancer getInstance(List<String> servers) {
        if (instance == null) {
            instance = new LoadBalancer(servers);
        }
        return instance;
    }

    // Round Robin Strategy
    public synchronized String getNextServer() {
        if (servers.isEmpty()) {
            throw new RuntimeException("No backend servers available.");
        }
        int index = currentIndex.getAndUpdate(i -> (i + 1) % servers.size());
        System.out.println("Selected server index: " + index);
        return servers.get(index);
    }


    // Least Connections Strategy (cần thêm số kết nối cho từng server)
    public String getLeastConnections(Map<String, Integer> connections) {
        return connections.entrySet()
                .stream()
                .min(Comparator.comparingInt(Map.Entry::getValue))
                .map(Map.Entry::getKey)
                .orElseThrow(() -> new RuntimeException("No backend servers available."));
    }

    // Weighted Round Robin Strategy
    public String getWeightedServer(Map<String, Integer> weights) {
        List<String> weightedList = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : weights.entrySet()) {
            weightedList.addAll(Collections.nCopies(entry.getValue(), entry.getKey()));
        }
        Random random = new Random();
        return weightedList.get(random.nextInt(weightedList.size()));
    }
}

