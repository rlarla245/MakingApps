package com.example.user.viewpager;

import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ViewPager view = (ViewPager)findViewById(R.id.main_viewpager);

        MyViewPagerAdapter myViewPagerAdapter = new MyViewPagerAdapter(getFragmentManager());
        view.setAdapter(myViewPagerAdapter);

        ViewPager view2 = (ViewPager)findViewById(R.id.sub_viewpager);

        MyViewPagerAdapter2 myViewPagerAdapter2 = new MyViewPagerAdapter2(getFragmentManager());
        view2.setAdapter(myViewPagerAdapter2);

        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }
}
