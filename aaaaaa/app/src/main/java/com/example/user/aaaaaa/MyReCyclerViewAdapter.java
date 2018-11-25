package com.example.user.aaaaaa;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class MyReCyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    int[] images = {R.drawable.i_1, R.drawable.i_2, R.drawable.i_7};

    public MyReCyclerViewAdapter() {
        // 그냥 생성한 것.
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // 메인 레이아웃 내 유저가 불러온 레이아웃이 적용됩니다.
        int width = parent.getResources().getDisplayMetrics().widthPixels / 3;
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_main, parent, false);
        view.setLayoutParams(new LinearLayout.LayoutParams(width, width));
        return new RowCell(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
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
            imageView = view.findViewById(R.id.image1);
        }
    }
}
