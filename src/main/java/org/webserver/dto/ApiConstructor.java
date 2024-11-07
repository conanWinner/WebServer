package org.webserver.dto;

public class ApiConstructor<T> {
    private String action;
    private T message;

    public ApiConstructor(String action, T message) {
        this.action = action;
        this.message = message;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public T getMessage() {
        return message;
    }

    public void setMessage(T message) {
        this.message = message;
    }
}
