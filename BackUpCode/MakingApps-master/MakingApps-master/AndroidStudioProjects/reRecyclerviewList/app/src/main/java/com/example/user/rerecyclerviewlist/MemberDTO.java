package com.example.user.rerecyclerviewlist;

/**
 * Created by user on 2018-02-14.
 */

public class MemberDTO {
    public MemberDTO(int image, String name, String message) {
        this.image = image;
        this.name = name;
        this.message = message;
    }

    public int image;
    public String name, message;
}
