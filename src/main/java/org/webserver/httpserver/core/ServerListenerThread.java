package org.webserver.httpserver.core;

import org.webserver.httpserver.gui.ServerGUI;
import org.webserver.httpserver.util.FormatTime;

import javax.net.ssl.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyStore;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

public class ServerListenerThread extends Thread{

    private int port;
    private String localhost;
    private boolean isHttps;
    private ServerSocket  serverSocket;
    private ServerGUI gui;
    public static volatile boolean isRunning = false;

    //set contains number connection of client
    static  Set<InetAddress> set = new HashSet<>();
    public static  Set<String> setBlackList = new HashSet<>();
    private Consumer<Integer> connectionCountCallback;  // Callback cho số lượng kết nối
    private Consumer<String> connectionListCallback;


    public ServerListenerThread(int port, String localhost, boolean isHttps) throws IOException {
        this.port = port;
        this.localhost = localhost;
        this.isHttps = isHttps;

        if (isHttps) {
            // Tạo SSLServerSocket cho HTTPS
            this.serverSocket = createSSLServerSocket(443);
        } else {
            // Tạo ServerSocket thông thường cho HTTP
            this.serverSocket = new ServerSocket(this.port, 50, InetAddress.getByName(this.localhost));
        }
    }

    private SSLServerSocket createSSLServerSocket(int port) throws IOException {
        try {
            // Tải Java Keystore (JKS)
            KeyStore keyStore = KeyStore.getInstance("JKS");
            try (FileInputStream fis = new FileInputStream("selfsigned.jks")) {
                keyStore.load(fis, "password".toCharArray()); // "password" là mật khẩu keystore
            }

            KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            kmf.init(keyStore, "password".toCharArray()); // Mật khẩu private key

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(kmf.getKeyManagers(), null, null);

            SSLServerSocketFactory ssf = sslContext.getServerSocketFactory();
            return (SSLServerSocket) ssf.createServerSocket(port);
        } catch (Exception e) {
            throw new IOException("Failed to create SSLServerSocket", e);
        }
    }

    public void setConnectionCountCallback(Consumer<Integer> connectionCountCallback) {
        this.connectionCountCallback = connectionCountCallback;
    }
    public void setConnectionListCallback(Consumer<String> connectionListCallback) {
        this.connectionListCallback = connectionListCallback;
    }

    public static Set<String> getBlackList() {
        return setBlackList;
    }

    @Override
    public void run() {
        try {
            isRunning = true;
            while(isRunning && serverSocket.isBound() && !serverSocket.isClosed() ){
                Socket socket = serverSocket.accept();

                if (isHttps && !(socket instanceof SSLSocket)) {
                    System.err.println("Received non-HTTPS connection on HTTPS port.");
                    socket.close();
                    continue;
                }

                String clientIp = socket.getInetAddress().getHostAddress();
                if (setBlackList.contains(clientIp)) {
                    socket.close(); // Đóng socket
                    continue; // Bỏ qua kết nối
                }

                set.add(socket.getInetAddress());

                updateConnectionCount(set.size());  // Callback
                updateConnectionList(socket.getInetAddress().getHostAddress() + "       " + new FormatTime().getFormattedTime()); // Callback

                HttpConnectionWorkerThread workerThread = new HttpConnectionWorkerThread(socket, isHttps);
                workerThread.start();
            }
        } catch (IOException e) {
            if (isRunning) {
                e.printStackTrace();
            }
            throw new RuntimeException(e);
        }finally {
            closeServerSocket();
        }
    }

    public void stopServer() {
        isRunning = false;
        closeServerSocket();
        this.interrupt();
    }

    private void closeServerSocket() {
        if (serverSocket != null && !serverSocket.isClosed()) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void updateConnectionCount(int connectionCount) {
        if (connectionCountCallback != null) {
            connectionCountCallback.accept(connectionCount);  // Gửi số lượng kết nối mới lên GUI
        }
    }

    private void updateConnectionList(String connection) {
        if (connectionCountCallback != null) {
            connectionListCallback.accept(connection);  // Gửi số lượng kết nối mới lên GUI
        }
    }

}
