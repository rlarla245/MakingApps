package com.example.user.recyclerview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView view = (RecyclerView)findViewById(R.id.main_recyclerview);
        // 리사이클러 뷰를 가져옵니다.

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 3);
        // 리사이클러 뷰에 적용할 뷰는 그리드 뷰이기 때문에 그리드 레이아웃 매니저를 통해 생성해줍니다.

        view.setLayoutManager(layoutManager);

        MyRecyclerViewAdapter myRecyclerViewAdapter = new MyRecyclerViewAdapter();
        view.setAdapter(myRecyclerViewAdapter);
    }
}
