package com.tsel.multipart.exception;

import org.springframework.http.HttpStatus;

public class MyWebException extends RuntimeException {

    private final HttpStatus errorStatus;

    public MyWebException(HttpStatus errorStatus, String message, Throwable exception) {
        super(message, exception);
        this.errorStatus = errorStatus;
    }

    public static MyWebException throwEx(HttpStatus errorStatus, String message, Throwable exception) {
        return new MyWebException(errorStatus, message, exception);
    }

    public static MyWebException throwEx(HttpStatus errorStatus, String message) {
        return new MyWebException(errorStatus, message, null);
    }

    public HttpStatus getErrorStatus() {
        return errorStatus;
    }
}
