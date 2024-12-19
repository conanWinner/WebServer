package org.webserver.httpserver.core;

import org.webserver.httpserver.gui.ServerGUI;
import org.webserver.httpserver.util.FormatTime;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

public class ServerListenerThread extends Thread{

    private int port;
    private String localhost;
    private ServerSocket  serverSocket;
    private ServerGUI gui;
    public static volatile boolean isRunning = false;

    //set contains number connection of client
    static  Set<InetAddress> set = new HashSet<>();
    public static  Set<String> setBlackList = new HashSet<>();
    private Consumer<Integer> connectionCountCallback;  // Callback cho số lượng kết nối
    private Consumer<String> connectionListCallback;


    public ServerListenerThread(int port, String webroot, String localhost) throws IOException {
        this.port = port;
        this.localhost = localhost;
        this.serverSocket = new ServerSocket(this.port, 50, InetAddress.getByName(this.localhost));
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
