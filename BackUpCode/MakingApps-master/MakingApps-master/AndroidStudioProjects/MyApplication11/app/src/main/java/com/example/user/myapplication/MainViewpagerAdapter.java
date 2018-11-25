package com.example.user.myapplication;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentStatePagerAdapter;

public class MainViewpagerAdapter extends FragmentStatePagerAdapter {
    public int[] images = {R.drawable.i_1, R.drawable.i_2, R.drawable.i_3, R.drawable.i_5, R.drawable.i_7};
    public MainViewpagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return new MainViewPagerFragment().newInstance(images[position]);
    }

    @Override
    public int getCount() {
        return images.length;
    }
}
