package com.example.user.listview;

import android.inputmethodservice.Keyboard;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<MemberDTO> memberDTOS = new ArrayList<>();
    public RecyclerViewAdapter() {
        memberDTOS.add(new MemberDTO(R.drawable.i_1, "서지수", "안녕하세요~ 러블리즈 서지수입니다!"));
        memberDTOS.add(new MemberDTO(R.drawable.i_2, "지수", "안녕하세요~ 러블리즈 서지수입니다!"));
        memberDTOS.add(new MemberDTO(R.drawable.i_3, "사막여우", "안녕하세요~ 사막여우입니다!"));
        memberDTOS.add(new MemberDTO(R.drawable.i_4, "짓뚜", "안녕하세요~ 러블리즈 서지수입니다!"));
        memberDTOS.add(new MemberDTO(R.drawable.i_5, "짓뚜피아", "안녕하세요~ 짓뚜피아 서지수입니다!"));
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listview_layout, parent, false);
        return new RowCell(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((RowCell)holder).circleImageView.setImageResource(memberDTOS.get(position).image);
        ((RowCell)holder).name.setText(memberDTOS.get(position).name);
        ((RowCell)holder).message.setText(memberDTOS.get(position).message);
    }

    @Override
    public int getItemCount() {
        return memberDTOS.size();
    }

    private class RowCell extends RecyclerView.ViewHolder {
        CircleImageView circleImageView;
        TextView name, message;
        public RowCell(View view) {
            super(view);

            circleImageView = (view).findViewById(R.id.listview_profile_image);
            name = (view).findViewById(R.id.name);
            message = (view).findViewById(R.id.message);
        }
    }
}
