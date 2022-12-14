package com.blackgreen.dios.entities.member;

import java.util.Date;
import java.util.Objects;

public class UserEntity {
    private String email;
    private String password;
    private String nickname;
    private String name;
    private String contact;

    private byte[] image;

    private String imageType;
    private String addressPostal;
    private String addressPrimary;
    private String addressSecondary;
    private Date registeredOn;


    public UserEntity(String email, String password, String nickname, String name, String contact, String addressPostal, String addressPrimary, String addressSecondary, Date registeredOn) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.name = name;
        this.contact = contact;
        this.addressPostal = addressPostal;
        this.addressPrimary = addressPrimary;
        this.addressSecondary = addressSecondary;
        this.registeredOn = registeredOn;
    }

    public String getEmail() {
        return email;
    }

    public UserEntity setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public UserEntity setPassword(String password) {
        this.password = password;
        return this;
    }

    public String getNickname() {
        return nickname;
    }

    public UserEntity setNickname(String nickname) {
        this.nickname = nickname;
        return this;
    }

    public String getName() {
        return name;
    }

    public UserEntity setName(String name) {
        this.name = name;
        return this;
    }

    public String getContact() {
        return contact;
    }

    public UserEntity setContact(String contact) {
        this.contact = contact;
        return this;
    }

    public byte[] getImage() {
        return image;
    }

    public UserEntity setImage(byte[] image) {
        this.image = image;
        return this;
    }

    public String getImageType() {
        return imageType;
    }

    public UserEntity setImageType(String imageType) {
        this.imageType = imageType;
        return this;
    }

    public String getAddressPostal() {
        return addressPostal;
    }

    public UserEntity setAddressPostal(String addressPostal) {
        this.addressPostal = addressPostal;
        return this;
    }

    public String getAddressPrimary() {
        return addressPrimary;
    }

    public UserEntity setAddressPrimary(String addressPrimary) {
        this.addressPrimary = addressPrimary;
        return this;
    }

    public String getAddressSecondary() {
        return addressSecondary;
    }

    public UserEntity setAddressSecondary(String addressSecondary) {
        this.addressSecondary = addressSecondary;
        return this;
    }

    public Date getRegisteredOn() {
        return registeredOn;
    }

    public UserEntity setRegisteredOn(Date registeredOn) {
        this.registeredOn = registeredOn;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserEntity that = (UserEntity) o;
        return Objects.equals(email, that.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email);
    }
}