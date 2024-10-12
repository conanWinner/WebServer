package org.webserver.http;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class HttpRequest extends HttpMessage{

    private HttpMethod method;
    private String requestTarget;
    private String originalHttpVersion; //from request
    private HttpVersion bestHttpVersion;
    private HashMap<String, String> headers = new HashMap<>();

    HttpRequest() {

    }

    public HttpMethod getMethod() {
        return method;
    }

    public void setMethod(String method) throws HttpParsingException {

        for (HttpMethod method1: HttpMethod.values()) {
            if (method.equals(method1.name())) {
                this.method = method1;
                return;
            }
        }

        throw new HttpParsingException(
                HttpStatusCode.SERVER_ERROR_501_NOT_IMPLEMENTED
        );

    }

    public String getRequestTarget() {
        return requestTarget;
    }

    public void setRequestTarget(String requestTarget) throws HttpParsingException {
        if (requestTarget == null || requestTarget.length() == 0) {
            throw new HttpParsingException(HttpStatusCode.SERVER_ERROR_500_INTERNAL_SERVER_ERROR);
        }
        this.requestTarget = requestTarget;
    }

    public String getHttpVersion() {
        return originalHttpVersion;
    }

    public void setHttpVersion(String originalHttpVersion) throws BadHttpVersionException, HttpParsingException {
        this.originalHttpVersion = originalHttpVersion;
        this.bestHttpVersion = HttpVersion.getBestCompatibleVersion(originalHttpVersion);

        if (this.bestHttpVersion == null) {

            //Unsupported
            throw new HttpParsingException(HttpStatusCode.SERVER_ERROR_505_HTTP_VERSION_NOT_SUPPORTED);

        }

    }

    public HttpVersion getBestHttpVersion() {
        return bestHttpVersion;
    }

    //header
    void addHeader(String key, String value) {

        headers.put(key, value);

    }

    public Set<String> getHeaderNames() {
        return headers.keySet();
    }

    public String getHeader(String headerName){
        return headers.get(headerName);
    }

}
