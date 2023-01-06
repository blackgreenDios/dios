package com.blackgreen.dios.entities.store;

import java.util.Objects;

public class ProductColorEntity {
    private int productIndex;
    private String color;

    public int getProductIndex() {
        return productIndex;
    }

    public ProductColorEntity setProductIndex(int productIndex) {
        this.productIndex = productIndex;
        return this;
    }

    public String getColor() {
        return color;
    }

    public ProductColorEntity setColor(String color) {
        this.color = color;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductColorEntity that = (ProductColorEntity) o;
        return productIndex == that.productIndex && Objects.equals(color, that.color);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productIndex, color);
    }
}
