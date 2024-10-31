package org.webserver.httpserver.exception;

public enum ErrorCode {
    SIGNUP_ERROR(1000, "There has been an error when signing up for an account"),
    EMAIL_EXIST(1001, "Email has already existed"),
    ;
    private int code;
    private String message;

    ErrorCode(int i, String s) {
        this.code = i;
        this.message = s;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
