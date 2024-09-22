package org.example.core;

import org.example.gui.ServerGUI;
import org.example.util.FormatTime;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

public class ServerListenerThread extends Thread{

    private int port;
    private String webroot;
    private String localhost;
    private ServerSocket  serverSocket;
    private ServerGUI gui;

    //set contains number connection of client
    static  Set<InetAddress> set = new HashSet<>();
    public static  Set<String> setBlackList = new HashSet<>();
    private Consumer<Integer> connectionCountCallback;  // Callback cho số lượng kết nối
    private Consumer<String> connectionListCallback;


    public ServerListenerThread(int port, String webroot, String localhost) throws IOException {
        this.port = port;
        this.webroot = webroot;
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
            while( serverSocket.isBound() && !serverSocket.isClosed() ){
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
            throw new RuntimeException(e);
        }finally {
            if(serverSocket != null){
                try {
                    serverSocket.close();
                } catch (IOException e) {
                }
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
