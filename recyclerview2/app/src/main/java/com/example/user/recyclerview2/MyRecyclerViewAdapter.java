package com.example.user.recyclerview2;

import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class MyRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    int[] images = {R.drawable.i_2, R.drawable.i_4, R.drawable.i_7};
    public MyRecyclerViewAdapter() {
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // 설정한 레이아웃의 '틀'을 가져오는 메소드
        int width = parent.getResources().getDisplayMetrics().widthPixels / 3;
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_item, parent, false);
        view.setLayoutParams(new LinearLayout.LayoutParams(width, width));
        // gravity를 주는 대표적인 레이아웃 툴
        return new RowCell(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        // 실제 적용된 이미지들을 넣어봅시다.
        ((RowCell)holder).imageView.setImageResource(images[position]);
    }

    @Override
    public int getItemCount() {
        // 카운트 메소드. 이미지 파일들이 몇개 있는지 확인해주는 메소드
        return images.length;
    }

    private static class RowCell extends RecyclerView.ViewHolder {
        public ImageView imageView;
        // 이미지뷰를 불러와야 되겠죠?
        public RowCell(View view) {
            super(view);
            imageView = view.findViewById(R.id.recyclerview_item_imageview);
        }
    }
}
