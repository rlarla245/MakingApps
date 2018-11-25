package com.example.user.myapplication;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * Created by user on 2018-03-07.
 */

public class MainViewPagerFragment extends Fragment {
    public static MainViewPagerFragment newInstance(int image) {

        Bundle args = new Bundle();
        args.putInt("image", image);

        MainViewPagerFragment fragment = new MainViewPagerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_viewpager_fragment, container, false);
        ImageView imageView = view.findViewById(R.id.fragment_imageview);
        imageView.setImageResource(getArguments().getInt("image"));
        return view;
    }
}
