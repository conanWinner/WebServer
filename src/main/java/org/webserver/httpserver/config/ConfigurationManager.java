package org.webserver.httpserver.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import org.webserver.httpserver.util.Json;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ConfigurationManager {

    private static ConfigurationManager instance;
    private Configuration currentConfiguration;

    private ConfigurationManager() {
    }

    // Singleton instance
    public static ConfigurationManager getInstance() {
        if (instance == null) {
            instance = new ConfigurationManager();
        }
        return instance;
    }

    /**
     * Load configuration from a file
     * @param filePath the path to the configuration file
     */
    public void loadConfigurationFile(String filePath) {
        try {
            // Read file content
            String content = Files.readString(Paths.get(filePath));
            // Parse JSON content
            JsonNode jsonNode = Json.parse(content);
            // Map JSON to Configuration object
            currentConfiguration = Json.fromJson(jsonNode, Configuration.class);

            // Validate configuration
            validateConfiguration(currentConfiguration);

        } catch (IOException e) {
            throw new ConfigurationException("Error reading configuration file: " + filePath, e);
        }
//        catch (JsonProcessingException e) {
//            throw new ConfigurationException("Error parsing configuration JSON: " + filePath, e);
//        }
    }

    /**
     * Get the current loaded configuration
     * @return current configuration object
     */
    public Configuration getCurrentConfiguration() {
        if (currentConfiguration == null) {
            throw new ConfigurationException("Configuration not loaded yet!");
        }
        return currentConfiguration;
    }

    /**
     * Reload configuration file
     * @param filePath the path to the configuration file
     */
    public void reloadConfigurationFile(String filePath) {
        loadConfigurationFile(filePath);
    }

    /**
     * Validate the loaded configuration to ensure all necessary fields are present
     * @param configuration the configuration object to validate
     */
    private void validateConfiguration(Configuration configuration) {
        if (configuration.getServer() == null) {
            throw new ConfigurationException("Server configuration is missing in the configuration file.");
        }
        if (configuration.getServer().getLocations() == null || configuration.getServer().getLocations().isEmpty()) {
            throw new ConfigurationException("At least one location must be specified in the configuration file.");
        }
    }

    // Custom exception class for configuration errors
    public static class ConfigurationException extends RuntimeException {
        public ConfigurationException(String message) {
            super(message);
        }

        public ConfigurationException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
