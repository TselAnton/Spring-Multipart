package com.tsel.multipart.data;

import java.io.Serializable;
import java.util.Objects;
import org.springframework.http.HttpStatus;

public class ResponseMessage implements Serializable {

    private static final long serialVersionUID = -2536769813622840623L;
    private final HttpStatus status;
    private String message;

    public ResponseMessage(String message) {
        this.status = HttpStatus.OK;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public HttpStatus getStatus() {
        return status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ResponseMessage that = (ResponseMessage) o;
        return status == that.status &&
                message.equals(that.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(status, message);
    }
}
