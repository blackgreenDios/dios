package com.blackgreen.dios.entities.store;

public class CartEntity {
    private int index;
    private String userEmail;
    private int count;
    private int itemIndex;
    private String orderColor;
    private String orderSize;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
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

    public String getOrderColor() {
        return orderColor;
    }

    public void setOrderColor(String orderColor) {
        this.orderColor = orderColor;
    }

    public String getOrderSize() {
        return orderSize;
    }

    public void setOrderSize(String orderSize) {
        this.orderSize = orderSize;
    }
}
