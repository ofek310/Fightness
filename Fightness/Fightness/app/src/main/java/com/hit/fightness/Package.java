package com.hit.fightness;

public class Package {
    private String date;
    private String price;
    private int done = 0;
    private int amount;
    private String email;

    public Package(String date, String price, int amount, String email) {
        this.date = date;
        this.price = price;
        this.amount = amount;
        this.email = email;
    }

    public Package(String date, String price, int done, int amount, String email) {
        this.date = date;
        this.price = price;
        this.done = done;
        this.amount = amount;
        this.email = email;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public int getDone() {
        return done;
    }

    public void setDone(int done) {
        this.done = done;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
