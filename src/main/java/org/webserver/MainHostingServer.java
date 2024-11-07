package org.webserver;

import org.webserver.config.Configuration;
import org.webserver.config.ConfigurationManager;
import org.webserver.core.Client;
import org.webserver.gui.LoginGUI;

import java.io.IOException;

public class MainHostingServer {
    public static void main(String [] args)  {
        ConfigurationManager.getInstance().loadConfigurationFile("src/main/resources/http.json");
        Configuration conf= ConfigurationManager.getInstance().getCurrentConfiguration();

        try {
            Client client = new Client(conf);
            new LoginGUI(client);
        } catch (IOException e) {
            System.err.println(e);
        }

    }
}
