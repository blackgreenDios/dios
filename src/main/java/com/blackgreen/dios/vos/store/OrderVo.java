package com.blackgreen.dios.vos.store;

import com.blackgreen.dios.entities.store.OrderEntity;

public class OrderVo extends OrderEntity {

    private String itemName;
    private byte[] image;
    private String imageMime;
    private String statusText;

    private String storeName;

    public String getStoreName() {
        return storeName;
    }

    public OrderVo setStoreName(String storeName) {
        this.storeName = storeName;
        return this;
    }

    public String getItemName() {
        return itemName;
    }

    public OrderVo setItemName(String itemName) {
        this.itemName = itemName;
        return this;
    }

    public byte[] getImage() {
        return image;
    }

    public OrderVo setImage(byte[] image) {
        this.image = image;
        return this;
    }

    public String getImageMime() {
        return imageMime;
    }

    public OrderVo setImageMime(String imageMime) {
        this.imageMime = imageMime;
        return this;
    }

    public String getStatusText() {
        return statusText;
    }

    public OrderVo setStatusText(String statusText) {
        this.statusText = statusText;
        return this;
    }
}
