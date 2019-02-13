package com.updatetest.viewpagertest_20190131;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class ViewPager_Fragment1 extends Fragment {
    public static ViewPager_Fragment1 newInstance(int image) {
        Bundle args = new Bundle();
        args.putInt("image", image);

        ViewPager_Fragment1 fragment = new ViewPager_Fragment1();
        fragment.setArguments(args);
        return fragment;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.viewpager_fragment1, container, false);

        ImageView imageView = view.findViewById(R.id.viewpager_imageview);
        imageView.setImageResource(getArguments().getInt("image"));

        return view;
    }
}
