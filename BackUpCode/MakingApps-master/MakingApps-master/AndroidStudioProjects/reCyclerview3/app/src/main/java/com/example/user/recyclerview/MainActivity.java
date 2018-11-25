package com.example.user.recyclerview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.WindowManager;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView view = (RecyclerView)findViewById(R.id.main_recyclerview);
        // 메인 레이아웃을 구동하는 역할을 하는 클래스이기 때문에 이렇게 불러와줘야 함

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 3);
        // 그리드 뷰로 적용시키겠다는 의미이므로 이렇게 재생성
        view.setLayoutManager(layoutManager);

        MyRecyclerViewAdapter myRecyclerViewAdapter = new MyRecyclerViewAdapter();
        view.setAdapter(myRecyclerViewAdapter);
        // 어댑터는 안드로이드 스튜디오가 기본적으로 제공하지 않는 모듈들을 사용할 수 있게 변환시켜 주는 역할이므로
        // 이렇게 연결시켜줘야 함.

        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }
}
