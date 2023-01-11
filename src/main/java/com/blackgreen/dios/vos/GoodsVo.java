package com.blackgreen.dios.vos;

import com.blackgreen.dios.entities.store.ItemEntity;

public class GoodsVo extends ItemEntity {
    private String[] colors;

    private String[] sizes;

    private String brandName;

    public String getBrandName() {
        return brandName;
    }

    public GoodsVo setBrandName(String brandName) {
        this.brandName = brandName;
        return this;
    }

    public String[] getColors() {
        return colors;
    }

    public void setColors(String[] colors) {
        this.colors = colors;
    }

    public void setColor(String[] color) {
        this.colors = colors;
    }

    public String[] getSizes() {
        return sizes;
    }

    public void setSizes(String[] sizes) {
        this.sizes = sizes;
    }
}
