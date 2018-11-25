package com.example.user.recyclerlist;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by user on 2018-02-14.
 */

public class MyRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    // 당연히 이전 시간에 했던 것과 마찬가지로 어댑터 생성이 필요합니다.
    private ArrayList<memberDTO> memberDTOS = new ArrayList<>();

    public MyRecyclerViewAdapter() {
        memberDTOS.add(new memberDTO(R.drawable.i_1, "서지수", "지수는 무조건 사랑입니다!"));
        memberDTOS.add(new memberDTO(R.drawable.i_2, "지수", "지수를 보세요, 예쁘지 않습니까!"));
        memberDTOS.add(new memberDTO(R.drawable.i_3, "짓뚜", "지수는 귀엽기까지!"));
        memberDTOS.add(new memberDTO(R.drawable.i_4, "지뚜피아", "지수는 완전체!"));
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_item, parent, false);
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
        TextView name;
        TextView message;

        public RowCell(View view) {
            super(view);
            circleImageView = (view).findViewById(R.id.profile_image);
            name = (view).findViewById(R.id.name);
            message = (view).findViewById(R.id.message);

        }
    }
}
