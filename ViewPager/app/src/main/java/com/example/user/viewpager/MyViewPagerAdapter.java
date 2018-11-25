package com.example.user.viewpager;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by user on 2018-02-13.
 */

public class MyViewPagerAdapter extends android.support.v13.app.FragmentStatePagerAdapter {
    int[] images = {R.drawable.a, R.drawable.b, R.drawable.c, R.drawable.d};
    public MyViewPagerAdapter(android.app.FragmentManager fm) {
        super(fm);
    }

    @Override
    public android.app.Fragment getItem(int position) {
        return new ImageFragment().newInstance(images[position]);
    }

    @Override
    public int getCount() {
        return images.length;
    }
}
