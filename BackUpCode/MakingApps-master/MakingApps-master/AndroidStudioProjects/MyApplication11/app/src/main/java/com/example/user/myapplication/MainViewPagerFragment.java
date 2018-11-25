package com.example.user.myapplication;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

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
        ImageView imageView = view.findViewById(R.id.main_viewpager_imageview);
        imageView.setImageResource(getArguments().getInt("image"));
        return view;
    }
}
