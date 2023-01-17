package com.blackgreen.dios.entities.record;

import java.util.Date;
import java.util.Objects;

public class ElementEntity {

    private String userEmail;
    private byte[] image;
    private String imageType;
    private String diary;
    private String add;
    private Date todayDate;

    private int index;

    public String getUserEmail() {
        return userEmail;
    }

    public ElementEntity setUserEmail(String userEmail) {
        this.userEmail = userEmail;
        return this;
    }

    public byte[] getImage() {
        return image;
    }

    public ElementEntity setImage(byte[] image) {
        this.image = image;
        return this;
    }

    public String getImageType() {
        return imageType;
    }

    public ElementEntity setImageType(String imageType) {
        this.imageType = imageType;
        return this;
    }

    public String getDiary() {
        return diary;
    }

    public ElementEntity setDiary(String diary) {
        this.diary = diary;
        return this;
    }

    public String getAdd() {
        return add;
    }

    public ElementEntity setAdd(String add) {
        this.add = add;
        return this;
    }

    public Date getTodayDate() {
        return todayDate;
    }

    public ElementEntity setTodayDate(Date todayDate) {
        this.todayDate = todayDate;
        return this;
    }

    public int getIndex() {
        return index;
    }

    public ElementEntity setIndex(int index) {
        this.index = index;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ElementEntity that = (ElementEntity) o;
        return Objects.equals(userEmail, that.userEmail) && Objects.equals(todayDate, that.todayDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userEmail, todayDate);
    }


}

