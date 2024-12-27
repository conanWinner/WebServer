package org.webserver.dto.request;

public class StopRequest {
    private String subDomain;

    public StopRequest() {
    }

    public StopRequest(String subDomain) {
        this.subDomain = subDomain;
    }

    public String getSubDomain() {
        return subDomain;
    }

    public void setSubDomain(String subDomain) {
        this.subDomain = subDomain;
    }
}
