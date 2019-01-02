package com.du.chattingapp.Sidebars;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.du.chattingapp.R;

import java.util.ArrayList;

public class SidebarFourthMembers extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.sidebar_fourth_members, container, false);

        // 리사이클러 뷰 설정
        RecyclerView recyclerView = view.findViewById(R.id.sidebar_fourthmember_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        RecyclerViewAdapter recyclerViewAdapter = new RecyclerViewAdapter();
        recyclerView.setAdapter(recyclerViewAdapter);

        return view;
    }

    // 어댑터 설정
    class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private ArrayList<PeoplesDTOs> fourthMembers = new ArrayList<>();

        public RecyclerViewAdapter() {
            fourthMembers
                    .add(new PeoplesDTOs(R.drawable.academy_logo, "박세환", "5대 부학회장", "호텔경영학과 12 졸업"));
            fourthMembers.add(new PeoplesDTOs(R.drawable.academy_logo, "여인호", "4기 학회원", "외식경영학과 12 졸업"));
            fourthMembers.add(new PeoplesDTOs(R.drawable.academy_logo, "경동훈", "4기 학회원", "외식경영학과 12 졸업"));
            fourthMembers.add(new PeoplesDTOs(R.drawable.academy_logo, "양문희", "4기 학회원", "호텔경영학과 13 졸업"));
            fourthMembers.add(new PeoplesDTOs(R.drawable.academy_logo, "김수지", "4기 학회원", "외식경영학과 13 졸업"));
            fourthMembers.add(new PeoplesDTOs(R.drawable.academy_logo, "허지선", "4기 학회원", "컨벤션경영학과 13 졸업"));
            fourthMembers.add(new PeoplesDTOs(R.drawable.academy_logo, "박민지", "4기 학회원", "호텔경영학과 14 졸업"));
            fourthMembers.add(new PeoplesDTOs(R.drawable.i4_1, "장지현", "4기 학회원", "호텔경영학과 14 졸업"));
            fourthMembers.add(new PeoplesDTOs(R.drawable.academy_logo, "이예진", "4기 학회원", "외식경영학과 14 졸업"));
            fourthMembers.add(new PeoplesDTOs(R.drawable.academy_logo, "박일주", "4기 학회원", "호텔경영학과 14 재학 중"));
            fourthMembers.add(new PeoplesDTOs(R.drawable.academy_logo, "곽다영", "5대 학회장", "외식경영학과 14 졸업"));
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.sidebarsecondmembers_item, parent, false);
            return new CustomViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            Glide.with(getView().getContext()).load(fourthMembers.get(position).imageView).into(((CustomViewHolder) holder).imageView);
            //        ((CustomViewHolder)holder).imageView.setImageResource(firstMembers.get(position).imageView);
            ((CustomViewHolder) holder).name.setText(fourthMembers.get(position).name);
            ((CustomViewHolder) holder).title.setText(fourthMembers.get(position).title);
            ((CustomViewHolder) holder).currentStatus.setText(fourthMembers.get(position).status);

            ((CustomViewHolder) holder).itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //
                }
            });
        }

        @Override
        public int getItemCount() {
            return fourthMembers.size();
        }

        // 아이템 위젯들 불러오기
        private class CustomViewHolder extends RecyclerView.ViewHolder {
            public ImageView imageView;
            public TextView name, title, currentStatus;

            public CustomViewHolder(View view) {
                super(view);
                imageView = view.findViewById(R.id.sidebar_firstmember_imageview);
                name = view.findViewById(R.id.sidebar_firstmembers_textview_name);
                title = view.findViewById(R.id.sidebar_firstmember_textview_title);
                currentStatus = view.findViewById(R.id.sidebar_firstmembers_textview_currentstatus);
            }
        }
    }
}
