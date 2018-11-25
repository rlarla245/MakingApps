package com.example.user.cardview;

import android.inputmethodservice.Keyboard;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by user on 2018-02-15.
 */

public class MyRecyclerviewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<CardviewDTO> cardviewDTOS = new ArrayList<>();
    public MyRecyclerviewAdapter() {
        cardviewDTOS.add(new CardviewDTO(R.drawable.i_1, "서지수", "반가워요~ 러블리즈 사막여우입니다!"));
        cardviewDTOS.add(new CardviewDTO(R.drawable.i_2, "서지수", "반가워요~ 러블리즈 사막여우입니다!"));
        cardviewDTOS.add(new CardviewDTO(R.drawable.i_3, "서지수", "반가워요~ 러블리즈 사막여우입니다!"));
        cardviewDTOS.add(new CardviewDTO(R.drawable.i_4, "서지수", "반가워요~ 러블리즈 사막여우입니다!"));
        cardviewDTOS.add(new CardviewDTO(R.drawable.i_5, "서지수", "반가워요~ 러블리즈 사막여우입니다!"));
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_item, parent, false);
        return new RowCell(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((RowCell)holder).imageView.setImageResource(cardviewDTOS.get(position).image);
        ((RowCell)holder).title.setText(cardviewDTOS.get(position).title);
        ((RowCell)holder).subtitle.setText(cardviewDTOS.get(position).subtitle);
    }

    @Override
    public int getItemCount() {
        return cardviewDTOS.size();
    }

    private class RowCell extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView title, subtitle;
        public RowCell(View view) {
            super(view);
            imageView = (view).findViewById(R.id.cardview_imageview);
            title = (view).findViewById(R.id.title);
            subtitle = (view).findViewById(R.id.subtitle);
        }
    }
}
