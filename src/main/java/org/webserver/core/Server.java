package org.webserver.core;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private int port;
    private String localhost;
    private ServerSocket serverSocket;

    public Server(int port, String localhost) {
        this.port = port;
        this.localhost = localhost;

        try {
            this.serverSocket = new ServerSocket(this.port, 50, InetAddress.getByName(this.localhost));
            while (serverSocket.isBound() && !serverSocket.isClosed()) {
                Socket socket = serverSocket.accept();
                String clientIP = socket.getInetAddress().getHostAddress();

                System.out.println("client ip: " + clientIP);
                WorkerThread workerThread = new WorkerThread(socket);
                workerThread.start();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
