package org.webserver.core;

import java.net.Socket;

public class WorkerThread extends Thread{
    private Socket socket;

    public WorkerThread(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run(){

    }
}
