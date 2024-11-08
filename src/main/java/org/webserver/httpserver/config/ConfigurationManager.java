package org.webserver.httpserver.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import org.webserver.httpserver.util.Json;

import java.io.*;

public class ConfigurationManager {

    private static ConfigurationManager myConfigurationManager;
    private static Configuration myCurrentConfiguration;

    private ConfigurationManager() {

    }

    public static ConfigurationManager getInstance() {
        if (myConfigurationManager == null) {
            myConfigurationManager = new ConfigurationManager();
        }
        return myConfigurationManager;
    }

    //used to load a config file by file path
    public void loadConfigurationFile(String filePath) {
        StringBuffer sb = new StringBuffer();
        try(InputStream inputStream = getClass().getClassLoader().getResourceAsStream(filePath)){
            if(inputStream != null){
                try(BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))){
                    String line;
                    while((line = reader.readLine()) != null){
                        sb.append(line);
                    }
                }
            }else{
                throw new HttpConfigurationException(new FileNotFoundException("File không tồn tại"));
            }
        }catch (IOException e){
            e.printStackTrace();
        }


        JsonNode conf = null;
        try {
            conf = Json.parse(sb.toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            myCurrentConfiguration = Json.fromJson(conf, Configuration.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    //Returns the current loaded config
    public Configuration getCurrentConfiguration() {
        if (myCurrentConfiguration == null) {
            throw new HttpConfigurationException("No configuration set");
        }
        return myCurrentConfiguration;
    }

}
