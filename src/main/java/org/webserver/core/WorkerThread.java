package org.webserver.core;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class WorkerThread extends Thread {
    private Socket socket;
    private InputStream is;
    private OutputStream os;
    private ObjectMapper mapper;

    public WorkerThread(Socket socket) {
        this.socket = socket;
        this.mapper = new ObjectMapper();
    }

    @Override
    public void run() {
        try {
            is = socket.getInputStream();
            os = socket.getOutputStream();

            while (true) {
                //            Đọc dữ lieu
                byte[] buffer = new byte[1024];
                int bytesRead = is.read(buffer);
                String jsonRequest = new String(buffer, 0, bytesRead, StandardCharsets.UTF_8);
                System.out.println("json: " + jsonRequest);

//           JSON => Api
                JsonNode rootNode = mapper.readTree(jsonRequest);
                String action = rootNode.get("action").asText();
                JsonNode messageNode = rootNode.get("message");

                HandleMessage handleMessage = null;
                if (messageNode != null) {
                    handleMessage = new HandleMessage(messageNode, os);
                } else {
                    System.out.println("message null");
                }

                switch (action) {
                    case "login":
                        if (handleMessage != null) handleMessage.handleLogin();
                        break;
                    case "get all webservices":
                        if (handleMessage != null) handleMessage.handleGetAllWebServices();
                        break;
                    case "create webservice":
                        if (handleMessage != null) handleMessage.handleLogin();
                        break;
                    case "update webservice":
                        if (handleMessage != null) handleMessage.handleLogin();
                        break;
                }
            }

        } catch (Exception e) {

        }

    }
}
