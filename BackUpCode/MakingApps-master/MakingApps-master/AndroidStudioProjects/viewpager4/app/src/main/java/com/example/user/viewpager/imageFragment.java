package com.example.user.viewpager;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class imageFragment extends Fragment {
    public static imageFragment newInstance(int image) {

        Bundle args = new Bundle();
        args.putInt("image", image);

        imageFragment fragment = new imageFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.viewpager_layout, container, false);
        ImageView imageView = (view).findViewById(R.id.viewpager_imageview);
        imageView.setImageResource(getArguments().getInt("image"));
        return view;
    }
}
