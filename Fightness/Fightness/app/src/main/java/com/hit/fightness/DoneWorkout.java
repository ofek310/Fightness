package com.hit.fightness;

public class DoneWorkout {
    String date;
    String name_workout;
//    int num_package;
    String time;
    String id;

    public DoneWorkout(String date, String name_workout, String time, String id) {
        this.date = date;
        this.name_workout = name_workout;
        this.time = time;
        this.id = id;
    }

    public DoneWorkout(){}

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getName_workout() {
        return name_workout;
    }

    public void setName_workout(String name_workout) {
        this.name_workout = name_workout;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
