package org.webserver.httpserver.exception;

import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class AppException {
    public static void sendBadRequest(OutputStream clientOs, ErrorCode errorCode) throws Exception {
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

    public static void sendUnauthorized(OutputStream clientOs, ErrorCode errorCode) throws Exception {
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

    public static void sendNotFound(OutputStream clientOs, ErrorCode errorCode) throws Exception {
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

    public static void sendConflict(OutputStream clientOs, ErrorCode errorCode) throws Exception {
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
