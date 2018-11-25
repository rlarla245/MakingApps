package com.example.user.reviewpager;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * Created by user on 2018-02-14.
 */
// 프레그먼트를 생성하는 이유는 하나의 액티비티만을 유지하면서 화면을 이동하는 효과를 얻기 위함
public class ViewPagerFragment extends Fragment {
    public static ViewPagerFragment newInstance(int imageresource) {
        // 이 클래스 메소드 덕분에 하나의 프레그먼트만을 활용하여 이미지 전환 효과만 누릴 수 있게 됨
        // 물론 그 덕에 코드는 조금 복잡해지나, 이렇게 생성하는 편이 훨씬 좋고 간결함.

        Bundle args = new Bundle();
        args.putInt("imageresource", imageresource);

        ViewPagerFragment fragment = new ViewPagerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
         View view = inflater.inflate(R.layout.viewpager_layout, container, false);
         // 내가 설정하고 싶은 뷰는 뷰페이져 레이아웃이므로 이렇게 설정

        ImageView imageView = (view).findViewById(R.id.viewpager_imageview);
        imageView.setImageResource(getArguments().getInt("imageresource"));

        return view;
    }
}
