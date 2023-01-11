package com.blackgreen.dios.entities.member;

import java.util.Date;
import java.util.Objects;

public class EmailAuthEntity {
    private int index;
    private String email;
    private String code;
    private String salt;
    private Date createdOn;
    private Date expiresOn;
    private boolean isExpired;



    public EmailAuthEntity() {
    }

    public EmailAuthEntity(int index, String email, String code, String salt, Date createdOn, Date expiresOn, boolean isExpired) {
        this.index = index;
        this.email = email;
        this.code = code;
        this.salt = salt;
        this.createdOn = createdOn;
        this.expiresOn = expiresOn;
        this.isExpired = isExpired;
    }

    public int getIndex() {
        return index;
    }

    public EmailAuthEntity setIndex(int index) {
        this.index = index;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public EmailAuthEntity setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getCode() {
        return code;
    }

    public EmailAuthEntity setCode(String code) {
        this.code = code;
        return this;
    }

    public String getSalt() {
        return salt;
    }

    public EmailAuthEntity setSalt(String salt) {
        this.salt = salt;
        return this;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public EmailAuthEntity setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
        return this;
    }

    public Date getExpiresOn() {
        return expiresOn;
    }

    public EmailAuthEntity setExpiresOn(Date expiresOn) {
        this.expiresOn = expiresOn;
        return this;
    }

    public boolean IsExpired() {
        return isExpired;
    }

    public EmailAuthEntity setIsExpired(boolean isExpired) {
        this.isExpired = isExpired;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EmailAuthEntity that = (EmailAuthEntity) o;
        return index == that.index;
    }

    @Override
    public int hashCode() {
        return Objects.hash(index);
    }
}