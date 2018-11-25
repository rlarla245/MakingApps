package com.example.user.recyclerview;

import android.renderscript.ScriptGroup;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.WindowManager;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {
    public static ImageView largeImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().hide();

        largeImage = (ImageView)findViewById(R.id.mainactivity_imageView);

        RecyclerView view = (RecyclerView)findViewById(R.id.mainactivity_recyclerview);
        // 리사이클러 뷰를 가져옵니다.

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        // 리사이클러 뷰에 적용할 뷰는 그리드 뷰이기 때문에 그리드 레이아웃 매니저를 통해 생성해줍니다.

        // 한 행에 3개의 아이템을 입력합니다.

        // 레이아웃 설정
        view.setLayoutManager(layoutManager);

        // 어댑터 설정
        MyRecyclerViewAdapter myRecyclerViewAdapter = new MyRecyclerViewAdapter();
        view.setAdapter(myRecyclerViewAdapter);
    }
}
