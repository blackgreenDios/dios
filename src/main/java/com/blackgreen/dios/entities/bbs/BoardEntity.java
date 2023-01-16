package com.blackgreen.dios.entities.bbs;

import java.util.Objects;

public class BoardEntity {
    private String id;

    private String text;

    private int order;

    public String getId() {
        return id;
    }

    public BoardEntity setId(String id) {
        this.id = id;
        return this;
    }

    public String getText() {
        return text;
    }

    public BoardEntity setText(String text) {
        this.text = text;
        return this;
    }

    public int getOrder() {
        return order;
    }

    public BoardEntity setOrder(int order) {
        this.order = order;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BoardEntity that = (BoardEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
