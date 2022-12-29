package com.blackgreen.dios.entities.store;

public class OrderItemEntity {
    private int index;
    private int count;
    private int itemIndex;
    private int itemPrice;
    private String id;
    private String clotheSizeId;
    private String shoesSizeId;
    private int orderNum;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getClotheSizeId() {
        return clotheSizeId;
    }

    public void setClotheSizeId(String clotheSizeId) {
        this.clotheSizeId = clotheSizeId;
    }

    public String getShoesSizeId() {
        return shoesSizeId;
    }

    public void setShoesSizeId(String shoesSizeId) {
        this.shoesSizeId = shoesSizeId;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getItemIndex() {
        return itemIndex;
    }

    public void setItemIndex(int itemIndex) {
        this.itemIndex = itemIndex;
    }

    public int getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(int itemPrice) {
        this.itemPrice = itemPrice;
    }

    public int getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(int orderNum) {
        this.orderNum = orderNum;
    }
}
