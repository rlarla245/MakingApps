package com.du.instagramprototypeproject.bottmNavigations;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import com.du.instagramprototypeproject.model.AlarmDTO;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static com.du.instagramprototypeproject.util.StatusCode.FRAGMENT_ARG;

public class AlarmFragment extends android.app.Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_alarm, container, false);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.alarmframgent_recyclerview);
        recyclerView.setAdapter(new AlarmRecyclerViewAdapter());
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        return view;
    }

    class AlarmRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private List<AlarmDTO> alarmDTOList = new ArrayList<>();

        public AlarmRecyclerViewAdapter() {
            // 현재 유저의 uid를 지정합니다.
            final String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

            // 내가 받은 알람들을 호출합니다.
            FirebaseDatabase.getInstance().getReference()
                    .child("alarms")
                    .orderByChild("destinationUid")
                    .equalTo(uid)
                    .addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    alarmDTOList.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        try {
                            AlarmDTO alarmDTO2 = snapshot.getValue(AlarmDTO.class);
                            // 내가 받은 알람들이 null 값이 아니고, 내가 나에게 보낸 알람이 아닐 경우!
                            if (alarmDTO2 != null && !alarmDTO2.uid.equals(uid)) {
                                alarmDTOList.add(snapshot.getValue(AlarmDTO.class));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    notifyDataSetChanged();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.item_commentview, parent, false);

            return new CustomViewHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
            final ImageView profileImage = ((CustomViewHolder) holder).profileImageView;

            // 유저 프로필 이미지 불러오기
            FirebaseDatabase.getInstance().getReference()
                    // 알람을 보낸 해당 uid의 프로필 이미지를 불러와야 합니다.
                    .child("profileImages").child(alarmDTOList.get(position).uid)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                @SuppressWarnings("VisibleForTests")
                                String url = dataSnapshot.getValue().toString();

                                Glide.with(getActivity())
                                        .load(url)
                                        .apply(new RequestOptions().circleCrop())
                                        .into(profileImage);
                            }

                            // 프로필 이미지가 없을 경우
                            else {
                                profileImage.setImageResource(R.mipmap.ic_launcher_foreground);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

            // 좋아요, 팔로우, 팔로잉 등의 여러 상황에 따라 다른 텍스트를 불러옵니다.
            switch (alarmDTOList.get(position).kind) {
                case 0:
                    String str_0 = alarmDTOList.get(position).userId + getString(R.string.alarm_favorite);

                    ((CustomViewHolder) holder).profileTextView.setText(str_0);

                    // 해당 피드를 누를 경우 해당하는 이미지로 넘겨줘야 더 좋겠죠?
                    ((CustomViewHolder)holder).profileTextView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(v.getContext(), CommentActivity.class);

                            // CommentActivity가 onStart()될 경우 불러오는 값들입니다.
                            // 데이터 베이스의 테이블 uid를 입력합니다.
                            intent.putExtra("imageUid", alarmDTOList.get(position).imageUid);

                            // 해당 유저의 uid 값을 입력합니다.
                            intent.putExtra("destinationUid", alarmDTOList.get(position).destinationUid);
                            startActivity(intent);
                        }
                    });
                    break;

                case 1:
                    String str_1 = alarmDTOList.get(position).userId + getString(R.string.alarm_who) + " '" +  alarmDTOList.get(position).message + "' " + getString(R.string.alarm_comment);

                    ((CustomViewHolder) holder).profileTextView.setText(str_1);

                    // 동일한 기능
                    ((CustomViewHolder)holder).profileTextView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(v.getContext(), CommentActivity.class);
                            // 데이터 베이스의 테이블 uid를 입력합니다.
                            intent.putExtra("imageUid", alarmDTOList.get(position).imageUid);

                            // 해당 유저의 uid 값을 입력합니다.
                            intent.putExtra("destinationUid", alarmDTOList.get(position).destinationUid);
                            startActivity(intent);
                        }
                    });
                    break;

                case 2:
                    String str_2 = alarmDTOList.get(position).userId + getString(R.string.alarm_follow);

                    ((CustomViewHolder) holder).profileTextView.setText(str_2);

                    // 팔로우 알람 시 팔로우를 누른 사람이 누군지 확인해야 되겠죠? 그 사람의 유저 프레그먼트로 이동합니다.
                    ((CustomViewHolder)holder).profileTextView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            android.app.Fragment fragment = new UserFragment();

                            // 해당 유저의 id와 이메일을 담아 유저 프레그먼트로 정보를 전송합니다.
                            // 유저 프레그먼트가 시작될 때 읽어들이는 데이터입니다.
                            Bundle bundle = new Bundle();
                            bundle.putString("destinationUid", alarmDTOList.get(position).uid);
                            bundle.putString("userId", alarmDTOList.get(position).userId);
                            bundle.putInt(FRAGMENT_ARG, 5);

                            fragment.setArguments(bundle);

                            getActivity().getFragmentManager().beginTransaction()
                                    .replace(R.id.main_content, fragment)
                                    .commit();
                        }
                    });
                    break;
            }
        }

        @Override
        public int getItemCount() {
            return alarmDTOList.size();
        }

        private class CustomViewHolder extends RecyclerView.ViewHolder {
            ImageView profileImageView;
            TextView profileTextView;

            CustomViewHolder(View itemView) {
                super(itemView);
                profileImageView = (ImageView) itemView.findViewById(R.id.commentviewitem_imageview_profile);
                profileTextView = (TextView) itemView.findViewById(R.id.commentviewitem_textview_profile);
            }
        }
    }
}
