package com.example.user.lastviewpager;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.widget.ImageView;

/**
 * Created by user on 2018-02-13.
 */

public class MyViewPagerAdapter extends FragmentStatePagerAdapter {
    public int[] images = {R.drawable.i_1, R.drawable.i_2, R.drawable.i_4, R.drawable.image_9, R.drawable.image_10};
    public MyViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return new ViewPager_Fragment().newInstance(images[position]);
    }

    @Override
    public int getCount() {
        return images.length;
    }
}
