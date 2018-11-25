package com.example.user.myapplication;

import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class MainRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    int[] images = {R.drawable.i_1, R.drawable.i_2,R.drawable.i_3,R.drawable.i_4,R.drawable.i_5};
    @NonNull
    @Override
    // 내가 설정한 recyclerview에 들어갈 레이아웃을 설정합니다.
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int width = parent.getResources().getDisplayMetrics().widthPixels / 3;
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_item, parent, false);
        view.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, width));
        return new RowCell(view);
    }

    @Override
    // 뷰 안에 설정한 값들을 입력합니다.
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((RowCell)holder).imageView.setImageResource(images[position]);
    }

    @Override
    public int getItemCount() {
        return images.length;
    }


    private static class RowCell extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public RowCell(View view) {
            super(view);
            // 아까 넣은 이미지 파일을 불러옵니다.
           imageView = view.findViewById(R.id.recyclerview_imageview);
        }
    }
}
