package com.blackgreen.dios.vos;

import com.blackgreen.dios.entities.store.ItemEntity;

public class GoodsVo extends ItemEntity {
    private String[] colors;

    private String[] sizes;


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
//    public String getShoesSize() {
//        return shoesSize;
//    }
//
//    public void setShoesSize(String shoesSize) {
//        this.shoesSize = shoesSize;
//    }
//
//    public String getClothesSize() {
//        return clothesSize;
//    }
//
//    public void setClothesSize(String clothesSize) {
//        this.clothesSize = clothesSize;
//    }
}
