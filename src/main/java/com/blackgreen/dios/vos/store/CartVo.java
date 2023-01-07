package com.blackgreen.dios.vos.store;

import com.blackgreen.dios.entities.store.CartEntity;

public class CartVo extends CartEntity {

    private String itemName;
    private int price;
    private byte[] image;
    private String imageMime;

    public String getItemName() {
        return itemName;
    }

    public CartVo setItemName(String itemName) {
        this.itemName = itemName;
        return this;
    }

    public int getPrice() {
        return price;
    }

    public CartVo setPrice(int price) {
        this.price = price;
        return this;
    }

    public byte[] getImage() {
        return image;
    }

    public CartVo setImage(byte[] image) {
        this.image = image;
        return this;
    }

    public String getImageMine() {
        return imageMime;
    }

    public CartVo setImageMine(String imageMine) {
        this.imageMime = imageMine;
        return this;
    }
}
