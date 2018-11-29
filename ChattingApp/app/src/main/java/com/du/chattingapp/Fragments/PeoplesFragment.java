package com.du.chattingapp.Fragments;

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
import com.du.chattingapp.Chat.MessageActivity;
import com.du.chattingapp.Models.ChatModel;
import com.du.chattingapp.Models.UserModel;
import com.du.chattingapp.R;
import com.du.chattingapp.SelectPeopleActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class PeoplesFragment extends Fragment {
    // 유저들을 담는 리스트입니다.
    public static List<UserModel> userModels;

    // uid 및 상대 uid
    public String uid;
    public String destinationUid;

    // chatModel 담읍시다.
    public List<ChatModel> chatModels = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.peoples_fragment, container, false);

        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // 리사이클러 뷰 호출, 레이아웃 매니저, 어댑터 설정
        RecyclerView recyclerView =
                (RecyclerView) view.findViewById(R.id.peoples_fragment_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(inflater.getContext()));
        recyclerView.setAdapter(new PeopleFragmentRecyclerViewAdapter());

        // 플로팅 버튼 호출 및 클릭 동작 설정
        FloatingActionButton floatingActionButton = (FloatingActionButton) view.findViewById(R.id.peoples_fragment_floatingbutton);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(v.getContext(), SelectPeopleActivity.class));
            }
        });

        /*------------------------------------------------------------------------------------------------------*/
        FirebaseDatabase.getInstance().getReference().child("chatrooms")
                .orderByChild("users/" + uid).equalTo(true).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                chatModels.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (snapshot.getValue(ChatModel.class).users.size() == 2) {
                        ChatModel serverChatmodel = snapshot.getValue(ChatModel.class);
                        chatModels.add(serverChatmodel);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        /*-------------------------------------------------------------------------------------------------------*/
        return view;
    }

    // 어댑터 설정
    class PeopleFragmentRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        // 생성자를 통해 초기 데이터들을 불러옵니다.
        public PeopleFragmentRecyclerViewAdapter() {
            userModels = new ArrayList<>();

            // 내 아이디
            final String myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

            // 유저 데이터들을 불러옵니다.
            FirebaseDatabase.getInstance().getReference().child("users").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // 친구 목록 추가 시 데이터를 불러오게 되는데 다음과 같은 코드가 없으면 똑같은 친구들을 또 불러옵니다.
                    userModels.clear();

                    // "dataSnapShot"이 데이터이므로 반복문을 통해 리스트에 넣어줍시다.
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        try {
                            UserModel userModel = snapshot.getValue(UserModel.class);

                            // 내 "uid"를 담고 있으면 넘어갑니다.
                            if (userModel.uid.equals(myUid)) {
                                continue;
                            }
                            userModels.add(userModel);
                        } catch (NullPointerException e2) {
                            return;
                        }
                    }

                    // 새로고침 버튼입니다. 이게 없으면 친구 목록이 뜨지 않습니다.
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
            // 아이템 뷰 설정
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.peoples_fragment_item, parent, false);
            return new CustomViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
            // 유저들의 프로필 이미지를 불러옵니다.
            Glide.with
                    (holder.itemView.getContext())
                    .load(userModels.get(position).profileImageUri)
                    .apply(new RequestOptions().circleCrop())
                    .into(((CustomViewHolder) holder).imageView);

            // 유저들의 이름을 설정합니다.
            ((CustomViewHolder) holder).textView.setText(userModels.get(position).userName);

            // 각각의 아이템 뷰를 누를 경우 채팅방으로 입장합니다.
            // 해당 데이터들의 룸 uid를 일괄 확인하는게 필요합니다. -> 불러오는 건 전체를 불러오는 건 아닌가?
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    // 상대방 uid 지정
                    destinationUid = userModels.get(position).uid;
                    System.out.println("상대방 uid 확인: " + destinationUid);

                    for (ChatModel testChatmodels : chatModels) {
                        // 기존에 나와 상대방이 포함된 채팅방일 경우
                        // 안되면 destinationUid를 userModels.get(position).uid로 표시해보자
                        if (testChatmodels != null && testChatmodels.users.size() == 2
                                && testChatmodels.users.containsKey(uid) && testChatmodels.users.containsKey(destinationUid)) {

                            // 메시지 액티비티로 인텐트 넘기면 될 듯?
                            Intent chat_intent = new Intent(view.getContext(), MessageActivity.class);

                            // 1:1 채팅일 경우 상대방 uid를 액티비티에 넘겨줍니다      .
                            chat_intent.putExtra("destinationUid", userModels.get(position).uid);

                            // 젤리빈 이상부터 적용합니다.
                            ActivityOptions activityOptions = null;
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                                activityOptions = ActivityOptions.makeCustomAnimation(view.getContext(), R.anim.fromright, R.anim.toleft);
                                startActivity(chat_intent, activityOptions.toBundle());
                            }
                            return;
                        }
                    }

                    // 채팅방이 비었을 경우
                    ChatModel newRoom = new ChatModel();
                    newRoom.users.put(uid, true);
                    newRoom.users.put(destinationUid, true);
                    FirebaseDatabase.getInstance().getReference().child("chatrooms").push().setValue(newRoom)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    // 메시지 액티비티로 인텐트 넘기면 될 듯?
                                    Intent chat_intent = new Intent(view.getContext(), MessageActivity.class);

                                    // 1:1 채팅일 경우 상대방 uid를 액티비티에 넘겨줍니다      .
                                    chat_intent.putExtra("destinationUid", userModels.get(position).uid);

                                    // 젤리빈 이상부터 적용합니다.
                                    ActivityOptions activityOptions = null;
                                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                                        activityOptions = ActivityOptions.makeCustomAnimation(view.getContext(), R.anim.fromright, R.anim.toleft);
                                        startActivity(chat_intent, activityOptions.toBundle());
                                    }
                                }
                            });

                    // 하단이 기존 메소드
                    /*------------------------------------------------------------------------------------------------*/
                    /*
                    Intent chat_intent = new Intent(view.getContext(), MessageActivity.class);

                    // 1:1 채팅일 경우 상대방 uid를 액티비티에 넘겨줍니다      .
                    chat_intent.putExtra("destinationUid", userModels.get(position).uid);

                    // 젤리빈 이상부터 적용합니다.
                    ActivityOptions activityOptions = null;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                        activityOptions = ActivityOptions.makeCustomAnimation(view.getContext(), R.anim.fromright, R.anim.toleft);
                        startActivity(chat_intent, activityOptions.toBundle());
                    }
                    */
                }
            });

            // 상태메시지가 공백이 아닐 경우 입력합니다.
            if (userModels.get(position).status_message != null) {
                ((CustomViewHolder) holder).comment.setText(userModels.get(position).status_message);
            }
        }

        @Override
        public int getItemCount() {
            return userModels.size();
        }

        private class CustomViewHolder extends RecyclerView.ViewHolder {
            public ImageView imageView;
            public TextView textView;
            public TextView comment;

            // 생성자 활용
            public CustomViewHolder(View view) {
                super(view);
                imageView = (ImageView) view.findViewById(R.id.peoples_fragment_item_imageview);
                textView = (TextView) view.findViewById(R.id.peoples_fragment_item_textview);
                comment = (TextView) view.findViewById(R.id.peoples_fragment_item_textview_message);
            }
        }
    }
}
