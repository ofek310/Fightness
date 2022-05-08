package com.hit.fightness;

import java.io.Serializable;
import java.util.ArrayList;

public class Workout implements Serializable {

    private String type;
    private String trainer;
    private String time;
    private int quantity;
    private String subscription;
    private String date;
    private long day;
    private String id;
    private ArrayList<String> participants = new ArrayList<>();
    private ArrayList<String> waiting = new ArrayList<>();

    public Workout() {
    }

    public Workout(String type, String trainer, String time, int quantity, String subscription, long day, String id, ArrayList<String> participants, ArrayList<String> waiting) {
        this.type = type;
        this.trainer = trainer;
        this.time = time;
        this.quantity = quantity;
        this.subscription = subscription;
        this.day = day;
        this.id = id;
        this.participants = participants;
        this.waiting = waiting;
    }

    public Workout(String date, String type, String trainer, String time, int quantity, String subscription, long day, String id, ArrayList<String> participants, ArrayList<String> waiting) {
        this.type = type;
        this.trainer = trainer;
        this.time = time;
        this.quantity = quantity;
        this.subscription = subscription;
        this.date = date;
        this.day = day;
        this.id = id;
        this.participants = participants;
        this.waiting = waiting;
    }

    public Workout(String date, String type, String trainer, String time, int quantity, String subscription, long day, String id) {
        this.type = type;
        this.trainer = trainer;
        this.time = time;
        this.quantity = quantity;
        this.subscription = subscription;
        this.date = date;
        this.day = day;
        this.id = id;
    }

    //    public Workout(String trainer, String time, String type) {
//        this.type = type;
//        this.trainer = trainer;
//        this.time = time;
//    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTrainer() {
        return trainer;
    }

    public void setTrainer(String trainer) {
        this.trainer = trainer;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getSubscription() {
        return subscription;
    }

    public void setSubscription(String subscription) {
        this.subscription = subscription;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public long getDay() {
        return day;
    }

    public void setDay(long day) {
        this.day = day;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ArrayList<String> getParticipants() {
        return participants;
    }

    public void setParticipants(ArrayList<String> participants) {
        this.participants = participants;
    }

    public ArrayList<String> getWaiting() {
        return waiting;
    }

    public void setWaiting(ArrayList<String> waiting) {
        this.waiting = waiting;
    }
}
