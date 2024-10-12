package org.webserver.httpserver.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.webserver.http.HttpParser;
import org.webserver.http.HttpParsingException;
import org.webserver.http.HttpRequest;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class HttpConnectionWorkerThread extends Thread{

    private Socket socket;

    public HttpConnectionWorkerThread(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {

        InputStream is = null;
        OutputStream os = null;
        HttpParser httpParser = new HttpParser();


        try {
            is = socket.getInputStream();
            os = socket.getOutputStream();

            final StringBuilder lineBuilder = new StringBuilder();
            int b;
            boolean wasNewLine = false;

            while ((b = is.read()) >= 0) {
                if (b == '\r') {

                    int next = is.read();
                    lineBuilder.append((char) b);

                    if (next == '\n') {
                        lineBuilder.append((char) next);
                        if (wasNewLine) {
                            break;
                        }
                        wasNewLine = true;
                    }
                } else {
                    lineBuilder.append((char) b);
                    wasNewLine = false;
                }
            }

            InputStream data = new ByteArrayInputStream(
                    lineBuilder.toString().getBytes(
                            StandardCharsets.UTF_8
                    )
            );

            HttpRequest request = null;
            try {
                request = httpParser.parseHttpRequest(
                        data
                );
            } catch (HttpParsingException e) {
            }

            //TODO
            HandlerRequest handlerRequest = new HandlerRequest();
            if (request != null) {
                handlerRequest.handleGetRequest(request, os);
            }

        }catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if(is != null){
                    is.close();
                }
                if(os != null){
                    os.close();
                }
                if(socket != null) {
                    socket.close();
                }
            } catch (IOException ignored) {}
        }



    }
}
