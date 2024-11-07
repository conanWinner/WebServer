package org.webserver.core;

import org.webserver.config.Configuration;

import java.io.*;
import java.net.Socket;

public class Client {
    private Configuration conf;
    private Socket socket;
    private InputStream in;
    private OutputStream out;
    private String username;

    public Client(Configuration conf) throws IOException {
        this.conf = conf;
        this.socket = new Socket(this.conf.getLocalhost(), this.conf.getPort());
        this.in = this.socket.getInputStream();
        this.out = this.socket.getOutputStream();
    }

    public Socket getSocket() {
        return socket;
    }


    public InputStream getIn() {
        return in;
    }


    public OutputStream getOut() {
        return out;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }


}
