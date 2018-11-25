package com.example.user.myapplication1;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by user on 2018-03-08.
 */

public class MainRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    ArrayList<MemberDTOs> memberDTOs = new ArrayList<>();

    public MainRecyclerViewAdapter() {
        memberDTOs.add(new MemberDTOs(R.drawable.i_1, "서지수", "반갑습니다, 테스트 앱입니다."));
        memberDTOs.add(new MemberDTOs(R.drawable.i_2, "서지수", "반갑습니다, 테스트 앱입니다."));
        memberDTOs.add(new MemberDTOs(R.drawable.i_3, "서지수", "반갑습니다, 테스트 앱입니다."));
        memberDTOs.add(new MemberDTOs(R.drawable.i_4, "서지수", "반갑습니다, 테스트 앱입니다."));
        memberDTOs.add(new MemberDTOs(R.drawable.i_5, "서지수", "반갑습니다, 테스트 앱입니다."));
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_item, parent, false);
        return new RowCell(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((RowCell)holder).imageView.setImageResource(memberDTOs.get(position).imageview);
        ((RowCell)holder).name.setText(memberDTOs.get(position).name);
        ((RowCell)holder).message.setText(memberDTOs.get(position).message);
    }

    @Override
    public int getItemCount() {
        return memberDTOs.size();
    }

    private static class RowCell extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView name, message;
        public RowCell(View view) {
            super(view);
            imageView = view.findViewById(R.id.circle_imageview);
            name = view.findViewById(R.id.name);
            message = view.findViewById(R.id.message);
        }
    }
}
