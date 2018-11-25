package com.example.user.fragment3.NavigationFragments;

import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.user.fragment3.R;

/**
 * Created by user on 2018-03-15.
 */

public class AcademyEventsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    int[] images = {R.drawable.p_1, R.drawable.p_2, R.drawable.p_3, R.drawable.p_4, R.drawable.p_5, R.drawable.p_6, R.drawable.p_7, R.drawable.p_8, R.drawable.p_9, R.drawable.p_10, R.drawable.p_11, R.drawable.p_12, R.drawable.p_13, R.drawable.p_14, R.drawable.p_15, R.drawable.p_16, R.drawable.p_17, R.drawable.p_18};
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int width = parent.getResources().getDisplayMetrics().widthPixels / 3;
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.photos_recyclerview, parent, false);
        view.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, width));
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
            imageView = view.findViewById(R.id.photos_recyclerview_imageview);
        }
    }
}
