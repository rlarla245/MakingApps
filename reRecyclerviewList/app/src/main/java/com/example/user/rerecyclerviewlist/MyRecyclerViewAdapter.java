package com.example.user.rerecyclerviewlist;

import android.inputmethodservice.Keyboard;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by user on 2018-02-14.
 */

public class MyRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<MemberDTO> memberDTOS = new ArrayList<>();
    public MyRecyclerViewAdapter() {
        memberDTOS.add(new MemberDTO(R.drawable.i_1, "서지수", "안녕하세요, 지수는 사랑인거 아시죠???"));
        memberDTOS.add(new MemberDTO(R.drawable.i_2, "지수", "감사합니다~"));
        memberDTOS.add(new MemberDTO(R.drawable.i_3, "짓뚜", "안녕하세요, 지수는 사랑인거 아시죠???"));
        memberDTOS.add(new MemberDTO(R.drawable.i_4, "짓뚜피아", "안녕하세요, 지수는 사랑인거 아시죠???"));
        memberDTOS.add(new MemberDTO(R.drawable.i_5, "사랑해", "안녕하세요, 지수는 사랑인거 아시죠???"));
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_layout, parent, false);

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

    private static class RowCell extends RecyclerView.ViewHolder {
        CircleImageView circleImageView;
        TextView name, message;
        public RowCell(View view) {
            super(view);
            circleImageView = (view).findViewById(R.id.profile_image);
            name = (view).findViewById(R.id.name);
            message = (view).findViewById(R.id.message);
        }
    }
}
