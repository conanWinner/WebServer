package org.webserver.dto.response;

public class WebServiceResponse {
    private String serviceName;
    private String status;
    private String IPHost;
    private int port;
    private String subDomain;
    private String username;

    public WebServiceResponse() {
    }

    public WebServiceResponse(String serviceName, String status, String IPHost, int port, String subDomain, String username) {
        this.serviceName = serviceName;
        this.status = status;
        this.IPHost = IPHost;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getIPHost() {
        return IPHost;
    }

    public void setIPHost(String IPHost) {
        this.IPHost = IPHost;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
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
