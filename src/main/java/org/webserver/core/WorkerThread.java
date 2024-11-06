package org.webserver.core;

import java.io.IOException;
import java.net.Socket;
import java.io.InputStream;
import java.io.OutputStream;
public class WorkerThread extends Thread{
    private Socket socket;
    private InputStream is;
    private OutputStream os;

    public WorkerThread(Socket socket) {
        this.socket = socket;
        InputStream is = null;
        OutputStream os = null;
    }

    @Override
    public void run(){

        String action = "";
        try {
            is = socket.getInputStream();
            os = socket.getOutputStream();
            while (true){
//                action = is.rea
            }
        }catch (Exception e){

        }

    }
}
