package com.example.user.option2;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;

/**
 * Created by user on 2018-02-14.
 */

public class MyViewPagerAdapter extends FragmentStatePagerAdapter {
    int[] images ={R.drawable.i_1, R.drawable.i_2, R.drawable.i_4, R.drawable.image_9};
    public MyViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return new ImageFragment().newInstance(images[position]);
    }

    @Override
    public int getCount() {
        return images.length;
    }
}
