package com.example.user.cardview;

import android.content.Intent;
import android.inputmethodservice.Keyboard;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by user on 2018-02-16.
 */

public class MyCardViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public ArrayList<CardViewDTO> cardViewDTOS = new ArrayList<>();

    public MyCardViewAdapter() {
        cardViewDTOS.add(new CardViewDTO(R.drawable.i_1, "첫 번째 사진", "러블리즈 서지수입니다."));
        cardViewDTOS.add(new CardViewDTO(R.drawable.i_2, "두 번째 사진", "러블리즈 서지수입니다."));
        cardViewDTOS.add(new CardViewDTO(R.drawable.i_3, "세 번째 사진", "러블리즈 서지수입니다."));
        cardViewDTOS.add(new CardViewDTO(R.drawable.i_4, "네 번째 사진", "러블리즈 서지수입니다."));
        cardViewDTOS.add(new CardViewDTO(R.drawable.i_5, "다섯번째 사진", "러블리즈 서지수입니다."));
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_layout, parent, false);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        return new RowCell(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        ((RowCell)holder).imageView.setImageResource(cardViewDTOS.get(position).image);
        ((RowCell)holder).title.setText(cardViewDTOS.get(position).title);
        ((RowCell)holder).subtitle.setText(cardViewDTOS.get(position).subtitle);

        ((RowCell)holder).imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(view.getContext(), cardViewDTOS.get(position).title.toString(), Toast.LENGTH_SHORT).show();
            }
        });
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
            title = (view).findViewById(R.id.title);
            subtitle = (view).findViewById(R.id.subtitle);
        }
    }
}
