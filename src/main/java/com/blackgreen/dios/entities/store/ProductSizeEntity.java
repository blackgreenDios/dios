package com.blackgreen.dios.entities.store;

import java.util.Objects;

public class ProductSizeEntity {
    private String itemName;
    private String size;

    public String getItemName() {
        return itemName;
    }

    public ProductSizeEntity setItemName(String itemName) {
        this.itemName = itemName;
        return this;
    }

    public String getSize() {
        return size;
    }

    public ProductSizeEntity setSize(String size) {
        this.size = size;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductSizeEntity that = (ProductSizeEntity) o;
        return Objects.equals(itemName, that.itemName) && Objects.equals(size, that.size);
    }

    @Override
    public int hashCode() {
        return Objects.hash(itemName, size);
    }
}
