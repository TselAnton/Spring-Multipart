package com.tsel.multipart.data;

import java.io.InputStream;
import java.util.Objects;

public class UnloadedFile {

    private InputStream stream;
    private String contentType;
    private long contentLength;

    public UnloadedFile(InputStream stream, String contentType, long contentLength) {
        this.stream = stream;
        this.contentType = contentType;
        this.contentLength = contentLength;
    }

    public InputStream getStream() {
        return stream;
    }

    public void setStream(InputStream stream) {
        this.stream = stream;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public long getContentLength() {
        return contentLength;
    }

    public void setContentLength(long contentLength) {
        this.contentLength = contentLength;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        UnloadedFile that = (UnloadedFile) o;
        return contentLength == that.contentLength &&
            stream.equals(that.stream) &&
            contentType.equals(that.contentType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(stream, contentType, contentLength);
    }
}
