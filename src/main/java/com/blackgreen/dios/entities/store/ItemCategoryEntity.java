package com.blackgreen.dios.entities.store;

public class ItemCategoryEntity {
    private String id;
    private String text;

    public String getId() {
        return id;
    }

    public ItemCategoryEntity setId(String id) {
        this.id = id;
        return this;
    }

    public String getText() {
        return text;
    }

    public ItemCategoryEntity setText(String text) {
        this.text = text;
        return this;
    }
}
