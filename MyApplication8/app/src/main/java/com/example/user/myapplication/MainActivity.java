package com.example.user.myapplication;

import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ViewPager viewPager = (ViewPager)findViewById(R.id.activity_main_viewpager);

        // 기존의 레이아웃틀은 유지하므로 layoutManager는 필요 없음.
        MainViewPagerAdapter mainViewPagerAdapter = new MainViewPagerAdapter(getFragmentManager());
        viewPager.setAdapter(mainViewPagerAdapter);






    }
}
