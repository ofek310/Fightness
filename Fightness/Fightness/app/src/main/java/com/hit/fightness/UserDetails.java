package com.hit.fightness;

import android.widget.Spinner;

public class UserDetails {
    private String name;
    private String email;
    private String birthday;
    private String phoneNumber;
    private int gender;
    private String height;
    private String weight;
    private String image;
    private boolean admin;

    public UserDetails(){}

    public UserDetails(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public UserDetails(String name, String email, String birthday,String phoneNumber, int gender, String height, String weight, String image, boolean admin) {
        this.name = name;
        this.email = email;
        this.birthday = birthday;
        this.phoneNumber = phoneNumber;
        this.gender = gender;
        this.height = height;
        this.weight = weight;
        this.image = image;
        this.admin = admin;
    }

    public UserDetails(String name, String email, String birthday,String phoneNumber, int gender, String height, String weight, boolean admin) {
        this.name = name;
        this.email = email;
        this.birthday = birthday;
        this.phoneNumber = phoneNumber;
        this.gender = gender;
        this.height = height;
        this.weight = weight;
        this.admin = admin;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }
}
