package org.webserver.httpserver.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.webserver.http.HttpMethod;
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

            // read data from web => saving in lineBuilder
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

            System.out.println("+=====+++++++++++++++  \n" + lineBuilder);

            // parse data from lineBuilder
            InputStream data = new ByteArrayInputStream(
                    lineBuilder.toString().getBytes(
                            StandardCharsets.UTF_8
                    )
            );

            // check format ( parseHttpRequest  )
            HttpRequest request = null;
            try {
                request = httpParser.parseHttpRequest(
                        data
                );
            } catch (HttpParsingException e) {
            }

            //TODO
            HandlerRequest handlerRequest = new HandlerRequest();

            HttpMethod method = request.getMethod();

            if (method == null) {
                throw new RuntimeException("HTTP Method is null in the request");
            }

            System.out.println(request.getRequestTarget());

            if (method.equals(HttpMethod.GET)) {
                handlerRequest.handleGetRequest(request, os);
            } else if (method.equals(HttpMethod.POST)) {
                handlerRequest.handlePostRequest(request, os, is);
            }else if(method.equals(HttpMethod.PUT)){
                handlerRequest.handlePutRequest(request, os, is);
            }else if(method.equals(HttpMethod.DELETE)){
                handlerRequest.handleDeleteRequest(request, os, is);
            }
            else if (request.getMethod().equals(HttpMethod.OPTIONS)) {
                os.write("HTTP/1.1 204 No Content\r\n".getBytes());
                os.write("Access-Control-Allow-Origin: *\r\n".getBytes());
                os.write("Access-Control-Allow-Methods: GET, POST, PUT, DELETE, OPTIONS\r\n".getBytes());
                os.write("Access-Control-Allow-Headers: Content-Type\r\n".getBytes());
                os.write("\r\n".getBytes());
                os.flush();
            }

        }catch (IOException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
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
