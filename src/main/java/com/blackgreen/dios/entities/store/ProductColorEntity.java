package com.blackgreen.dios.entities.store;

import java.util.Objects;

public class ProductColorEntity {
    private String itemName;
    private String color;

    public String getItemName() {
        return itemName;
    }

    public ProductColorEntity setItemName(String itemName) {
        this.itemName = itemName;
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
        return Objects.equals(itemName, that.itemName) && Objects.equals(color, that.color);
    }

    @Override
    public int hashCode() {
        return Objects.hash(itemName, color);
    }
}
