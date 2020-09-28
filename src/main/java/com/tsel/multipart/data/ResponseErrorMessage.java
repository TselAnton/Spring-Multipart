package com.tsel.multipart.data;

import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Objects;
import org.springframework.http.HttpStatus;

public class ResponseErrorMessage implements Serializable {

    private static final long serialVersionUID = -657531813485040916L;

    private HttpStatus status;
    private String exceptionMessage;
    private String[] causeBy;

    public ResponseErrorMessage(HttpStatus status, String exceptionMessage, Throwable causeBy) {
        this.status = status;
        this.exceptionMessage = exceptionMessage;
        this.causeBy = convertThrowableStackTraceToString(causeBy);
    }

    public HttpStatus getStatus() {
        return status;
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }

    public String getExceptionMessage() {
        return exceptionMessage;
    }

    public void setExceptionMessage(String exceptionMessage) {
        this.exceptionMessage = exceptionMessage;
    }

    public String[] getCauseBy() {
        return causeBy;
    }

    public void setCauseBy(String[] causeBy) {
        this.causeBy = causeBy;
    }

    private String[] convertThrowableStackTraceToString(Throwable throwable) {
        if (throwable == null) {
            return new String[0];
        }

        StringWriter writer = new StringWriter();
        throwable.printStackTrace(new PrintWriter(writer));
        return writer.toString().split("\r\n\tat ");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ResponseErrorMessage that = (ResponseErrorMessage) o;
        return status == that.status &&
                exceptionMessage.equals(that.exceptionMessage) &&
                Arrays.equals(causeBy, that.causeBy);
    }

    @Override
    public int hashCode() {
        return Objects.hash(status, exceptionMessage, causeBy);
    }
}
