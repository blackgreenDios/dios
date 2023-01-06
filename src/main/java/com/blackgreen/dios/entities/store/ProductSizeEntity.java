package com.blackgreen.dios.entities.store;

import java.util.Objects;

public class ProductSizeEntity {
    private int productIndex;
    private String size;

    public int getProductIndex() {
        return productIndex;
    }

    public ProductSizeEntity setProductIndex(int productIndex) {
        this.productIndex = productIndex;
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
        return productIndex == that.productIndex && Objects.equals(size, that.size);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productIndex, size);
    }
}
