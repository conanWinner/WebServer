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

    public static class ServerConfig {
        private int listen;
        private String serverName;
        private SSL  ssl;
        private List<Location> locations;

        public int getListen() {
            return listen;
        }
        public SSL  getSsl() {  return ssl; }
        public String getServerName() {
            return serverName;
        }

        public List<Location> getLocations() {
            return locations;
        }

    }

    public static class SSL {
        private boolean enabled;
        private String port;
        private String keystore;
        private String keystorePassword;
        public boolean getEnabled() {
            return enabled;
        }

        public String getPort() {
            return port;
        }

        public String getKeystore() {
            return keystore;
        }

        public String getKeystorePassword() {
            return keystorePassword;
        }
    }

    public static class Location {
        private String path;
        private String root;
        private String index;

        public String getPath() {
            return path;
        }


        public String getRoot() {
            return root;
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

        public List<String> getBackendServers() {
            return backendServers;
        }

    }
}
