package org.webserver.httpserver.proxy;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class RequestForwarder {

    /**
     * Forward a request to the specified backend server.
     *
     * @param backendUrl URL of the backend server
     * @param method HTTP method (GET, POST, etc.)
     * @param path Path from the original request
     * @param body Body content for POST requests (can be null for GET)
     * @return Response body from the backend server
     * @throws IOException If an error occurs during forwarding
     */
    public String forwardRequest(String backendUrl, String method, String path, String body) throws IOException {
        // Tạo URL đầy đủ từ backend URL và path
        URL url = new URL(backendUrl + path);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod(method);

        // Nếu là POST và có body, gửi body đi
        if ("POST".equalsIgnoreCase(method) && body != null) {
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json"); // Đặt Content-Type nếu cần
            try (OutputStream os = connection.getOutputStream()) {
                os.write(body.getBytes());
                os.flush();
            }
        }

        // Xử lý phản hồi từ backend
        int responseCode = connection.getResponseCode();
        if (responseCode >= 200 && responseCode < 300) {
            // Đọc nội dung phản hồi từ backend server
            try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    response.append(line);
                }
                return response.toString();
            }
        } else {
            // Phản hồi thất bại từ backend
            throw new RuntimeException("Failed to forward request. HTTP code: " + responseCode);
        }
    }
}
