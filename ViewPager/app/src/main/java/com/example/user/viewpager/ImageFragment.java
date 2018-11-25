package com.example.user.viewpager;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class ImageFragment extends Fragment {
    // 프레그먼트 상속은 기타 별다른 메소드나 생성자를 요하지않음.

    public static ImageFragment newInstance(int Image) {
        
        Bundle args = new Bundle();
        args.putInt("image", Image);
        
        ImageFragment fragment = new ImageFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.imagefragment, container, false);
        ImageView imageView = view.findViewById(R.id.fragment_imageview);
        imageView.setImageResource(getArguments().getInt("image"));
        return view;
    }
}
