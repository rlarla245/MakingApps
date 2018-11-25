package com.example.user.viewpager;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * Created by user on 2018-02-13.
 */

public class Fragment extends android.app.Fragment {

    public static Fragment newInstance(int image) {

        Bundle args = new Bundle();
        args.putInt("image", image);

        Fragment fragment = new Fragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.viewpager, container, false);
        ImageView imageView = (view).findViewById(R.id.imageview);
        imageView.setImageResource(getArguments().getInt("image"));
        return view;
    }
}
