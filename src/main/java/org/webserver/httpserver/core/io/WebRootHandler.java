package org.webserver.httpserver.core.io;


import java.io.InputStream;



public class WebRootHandler {

    private InputStream webRoot;

    public WebRootHandler(String webRootPath) throws WebRootNotFoundException {
        webRoot = getClass().getClassLoader().getResourceAsStream(webRootPath);
        if (webRoot == null){
            throw new WebRootNotFoundException("Webroot provided does not exist or is not a folder");
        }
    }

}

