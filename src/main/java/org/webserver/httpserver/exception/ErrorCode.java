package org.webserver.httpserver.exception;

import org.webserver.http.HttpStatusCode;

public enum ErrorCode {
    EMAIL_EXISTED(HttpStatusCode.CLIENT_ERROR_409_CONFLICT, "Email has already existed"),
    USER_NOT_FOUND(HttpStatusCode.CLIENT_ERROR_404_NOT_FOUND, "User not found"),
    ERROR_CREATE_USER(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST, "Occur an error while create a new User"),
    ERROR_DELETE_USER(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST, "Occur an error while delete a user"),
    ERROR_UPDATE_USER(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST, "Occur an error while update a user"),
    WRONG_PASSWORD(HttpStatusCode.CLIENT_ERROR_401_UNAUTHORIZED, "Your Old Password was wrong"),
    UNAUTHORIZED(HttpStatusCode.CLIENT_ERROR_401_UNAUTHORIZED, "Email or password was wrong!"),

    ;

    private HttpStatusCode httpStatusCode;
    private String message;

    ErrorCode(HttpStatusCode httpStatusCode, String s) {
        this.httpStatusCode = httpStatusCode;
        this.message = s;
    }

    public HttpStatusCode getHttpStatusCode() {
        return httpStatusCode;
    }

    public void setHttpStatusCode(HttpStatusCode httpStatusCode) {
        this.httpStatusCode = httpStatusCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
