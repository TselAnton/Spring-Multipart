package com.tsel.multipart.data;

import java.io.Serializable;
import java.util.Objects;

public class FileInfo implements Serializable {

    private static final long serialVersionUID = -1292811746863717000L;

    private String fileName;
    private String size;
    private String createdTime;

    public FileInfo() {
    }

    public FileInfo(String fileName, String size, String createdTime) {
        this.fileName = fileName;
        this.size = size;
        this.createdTime = createdTime;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(String createdTime) {
        this.createdTime = createdTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FileInfo that = (FileInfo) o;
        return fileName.equals(that.fileName) &&
                Objects.equals(size, that.size) &&
                Objects.equals(createdTime, that.createdTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fileName, size, createdTime);
    }
}
