package com.example.user.fragment3.NavigationFragments;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.WindowManager;

import com.example.user.fragment3.R;

public class AcademyEventsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photos);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        RecyclerView view = (RecyclerView)findViewById(R.id.photo_activity_recyclerview);

        AcademyEventsAdapter photosActivityAdapter = new AcademyEventsAdapter();
        view.setAdapter(photosActivityAdapter);

        view.setLayoutManager(new GridLayoutManager(this, 3));
    }
}
