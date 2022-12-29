package com.blackgreen.dios.entities.record;

import java.util.Date;
import java.util.Objects;

public class CountEntity {

    private String userEmail;
    private Date todayDate;
    private int squatCount;
    private int squatSetting;
    private int lungeCount;
    private int lungeSetting;
    private int plankCount;
    private int plankSetting;

    public String getUserEmail() {
        return userEmail;
    }

    public CountEntity setUserEmail(String userEmail) {
        this.userEmail = userEmail;
        return this;
    }

    public Date getTodayDate() {
        return todayDate;
    }

    public CountEntity setTodayDate(Date todayDate) {
        this.todayDate = todayDate;
        return this;
    }

    public int getSquatCount() {
        return squatCount;
    }

    public CountEntity setSquatCount(int squatCount) {
        this.squatCount = squatCount;
        return this;
    }

    public int getSquatSetting() {
        return squatSetting;
    }

    public CountEntity setSquatSetting(int squatSetting) {
        this.squatSetting = squatSetting;
        return this;
    }

    public int getLungeCount() {
        return lungeCount;
    }

    public CountEntity setLungeCount(int lungeCount) {
        this.lungeCount = lungeCount;
        return this;
    }

    public int getLungeSetting() {
        return lungeSetting;
    }

    public CountEntity setLungeSetting(int lungeSetting) {
        this.lungeSetting = lungeSetting;
        return this;
    }

    public int getPlankCount() {
        return plankCount;
    }

    public CountEntity setPlankCount(int plankCount) {
        this.plankCount = plankCount;
        return this;
    }

    public int getPlankSetting() {
        return plankSetting;
    }

    public CountEntity setPlankSetting(int plankSetting) {
        this.plankSetting = plankSetting;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CountEntity count = (CountEntity) o;
        return Objects.equals(userEmail, count.userEmail) && Objects.equals(todayDate, count.todayDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userEmail, todayDate);
    }
}
