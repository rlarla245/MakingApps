package com.example.user.myapplication;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public ArrayList<CardviewDTOs> cardviewDTOs = new ArrayList<>();

    public MainRecyclerViewAdapter() {
        cardviewDTOs.add(new CardviewDTOs(R.drawable.i_1, "김두현", "1기 부학회장\n2기 임시 학회장\n\n기획자, 어플리케이션 개발자"));
        cardviewDTOs.add(new CardviewDTOs(R.drawable.i_2, "김두현", "반갑습니다, 테스트 앱입니다.\n경력사항은 나중에 적겠습니다\n매우 귀찮거든요"));
        cardviewDTOs.add(new CardviewDTOs(R.drawable.i_3, "김두현", "반갑습니다, 테스트 앱입니다.\n경력사항은 나중에 적겠습니다\n매우 귀찮거든요"));
        cardviewDTOs.add(new CardviewDTOs(R.drawable.i_5, "김두현", "반갑습니다, 테스트 앱입니다.\n경력사항은 나중에 적겠습니다\n매우 귀찮거든요"));
        cardviewDTOs.add(new CardviewDTOs(R.drawable.i_7, "김두현", "반갑습니다, 테스트 앱입니다.\n경력사항은 나중에 적겠습니다\n매우 귀찮거든요"));
    }

    @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_cardview, parent, false);
            return new RowCell(view);
        }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((RowCell)holder).imageView.setImageResource(cardviewDTOs.get(position).imageView);
        ((RowCell)holder).title.setText(cardviewDTOs.get(position).title);
        ((RowCell)holder).subtitle.setText(cardviewDTOs.get(position).subtitle);
    }

    @Override
    public int getItemCount() {
        return cardviewDTOs.size();
    }


    private static class RowCell extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView title, subtitle;
        public RowCell(View view) {
            super(view);
            imageView = view.findViewById(R.id.cardview_imageview);
            title = view.findViewById(R.id.title);
            subtitle = view.findViewById(R.id.sub_title);
        }
    }
}
