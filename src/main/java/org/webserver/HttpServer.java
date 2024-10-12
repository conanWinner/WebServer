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

        Configuration conf = ConfigurationManager.getInstance().getCurrentConfiguration();

        try {

            ServerListenerThread serverListener = new ServerListenerThread(conf.getPort(), conf.getWebroot(), conf.getLocalhost());
//            serverListener.start();
            ServerGUI serverGUI = new ServerGUI(serverListener);

            //update count client
            serverListener.setConnectionCountCallback(serverGUI::GUIupdateConnectionCount);
            serverListener.setConnectionListCallback(serverGUI::GUIaddActiveUser);


        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

}
