package com.example.user.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.webkit.WebView;

import java.util.zip.Inflater;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 메인 레이아웃을 불러옵니다.
        setContentView(R.layout.activity_main);

        // 메인 레이아웃 내 recyclerview를 불러옵니다.
        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.main_recyclerview);

        // 인스타그램처럼 격자무늬 디자인을 만드는 것이 목표이므로 GridView로 합니다.
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 3);
        recyclerView.setLayoutManager(layoutManager);

        // 어댑터는 기존 안드로이드 스튜디오가 제공하지 않는 뷰를 사용했을 때 적용가능하도록 변경해줍니다.
        MainRecyclerViewAdapter mainRecyclerViewAdapter = new MainRecyclerViewAdapter();
        recyclerView.setAdapter(mainRecyclerViewAdapter);
    }
}
