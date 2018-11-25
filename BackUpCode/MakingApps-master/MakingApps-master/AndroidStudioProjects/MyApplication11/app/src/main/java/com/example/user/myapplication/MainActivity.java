package com.example.user.myapplication;

import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ViewPager view = (ViewPager)findViewById(R.id.main_viewpager);

        MainViewpagerAdapter mainViewpagerAdapter = new MainViewpagerAdapter(getFragmentManager());
        view.setAdapter(mainViewpagerAdapter);
    }
}
