package com.example.user.cardview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView view = (RecyclerView)findViewById(R.id.main_recyclerview);

        view.setLayoutManager(new LinearLayoutManager(this));

        MyRecyclerviewAdapter myRecyclerviewAdapter = new MyRecyclerviewAdapter();
        view.setAdapter(myRecyclerviewAdapter);
    }
}
