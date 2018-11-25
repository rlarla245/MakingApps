package com.example.user.fragment3.NavigationFragments;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.user.fragment3.R;

import java.util.ArrayList;

public class NavigationRecyclerviewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<NavigationCardViewDTOs> navigationCardViewDTOs = new ArrayList<>();

    public NavigationRecyclerviewAdapter() {
        navigationCardViewDTOs.add(new NavigationCardViewDTOs(R.drawable.f1, "전수현", "현 삼성전자 재직", "4기 부학회장\n\n컨벤션경영학과 13 졸업"));
        navigationCardViewDTOs.add(new NavigationCardViewDTOs(R.drawable.f2, "백선영", "현 GS 리테일 재직", "2기 기획팀장\n\n컨벤션경영학과 13 졸업"));
        navigationCardViewDTOs.add(new NavigationCardViewDTOs(R.drawable.f3, "신도영", "현 힐튼호텔 재직", "2기 학회원\n\n컨벤션경영학과 12 졸업"));
        navigationCardViewDTOs.add(new NavigationCardViewDTOs(R.drawable.f4, "신은지", "불명", "4기 부학회장\n\n외식경영학과 13 졸업"));
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
