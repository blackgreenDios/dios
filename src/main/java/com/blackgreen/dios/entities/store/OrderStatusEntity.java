package com.blackgreen.dios.entities.store;

import java.util.Objects;

public class OrderStatusEntity {
    private int index;
    private String status;

    public int getIndex() {
        return index;
    }

    public OrderStatusEntity setIndex(int index) {
        this.index = index;
        return this;
    }

    public String getStatus() {
        return status;
    }

    public OrderStatusEntity setStatus(String status) {
        this.status = status;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderStatusEntity that = (OrderStatusEntity) o;
        return index == that.index;
    }

    @Override
    public int hashCode() {
        return Objects.hash(index);
    }
}
