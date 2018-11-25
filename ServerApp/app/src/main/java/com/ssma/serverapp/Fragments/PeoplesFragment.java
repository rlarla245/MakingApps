package com.ssma.serverapp.Fragments;

import android.app.ActivityOptions;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ssma.serverapp.Chat.MessageActivity;
import com.ssma.serverapp.Model.UserModel;
import com.ssma.serverapp.R;

import java.util.ArrayList;
import java.util.List;

public class PeoplesFragment extends Fragment {
    public static List<UserModel> userModels;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.peoples_fragment, container, false);

        if (FirebaseAuth.getInstance().getCurrentUser().getUid() == null) {
            getActivity().finish();
        }

        // 리사이클러 뷰 호출, 레이아웃 매니저 생성, 어댑터 생성
        RecyclerView recyclerView =
                (RecyclerView) view.findViewById(R.id.peoples_fragment_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(inflater.getContext()));
        recyclerView.setAdapter(new PeopleFragmentRecyclerViewAdapter());

        // 플로팅 버튼 호출
        FloatingActionButton floatingActionButton = (FloatingActionButton) view.findViewById(R.id.peoples_fragment_floatingbutton);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 대화 상대를 정해줘야죠.
                startActivity(new Intent(view.getContext(), SelectPeopleActivity.class));
            }
        });
        return view;
    }

    // 액티비티가 띄워질 경우 자동으로 실행하는 메소드입니다.
    @Override
    public void onStart() {
        super.onStart();
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            getActivity().finish();
        }
    }

    class PeopleFragmentRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        // 생성자를 통해 리사이클러 뷰가 담을 '초기' 데이터들을 설정 및 데이터베이스에서 호출해줍니다.
        public PeopleFragmentRecyclerViewAdapter() {
            userModels = new ArrayList<>();

            // 내 아이디
            final String myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

            try {
                FirebaseDatabase.getInstance().getReference().child("users").addValueEventListener(new ValueEventListener() {
                    @Override
                    // 서버에서 넘어온 데이터를 의미합니다.
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // 친구 목록 추가 시 데이터를 불러오게 되는데 다음과 같은 코드가 없으면 똑같은 친구들을 또 불러옵니다.
                        userModels.clear();

                        // dataSnapshot이 데이터이므로 반복문을 통해 리스트에 넣어줍시다.
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            UserModel userModel = snapshot.getValue(UserModel.class);

                            // 해당 리스트에 내 uid 값과 동일할 경우 넘어갑니다.
                            if (userModel.uid.equals(myUid)) {
                                // 건너뜁니다.
                                continue;
                            }
                            userModels.add(userModel);
                        }
                        // 새로고침 버튼입니다. 이게 없으면 친구 목록이 뜨지 않습니다.
                        notifyDataSetChanged();
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            } catch (NullPointerException e) {
                return;
            }
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.peoples_fragment_item, parent, false);
            return new CustomViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
            // 프로필 이미지를 띄웁시다.
            Glide.with
                    (holder.itemView.getContext())
                    // load 메소드가 가장 중요합니다.
                    .load(userModels.get(position).profileImageUri)
                    .apply(new RequestOptions().circleCrop())
                    .into(((CustomViewHolder) holder).imageView);

            // 이름 설정
            ((CustomViewHolder) holder).textView.setText(userModels.get(position).userName);

            // itemView를 누를 경우
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // 채팅방으로 이동합니다.
                    Intent chat_intent = new Intent(view.getContext(), MessageActivity.class);

                    // 상대방 uid를 의미합니다. 채팅창으로 이동되는 인텐트에 입력합니다.
                    // 포지션 값의 uid로 연결이 되는 이유는 애초에 해당 포지션값의 itemView를 눌렀으니까.
                    chat_intent.putExtra("destinationUid", userModels.get(position).uid);

                    // 젤리빈 이상부터 적용합니다.
                    ActivityOptions activityOptions = null;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                        // 애니메이션 효과를 줍니다.
                        // 액티비티를 넘길 때에는 아래와 같은 방식으로 애니메이션 효과를 줄 수 있습니다.
                        activityOptions = ActivityOptions.makeCustomAnimation(view.getContext(), R.anim.fromright, R.anim.toleft);
                        startActivity(chat_intent, activityOptions.toBundle());
                    }
                }
            });

            // 상태 메시지 출력. if 조건문을 왜 쓴건지..?
            if (userModels.get(position).status_message != null) {
                ((CustomViewHolder) holder).comment.setText(userModels.get(position).status_message);
            }
        }

        @Override
        public int getItemCount() {
            // 데이터베이스에 가입된 유저들을 출력합니다.
            return userModels.size();
        }

        private class CustomViewHolder extends RecyclerView.ViewHolder {
            public ImageView imageView;
            public TextView textView;
            public TextView comment;

            public CustomViewHolder(View view) {
                super(view);
                imageView = (ImageView) view.findViewById(R.id.peoples_fragment_item_imageview);
                textView = (TextView) view.findViewById(R.id.peoples_fragment_item_textview);
                comment = (TextView) view.findViewById(R.id.peoples_fragment_item_textview_message);
            }
        }
    }
}
