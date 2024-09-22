package org.example;

import org.example.config.Configuration;
import org.example.config.ConfigurationManager;
import org.example.core.ServerListenerThread;
import org.example.gui.ServerGUI;

import java.io.IOException;

public class HttpServer {

    public static void main (String[] args) {
        System.out.println("Server running ...");
        ConfigurationManager.getInstance().loadConfigurationFile("src/main/resources/http.json");

        Configuration conf = ConfigurationManager.getInstance().getCurrentConfiguration();

        try {

            ServerListenerThread serverListener = new ServerListenerThread(conf.getPort(), conf.getWebroot(), conf.getLocalhost());
//            serverListener.start();
            ServerGUI serverGUI = new ServerGUI(serverListener);

            //update count client
            serverListener.setConnectionCountCallback(serverGUI::updateConnectionCount);
            serverListener.setConnectionListCallback(serverGUI::addActiveUser);


        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

}
