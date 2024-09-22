package org.example.core;

import org.example.pageResponseHTML.HTML;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class HttpConnectionWorkerThread extends Thread{

    private Socket socket;

    public HttpConnectionWorkerThread(Socket socket) {

        this.socket = socket;

    }

    @Override
    public void run() {

        InputStream is = null;
        OutputStream os = null;

        try {

            is = socket.getInputStream();
            os = socket.getOutputStream();

            final String CODE_HTML = HTML.CODE_HTML;
            final String CRLF = "\r\n";
            final String HTTP_RESPONSE = "HTTP/1.1 200 OK" + CRLF + //starts line
                    "Content-Type: text/html" + CRLF +   //
                    "Content-Length:" + CODE_HTML.getBytes().length + CRLF +   //    header
                    CRLF +
                    CODE_HTML +
                    CRLF + CRLF;
            os.write(HTTP_RESPONSE.getBytes());

        }catch (IOException e) {
            throw new RuntimeException(e);
        }finally {
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
