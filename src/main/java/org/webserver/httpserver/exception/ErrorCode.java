package org.webserver.httpserver.exception;

public enum ErrorCode {
    EMAIL_EXISTED(409, "Conflict", "Email has already existed"),
    EMAIL_NOT_FOUND(404, "Not Found", "Email not found"),
    ERROR_CREATE_USER(400, "Bad Request", "Occur an error while create a new User"),

    WRONG_PASSWORD(401, "Unauthorized", "Your Old Password was wrong"),
    UNAUTHORIZED(401,"Unauthorized", "Email or password was wrong!"),
    ;

    private int code;
    private String httpStatus;
    private String message;

    ErrorCode(int code, String httpStatus, String s) {
        this.code = code;
        this.httpStatus = httpStatus;
        this.message = s;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getHttpStatus() {
        return httpStatus;
    }

    public void setHttpStatus(String httpStatus) {
        this.httpStatus = httpStatus;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
