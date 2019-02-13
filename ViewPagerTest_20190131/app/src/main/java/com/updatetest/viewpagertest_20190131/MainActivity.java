package com.updatetest.viewpagertest_20190131;

import android.annotation.SuppressLint;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;

import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ViewPager viewPager = findViewById(R.id.mainactivty_viewPager);
        viewPager.setAdapter(new ViewPagerAdapter(getSupportFragmentManager()));

        TabLayout tabLayout = findViewById(R.id.mainactivty_tablayout);
        tabLayout.addTab(tabLayout.newTab().setText("1번 사진"));
        tabLayout.addTab(tabLayout.newTab().setText("2번 사진"));
        tabLayout.addTab(tabLayout.newTab().setText("3번 사진"));
        tabLayout.addTab(tabLayout.newTab().setText("4번 사진"));
        tabLayout.addTab(tabLayout.newTab().setText("5번 사진"));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }
}

class ViewPagerAdapter extends FragmentStatePagerAdapter {
    int[] images = {R.drawable.i_1, R.drawable.i_5, R.drawable.i_3, R.drawable.i_4, R.drawable.i_5};

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return new ViewPager_Fragment1().newInstance(images[position]);
    }

    @Override
    public int getCount() {
        return images.length;
    }
}
