package com.example.user.myapplication;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by user on 2018-03-08.
 */

public class CardviewDTOs {
    public int imageView;
    public String title, subtitle;

    public CardviewDTOs(int imageView, String title, String subtitle) {
        this.imageView = imageView;
        this.title = title;
        this.subtitle = subtitle;
    }

}
