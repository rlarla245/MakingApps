package com.example.user.fragment3.NavigationFragments;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.user.fragment3.R;

public class Secondpeoples extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second_peoples);

        RecyclerView view = (RecyclerView)findViewById(R.id.second_peoples_recyclerview);
        
        SecondPeoplesAdapter second_peoples_adapter = new SecondPeoplesAdapter();
        view.setAdapter(second_peoples_adapter);
        
        view.setLayoutManager(new LinearLayoutManager(this));

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        ImageView imageButton = (ImageView)findViewById(R.id.toolbar_email);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(Secondpeoples.this, "서버 연결이 필요합니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
