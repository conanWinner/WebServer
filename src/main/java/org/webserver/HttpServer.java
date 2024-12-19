package org.webserver;

import org.webserver.httpserver.config.Configuration;
import org.webserver.httpserver.config.ConfigurationManager;
import org.webserver.httpserver.core.ServerListenerThread;
import org.webserver.httpserver.gui.ServerGUI;

import java.io.IOException;

public class HttpServer {

    public static void main (String[] args) {
        System.out.println("Server running ...");
        ConfigurationManager.getInstance().loadConfigurationFile("src/main/resources/http.json");

        Configuration config = ConfigurationManager.getInstance().getCurrentConfiguration();

        // Access server details
        System.out.println("Server Listening on: " + config.getServer().getListen());
        System.out.println("Server Name: " + config.getServer().getServerName());

        // Access locations
        for (Configuration.Location location : config.getServer().getLocations()) {
            System.out.println("Path: " + location.getPath());
            System.out.println("Root: " + location.getRoot());
            System.out.println("Index: " + location.getIndex());
        }

        // ============  Print config load balancing   =============
        Configuration.LoadBalancerConfig lbConfig = config.getLoadBalancer();

        System.out.println("Load Balancing Strategy: " + lbConfig.getStrategy());
        System.out.println("Backend Servers: " + lbConfig.getBackendServers());

        ServerGUI serverGUI = new ServerGUI(config);

    }

}
