package org.webserver.dto.request;

public class CreateWebServiceRequest {
    private String serviceName;
    private String port;
    private String subDomain;
    private String username;

    public CreateWebServiceRequest() {
    }

    public CreateWebServiceRequest(String serviceName, String port, String subDomain, String username) {
        this.serviceName = serviceName;
        this.port = port;
        this.subDomain = subDomain;
        this.username = username;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getSubDomain() {
        return subDomain;
    }

    public void setSubDomain(String subDomain) {
        this.subDomain = subDomain;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
