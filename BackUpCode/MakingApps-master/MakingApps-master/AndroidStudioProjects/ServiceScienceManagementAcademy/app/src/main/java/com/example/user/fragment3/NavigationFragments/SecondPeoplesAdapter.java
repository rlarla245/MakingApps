package com.example.user.fragment3.NavigationFragments;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.user.fragment3.R;

import java.util.ArrayList;

/**
 * Created by user on 2018-03-15.
 */

public class SecondPeoplesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<SecondPeoplesCardviewDTOs> second_peoples_cardviewDTOs = new ArrayList<>();

    public SecondPeoplesAdapter() {
        second_peoples_cardviewDTOs.add(new SecondPeoplesCardviewDTOs(R.drawable.people, "전수현", "현 삼성전자 재직", "4대 학회장\n\n컨벤션경영학과 13 졸업"));
        second_peoples_cardviewDTOs.add(new SecondPeoplesCardviewDTOs(R.drawable.people, "김영원", "현 해군 재직", "3대 학회장\n\n호텔경영학과 11 졸업"));
        second_peoples_cardviewDTOs.add(new SecondPeoplesCardviewDTOs(R.drawable.people, "신은지", "불명", "2기 학회원\n\n외식경영학과 12 졸업"));
        second_peoples_cardviewDTOs.add(new SecondPeoplesCardviewDTOs(R.drawable.people, "신홍섭", "불명", "2기 학회원\n\n호텔경영학과 12 졸업"));
        second_peoples_cardviewDTOs.add(new SecondPeoplesCardviewDTOs(R.drawable.people, "박여름", "불명", "2기 학회원\n\n호텔경영학과 11 졸업"));
        second_peoples_cardviewDTOs.add(new SecondPeoplesCardviewDTOs(R.drawable.people, "우승주", "불명", "2기 학회원\n\n컨벤션경영학과 13 졸업"));
        second_peoples_cardviewDTOs.add(new SecondPeoplesCardviewDTOs(R.drawable.people, "박재은", "불명", "2기 학회원\n\n컨벤션경영학과 12 졸업"));
}

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.navigation_fragment2_cardview, parent, false);
        return new RowCell(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((RowCell)holder).imageView.setImageResource(second_peoples_cardviewDTOs.get(position).imageview);
        ((RowCell)holder).name.setText(second_peoples_cardviewDTOs.get(position).name);
        ((RowCell)holder).status.setText(second_peoples_cardviewDTOs.get(position).status);
        ((RowCell)holder).workspace.setText(second_peoples_cardviewDTOs.get(position).workspace);
    }

    @Override
    public int getItemCount() {
        return second_peoples_cardviewDTOs.size();
    }

    private static class RowCell extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView name, status, workspace;

        public RowCell(View view) {
            super(view);
            imageView = view.findViewById(R.id.i_1);
            name = view.findViewById(R.id.name);
            workspace = view.findViewById(R.id.workspace);
            status = view.findViewById(R.id.status);
        }
    }
}
