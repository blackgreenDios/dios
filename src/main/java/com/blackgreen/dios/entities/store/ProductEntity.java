package com.blackgreen.dios.entities.store;

import java.util.Objects;

public class ProductEntity {
    private String name;
    private String categoryId;
    private String brand;
    private int price;
    private byte[] image;
    private String imageType;
    private String detail;

    public String getName() {
        return name;
    }

    public ProductEntity setName(String name) {
        this.name = name;
        return this;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public ProductEntity setCategoryId(String categoryId) {
        this.categoryId = categoryId;
        return this;
    }

    public String getBrand() {
        return brand;
    }

    public ProductEntity setBrand(String brand) {
        this.brand = brand;
        return this;
    }

    public byte[] getImage() {
        return image;
    }

    public ProductEntity setImage(byte[] image) {
        this.image = image;
        return this;
    }

    public String getImageType() {
        return imageType;
    }

    public ProductEntity setImageType(String imageType) {
        this.imageType = imageType;
        return this;
    }

    public String getDetail() {
        return detail;
    }

    public ProductEntity setDetail(String detail) {
        this.detail = detail;
        return this;
    }

    public int getPrice() {
        return price;
    }

    public ProductEntity setPrice(int price) {
        this.price = price;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductEntity that = (ProductEntity) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
