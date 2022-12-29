package com.blackgreen.dios.entities.bbs;

import java.util.Objects;

public class ImageEntity {
    private int index;
    private String fileName;
    private String fileMime;
    private byte[] data;

    public int getIndex() {
        return index;
    }

    public ImageEntity setIndex(int index) {
        this.index = index;
        return this;
    }

    public String getFileName() {
        return fileName;
    }

    public ImageEntity setFileName(String fileName) {
        this.fileName = fileName;
        return this;
    }

    public String getFileMime() {
        return fileMime;
    }

    public ImageEntity setFileMime(String fileMime) {
        this.fileMime = fileMime;
        return this;
    }

    public byte[] getData() {
        return data;
    }

    public ImageEntity setData(byte[] data) {
        this.data = data;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ImageEntity that = (ImageEntity) o;
        return index == that.index;
    }

    @Override
    public int hashCode() {
        return Objects.hash(index);
    }
}
