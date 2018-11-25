package com.example.user.myapplication;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class ViewpagerFragment extends Fragment {
    // static 덕분에 하나의 프레그먼트로 여러 이미지를 사용할 수 있음
    public static ViewpagerFragment newInstance(int imageUri) {

        Bundle args = new Bundle();
        args.putInt("imageUrl", imageUri);

        ViewpagerFragment fragment = new ViewpagerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.viewpager_fragment, container, false);
        ImageView imageView = view.findViewById(R.id.viewpager_fragment_imageview);
        imageView.setImageResource(getArguments().getInt("imageUrl"));
        return view;
    }
}
