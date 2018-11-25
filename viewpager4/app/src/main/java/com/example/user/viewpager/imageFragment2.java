package com.example.user.viewpager;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * Created by user on 2018-02-16.
 */

public class imageFragment2 extends Fragment {
    public static imageFragment2 newInstance(int image2) {

        Bundle args = new Bundle();
        args.putInt("image2", image2);

        imageFragment2 fragment = new imageFragment2();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view2 = inflater.inflate(R.layout.viewpager_layout2, container, false);
        ImageView imageView2 = (view2).findViewById(R.id.viewpager_imageview2);
        imageView2.setImageResource(getArguments().getInt("image2"));
        return view2;
    }
}
