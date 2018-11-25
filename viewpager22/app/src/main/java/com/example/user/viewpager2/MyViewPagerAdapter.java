package com.example.user.viewpager2;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentStatePagerAdapter;

/**
 * Created by user on 2018-02-14.
 */

public class MyViewPagerAdapter extends FragmentStatePagerAdapter {
    int[] images = {R.drawable.i_1, R.drawable.i_2, R.drawable.i_3, R.drawable.i_4, R.drawable.i_5, R.drawable.image_07, R.drawable.image_90, R.drawable.image_10, R.drawable.gif, R.drawable.image_7, R.drawable.image_11};
    public MyViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return new ViewPagerFragment().newInstance(images[position]);
    }

    @Override
    public int getCount() {
        return images.length;
    }
}
