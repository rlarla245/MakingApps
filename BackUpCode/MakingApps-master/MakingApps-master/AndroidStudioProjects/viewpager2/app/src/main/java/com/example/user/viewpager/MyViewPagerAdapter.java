package com.example.user.viewpager;

import android.app.*;
import android.app.Fragment;
import android.support.v13.app.FragmentStatePagerAdapter;

/**
 * Created by user on 2018-02-13.
 */

public class MyViewPagerAdapter extends FragmentStatePagerAdapter {
    int[] images = {R.drawable.i_1, R.drawable.i_3, R.drawable.i_7};
    public MyViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return new com.example.user.viewpager.Fragment().newInstance(images[position]);
    }

    @Override
    public int getCount() {
        return images.length;
    }
}
