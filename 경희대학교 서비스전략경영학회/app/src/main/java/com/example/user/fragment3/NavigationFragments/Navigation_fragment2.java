package com.example.user.fragment3.NavigationFragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.user.fragment3.R;

public class Navigation_fragment2 extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_fragment2);

        RecyclerView view = (RecyclerView)findViewById(R.id.navigation_fragment2_recyclerview);

        NavigationRecyclerviewAdapter navigationRecyclerviewAdapter = new NavigationRecyclerviewAdapter();
        view.setAdapter(navigationRecyclerviewAdapter);

        view.setLayoutManager(new LinearLayoutManager(this));

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        ImageView imageButton = (ImageView)findViewById(R.id.toolbar_email);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(Navigation_fragment2.this, "서버 연결이 필요합니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
