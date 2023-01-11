package com.blackgreen.dios.vos.store;

import com.blackgreen.dios.entities.store.OrderEntity;

public class OrderVo extends OrderEntity {

    private String itemName;
    private byte[] image;
    private String imageMime;

    private String status;

    private String storeName;

    private String priceCnt;


    public String getPriceCnt() {
        return priceCnt;
    }

    public void setPriceCnt(String priceCnt) {
        this.priceCnt = priceCnt;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public String getImageMime() {
        return imageMime;
    }

    public void setImageMime(String imageMime) {
        this.imageMime = imageMime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }
}
