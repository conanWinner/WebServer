package org.webserver.http;

public enum HttpStatusCode {

    /* --- CLIENT ERRORS --- */
    CLIENT_ERROR_400_BAD_REQUEST(400, "Bad Request"),
    CLIENT_ERROR_401_METHOD_NOT_ALLOWED(401, "Method Not Allowed"),
    CLIENT_ERROR_403_FORBIDDEN(403, "Forbidden" ),
    CLIENT_ERROR_404_NOT_FOUND(404, "Not Found" ),
    CLIENT_ERROR_404_NOT_FOUND(404, "Not Found" ),
    CLIENT_ERROR_414_URI_TOO_LONG(414, "URI Too Long"),


    /* --- SERVER ERRORS --- */
    SERVER_ERROR_500_INTERNAL_SERVER_ERROR(500, "Internal Server Error"),
    SERVER_ERROR_501_NOT_IMPLEMENTED(501, "Not Implemented"),
    SERVER_ERROR_505_HTTP_VERSION_NOT_SUPPORTED(505, "Http Version Not Supported"),
    OK(200,"OK" );


    public final int STATUS_CODE;
    public final String MESSAGE;

    HttpStatusCode(int STATUS_CODE, String MESSAGE) {
        this.STATUS_CODE = STATUS_CODE;
        this.MESSAGE = MESSAGE;
    }

}
