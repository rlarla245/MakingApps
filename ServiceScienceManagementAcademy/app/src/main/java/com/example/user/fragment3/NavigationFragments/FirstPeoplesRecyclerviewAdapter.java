package com.example.user.fragment3.NavigationFragments;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.user.fragment3.R;

import java.util.ArrayList;

public class FirstPeoplesRecyclerviewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<FirstPeoplesCardViewDTOs> navigationCardViewDTOs = new ArrayList<>();

    public FirstPeoplesRecyclerviewAdapter() {
        navigationCardViewDTOs.add(new FirstPeoplesCardViewDTOs(R.drawable.people, "김명준", "현 CJ 재직", "2대 부학회장\n\n호텔경영학과 08 졸업"));
        navigationCardViewDTOs.add(new FirstPeoplesCardViewDTOs(R.drawable.people, "구본형", "현 하나투어 재직", "초대 학회원\n\n관광경영학과 07 졸업"));
        navigationCardViewDTOs.add(new FirstPeoplesCardViewDTOs(R.drawable.people, "김지민", "현 Ebay 재직", "1기 학회원\n\n외식경영학과 10 졸업"));
        navigationCardViewDTOs.add(new FirstPeoplesCardViewDTOs(R.drawable.people, "송형주", "현 대학원 재학", "초대 학회장\n\n호텔경영학과 10 졸업"));
        navigationCardViewDTOs.add(new FirstPeoplesCardViewDTOs(R.drawable.people, "정혜원", "불명", "1기 학회원\n\n컨벤션경영학과 12 졸업"));
        navigationCardViewDTOs.add(new FirstPeoplesCardViewDTOs(R.drawable.people, "공지훈", "불명", "1기 학회원\n\n컨벤션경영학과 12 졸업"));
        navigationCardViewDTOs.add(new FirstPeoplesCardViewDTOs(R.drawable.people, "신도영", "현 힐튼호텔 재직", "1기 학회원\n\n컨벤션경영학과 12 졸업"));
        navigationCardViewDTOs.add(new FirstPeoplesCardViewDTOs(R.drawable.people, "백선영", "현 GS리테일 재직", "1기 HR팀장\n\n컨벤션경영학과 13 졸업"));
        navigationCardViewDTOs.add(new FirstPeoplesCardViewDTOs(R.drawable.people, "조보람", "현 신한은행 재직", "1기 기획팀장\n\n컨벤션경영학과 13 졸업"));
        navigationCardViewDTOs.add(new FirstPeoplesCardViewDTOs(R.drawable.people, "김두현", "학생", "2대 부학회장\n\n호텔경영, 영문학과 13 재학"));
        navigationCardViewDTOs.add(new FirstPeoplesCardViewDTOs(R.drawable.people, "이성혁", "학생", "1기 홍보팀장\n\n호텔경영학과 13 재학"));
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.navigation_fragment2_cardview, parent, false);
        return new RowCell(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((RowCell)holder).imageView.setImageResource(navigationCardViewDTOs.get(position).imageview);
        ((RowCell)holder).name.setText(navigationCardViewDTOs.get(position).name);
        ((RowCell)holder).workspace.setText(navigationCardViewDTOs.get(position).workspace);
        ((RowCell)holder).status.setText(navigationCardViewDTOs.get(position).status);

    }

    @Override
    public int getItemCount() {
        return navigationCardViewDTOs.size();
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
