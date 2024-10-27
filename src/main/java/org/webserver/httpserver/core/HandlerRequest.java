package org.webserver.httpserver.core;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.webserver.common.Constants;
import org.webserver.http.HttpRequest;
import org.webserver.httpserver.entity.User;
import org.webserver.httpserver.repository.UserRepository;
import org.webserver.httpserver.util.Json;
import org.webserver.util.FileUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class HandlerRequest {

    private static final UserRepository userRepository = new UserRepository();
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
        }catch (NumberFormatException e) {
        }

//        fileName = new StringBuilder("/");
//        if (iduser > 0) {
//            for (int i = 1; i < array.length - 1; i++){
//                fileName.append(array[i]).append("/");
//            }
//        }

        System.out.println(iduser);
        for(int i = 0; i < array.length; i++)
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


        // ===============response to GUI client==============
        outputStream.write(responseMetadata.toString().getBytes(StandardCharsets.UTF_8));



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
            System.out.println(remaining);
        }

        final String body = os.toString();

        //Data with format JSON
        System.out.println(Json.parse(body));
        JsonNode json = Json.parse(body);

        //Convert json => User
        User user = Json.fromJson(json, User.class);

        //
        String fileName = request.getRequestTarget();


        int checkResponse = 0;
        String body1 = "";  //body response
        String result = ""; //result response


        switch (fileName) {

            case "/api/users":
                checkResponse = 1;

                UserRepository.saveUser(user.getFullname(), user.getUsername(), user.getPassword(), user.getEmail(), user.getPhonenumber(), user.getAddress());
                body1 = "{\"message\": \"Success\"}";

                break;

            case "/api/login":
                checkResponse = 1;

                int iduser = UserRepository.loginUser(user.getEmail(), user.getPassword());
                Map<String, Object> resultMap = new HashMap<>();
                resultMap.put("iduser", iduser);

                Map<String, Object> bodyMap = new HashMap<>();
                bodyMap.put("message", "Success");
                bodyMap.put("result", resultMap);

                body1 = mapper.writeValueAsString(bodyMap);

                break;

//            case "/api/users/":
//                UserRepository.findById(@Parameter("id"));
//                break;

            default:
                break;

        }

        if (checkResponse == 0) {

            String httpResponse =
                    "HTTP/1.1 200 OK\r\n" +
                            "Content-Type: application/json; charset=UTF-8\r\n" +
                            "Content-Length: " + "{\"message\":\"Error\"}".getBytes(StandardCharsets.UTF_8).length + "\r\n" +
                            "Access-Control-Allow-Origin: *\r\n" +  // Để tránh lỗi CORS
                            "\r\n" +
                            "{\"message\":\"Success\"}";

            clientOs.write(httpResponse.getBytes(StandardCharsets.UTF_8));
            clientOs.flush();
            clientOs.close();

        } else {

            sendResponse(clientOs, body1);

        }



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



}
