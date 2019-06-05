package com.du.chattingapp;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.du.chattingapp.Chat.GroupMessageActivity;
import com.du.chattingapp.Chat.MessageActivity;
import com.du.chattingapp.Models.ChatModel;
import com.du.chattingapp.Models.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class SelectPeopleActivity extends AppCompatActivity {
    ChatModel chatModel = new ChatModel();
    List<ChatModel> chatRoomFromDB = new ArrayList<>();
    List<String> chatRoomUidFromDB = new ArrayList<>();

    // 왜 전역 변수로 선언?
    FloatingActionButton floatingActionButton;

    // 2명 이상 선택했을 경우 단체 채팅방으로 이동하기 위해 사용하는 카운트 변수입니다.
    int partnerCount = 0;

    // 단체 채팅방이 존재하는 지 확인하는 기준 값입니다.
    int criteriaNumber = 0;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_select_people);

        // 상태창 제거
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // 리사이클러 뷰 호출, 어댑터 설정, 레이아웃 매니저 설정
        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.selectpeopleactivity_recyclerview);
        recyclerView.setAdapter(new SelectPeopleRecyclerViewAdapter());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 플로팅 버튼 호출
        floatingActionButton = (FloatingActionButton) findViewById(R.id.selectpeopleactivity_floatingbutton);

        // 채팅방 정보, uid 값을 불러옵니다.
        FirebaseDatabase.getInstance().getReference().child("chatrooms")
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                chatRoomFromDB.clear();
                chatRoomUidFromDB.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    chatRoomFromDB.add(snapshot.getValue(ChatModel.class));
                    chatRoomUidFromDB.add(snapshot.getKey());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        // 플로팅 버튼을 누를 경우
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                // 최소한 나 포함 3명이어야 단체 채팅방을 열 수 있습니다.
                if (partnerCount < 2) {
                    Toast.makeText(SelectPeopleActivity.this, "적어도 두 명의 상대방이 필요합니다.", Toast.LENGTH_SHORT).show();
                }

                else {
                    // 생성하고 채팅방 접속을 바로 하는 것이 아니라 chatFragment에서 접근하므로 이런 식으로
                    // 코드를 작성해도 정상 작동합니다.
                    // 내 uid도 입력해야합니다.
                    String myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

                    chatModel.users.put(myUid, true);

                    // DB 내 모든 채팅방을 받아옵니다.
                    for (int i = 0; i < chatRoomFromDB.size(); i++) {
                        // 테스트 리스트
                        ArrayList<String> testRoomUsers = new ArrayList<>();
                        ArrayList<String> testChatUsers = new ArrayList<>();

                        // 채팅방 내 유저들 불러옵니다.
                        for (String users : chatRoomFromDB.get(i).users.keySet()) {
                            testRoomUsers.add(users);
                        }

                        // 선택한 유저들의 집합
                        for (String users : chatModel.users.keySet()) {
                            testChatUsers.add(users);
                        }

                        // 기존에 존재하는 채팅방인가?
                        if (testRoomUsers.toString().equals(testChatUsers.toString())) {
                            Intent intent = null;

                            String roomUid = chatRoomUidFromDB.get(i);
                            Toast.makeText(SelectPeopleActivity.this, "이미 존재하는 채팅방입니다.\n채팅방으로 이동합니다.", Toast.LENGTH_SHORT).show();

                            // 존재하는 단체 채팅방이므로, 해당 채팅방으로 인텐트를 넘겨줍니다.
                            intent = new Intent(v.getContext(), GroupMessageActivity.class);

                            // 방의 key 값(uid)을 넘겨주는 의미입니다.
                            // 상단의 방 uid 불러오는 게 가장 까다로운 코드로 보입니다.
                            intent.putExtra("destinationRoom", roomUid);

                            // 애니메이션을 주는 코드입니다.
                            // 마찬가지로 액티비티 전환 시에도 애니메이션 효과를 줄 수 있습니다.
                            ActivityOptions activityOptions = null;
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                                activityOptions = ActivityOptions.makeCustomAnimation(v.getContext(), R.anim.fromright, R.anim.toleft);
                                startActivity(intent, activityOptions.toBundle());
                            } else {
                                startActivity(intent);
                            }

                            // 존재하는 채팅방입니다.
                            criteriaNumber = 1;
                            break;
                        }
                    }

                    // 채팅방을 새로 생성하는 경우 해당 유저들이 있는 채팅방으로 이동시킵니다.
                        if (criteriaNumber == 0) {
                        Toast.makeText(SelectPeopleActivity.this, "채팅방이 생성되었습니다.", Toast.LENGTH_SHORT).show();
                        FirebaseDatabase.getInstance().getReference().child("chatrooms").push().setValue(chatModel)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        // DB 내 모든 채팅방을 받아옵니다.
                                        for (int i = 0; i < chatRoomFromDB.size(); i++) {
                                            // 테스트 리스트
                                            ArrayList<String> testRoom2Users = new ArrayList<>();
                                            ArrayList<String> testChat2Users = new ArrayList<>();

                                            // 존재하는 채팅방
                                            for (String users : chatRoomFromDB.get(i).users.keySet()) {
                                                testRoom2Users.add(users);
                                            }

                                            // 생성한 채팅방
                                            for (String users : chatModel.users.keySet()) {
                                                testChat2Users.add(users);
                                            }

                                            // 사실 존재하지 않는 채팅방을 생성하는 거라 이럴 필요가 없긴 한데
                                            // 단체 채팅방을 채팅방 uid로 이동시키고 있기 때문에 이러한 과정이
                                            // 불필요하게 생성됨. db에 채팅방 생성하고 그 uid값 바로 불러오면 상관없음
                                            if (testRoom2Users.toString().equals(testChat2Users.toString())) {
                                                Intent intent = null;
                                                String roomUid = chatRoomUidFromDB.get(i);

                                                intent = new Intent(v.getContext(), GroupMessageActivity.class);

                                                // 방의 key 값(uid)을 넘겨주는 의미입니다.
                                                // 상단의 방 uid 불러오는 게 가장 까다로운 코드로 보입니다.
                                                intent.putExtra("destinationRoom", roomUid);

                                                // 애니메이션을 주는 코드입니다.
                                                // 마찬가지로 액티비티 전환 시에도 애니메이션 효과를 줄 수 있습니다.
                                                ActivityOptions activityOptions = null;
                                                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                                                    activityOptions = ActivityOptions.makeCustomAnimation(v.getContext(), R.anim.fromright, R.anim.toleft);
                                                    startActivity(intent, activityOptions.toBundle());
                                                } else {
                                                    startActivity(intent);
                                                }
                                            }
                                        }
                                    }
                                });
                        // 단체 채팅방 열었으니 선택창은 꺼줍니다.
                        // finish();
                    }

                    // 모든 과정을 마쳤으므로
                    // 다시 원래대로
                    criteriaNumber = 0;
                }
            }
        });
    }

    // 어댑터 설정
    // 친구 목록을 불러옵니다.
    class SelectPeopleRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        List<UserModel> userModels;

        public SelectPeopleRecyclerViewAdapter() {
            userModels = new ArrayList<>();
            // 내 아이디
            final String myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

            // 유저들에 대한 데이터를 불러옵니다.
            FirebaseDatabase.getInstance().getReference().child("users").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // 친구 목록 추가 시 데이터를 불러오게 되는데 다음과 같은 코드가 없으면 똑같은 친구들을 또 불러옵니다.
                    userModels.clear();

                    // dataSnapshot이 데이터이므로 반복문을 통해 리스트에 넣어줍시다.
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        UserModel userModel = snapshot.getValue(UserModel.class);

                        // 내 id는 빼고 불러와야 합니다.
                        if (userModel.uid.equals(myUid)) {
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
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.peoples_fragment_item_select, parent, false);

            return new CustomViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
            // 프로필 이미지를 호출합니다.
            Glide.with
                    (holder.itemView.getContext())
                    .load(userModels.get(position).profileImageUri)
                    .apply(new RequestOptions().circleCrop())
                    .into(((CustomViewHolder) holder).imageView);

            // 이름 설정합니다.
            ((CustomViewHolder) holder).textView.setText(userModels.get(position).userName);

            // 한 명의 유저를 누를 경우 개인 채팅방으로 자동 이동됩니다.
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent chat_intent = new Intent(view.getContext(), MessageActivity.class);
                    chat_intent.putExtra("destinationUid", userModels.get(position).uid);

                    // 젤리빈 이상부터 적용합니다. 애니메이션 효과를 줘서 액티비티를 전환합니다.
                    ActivityOptions activityOptions = null;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                        activityOptions = ActivityOptions.makeCustomAnimation(view.getContext(), R.anim.fromright, R.anim.toleft);
                        startActivity(chat_intent, activityOptions.toBundle());
                    }
                }
            });

            // 상태메시지가 없을 경우 출력합니다. 마찬가지로 왜 조건문을 썼는지는 모르겠음.
            if (userModels.get(position).status_message != null) {
                ((CustomViewHolder) holder).comment.setText(userModels.get(position).status_message);
            }

            ((CustomViewHolder) holder).
                    // 체크 여부에 따라 반응합니다. 요거시 여기서 가장 중요합니다.
                            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    // 체크 된상태, 즉 불러온 상태
                    if (b) {
                        // 체크된 유저의 uid를 불러옵시다. 이를 true 값으로 표시합니다.
                        // users 해시맵에 담습니다.
                        // 실제로 채팅방을 여는 코드는 아니므로 여기서 해당 유저들을 다 담아줘야 합니다 + 나는 제외
                        chatModel.users.put(userModels.get(position).uid, true);
                        partnerCount += 1;

                    } else {
                        // 체크 취소 상태
                        chatModel.users.remove(userModels.get(position).uid);
                        partnerCount -= 1;
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return userModels.size();
        }

        private class CustomViewHolder extends RecyclerView.ViewHolder {
            public ImageView imageView;
            public TextView textView;
            public TextView comment;
            public CheckBox checkBox;

            public CustomViewHolder(View view) {
                super(view);
                imageView = (ImageView) view.findViewById(R.id.peoples_fragment_item_imageview);
                textView = (TextView) view.findViewById(R.id.peoples_fragment_item_textview);
                comment = (TextView) view.findViewById(R.id.peoples_fragment_item_textview_message);
                checkBox = (CheckBox) view.findViewById(R.id.peoples_fragment_item_select_checkbox);
            }
        }
    }
}