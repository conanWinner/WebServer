package org.webserver.httpserver.core;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.webserver.common.Constants;
import org.webserver.http.HttpRequest;
import org.webserver.httpserver.entity.User;
import org.webserver.httpserver.entity.UserUpdate;
import org.webserver.httpserver.repository.UserRepository;
import org.webserver.httpserver.util.Json;
import org.webserver.util.FileUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class HandlerRequest {

//    private static final UserRepository userRepository = new UserRepository();
    private static final ObjectMapper mapper = new ObjectMapper();

    public void handleGetRequest(HttpRequest request, OutputStream outputStream) throws IOException {

        String fileName = request.getRequestTarget();

        if (fileName == null || fileName.equals("/")) {
            fileName = "/index.html";
        }


        // ========== format iduser ==========
        String[] array = fileName.toString().split("/");
        int iduser = 0;
        try {
            Integer.parseInt(array[array.length - 1]);
        } catch (NumberFormatException e) {
        }

//        fileName = new StringBuilder("/");
//        if (iduser > 0) {
//            for (int i = 1; i < array.length - 1; i++){
//                fileName.append(array[i]).append("/");
//            }
//        }

        System.out.println(iduser);
        for (int i = 0; i < array.length; i++)
            System.out.println(i + " " + array[i]);

        System.out.println("======================================");
//        ==========================================


        if (FileUtils.exist(Constants.FILES_DIR)) {
            fileName = Constants.FILES_DIR + fileName;
        } else {
            outputStream.write("HTTP/1.1 404 Not Found\r\n\r\n<h1>File not found!</h1>".getBytes(StandardCharsets.UTF_8));
            return;
        }

        // ========= Convert to format 200 OK =============
        final StringBuilder responseMetadata = new StringBuilder();
        responseMetadata.append("HTTP/1.1 200 OK\r\n");
        responseMetadata.append(String.format("Content-Type: %s\r\n", FileUtils.probeContentType(fileName)));

        final InputStream fileStream = FileUtils.getInputStream(fileName);
        if (fileStream != null) {
            responseMetadata.append(String.format("Content-Length: %d\r\n", fileStream.available()));
        }
        responseMetadata.append("\r\n");


        // ===============response to GUI client (just header) ==============
        outputStream.write(responseMetadata.toString().getBytes(StandardCharsets.UTF_8));


        // =============== reponse to GUI client with body content ===========
        try (fileStream) {
            fileStream.transferTo(outputStream);
        }
    }


    public void handlePostRequest(HttpRequest request,
                                  OutputStream clientOs,
                                  InputStream inputStream) throws Exception {
        int remaining = Integer.parseInt(request.getHeader("Content-Length"));
        final ByteArrayOutputStream os = new ByteArrayOutputStream();
        final byte[] buff = new byte[2048];

        while (remaining > 0) {
            int read = inputStream.read(buff, 0, Math.min(remaining, buff.length));
            os.write(buff, 0, read);
            remaining -= read;
//            System.out.println(remaining);
        }

        final String body = os.toString();

        //Data with format JSON
        JsonNode json = Json.parse(body);
        System.out.println("json:   " + json);


        //Convert json => User
        User user = Json.fromJson(json, User.class);

        // URL target
        String fileName = request.getRequestTarget();


        int checkResponse = 0;
        String body1 = "";  //body response
        String result = ""; //result response


        switch (fileName) {
            // Create user
            case "/api/users":
                checkResponse = 1;

                // Kiểm tra tồn tại email
                if(UserRepository.existByEmail(user.getEmail())){
                    body1 = "{\"message\": \"Email has already existed\"}";
                }else{
                    boolean checkSaveUser = UserRepository.saveUser(user.getFullName(), user.getPassword(), user.getEmail(), user.getPhoneNumber(), user.getAddress());
                    if (checkSaveUser) {
                        body1 = "{\"message\": \"Success\"}";
                    }else{
                        body1 = "{\"message\": \"Occur an error while create a new User\"}";
                    }
                }
                break;

            // Login user
            case "/api/login":
                checkResponse = 1;

//                int iduser = UserRepository.loginUser(user.getEmail(), user.getPassword());
                User userR = UserRepository.loginUser(user.getEmail(), user.getPassword());
                Map<String, Object> bodyMap = new HashMap<>();
                if (userR != null) {
                    String userJson = userR.toJson();

                    bodyMap.put("message", "Success");
                    bodyMap.put("result", userJson);
                } else {
                    bodyMap.put("message", "Email or password was wrong!");
                }
                body1 = mapper.writeValueAsString(bodyMap);
                break;

            case "/api/users/":
//                UserRepository.findById(@Parameter("id"));
                break;

            default:
                break;

        }

        if (checkResponse == 0) {
            sendBadRequest(clientOs);
        } else {
            sendResponse(clientOs, body1);
        }
    }

    public void handlePutRequest(HttpRequest request, OutputStream clientOs, InputStream inputStream) throws Exception {
        int remaining = Integer.parseInt(request.getHeader("Content-Length"));
        final ByteArrayOutputStream os = new ByteArrayOutputStream();
        final byte[] buff = new byte[2048];

        while (remaining > 0) {
            int read = inputStream.read(buff, 0, Math.min(remaining, buff.length));
            os.write(buff, 0, read);
            remaining -= read;
        }

        final String body = os.toString();
        //Data with format JSON
        JsonNode json = Json.parse(body);
        System.out.println("json:   " + json);

        //Convert json => UserUpdate
        UserUpdate userUpdate = Json.fromJson(json, UserUpdate.class);

        // URL target -> /api/users/email%40gmail.com
        String fileName = request.getRequestTarget();
        System.out.println();

//        int checkResponse = 0;
        String body1 = "";  //body response

//            Handling Update user's information
        String emailUser = fileName.substring("api/users/".length()); // Lấy email từ URL
        emailUser = URLDecoder.decode(emailUser, StandardCharsets.UTF_8).split("/")[1];
        System.out.println("check email: " + emailUser);
        if(!UserRepository.existByEmail(emailUser)){
            body1 = "{\"message\": \"Email not exist\"}";
//            System.out.println("check email");
        }else if(UserRepository.loginUser(emailUser, userUpdate.getOldPassword()) == null){
            body1 = "{\"message\": \"Your Old Password was wrong\"}";
//            System.out.println("check password");
        }else{
//            boolean checkResponse =  UserRepository.updateUser(userUpdate, emailUser);
//            if(checkResponse){
//                body1 = "{\"message\": \"Success\"}";
//            } else {
//                sendBadRequest(clientOs);
//            }
        }
//        System.out.println("check send bad request");
        sendBadRequest(clientOs);

//        sendResponse(clientOs, body1);
    }


    public void sendResponse(OutputStream clientOs, String body) throws Exception {
        JsonNode jsonNode = mapper.readTree(body);

        // Xây dựng nội dung phản hồi
        String jsonResponse = mapper.writeValueAsString(jsonNode);

        String httpResponse =
                "HTTP/1.1 200 OK\r\n" +
                        "Content-Type: application/json; charset=UTF-8\r\n" +
                        "Content-Length: " + jsonResponse.getBytes(StandardCharsets.UTF_8).length + "\r\n" +
                        "Access-Control-Allow-Origin: *\r\n" +  // Để tránh lỗi CORS
                        "\r\n" +
                        jsonResponse;

        clientOs.write(httpResponse.getBytes(StandardCharsets.UTF_8));
        clientOs.flush();
        clientOs.close();
    }



    private void sendBadRequest(OutputStream clientOs) throws Exception{
        String jsonResponse = "{\"message\":\"Bad Request\"}";
        byte[] responseBytes = jsonResponse.getBytes(StandardCharsets.UTF_8);
        String httpResponse =
                "HTTP/1.1 400 Bad Request\r\n" +
                        "Content-Type: application/json; charset=UTF-8\r\n" +
                        "Content-Length: " + responseBytes.length + "\r\n" +
                        "Access-Control-Allow-Origin: *\r\n" +  // Để tránh lỗi CORS
                        "Access-Control-Allow-Methods: GET, POST, PUT, DELETE, OPTIONS\r\n" +
                        "Access-Control-Allow-Headers: Content-Type, Authorization\r\n" +
                        "\r\n" +
                        jsonResponse;

        clientOs.write(httpResponse.getBytes(StandardCharsets.UTF_8));
        clientOs.flush();
        clientOs.close();
    }

}
