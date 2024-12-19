package org.webserver.httpserver.core;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.webserver.http.HttpRequest;
import org.webserver.http.HttpStatusCode;
import org.webserver.httpserver.config.Configuration;
import org.webserver.httpserver.config.ConfigurationManager;
import org.webserver.httpserver.entity.User;
import org.webserver.httpserver.entity.UserUpdate;
import org.webserver.httpserver.exception.ErrorCode;
import org.webserver.httpserver.proxy.RequestForwarder;
import org.webserver.httpserver.repository.UserRepository;
import org.webserver.httpserver.util.Json;
import org.webserver.util.FileUtils;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class HandlerRequest {

    private static final ObjectMapper mapper = new ObjectMapper();
    private static final Configuration config = ConfigurationManager.getInstance().getCurrentConfiguration();

    // =================  Load balancing  ================
    public void handleRequest(HttpRequest request, OutputStream outputStream) throws IOException {
        LoadBalancer loadBalancer = new LoadBalancer(Arrays.asList(
                "http://localhost:8081",
                "http://localhost:8082",
                "http://localhost:8083"
        ));

        // Chọn backend server
        String backendServer = loadBalancer.getNextServer();

        // Sử dụng RequestForwarder để chuyển tiếp yêu cầu
        RequestForwarder forwarder = new RequestForwarder();
        String responseBody;

        try {
            // Phân biệt phương thức HTTP
            if ("GET".equalsIgnoreCase(request.getMethod().name())) {
                responseBody = forwarder.forwardRequest(backendServer, "GET", request.getRequestTarget(), null);
            } else if ("POST".equalsIgnoreCase(request.getMethod().name())) {
                responseBody = forwarder.forwardRequest(backendServer, "POST", request.getRequestTarget(), null); //null = body
            } else {
                // Không hỗ trợ các phương thức khác
                outputStream.write("HTTP/1.1 405 Method Not Allowed\r\n\r\n<h1>Method Not Allowed</h1>".getBytes());
                return;
            }

            // Trả phản hồi về client
            outputStream.write("HTTP/1.1 200 OK\r\n\r\n".getBytes());
            outputStream.write(responseBody.getBytes());
        } catch (Exception e) {
            // Xử lý lỗi nếu backend server gặp vấn đề
            outputStream.write("HTTP/1.1 500 Internal Server Error\r\n\r\n<h1>Internal Server Error</h1>".getBytes());
        }
    }

    public void handleGetRequest(HttpRequest request, OutputStream outputStream) throws IOException {
        String requestTarget = request.getRequestTarget();

        if (requestTarget.startsWith("/api/getAllUsers")) {
            // Phân tích query string
            String[] parts = requestTarget.split("\\?");
            String path = parts[0]; // "/users"
            String queryString = parts.length > 1 ? parts[1] : "";
            Map<String, String> queryParams = parseQueryString(queryString);

            // Giả lập dữ liệu người dùng
            Map<String, Map<String, String>> fakeUserData = new HashMap<>();
            {
                UserRepository.getAllUsers().forEach(user ->{
                    Map<String, String> user1 = new HashMap<>();
                    user1.put("iduser", user.getIduser());
                    user1.put("fullname", user.getFullName());
                    user1.put("email", user.getEmail());
                    user1.put("phonenumber", user.getPhoneNumber());
                    user1.put("address", user.getAddress());
                    fakeUserData.put(user.getIduser(), user1);
                });
            }

            String userId = queryParams.get("iduser");
            if (userId != null && fakeUserData.containsKey(userId)) {
                // Trả về thông tin 1 user
                Map<String, String> userData = fakeUserData.get(userId);
                String jsonResponse = "{\n" +
                        "  \"id\": \"" + userData.get("iduser") + "\",\n" +
                        "  \"fullname\": \"" + userData.get("fullname") + "\",\n" +
                        "  \"email\": \"" + userData.get("email") + "\",\n" +
                        "  \"phonenumber\": \"" + userData.get("phonenumber") + "\",\n" +
                        "  \"address\": \"" + userData.get("address") + "\"\n" +
                        "}";

                StringBuilder responseMetadata = new StringBuilder();
                responseMetadata.append("HTTP/1.1 200 OK\r\n");
                responseMetadata.append("Content-Type: application/json\r\n");
                responseMetadata.append("Content-Length: ").append(jsonResponse.getBytes(StandardCharsets.UTF_8).length).append("\r\n");
                responseMetadata.append("\r\n");

                outputStream.write(responseMetadata.toString().getBytes(StandardCharsets.UTF_8));
                outputStream.write(jsonResponse.getBytes(StandardCharsets.UTF_8));
            } else {
                // Không có id hoặc không tìm thấy userId trong dữ liệu,
                // ta sẽ trả về tất cả người dùng
                StringBuilder jsonResponse = new StringBuilder();
                jsonResponse.append("[\n");
                int count = 0;
                for (Map<String, String> user : fakeUserData.values()) {
                    if (count > 0) jsonResponse.append(",\n");
                    jsonResponse.append("  {\n")
                            .append("    \"iduser\": \"").append(user.get("iduser")).append("\",\n")
                            .append("    \"fullname\": \"").append(user.get("fullname")).append("\",\n")
                            .append("    \"email\": \"").append(user.get("email")).append("\",\n")
                            .append("    \"phonenumber\": \"").append(user.get("phonenumber")).append("\",\n")
                            .append("    \"address\": \"").append(user.get("address")).append("\"\n")
                            .append("  }");
                    count++;
                }
                jsonResponse.append("\n]");

                String response = jsonResponse.toString();
                StringBuilder responseMetadata = new StringBuilder();
                responseMetadata.append("HTTP/1.1 200 OK\r\n");
                responseMetadata.append("Content-Type: application/json\r\n");
                responseMetadata.append("Content-Length: ").append(response.getBytes(StandardCharsets.UTF_8).length).append("\r\n");
                responseMetadata.append("\r\n");

                outputStream.write(responseMetadata.toString().getBytes(StandardCharsets.UTF_8));
                outputStream.write(response.getBytes(StandardCharsets.UTF_8));
            }
        } else {
            // Code xử lý file tĩnh như cũ
            String fileName = requestTarget;
            String path = "";
            String root = "";
            String index = "";

            for (Configuration.Location location : config.getServer().getLocations()) {
                if (location.getRoot() != null) {
                    path = location.getPath();
                    root = location.getRoot();
                    index = location.getIndex();
                }
            }

            if (fileName == null || fileName.equals(path)) {
                fileName = "/" + index;
            }

            if (FileUtils.exist(root)) {
                fileName = root + fileName;
            } else {
                outputStream.write("HTTP/1.1 404 Not Found\r\n\r\n<h1>File not found!</h1>".getBytes(StandardCharsets.UTF_8));
                return;
            }

            final StringBuilder responseMetadata = new StringBuilder();
            responseMetadata.append("HTTP/1.1 200 OK\r\n");
            responseMetadata.append(String.format("Content-Type: %s\r\n", FileUtils.probeContentType(fileName)));

            final InputStream fileStream = FileUtils.getInputStream(fileName);
            if (fileStream != null) {
                responseMetadata.append(String.format("Content-Length: %d\r\n", fileStream.available()));
            }
            responseMetadata.append("\r\n");

            outputStream.write(responseMetadata.toString().getBytes(StandardCharsets.UTF_8));

            try (fileStream) {
                fileStream.transferTo(outputStream);
            }
        }
    }

    private Map<String, String> parseQueryString(String query) {
        Map<String, String> queryPairs = new HashMap<>();
        if (query == null || query.trim().isEmpty()) {
            return queryPairs;
        }

        String[] pairs = query.split("&");
        for (String pair : pairs) {
            int idx = pair.indexOf("=");
            if (idx > 0 && idx < pair.length() - 1) {
                String key = pair.substring(0, idx);
                String value = pair.substring(idx + 1);
                queryPairs.put(key, value);
            }
        }
        return queryPairs;
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
                if (UserRepository.existByIduser(user.getIduser())) {
                    errorBody = ErrorCode.EMAIL_EXISTED;
                    sendConflict(clientOs, errorBody);
                    return;
                } else {
                    boolean checkSaveUser = UserRepository.saveUser(user.getFullName(), user.getPassword(), user.getEmail(), user.getPhoneNumber(), user.getAddress());
                    if (checkSaveUser) {
                        body1 = "{\"message\": \"Success\"}";
                        sendResponse(clientOs, body1);
                        return;
                    } else {
                        errorBody = ErrorCode.ERROR_CREATE_USER;
                        sendBadRequest(clientOs, errorBody);
                        return;
                    }
                }

                // Login user
            case "/api/login":
//                int iduser = UserRepository.loginUser(user.getEmail(), user.getPassword());
                if(!UserRepository.existByIduser(user.getIduser())){
                    errorBody = ErrorCode.USER_NOT_FOUND;
                    sendUnauthorized(clientOs, errorBody);
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
                    sendUnauthorized(clientOs, errorBody);
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
        JsonNode json = Json.parse(body);
        System.out.println("json:   " + json);

        UserUpdate userUpdate = Json.fromJson(json, UserUpdate.class);

        // URL target -> /api/users/iduser
        String fileName = request.getRequestTarget();
        ErrorCode errorBody;
        String body1 = "";

//            Handling Update user's information
        String iduserStr = fileName.substring("/api/updateUser/".length());

        if (!UserRepository.existByIduser(iduserStr)) {
            errorBody = ErrorCode.USER_NOT_FOUND;
            sendNotFound(clientOs, errorBody);
            return;
        }

        if (UserRepository.updateUser(userUpdate, iduserStr)) {
            body1 = "{\"message\": \"Success\"}";
        } else {
            sendBadRequest(clientOs, ErrorCode.ERROR_UPDATE_USER);
        }

        sendResponse(clientOs, body1);
    }

    public void handleDeleteRequest(HttpRequest request, OutputStream clientOs, InputStream inputStream) throws Exception {

        // URL target
        String fileName = request.getRequestTarget();
        String iduserStr = fileName.substring("/api/users/".length());
        int iduser = Integer.parseInt(URLDecoder.decode(iduserStr, StandardCharsets.UTF_8));
        ErrorCode errorBody;

// Kiểm tra iduser
        if (!UserRepository.existByIduser(iduserStr)) {
            errorBody = ErrorCode.USER_NOT_FOUND;
            sendNotFound(clientOs, errorBody);
            return;
        }

// Thực hiện xóa
        if (!UserRepository.deleteUser(iduser)) {
            errorBody = ErrorCode.ERROR_DELETE_USER;
            sendBadRequest(clientOs, errorBody);
            return;
        }

// Gửi phản hồi thành công
        String responseBody = "{\"message\": \"Success\"}";
        sendResponse(clientOs, responseBody);

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

    private void sendBadRequest(OutputStream clientOs, ErrorCode errorCode) throws Exception {
        int statusCode = errorCode.getHttpStatusCode().STATUS_CODE;
        String messageHttp = errorCode.getHttpStatusCode().MESSAGE;

        String jsonResponse = "{\"message\": " + "\"" + errorCode.getMessage() + "\"}";
        byte[] responseBytes = jsonResponse.getBytes(StandardCharsets.UTF_8);
        String httpResponse =
                "HTTP/1.1 " + statusCode + " " + messageHttp + "\r\n" +
                        "Content-Type: application/json; charset=UTF-8\r\n" +
                        "Content-Length: " + responseBytes.length + "\r\n" +
                        "Access-Control-Allow-Origin: *\r\n" +  // Để tránh lỗi CORS
                        "Access-Control-Allow-Methods: GET, POST, PUT, DELETE, OPTIONS\r\n" +
                        "Access-Control-Allow-Headers: Content-Type, Authorization\r\n" +
                        "\r\n" +
                        jsonResponse;
        System.out.println("Send bad request: " + httpResponse);
        clientOs.write(httpResponse.getBytes(StandardCharsets.UTF_8));
        clientOs.flush();
        clientOs.close();
    }

    private void sendUnauthorized(OutputStream clientOs, ErrorCode errorCode) throws Exception {
        int statusCode = errorCode.getHttpStatusCode().STATUS_CODE;
        String messageHttp = errorCode.getHttpStatusCode().MESSAGE;

        String jsonResponse = "{\"message\": " + "\"" + errorCode.getMessage() + "\"}";
        byte[] responseBytes = jsonResponse.getBytes(StandardCharsets.UTF_8);
        String httpResponse =
                "HTTP/1.1 " + statusCode + " " + messageHttp + "\r\n" +
                        "Content-Type: application/json; charset=UTF-8\r\n" +
                        "Content-Length: " + responseBytes.length + "\r\n" +
                        "Access-Control-Allow-Origin: *\r\n" +  // Để tránh lỗi CORS
                        "Access-Control-Allow-Methods: GET, POST, PUT, DELETE, OPTIONS\r\n" +
                        "Access-Control-Allow-Headers: Content-Type, Authorization\r\n" +
                        "\r\n" +
                        jsonResponse;
        System.out.println("Send Unauthorized: " + httpResponse);
        clientOs.write(httpResponse.getBytes(StandardCharsets.UTF_8));
        clientOs.flush();
        clientOs.close();
    }

    private void sendNotFound(OutputStream clientOs, ErrorCode errorCode) throws Exception {
        int statusCode = errorCode.getHttpStatusCode().STATUS_CODE;
        String messageHttp = errorCode.getHttpStatusCode().MESSAGE;

        String jsonResponse = "{\"message\": " + "\"" + errorCode.getMessage() + "\"}";
        byte[] responseBytes = jsonResponse.getBytes(StandardCharsets.UTF_8);
        String httpResponse =
                "HTTP/1.1 " + statusCode + " " + messageHttp + "\r\n" +
                        "Content-Type: application/json; charset=UTF-8\r\n" +
                        "Content-Length: " + responseBytes.length + "\r\n" +
                        "Access-Control-Allow-Origin: *\r\n" +  // Để tránh lỗi CORS
                        "Access-Control-Allow-Methods: GET, POST, PUT, DELETE, OPTIONS\r\n" +
                        "Access-Control-Allow-Headers: Content-Type, Authorization\r\n" +
                        "\r\n" +
                        jsonResponse;

        System.out.println("Send not found: " + httpResponse);
        clientOs.write(httpResponse.getBytes(StandardCharsets.UTF_8));
        clientOs.flush();
        clientOs.close();
    }

    private void sendConflict(OutputStream clientOs, ErrorCode errorCode) throws Exception {
        int statusCode = errorCode.getHttpStatusCode().STATUS_CODE;
        String messageHttp = errorCode.getHttpStatusCode().MESSAGE;

        String jsonResponse = "{\"message\": " + "\"" + errorCode.getMessage() + "\"}";
        byte[] responseBytes = jsonResponse.getBytes(StandardCharsets.UTF_8);
        String httpResponse =
                "HTTP/1.1 " + statusCode + " " + messageHttp + "\r\n" +
                        "Content-Type: application/json; charset=UTF-8\r\n" +
                        "Content-Length: " + responseBytes.length + "\r\n" +
                        "Access-Control-Allow-Origin: *\r\n" +  // Để tránh lỗi CORS
                        "Access-Control-Allow-Methods: GET, POST, PUT, DELETE, OPTIONS\r\n" +
                        "Access-Control-Allow-Headers: Content-Type, Authorization\r\n" +
                        "\r\n" +
                        jsonResponse;

        System.out.println("Send conflict: " + httpResponse);
        clientOs.write(httpResponse.getBytes(StandardCharsets.UTF_8));
        clientOs.flush();
        clientOs.close();
    }


}
