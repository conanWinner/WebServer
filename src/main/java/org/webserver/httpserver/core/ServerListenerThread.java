package org.webserver.httpserver.core;

import org.webserver.httpserver.gui.ServerGUI;
import org.webserver.httpserver.util.FormatTime;

import javax.net.ssl.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

public class ServerListenerThread extends Thread{

    private int port;
    private String webroot;
    private String localhost;
    private SSLServerSocket   serverSocket;
    private ServerGUI gui;
    public static volatile boolean isRunning = false;

    //set contains number connection of client
    static  Set<InetAddress> set = new HashSet<>();
    public static  Set<String> setBlackList = new HashSet<>();
    private Consumer<Integer> connectionCountCallback;  // Callback cho số lượng kết nối
    private Consumer<String> connectionListCallback;


    public ServerListenerThread(int port, String webroot, String localhost) throws IOException, NoSuchAlgorithmException, KeyStoreException, CertificateException, UnrecoverableKeyException, KeyManagementException {
        this.port = port;
        this.webroot = webroot;
        this.localhost = localhost;

//        Tạo và cấu hình SSLContext
        SSLContext sslContext = SSLContext.getInstance("TLS");
        KeyStore keyStore = KeyStore.getInstance("JKS");

//        Tải KeyStore (chứa chứng chỉ SSL/TLS của mình)
        try (InputStream keyStoreStream = new FileInputStream("keystore.jks")) {
            keyStore.load(keyStoreStream, "0905640692t".toCharArray());
        }
        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
        keyManagerFactory.init(keyStore, "0905640692t".toCharArray());
        sslContext.init(keyManagerFactory.getKeyManagers(), null, null);

//        Tạo SSLServerSocket từ SSLServerSocketFactory
        SSLServerSocketFactory sslServerSocketFactory = sslContext.getServerSocketFactory();
        this.serverSocket = (SSLServerSocket) sslServerSocketFactory.createServerSocket(this.port, 50, InetAddress.getByName(this.localhost));
        this.serverSocket.setEnabledProtocols(new String[]{"TLSv1.2", "TLSv1.3", "TLSv1.1", "TLSv1"});
        this.serverSocket.setEnabledCipherSuites(this.serverSocket.getSupportedCipherSuites());
//        this.serverSocket.setEnabledCipherSuites(new String[]{
//                "TLS_AES_256_GCM_SHA384",
//                "TLS_AES_128_GCM_SHA256",
//                "TLS_CHACHA20_POLY1305_SHA256",
//                "TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384",
//                "TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256",
//                "TLS_RSA_WITH_AES_256_GCM_SHA384",
//                "TLS_RSA_WITH_AES_128_GCM_SHA256"
//        });

        System.out.println("Supported protocols: " + Arrays.toString(this.serverSocket.getSupportedProtocols()));
        System.out.println("Enabled protocols: " + Arrays.toString(this.serverSocket.getEnabledProtocols()));
        System.out.println("Supported cipher suites: " + Arrays.toString(this.serverSocket.getSupportedCipherSuites()));
        System.out.println("Enabled cipher suites: " + Arrays.toString(this.serverSocket.getEnabledCipherSuites()));

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
                SSLSocket socket = (SSLSocket) serverSocket.accept();
                String clientIp = socket.getInetAddress().getHostAddress();
                if (setBlackList.contains(clientIp)) {
                    socket.close(); // Đóng socket
                    continue; // Bỏ qua kết nối
                }

                System.err.println("================>IP:    " + socket.getInetAddress().getHostAddress());
                set.add(socket.getInetAddress());

                updateConnectionCount(set.size());  // Callback
                updateConnectionList(socket.getInetAddress().getHostAddress() + "       " + new FormatTime().getFormattedTime()); // Callback

                System.err.println("================>On time:    " + new FormatTime().getFormattedTime());

                HttpConnectionWorkerThread  httpConnectionWorkerThread = new HttpConnectionWorkerThread(socket);
                httpConnectionWorkerThread.start();
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
