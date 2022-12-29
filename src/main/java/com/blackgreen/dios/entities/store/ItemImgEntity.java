package com.blackgreen.dios.entities.store;

public class ItemImgEntity {
    private int index;
    private String fileName;
    private String fileMime;
    private byte[] data;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileMime() {
        return fileMime;
    }

    public void setFileMime(String fileMime) {
        this.fileMime = fileMime;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}
