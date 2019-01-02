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
import com.bumptech.glide.request.RequestOptions;
import com.du.chattingapp.R;

import java.util.ArrayList;

public class SidebarFirstMembers extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.sidebar_first_members, container, false);

        // 리사이클러 뷰 설정
        RecyclerView recyclerView = view.findViewById(R.id.sidebar_firstmember_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        RecyclerViewAdapter recyclerViewAdapter = new RecyclerViewAdapter();
        recyclerView.setAdapter(recyclerViewAdapter);

        return view;
    }

    // 어댑터 설정
    class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private ArrayList<PeoplesDTOs> firstMembers = new ArrayList<>();
        public RecyclerViewAdapter() {
            firstMembers
                    .add(new PeoplesDTOs(R.drawable.i_1, "이재원", "초대 학회장", "현 컨설팅 업체 재직 중\n\n호텔경영, 컴퓨터공학 07 졸업"));
            firstMembers.add(new PeoplesDTOs(R.drawable.i_2, "김명준", "2대 학회장", "현 CJ 재직 중\n\n호텔경영학과 08 졸업"));
            firstMembers.add(new PeoplesDTOs(R.drawable.i_3, "구본형", "1기 학회원", "현 하나투어 재직 중\n\n관광경영학과 07 졸업"));
            firstMembers.add(new PeoplesDTOs(R.drawable.i_4, "김지민", "1기 학회원", "현 eBay Korea 재직 중\n\n외식경영학과 10 졸업"));
            firstMembers.add(new PeoplesDTOs(R.drawable.i_5, "송형주", "1대 학회장", "현 펜스테이트 대학 박사과정 재학(Hospitality)\n\n호텔경영학과 10 졸업"));
            firstMembers.add(new PeoplesDTOs(R.drawable.i_6, "정혜원", "1기 학회원", "컨벤션경영학과 12 졸업"));
            firstMembers.add(new PeoplesDTOs(R.drawable.academy_logo, "공지훈", "1기 학회원", "컨벤션경영학과 12 졸업"));
            firstMembers.add(new PeoplesDTOs(R.drawable.i_8, "신도영", "1기 학회원", "현 힐튼호텔 재직 중\n\n컨벤션경영학과 12 졸업"));
            firstMembers.add(new PeoplesDTOs(R.drawable.i_9, "백선영", "1기 HR팀장", "현 GS리테일 재직 중\n\n컨벤션경영학과 13 졸업"));
            firstMembers.add(new PeoplesDTOs(R.drawable.i_10, "조보람", "1기 기획팀장", "현 신한은행 재직 중\n\n컨벤션경영학과 13 졸업"));
            firstMembers.add(new PeoplesDTOs(R.drawable.i_11, "김두현", "2대 부학회장", "호텔경영, 영문학과 13 재학 중"));
            firstMembers.add(new PeoplesDTOs(R.drawable.academy_logo, "이성혁", "1기 홍보팀장", "호텔경영학과 13 재학 중"));
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.sidebarfirstmembers_item, parent, false);
            return new CustomViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            Glide.with(getView().getContext()).load(firstMembers.get(position).imageView).into(((CustomViewHolder)holder).imageView);
    //        ((CustomViewHolder)holder).imageView.setImageResource(firstMembers.get(position).imageView);
            ((CustomViewHolder)holder).name.setText(firstMembers.get(position).name);
            ((CustomViewHolder)holder).title.setText(firstMembers.get(position).title);
            ((CustomViewHolder)holder).currentStatus.setText(firstMembers.get(position).status);

            ((CustomViewHolder)holder).itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //
                }
            });
        }

        @Override
        public int getItemCount() {
            return firstMembers.size();
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