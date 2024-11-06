package org.webserver;

import org.webserver.config.Configuration;
import org.webserver.config.ConfigurationManager;
import org.webserver.core.Server;

public class MainHostingServer {
    public static void main(String [] args){
        ConfigurationManager.getInstance().loadConfigurationFile("src/main/resources/http.json");
        Configuration configuration = ConfigurationManager.getInstance().getCurrentConfiguration();

        new Server(configuration.getPort(), configuration.getLocalhost());
    }
}
