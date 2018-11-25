package com.example.user.recyclerlist;

/**
 * Created by user on 2018-02-14.
 */

public class memberDTO {
    public memberDTO(int image, String name, String message) {
        // 생성자가 있어야 컴퓨터가 자동으로 각각의 요소를 인식한 뒤 연결(생성)시켜 줄 수 있습니다.
        this.image = image;
        this.name = name;
        this.message = message;
    }

    public int image;
    public String name, message;
}
