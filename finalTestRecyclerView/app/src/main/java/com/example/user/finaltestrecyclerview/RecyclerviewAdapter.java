package com.example.user.finaltestrecyclerview;

import android.inputmethodservice.Keyboard;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * Created by user on 2018-02-13.
 */

public class RecyclerviewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    int[] images = {R.drawable.i_1, R.drawable.i_3, R.drawable.i_7};
    public RecyclerviewAdapter() {
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int width = parent.getResources().getDisplayMetrics().widthPixels / 3;
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_item, parent, false);
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
            imageView = (view).findViewById(R.id.item_image1);
        }
    }
}
