package org.webserver.httpserver.core;

import org.slf4j.Logger;
import org.webserver.common.Constants;
import org.webserver.http.HttpRequest;
import org.webserver.util.FileUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class HandlerRequest {

    public void handleGetRequest(HttpRequest request, OutputStream outputStream) throws IOException {

        String fileName = request.getRequestTarget();

        if (fileName == null || fileName.equals("/")) {
            fileName = "/index.html";
        }

        if (FileUtils.exist(Constants.FILES_DIR)) {
            fileName = Constants.FILES_DIR + fileName;
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
