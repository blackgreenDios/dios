package com.blackgreen.dios.entities.store;

import java.util.Objects;

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

    public CartEntity setIndex(int index) {
        this.index = index;
        return this;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public CartEntity setUserEmail(String userEmail) {
        this.userEmail = userEmail;
        return this;
    }

    public int getCount() {
        return count;
    }

    public CartEntity setCount(int count) {
        this.count = count;
        return this;
    }

    public int getItemIndex() {
        return itemIndex;
    }

    public CartEntity setItemIndex(int itemIndex) {
        this.itemIndex = itemIndex;
        return this;
    }

    public String getOrderColor() {
        return orderColor;
    }

    public CartEntity setOrderColor(String orderColor) {
        this.orderColor = orderColor;
        return this;
    }

    public String getOrderSize() {
        return orderSize;
    }

    public CartEntity setOrderSize(String orderSize) {
        this.orderSize = orderSize;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CartEntity that = (CartEntity) o;
        return index == that.index;
    }

    @Override
    public int hashCode() {
        return Objects.hash(index);
    }
}


