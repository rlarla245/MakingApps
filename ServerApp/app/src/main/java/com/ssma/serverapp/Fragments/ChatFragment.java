package com.ssma.serverapp.Fragments;

import android.app.ActivityOptions;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ssma.serverapp.Chat.GroupMessageActivity;
import com.ssma.serverapp.Chat.MessageActivity;
import com.ssma.serverapp.LoginActivity;
import com.ssma.serverapp.Model.ChatModel;
import com.ssma.serverapp.Model.UserModel;
import com.ssma.serverapp.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.TreeMap;

public class ChatFragment extends Fragment {
    // Timestamp가 유니코드로 저장되어 있으므로 변환합니다.
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm");

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.chatfragment, container, false);

        if (FirebaseAuth.getInstance().getCurrentUser().getUid() == null) {
            startActivity(new Intent(view.getContext(), LoginActivity.class));
        }

        // 리사이클러 뷰 호출, 레이아웃 매니저 설정, 어댑터 설정
        RecyclerView recyclerView = (RecyclerView)view.findViewById(R.id.chatfragment_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(inflater.getContext()));
        recyclerView.setAdapter(new RecyclerviewAdapter());

        return view;
    }

    class RecyclerviewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private String uid;
        private List<ChatModel> chatRoomModels = new ArrayList<>();

        // 키 리스트 생성
        private List<String> keys = new ArrayList<>();

        // 대화하는 사람들의 데이터를 모아봅시다.
        private ArrayList<String> destinationUsers = new ArrayList<>();

        // 생성자를 통해 초기 세팅 데이터들을 불러옵니다.
        public RecyclerviewAdapter() {
            // 본인 uid
            uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

            FirebaseDatabase.getInstance().getReference().child("chatrooms")
                // 내가 속한 채팅방에 들어갈 수 있습니다.
                // 슬래쉬(/)를 통해 하위 데이터 집단에 접근할 수 있습니다. 데이터베이스 참고 바랍니다.
                .orderByChild("users/" + uid).equalTo(true).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // 재 진입 시 모든 메시지 코멘트가 중복해서 호출됩니다.
                    chatRoomModels.clear();

                    for (DataSnapshot item : dataSnapshot.getChildren()) {
                        chatRoomModels.add(item.getValue(ChatModel.class));

                        // 해당 메시지 채팅룸 uid를 의미합니다.
                        keys.add(item.getKey());
                    }
                    // 새로고침
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
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chatfragment_item, parent, false);

            return new CustomViewHolder(view);
        }

       @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
            final CustomViewHolder customViewHolder = (CustomViewHolder)holder;
            // 상대방 uid를 null값으로 처리합니다.
            String destinationUid = null;

            // 챗방에 있는 유저들 체크(키 값만 불러옵니다.)
            for (String user : chatRoomModels.get(position).users.keySet()) {
                // 내가 아닌 사람을 상대방 uid로 지정
                if (!user.equals(uid)) {
                    destinationUid = user;

                    // 현재 대화하고 있는 유저들을 넣어 봅시다.
                    destinationUsers.add(destinationUid);
                }
            }

            // 현재 1:1 채팅기능에 중점을 뒀었기 때문에 여기서 채팅방의 '마지막 유저'의 이미지 및 이름만 불러오게 되므로
            // 이를 수정해봅시다.
            if (destinationUsers.size() == 1) {
                // 데이터베이스에서 그 유저가 누구인지 불러옵시다.
                FirebaseDatabase.getInstance().getReference().child("users").child(destinationUid)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                UserModel destiUserModel = dataSnapshot.getValue(UserModel.class);

                                // 프로필 이미지 불러옵니다.
                                Glide.with(customViewHolder.itemView.getContext())
                                        .load(destiUserModel.profileImageUri)
                                        .apply(new RequestOptions().circleCrop())
                                        .into(customViewHolder.profileimage);

                                // 상대방 이름을 지정합니다.
                                customViewHolder.dest_name.setText(destiUserModel.userName);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
            }

            // 단체 채팅방의 경우 어떻게 될지 봅시다.
            else {
                customViewHolder.profileimage.setImageResource(R.drawable.academy_logo);
                customViewHolder.dest_name.setText("그룹 채팅방");
            }

            // 메시지를 내림 차순으로 정렬 후 마지막 메시지의 키 값을 가져오게 해야 합니다.
            // 아래 코드를 통해 메시지를 자동으로 내림차순으로 불러옵니다.
            // 처음 써보는 기능(TreeMap<>()) - Collections 클래스의 메소드입니다.
            Map<String, ChatModel.Comment> messageMap = new TreeMap<>(Collections.reverseOrder());

            // 채팅 내용을 넣어줍니다.
            messageMap.putAll(chatRoomModels.get(position).message_comments);

            // 메시지가 하나라도 있을 경우에만 설정합니다.
            if (messageMap.keySet().toArray().length > 0) {
                // 내림차순이니까 0번 인덱스입니다.
                String lastMessageKey = (String) messageMap.keySet().toArray()[0];

                // 채팅방에 마지막 메시지를 찍어줘야죠?
                customViewHolder.last_meesage.setText(chatRoomModels.get(position).message_comments.get(lastMessageKey).message);

                // 타임스탬프 찍기
                simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
                long unixTime = (long) chatRoomModels.get(position).message_comments.get(lastMessageKey).timestamp;
                Date date = new Date(unixTime);
                customViewHolder.timestamp.setText(simpleDateFormat.format(date));
            }

            // 대화방으로 들어가 봅시다.
            customViewHolder.itemLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = null;

                    // 단체 채팅방으로 들어갑니다.
                    // 방 uid만 입력한다는 것은 단체 채팅방 액티비티에서 해당 uid 정보만 받겠다는 뜻입니다.
                    if (chatRoomModels.get(position).users.size() > 2){
                        intent = new Intent(v.getContext(), GroupMessageActivity.class);

                        // 방의 key 값(uid)을 넘겨주는 의미입니다.
                        // 상단의 방 uid 불러오는 게 가장 까다로운 코드로 보입니다.
                        intent.putExtra("destinationRoom", keys.get(position));
                    }

                    // 1:1 채팅방으로 들어갑니다.
                    else {
                        intent = new Intent(v.getContext(), MessageActivity.class);
                        // 여기선 채팅방의 uid값을 보내주는 것이 아니라
                        // 현재 position 값에 있는 상대방의 uid 데이터를 불러옵니다.
                        // 편의상 단체 채팅방은 방 uid로, 1:1 채팅은 상대방 uid로 받아옵니다.
                        intent.putExtra("destinationUid", destinationUsers.get(position));
                    }

                    // 애니메이션을 주는 코드입니다.
                    // 마찬가지로 액티비티 전환 시에도 애니메이션 효과를 줄 수 있습니다.
                    ActivityOptions activityOptions = null;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                        activityOptions = ActivityOptions.makeCustomAnimation(v.getContext(), R.anim.fromright, R.anim.toleft);
                        startActivity(intent, activityOptions.toBundle());
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            // 결국 '내'가 들어간 모든 채팅방의 사이즈만큼만 불러옵니다.
            return chatRoomModels.size();
        }

        private class CustomViewHolder extends RecyclerView.ViewHolder {
            public ImageView profileimage;
            public TextView dest_name;
            public TextView last_meesage;
            public TextView timestamp;
            public LinearLayout itemLayout;

            public CustomViewHolder(final View view) {
                super(view);
                profileimage = (ImageView)view.findViewById(R.id.chatfragment_imageview_profileimage);
                dest_name = (TextView)view.findViewById(R.id.chatfragment_textview_destinationuid);
                last_meesage = (TextView)view.findViewById(R.id.chatfragment_textview_lastmessage);
                timestamp = (TextView)view.findViewById(R.id.chatfragment_textview_timestamp);
                itemLayout = (LinearLayout)view.findViewById(R.id.chatfragment_linearlayout);
            }
        }
    }
}
