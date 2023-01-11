package com.blackgreen.dios.entities.store;

import java.util.Date;

public class ItemEntity {
    private int index;
    private String categoryId;
    private int sellerIndex;
    private String itemName;
    private String itemDetail;
    private int price;
    private int count;
    private Date createdOn;
    private String titleImageName;
    private String titleImageMime;
    private byte[] titleImageData;


    public String getTitleImageName() {
        return titleImageName;
    }

    public void setTitleImageName(String titleImageName) {
        this.titleImageName = titleImageName;
    }

    public String getTitleImageMime() {
        return titleImageMime;
    }

    public void setTitleImageMime(String titleImageMime) {
        this.titleImageMime = titleImageMime;
    }

    public byte[] getTitleImageData() {
        return titleImageData;
    }

    public void setTitleImageData(byte[] titleImageData) {
        this.titleImageData = titleImageData;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public int getSellerIndex() {
        return sellerIndex;
    }

    public void setSellerIndex(int sellerIndex) {
        this.sellerIndex = sellerIndex;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemDetail() {
        return itemDetail;
    }

    public void setItemDetail(String itemDetail) {
        this.itemDetail = itemDetail;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }
}
