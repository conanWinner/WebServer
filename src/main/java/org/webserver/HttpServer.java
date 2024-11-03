package org.webserver;

import org.webserver.httpserver.config.Configuration;
import org.webserver.httpserver.config.ConfigurationManager;
import org.webserver.httpserver.gui.ServerGUI;



public class HttpServer {

    public static void main (String[] args) {
        ConfigurationManager.getInstance().loadConfigurationFile("src/main/resources/http.json");

        Configuration conf = ConfigurationManager.getInstance().getCurrentConfiguration();
        ServerGUI serverGUI = new ServerGUI(conf);
    }

}
