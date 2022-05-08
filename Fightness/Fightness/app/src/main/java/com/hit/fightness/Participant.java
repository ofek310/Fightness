package com.hit.fightness;

import android.widget.ImageView;
import android.widget.TextView;

public class Participant {
    private String name;
    private String imageView;

    public Participant(String name) {
        this.name = name;
    }

    public Participant(String name, String imageView) {
        this.name = name;
        this.imageView = imageView;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageView() {
        return imageView;
    }

    public void setImageView(String imageView) {
        this.imageView = imageView;
    }
}
