package org.webserver.dto.request;

public class WebServiceRequest {
    private String username;

    public WebServiceRequest() {
    }

    public WebServiceRequest(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
