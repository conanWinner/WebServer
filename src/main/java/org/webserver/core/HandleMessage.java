package org.webserver.core;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.webserver.dto.ApiConstructor;
import org.webserver.dto.request.LoginRequest;
import org.webserver.entity.User;
import org.webserver.repository.UserRepository;

import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class HandleMessage {
    private JsonNode messageNode;
    private OutputStream clientOs;
    private ObjectMapper mapper;

    public HandleMessage(JsonNode messageNode, OutputStream clientOs) {
        this.messageNode = messageNode;
        this.clientOs = clientOs;
        this.mapper = new ObjectMapper();
    }

    public void handleLogin() {
        try {
            LoginRequest loginRequest = mapper.treeToValue(messageNode, LoginRequest.class);

            String username = loginRequest.getUsername();
            String password = loginRequest.getPassword();
            User user = UserRepository.loginUser(username, password);

            ApiConstructor<String> api;
            String jsonResponse = "";
            if(user != null){
                api = new ApiConstructor<>("login", "Success");
                jsonResponse = mapper.writeValueAsString(api);
            }else{
                api = new ApiConstructor<>("login", "Login failed");
                jsonResponse = mapper.writeValueAsString(api);
            }
            clientOs.write(jsonResponse.getBytes(StandardCharsets.UTF_8));
            clientOs.flush();
        }catch (JsonProcessingException e){
            System.err.println(e);
        }catch (Exception e){
            System.err.println(e);
        }
    }
}
