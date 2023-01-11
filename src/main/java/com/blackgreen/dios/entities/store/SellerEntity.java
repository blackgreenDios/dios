package com.blackgreen.dios.entities.store;

public class SellerEntity {
    private int index;
    private String storeName;
    private String homePage;
    private String contactFirst;
    private String contactSecond;
    private String contactThird;
    private String imageName;
    private String imageMime;
    private byte[] imageData;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getHomePage() {
        return homePage;
    }

    public void setHomePage(String homePage) {
        this.homePage = homePage;
    }

    public String getContactFirst() {
        return contactFirst;
    }

    public void setContactFirst(String contactFirst) {
        this.contactFirst = contactFirst;
    }

    public String getContactSecond() {
        return contactSecond;
    }

    public void setContactSecond(String contactSecond) {
        this.contactSecond = contactSecond;
    }

    public String getContactThird() {
        return contactThird;
    }

    public void setContactThird(String contactThird) {
        this.contactThird = contactThird;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getImageMime() {
        return imageMime;
    }

    public void setImageMime(String imageMime) {
        this.imageMime = imageMime;
    }

    public byte[] getImageData() {
        return imageData;
    }

    public void setImageData(byte[] imageData) {
        this.imageData = imageData;
    }
}
