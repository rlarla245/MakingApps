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

public class SidebarFifthMembers extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.sidebar_fifth_members, container, false);

        // 리사이클러 뷰 설정
        RecyclerView recyclerView = view.findViewById(R.id.sidebar_fifthmember_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        RecyclerViewAdapter recyclerViewAdapter = new RecyclerViewAdapter();
        recyclerView.setAdapter(recyclerViewAdapter);

        return view;
    }

    // 어댑터 설정
    class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private ArrayList<PeoplesDTOs> fifthMembers = new ArrayList<>();

        public RecyclerViewAdapter() {
            fifthMembers
                    .add(new PeoplesDTOs(R.drawable.academy_logo, "고재훈", "5기 학회원", "외식경영학과 13 졸업"));
            fifthMembers.add(new PeoplesDTOs(R.drawable.academy_logo, "박진우", "6대 학회장", "컨벤션경영학과 13 재학 중"));
            fifthMembers.add(new PeoplesDTOs(R.drawable.academy_logo, "신지원", "5기 학회원", "외식경영학과 14 졸업"));
            fifthMembers.add(new PeoplesDTOs(R.drawable.academy_logo, "장다해", "5기 학회원", "외식경영학과 14 졸업"));
            fifthMembers.add(new PeoplesDTOs(R.drawable.academy_logo, "박혜린", "5기 학회원", "컨벤션경영학과 14 재학 중"));
            fifthMembers.add(new PeoplesDTOs(R.drawable.academy_logo, "이다희", "6대 부학회장", "외식경영학과 15 재학 중"));
            fifthMembers.add(new PeoplesDTOs(R.drawable.academy_logo, "오정환", "5기 학회원", "외식경영학과 15 재학 중"));
            fifthMembers.add(new PeoplesDTOs(R.drawable.academy_logo, "안진주", "5기 학회원", "호텔경영학과 15 재학 중"));
            fifthMembers.add(new PeoplesDTOs(R.drawable.academy_logo, "정세빈", "5기 학회원", "호텔경영학과 15 재학 중"));
            fifthMembers.add(new PeoplesDTOs(R.drawable.academy_logo, "김미경", "5기 학회원", "외식경영학과 16 재학 중"));
            fifthMembers.add(new PeoplesDTOs(R.drawable.academy_logo, "현송희", "5기 학회원", "호텔경영학과 16 재학 중"));
            fifthMembers.add(new PeoplesDTOs(R.drawable.academy_logo, "채은빈", "5기 학회원", "외식경영학과 16 재학 중"));
            fifthMembers.add(new PeoplesDTOs(R.drawable.academy_logo, "이혜수", "5기 학회원", "외식경영학과 16 재학 중"));
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
            Glide.with(getView().getContext()).load(fifthMembers.get(position).imageView).into(((CustomViewHolder) holder).imageView);
            //        ((CustomViewHolder)holder).imageView.setImageResource(firstMembers.get(position).imageView);
            ((CustomViewHolder) holder).name.setText(fifthMembers.get(position).name);
            ((CustomViewHolder) holder).title.setText(fifthMembers.get(position).title);
            ((CustomViewHolder) holder).currentStatus.setText(fifthMembers.get(position).status);

            ((CustomViewHolder) holder).itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //
                }
            });
        }

        @Override
        public int getItemCount() {
            return fifthMembers.size();
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
