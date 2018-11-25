package com.example.user.viewpager;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentStatePagerAdapter;

/**
 * Created by user on 2018-02-16.
 */

public class MyViewPagerAdapter2 extends FragmentStatePagerAdapter {
    int[] images2 = {R.drawable.logo, R.drawable.google_logo};
    public MyViewPagerAdapter2(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return imageFragment2.newInstance(images2[position]);
    }

    @Override
    public int getCount() {
        return images2.length;
    }
}
