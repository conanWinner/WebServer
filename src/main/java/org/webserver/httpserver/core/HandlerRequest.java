package org.webserver.httpserver.core;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.webserver.common.Constants;
import org.webserver.http.HttpRequest;
import org.webserver.http.HttpStatusCode;
import org.webserver.httpserver.entity.User;
import org.webserver.httpserver.entity.UserUpdate;
import org.webserver.httpserver.exception.AppException;
import org.webserver.httpserver.exception.ErrorCode;
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

//        System.out.println(iduser);
//        for (int i = 0; i < array.length; i++)
//            System.out.println(i + " " + array[i]);
//
//        System.out.println("======================================");
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

        final String body = readBodyContent(request, inputStream);

        //Data with format JSON
        JsonNode json = Json.parse(body);
        System.out.println("json:   " + json);


        //Convert json => User (fullName, email, password, address, phoneNumber)
        User user = Json.fromJson(json, User.class);

        // URL target
        String fileName = request.getRequestTarget();

        ErrorCode errorBody;
        String body1 = "";  //body response
        switch (fileName) {
            // Create user
            case "/api/users":
                // Kiểm tra tồn tại email
                if (UserRepository.existByEmail(user.getEmail())) {
                    errorBody = ErrorCode.EMAIL_EXISTED;
                    AppException.sendConflict(clientOs, errorBody);
                    return;
                } else {
                    boolean checkSaveUser = UserRepository.saveUser(user.getFullName(), user.getPassword(), user.getEmail(), user.getPhoneNumber(), user.getAddress());
                    if (checkSaveUser) {
                        body1 = "{\"message\": \"Success\"}";
                        sendResponse(clientOs, body1);
                        return;
                    } else {
                        errorBody = ErrorCode.ERROR_CREATE_USER;
                        AppException.sendBadRequest(clientOs, errorBody);
                        return;
                    }
                }

                // Login user
            case "/api/login":
//                int iduser = UserRepository.loginUser(user.getEmail(), user.getPassword());
                if(!UserRepository.existByEmail(user.getEmail())){
                    errorBody = ErrorCode.EMAIL_NOT_FOUND;
                    AppException.sendUnauthorized(clientOs, errorBody);
                    return;
                }

                User userR = UserRepository.loginUser(user.getEmail(), user.getPassword());
                Map<String, Object> bodyMap = new HashMap<>();
                if (userR != null) {
                    String userJson = userR.toJson();

                    bodyMap.put("message", "Success");
                    bodyMap.put("result", userJson);
                    body1 = mapper.writeValueAsString(bodyMap);
                    sendResponse(clientOs, body1);
                    return;
                } else {
                    errorBody = ErrorCode.UNAUTHORIZED;
                    AppException.sendUnauthorized(clientOs, errorBody);
                    return;
                }
            case "/api/users/":
//                UserRepository.findById(@Parameter("id"));
                break;

            default:
                break;
        }
    }

    public void handlePutRequest(HttpRequest request, OutputStream clientOs, InputStream inputStream) throws Exception {
        final String body = readBodyContent(request, inputStream);
        //Data with format JSON
        JsonNode json = Json.parse(body);
        System.out.println("json:   " + json);

        //Convert json => UserUpdate (fullName, oldPassword, newPassword, address, phoneNumber)
        UserUpdate userUpdate = Json.fromJson(json, UserUpdate.class);

        // URL target -> /api/users/email%40gmail.com
        String fileName = request.getRequestTarget();
//        int checkResponse = 0;
        ErrorCode errorBody;
        String body1 = "";

//            Handling Update user's information
        String emailUser = fileName.substring("api/users/".length()); // Lấy email từ URL
        emailUser = URLDecoder.decode(emailUser, StandardCharsets.UTF_8).split("/")[1];

        if (!UserRepository.existByEmail(emailUser)) {
            errorBody = ErrorCode.EMAIL_NOT_FOUND;
            AppException.sendNotFound(clientOs, errorBody);
            return;
        } else if (UserRepository.loginUser(emailUser, userUpdate.getOldPassword()) == null) {
            errorBody = ErrorCode.WRONG_PASSWORD;
            AppException.sendUnauthorized(clientOs, errorBody);
            return;
        } else {
            boolean checkResponse = UserRepository.updateUser(userUpdate, emailUser);
            if (checkResponse) {
                body1 = "{\"message\": \"Success\"}";
            } else {
                AppException.sendBadRequest(clientOs, ErrorCode.ERROR_UPDATE_USER);
            }
        }

        sendResponse(clientOs, body1);
    }

    public void handleDeleteRequest(HttpRequest request, OutputStream clientOs, InputStream inputStream) throws Exception {
        final String body = readBodyContent(request, inputStream);
        //Data with format JSON
        JsonNode json = Json.parse(body);
        System.out.println("json:   " + json);

        //Convert json => User (password)
        User user = Json.fromJson(json, User.class);

        // URL target
        String fileName = request.getRequestTarget();
        ErrorCode errorBody;
        String body1 = "";

//            Handling Delete user's information
        String emailUser = fileName.substring("api/users/".length()); // Lấy email từ URL
        emailUser = URLDecoder.decode(emailUser, StandardCharsets.UTF_8).split("/")[1];

        if (!UserRepository.existByEmail(emailUser)) {
            errorBody = ErrorCode.EMAIL_NOT_FOUND;
            AppException.sendNotFound(clientOs, errorBody);
            return;
        }else if (UserRepository.loginUser(emailUser, user.getPassword()) == null) {
            errorBody = ErrorCode.WRONG_PASSWORD;
            AppException.sendUnauthorized(clientOs, errorBody);
            return;
        } else {
            boolean checkResponse = UserRepository.deleteUser(emailUser);
            if (checkResponse) {
                body1 = "{\"message\": \"Success\"}";
            } else {
                AppException.sendBadRequest(clientOs, ErrorCode.ERROR_DELETE_USER);
            }
        }

        sendResponse(clientOs, body1);
    }


    private String readBodyContent(HttpRequest request, InputStream inputStream) throws IOException {
        int remaining = Integer.parseInt(request.getHeader("Content-Length"));
        final ByteArrayOutputStream os = new ByteArrayOutputStream();
        final byte[] buff = new byte[2048];

        while (remaining > 0) {
            int read = inputStream.read(buff, 0, Math.min(remaining, buff.length));
            os.write(buff, 0, read);
            remaining -= read;
        }

        return os.toString();
    }

    public void sendResponse(OutputStream clientOs, String body) throws Exception {
        int statusCode = HttpStatusCode.OK.STATUS_CODE;
        String message = HttpStatusCode.OK.MESSAGE;

        JsonNode jsonNode = mapper.readTree(body);

        // Xây dựng nội dung phản hồi
        String jsonResponse = mapper.writeValueAsString(jsonNode);

        String httpResponse =
                "HTTP/1.1 " + statusCode + " " + message + "\r\n" +
                        "Content-Type: application/json; charset=UTF-8\r\n" +
                        "Content-Length: " + jsonResponse.getBytes(StandardCharsets.UTF_8).length + "\r\n" +
                        "Access-Control-Allow-Origin: *\r\n" +  // Để tránh lỗi CORS
                        "\r\n" +
                        jsonResponse;

        clientOs.write(httpResponse.getBytes(StandardCharsets.UTF_8));
        clientOs.flush();
        clientOs.close();
    }




}
