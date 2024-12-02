package org.webserver.dto.request;

public class ActiveRequest {
    private String subDomain;

    public ActiveRequest() {
    }

    public ActiveRequest(String subDomain) {
        this.subDomain = subDomain;
    }

    public String getSubDomain() {
        return subDomain;
    }

    public void setSubDomain(String subDomain) {
        this.subDomain = subDomain;
    }
}
