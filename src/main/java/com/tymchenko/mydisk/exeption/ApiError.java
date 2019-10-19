package com.tymchenko.mydisk.exeption;

import org.springframework.http.HttpStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class ApiError {
    private HttpStatus status;
    private String nameStatus;

    public String getNameStatus() {
        return nameStatus;
    }

    public void setNameStatus(String nameStatus) {
        this.nameStatus = nameStatus;
    }

    private String message;

    private String debugMessage;
    private String todo;

    public String getTodo() {
        return todo;
    }

    public void setTodo(String todo) {
        this.todo = todo;
    }

    ApiError() {
    }

    ApiError(HttpStatus status) {
        this.status = status;
    }
    public ApiError(HttpStatus status, String nameStatus) {
        this.status = status;
        this.nameStatus = nameStatus;
    }

    ApiError(HttpStatus status, Throwable ex) {
        this.status = status;
        this.message = "Unexpected error";
        this.setDebugMessage(ex.getLocalizedMessage());
    }

    ApiError(HttpStatus status, String message, Throwable ex) {
        this();
        this.status = status;
        this.message = message;
        this.setDebugMessage(ex.getLocalizedMessage());
    }

    public HttpStatus getStatus() {
        return status;
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDebugMessage() {
        return debugMessage;
    }

    public void setDebugMessage(String debugMessage) {
        this.debugMessage = debugMessage;
    }
}
