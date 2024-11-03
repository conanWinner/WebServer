package org.webserver.httpserver.core;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerListenerHttp extends Thread{
    private final int httpPort;
    private final int httpsPort;
    private final String localhost;

    public ServerListenerHttp(int httpPort, int httpsPort, String localhost){
        this.httpPort = httpPort;
        this.httpsPort = httpsPort;
        this.localhost = localhost;
    }

    public void run(){
        System.out.println("HTTP Redirect Server đang chạy trên cổng " + httpPort);
        try (ServerSocket serverSocket = new ServerSocket(httpPort)) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                OutputStream os = clientSocket.getOutputStream();

                // Xây dựng phản hồi chuyển hướng
                String httpsUrl = "https://" + localhost + ":" + httpsPort + "/";
                String httpResponse =
                        "HTTP/1.1 301 Moved Permanently\r\n" +
                                "Location: " + httpsUrl + "\r\n" +
                                "Content-Length: 0\r\n" +
                                "Connection: close\r\n" +
                                "\r\n";

                os.write(httpResponse.getBytes());
                os.flush();
                clientSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
