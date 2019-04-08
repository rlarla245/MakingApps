package com.du.instagramprototypeproject.bottmNavigations;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.du.instagramprototypeproject.R;
import com.du.instagramprototypeproject.model.ContentDTO;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class GridFragment extends android.app.Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // 해당 레이아웃을 불러옵니다.
        View view = inflater.inflate(R.layout.fragment_grid, container, false);

        // 리사이클러 뷰를 설정합니다.
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.gridfragment_recyclerview);

        // 어댑터 및 레이아웃 매니저를 설정합니다.
        recyclerView.setAdapter(new GridFragmentRecyclerViewAdatper());

        // getActivity 대신 액티비티일 경우 this 처리를 하면 됩니다.
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));

        return view;
    }

    // 어댑터 설정
    class GridFragmentRecyclerViewAdatper extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        // 컨텐츠 데이터들을 불러오기 위해 리스트를 설정합니다.
        private ArrayList<ContentDTO> contentDTOs;

        public GridFragmentRecyclerViewAdatper() {
            contentDTOs = new ArrayList<>();
            // images 데이터 테이블의 데이터들을 '계속해서' 불러옵니다.
            FirebaseDatabase.getInstance().getReference().child("images")
                    .addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    contentDTOs.clear();
                    // 반복문을 통해 데이터를 불러옵니다.
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        contentDTOs.add(snapshot.getValue(ContentDTO.class));
                    }

                    // 데이터 테이블 내 "comments" child가 존재해 빈 공란의 이미지를 불러오게 됩니다.
                    // 이를 제거해야만 정상 작동합니다.
                    // 애초에 댓글 데이터들만 모이는 데이터 테이블을 만들었으면 일어나지 않았을 버그입니다.
                    contentDTOs.remove(contentDTOs.size() - 1);
                    notifyDataSetChanged();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    //
                }
            });
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            // 현재 사이즈 뷰 화면 크기의 가로 크기의 1/3값으로 설정
            int width = getResources().getDisplayMetrics().widthPixels / 3;

            // 다른 방식으로 표현 가능
            ImageView imageView = new ImageView(parent.getContext());
            imageView.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, width));

            return new CustomViewHolder(imageView);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
            // downloadUri로 지정된 uri를 불러와 이미지 뷰에 삽입합니다.
            Glide.with(holder.itemView.getContext())
                    .load(contentDTOs.get(position).imageUrl)
                    .apply(new RequestOptions().centerCrop())
                    .into(((CustomViewHolder) holder).imageView);

            // 그리드 뷰의 사진을 누를 경우 해당 유저로 이동합니다.
            ((CustomViewHolder)holder).imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    // 프레그먼트 번들에 해당 요소들을 넣습니다. 1) 유저 UID 2) 유저 이메일
                    bundle.putString("destinationUid", contentDTOs.get(position).uid);
                    bundle.putString("userId", contentDTOs.get(position).userId);

                    Fragment fragment = new UserFragment();
                    fragment.setArguments(bundle);

                    getFragmentManager().beginTransaction().replace(R.id.main_content, fragment).commit();
                }
            });
        }

        @Override
        public int getItemCount() {
            return contentDTOs.size();
        }

        private class CustomViewHolder extends RecyclerView.ViewHolder {
            public ImageView imageView;

            public CustomViewHolder(ImageView imageView) {
                // 확실히 독특한 방식이긴 함
                super(imageView);
                this.imageView = imageView;
            }
        }
    }
}


