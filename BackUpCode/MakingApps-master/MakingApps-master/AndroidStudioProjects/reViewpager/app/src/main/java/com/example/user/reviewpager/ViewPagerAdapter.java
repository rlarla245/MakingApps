package com.example.user.reviewpager;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentStatePagerAdapter;

/**
 * Created by user on 2018-02-14.
 */

// 뷰 페이져 또한 없는 기능이므로 메인 액티비티에서 작동시키기 위해 어댑터를 생성!
public class ViewPagerAdapter extends FragmentStatePagerAdapter {
    public int[] images = {R.drawable.i_1, R.drawable.i_2, R.drawable.i_3, R.drawable.i_4, R.drawable.i_5};

    public ViewPagerAdapter(FragmentManager fm) {
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
