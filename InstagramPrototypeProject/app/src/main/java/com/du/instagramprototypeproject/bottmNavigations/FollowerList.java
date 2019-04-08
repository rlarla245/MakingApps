package com.du.instagramprototypeproject.bottmNavigations;

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
import com.du.instagramprototypeproject.R;
import com.du.instagramprototypeproject.model.ContentDTO;
import com.du.instagramprototypeproject.model.FollowDTO;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static com.du.instagramprototypeproject.util.StatusCode.FRAGMENT_ARG;

public class FollowerList extends Fragment {
    public String uid;
    public ArrayList<String> followers = new ArrayList<>();
    public ArrayList<String> imageUrl = new ArrayList<>();
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_follow, container, false);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.followfragment_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(new FollowerRecyclerViewAdapter());

        return view;
    }

    class FollowerRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        // 리사이클러 뷰의 경우 생성자를 통해 초기 데이터들을 한 번에 불러오고 시작하게 됩니다.
        public FollowerRecyclerViewAdapter() {
            // 에러 방지
            if (getArguments() != null) {
                uid = getArguments().getString("uid");
            }

            // 팔로워 리스트 불러오기
            databaseReference.child("users").child(uid).child("followers")
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            followers.clear();

                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                followers.add(snapshot.getKey());
                            }
                            notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_commentview, parent, false);

            return new CustomViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
            databaseReference.child("profileImages").child(followers.get(position)).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        @SuppressWarnings("VisibleForTests")
                        String url = dataSnapshot.getValue().toString();

                        // 프로필 이미지를 적용합니다.
                        Glide.with(holder.itemView.getContext())
                                .load(url)
                                .apply(new RequestOptions().circleCrop())
                                .into(((CustomViewHolder)holder).userImageView);
                    }
                    else {
                        ((CustomViewHolder)holder).userImageView.setImageResource(R.mipmap.ic_launcher_foreground);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


            // 해당 uid의 유저 데이터 정보로 들어갑니다.
            databaseReference.child("users").child(followers.get(position)).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    FollowDTO followDTO = dataSnapshot.getValue(FollowDTO.class);

                    String userEmail = followDTO.userId.toString();
                    ((CustomViewHolder)holder).userId.setText(userEmail);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            // 팔로워 유저를 누를 경우 해당 유저의 유저 프레그먼트로 이동합니다.
            ((CustomViewHolder)holder).userId.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Fragment fragment = new UserFragment();

                    // 해당 유저의 id와 이메일을 담아 유저 프레그먼트로 정보를 전송합니다.
                    // 유저 프레그먼트가 시작될 때 읽어들이는 데이터입니다.
                    Bundle bundle = new Bundle();
                    bundle.putString("destinationUid", followers.get(position));
                    bundle.putString("userId", ((CustomViewHolder)holder).userId.getText().toString());
                    bundle.putInt(FRAGMENT_ARG, 5);

                    fragment.setArguments(bundle);

                    getActivity().getFragmentManager().beginTransaction()
                            .replace(R.id.main_content, fragment)
                            .commit();
                }
            });

            /*
            if (imageUrl.size() == followers.size()) {
                Glide.with(holder.itemView.getContext()).load(imageUrl.get(position))
                        .apply(new RequestOptions().circleCrop()).into(((CustomViewHolder) holder).userImageView);
            }
            */

            /*
            // 유저 이메일 받아오려고 images 데이터 테이블에 접근한 듯
            // 에러 - image를 올리지 않은 유저의 경우 공란이 발생
            if (followers.size() != 0) {
                databaseReference.child("images").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            ContentDTO contentDTO = snapshot.getValue(ContentDTO.class);

                            if (followers.get(position).equals(contentDTO.uid)) {
                                ((CustomViewHolder)holder).userId.setText(contentDTO.userId);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
            */
        }

        @Override
        public int getItemCount() {
            return followers.size();
        }

        private class CustomViewHolder extends RecyclerView.ViewHolder {
            public ImageView userImageView;
            public TextView userId;

            public CustomViewHolder(View view) {
                super(view);
                userImageView = view.findViewById(R.id.commentviewitem_imageview_profile);
                userId = view.findViewById(R.id.commentviewitem_textview_profile);
            }
        }
    }
}
