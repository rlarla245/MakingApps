package com.example.user.recyclerview;

import android.media.Image;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * Created by user on 2018-02-14.
 */

public class MyRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    // 코드 파일인 java 디렉토리에서는 주석 처리가 이렇게 가능하지만, 레이아웃에서는 "/" 자체가
    // 요소값을 닫는 역할을 하기도 하므로 사용이 불가능합니다. /**/ 그래서 이것을 활용합니다.

    int[] images = {R.drawable.i_1, R.drawable.i_2, R.drawable.i_3, R.drawable.i_4, R.drawable.i_5};
    // 실제 이미지들을 불러올 수 있게 이미지들을 가지고 있는 배열을 생성해줍니다.

    public MyRecyclerViewAdapter() {
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // 메인 레이아웃에 어떤 뷰를 생성시킬지 연결시키는 메소드
        // 이름도 직역하면 뷰를 생성하는 메소드이지 않은가.

        int width = parent.getResources().getDisplayMetrics().widthPixels / 3;

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_item, parent, false);
        // 메인레이아웃에 위 레이아웃의 설정을 연결시킨다는 뜻

        view.setLayoutParams(new ConstraintLayout.LayoutParams(width, width));

        return new RowCell(view);
        // 리사이클러 뷰에 들어갈 셀을 설정합시다.
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((RowCell)holder).imageView.setImageResource(images[position]);
    }

    @Override
    public int getItemCount() {
        return images.length;
        // 내가 설정한 이미지 영역에 몇개의 실제 이미지들을 활용할 것인가 카운트하는 메소드.
        // 몇개의 아이템을 활용할 것인가에 대한 것
    }

    private static class RowCell extends RecyclerView.ViewHolder {
        public ImageView imageView;
        // 이미지뷰를 불러와 아까 만든 이미지 뷰와 연결시켜줍니다.
        public RowCell(View view) {
            super(view);
            imageView = (view).findViewById(R.id.recyclerview_imageview);
        }
    }
}
