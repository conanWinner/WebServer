package org.webserver.httpserver.config;

import java.util.List;

public class Configuration {
    private ServerConfig server;
    private LoadBalancerConfig loadBalancer;

    public ServerConfig getServer() {
        return server;
    }

    public void setServer(ServerConfig server) {
        this.server = server;
    }

    public LoadBalancerConfig getLoadBalancer() {
        return loadBalancer;
    }

    public void setLoadBalancer(LoadBalancerConfig loadBalancer) {
        this.loadBalancer = loadBalancer;
    }

    public static class ServerConfig {
        private int listen;
        private String serverName;
        private String webroot;
        private List<Location> locations;

        public int getListen() {
            return listen;
        }

        public void setListen(int listen) {
            this.listen = listen;
        }

        public String getServerName() {
            return serverName;
        }

        public void setServerName(String serverName) {
            this.serverName = serverName;
        }

        public String getWebroot() {
            return webroot;
        }

        public void setWebroot(String webroot) {
            this.webroot = webroot;
        }

        public List<Location> getLocations() {
            return locations;
        }

        public void setLocations(List<Location> locations) {
            this.locations = locations;
        }
    }

    public static class Location {
        private String path;
        private String root;
        private String index;

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public String getRoot() {
            return root;
        }

        public void setRoot(String root) {
            this.root = root;
        }

        public String getIndex() {
            return index;
        }

        public void setIndex(String index) {
            this.index = index;
        }
    }

    // Cấu hình Load Balancer
    public static class LoadBalancerConfig {
        private String strategy; // Chiến lược phân phối: RoundRobin, LeastConnections, v.v.
        private List<String> backendServers; // Danh sách các backend servers

        public String getStrategy() {
            return strategy;
        }

        public void setStrategy(String strategy) {
            this.strategy = strategy;
        }

        public List<String> getBackendServers() {
            return backendServers;
        }

        public void setBackendServers(List<String> backendServers) {
            this.backendServers = backendServers;
        }
    }
}
