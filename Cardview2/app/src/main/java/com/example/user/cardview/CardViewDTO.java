package com.example.user.cardview;

/**
 * Created by user on 2018-02-14.
 */

public class CardViewDTO {
    public CardViewDTO(int image, String title, String subtitle) {
        this.image = image;
        this.title = title;
        this.subtitle = subtitle;
    }

    public int image;
    public String title, subtitle;
}
