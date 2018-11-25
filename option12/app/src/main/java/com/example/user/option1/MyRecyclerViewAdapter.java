package com.example.user.option1;

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
    public MyRecyclerViewAdapter() {
    }

    int[] images = {R.drawable.image_9, R.drawable.image_9, R.drawable.image_9, R.drawable.image_9};

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int width = parent.getResources().getDisplayMetrics().widthPixels / 3;
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.want_recyclerview, parent, false);
        view.setLayoutParams(new ConstraintLayout.LayoutParams(width, width));
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
            imageView = (view).findViewById(R.id.recyclerview_imageview);
        }
    }
}
