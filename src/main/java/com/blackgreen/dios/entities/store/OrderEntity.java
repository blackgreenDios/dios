package com.blackgreen.dios.entities.store;

import java.math.BigInteger;
import java.util.Date;
import java.util.Objects;

public class OrderEntity {

    private int index;
    private String userEmail;
    private BigInteger orderNum;
    private int count;
    private int itemIndex;
    private String orderColor;
    private String orderSize;
    private int price;
    private int orderStatus;
    private Date orderDate;

    public int getIndex() {
        return index;
    }

    public OrderEntity setIndex(int index) {
        this.index = index;
        return this;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public OrderEntity setUserEmail(String userEmail) {
        this.userEmail = userEmail;
        return this;
    }

    public BigInteger getOrderNum() {
        return orderNum;
    }

    public OrderEntity setOrderNum(BigInteger orderNum) {
        this.orderNum = orderNum;
        return this;
    }

    public int getCount() {
        return count;
    }

    public OrderEntity setCount(int count) {
        this.count = count;
        return this;
    }

    public int getItemIndex() {
        return itemIndex;
    }

    public OrderEntity setItemIndex(int itemIndex) {
        this.itemIndex = itemIndex;
        return this;
    }

    public String getOrderColor() {
        return orderColor;
    }

    public OrderEntity setOrderColor(String orderColor) {
        this.orderColor = orderColor;
        return this;
    }

    public String getOrderSize() {
        return orderSize;
    }

    public OrderEntity setOrderSize(String orderSize) {
        this.orderSize = orderSize;
        return this;
    }

    public int getPrice() {
        return price;
    }

    public OrderEntity setPrice(int price) {
        this.price = price;
        return this;
    }

    public int getOrderStatus() {
        return orderStatus;
    }

    public OrderEntity setOrderStatus(int orderStatus) {
        this.orderStatus = orderStatus;
        return this;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public OrderEntity setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderEntity that = (OrderEntity) o;
        return index == that.index;
    }

    @Override
    public int hashCode() {
        return Objects.hash(index);
    }
}
