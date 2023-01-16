package com.blackgreen.dios.entities.store;

import java.math.BigInteger;
import java.util.Date;
import java.util.Objects;

public class OrderEntity {

    private int index;
    private String userEmail;
    private String userName;
    private String userContact;
    private String userAddressPostal;
    private String userAddressPrimary;
    private String userAddressSecondary;
    private int cartIndex;
    private BigInteger orderNum;
    private int count;
    private int itemIndex;
    private String orderColor;
    private String orderSize;
    private int price;
    private String message;
    private String paymentMethod;
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

    public int getCartIndex() {
        return cartIndex;
    }

    public OrderEntity setCartIndex(int cartIndex) {
        this.cartIndex = cartIndex;
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

    public String getMessage() {
        return message;
    }

    public OrderEntity setMessage(String message) {
        this.message = message;
        return this;
    }

    public String getUserName() {
        return userName;
    }

    public OrderEntity setUserName(String userName) {
        this.userName = userName;
        return this;
    }

    public String getUserContact() {
        return userContact;
    }

    public OrderEntity setUserContact(String userContact) {
        this.userContact = userContact;
        return this;
    }

    public String getUserAddressPostal() {
        return userAddressPostal;
    }

    public OrderEntity setUserAddressPostal(String userAddressPostal) {
        this.userAddressPostal = userAddressPostal;
        return this;
    }

    public String getUserAddressPrimary() {
        return userAddressPrimary;
    }

    public OrderEntity setUserAddressPrimary(String userAddressPrimary) {
        this.userAddressPrimary = userAddressPrimary;
        return this;
    }

    public String getUserAddressSecondary() {
        return userAddressSecondary;
    }

    public OrderEntity setUserAddressSecondary(String userAddressSecondary) {
        this.userAddressSecondary = userAddressSecondary;
        return this;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public OrderEntity setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
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
