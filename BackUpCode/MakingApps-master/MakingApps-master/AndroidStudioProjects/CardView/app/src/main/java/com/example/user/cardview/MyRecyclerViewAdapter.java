package com.example.user.cardview;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;


public class MyRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<CardViewDTO> cardViewDTOS = new ArrayList<>();

    public MyRecyclerViewAdapter() {
        cardViewDTOS.add(new CardViewDTO(R.drawable.i_1, "서지수", "안녕하세요~ 러블리즈의 사막여우 서지수입니다!"));
        cardViewDTOS.add(new CardViewDTO(R.drawable.i_2, "서지수", "안녕하세요~ 러블리즈의 사막여우 서지수입니다!"));
        cardViewDTOS.add(new CardViewDTO(R.drawable.i_3, "서지수", "안녕하세요~ 러블리즈의 사막여우 서지수입니다!"));
        cardViewDTOS.add(new CardViewDTO(R.drawable.i_4, "서지수", "안녕하세요~ 러블리즈의 사막여우 서지수입니다!"));
        cardViewDTOS.add(new CardViewDTO(R.drawable.i_5, "서지수", "안녕하세요~ 러블리즈의 사막여우 서지수입니다!"));
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_item, parent, false);

        return new RowCell(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((RowCell)holder).imageView.setImageResource(cardViewDTOS.get(position).image);
        ((RowCell)holder).title.setText(cardViewDTOS.get(position).title);
        ((RowCell)holder).subtitle.setText(cardViewDTOS.get(position).subtitle);

    }

    @Override
    public int getItemCount() {
        return cardViewDTOS.size();
    }

    private static class RowCell extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView title, subtitle;

        public RowCell(View view) {
            super(view);
            imageView = (view).findViewById(R.id.cardview_imageview);
            title = (view).findViewById(R.id.cardview_title);
            subtitle = (view).findViewById(R.id.cardview_subtitle);
        }
    }
}
