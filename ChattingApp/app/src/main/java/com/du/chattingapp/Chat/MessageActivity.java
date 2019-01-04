package com.du.chattingapp.Chat;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.du.chattingapp.Models.ChatModel;
import com.du.chattingapp.Models.NotificationModel;
import com.du.chattingapp.Models.UserModel;
import com.du.chattingapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MessageActivity extends AppCompatActivity {
    // 필요한 변수 생성
    public static String destinationUid;
    public static Button input_button;
    public EditText input_text;
    public String uid;
    public String chatRoomUid;
    public RecyclerView recyclerView;
    public TextView with_who_text;
    // 1:1 채팅인지 1:多 채팅인지 확인하는 카운트 변수 입니다.
    int peopleCount = 0;
    // 연도 – 월 – 일 – 시간 – 분
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm");
    // 유저 데이터 불러오기
    private UserModel destinationUserModel;
    // Firebase 관련 메소드 불러오기
    private DatabaseReference databaseReference;
    private ValueEventListener valueEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        // 상태창 제거
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // 변수 호출
        destinationUid = getIntent().getStringExtra("destinationUid");
        input_button = (Button) findViewById(R.id.messageactivity_inputbutton);
        input_text = (EditText) findViewById(R.id.messageactivity_inputtext);

        // 상대방 이름을 불러옵니다.
        with_who_text = (TextView) findViewById(R.id.messageactivity_textview_who);

        // 현재 유저 uid 불러오기
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // recyclerview 불러오기
        recyclerView = (RecyclerView) findViewById(R.id.messageactivity_recyclerview);

        // 전송
        input_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChatModel chatModel = new ChatModel();

                // 나 - true, 상대방 - true로 해시맵에 입력됩니다.
                chatModel.users.put(uid, true);
                chatModel.users.put(destinationUid, true);

                // 대화방이 없을 경우 생성합니다.
                if (chatRoomUid == null) {
                    // 데이터 확인하는 도중 중복 실행 방지
                    input_button.setEnabled(false);
                    // 채팅방 이름을 입력합니다. Push 메소드를 통해 데이터베이스 내 채팅방 이름을 임의적으로 생성되게 합니다.
                    FirebaseDatabase.getInstance().getReference().child("chatrooms").push().setValue(chatModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            checkChatRoom();
                            input_text.setText("");
                        }
                    });
                } else {
                    // 이미 채팅방이 생성되었을 경우, 그 채팅방의 값을 불러오겠습니다.
                    ChatModel.Comment comment = new ChatModel.Comment();
                    comment.uid = uid;
                    comment.message = input_text.getText().toString();
                    comment.timestamp = ServerValue.TIMESTAMP;
                    FirebaseDatabase.getInstance().getReference().child("chatrooms").child(chatRoomUid).child("message_comments").push().setValue(comment).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            sendGcm();
                            input_text.setText("");
                        }
                    });
                }
            }
        });
        checkChatRoom();
    }

    // 푸시 메시지를 보내야 합니다.
    void sendGcm() {
        Gson gson = new Gson();

        NotificationModel notificationModel = new NotificationModel();

        // 현재 유저 네임으로 메시지를 띄울 때 사용하기 위함입니다.
        String userName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();

        notificationModel.to = destinationUserModel.pushToken;
        notificationModel.notification.title = userName;
        notificationModel.notification.text = input_text.getText().toString();

        // 푸시 메시지 출력에 필요한 변수들입니다.
        notificationModel.data.title = userName;
        notificationModel.data.text = input_text.getText().toString();
        notificationModel.data.caseNumber = "0";
        notificationModel.data.index = uid;

        // 포스트 맨과 같은 바디를 생성했습니다.
        RequestBody requestBody =
                RequestBody.create(MediaType.parse("application/json; charset=utf8"),
                        gson.toJson(notificationModel));

        // 포스트 맨과 같은 헤더 부분입니다.
        Request request = new Request.Builder()
                .header("Content-Type","application/json")
                // 해당 서버키를 입력합니다.
                .addHeader("Authorization","key=AIzaSyCDpcPkE61tZtjVRdO3JoJ9AhWdrqEwzFA")
                // 이것도 맞는지 확인해야 합니다.
                .url("https://gcm-http.googleapis.com/gcm/send")
                .post(requestBody)
                .build();

        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

            }
        });
    }

    // 유저를 누를 경우 자동으로 채팅방이 생성되는 코드입니다,
    public void checkChatRoom() {
        FirebaseDatabase.getInstance().getReference().child("chatrooms")
                .orderByChild("users/" + uid).equalTo(true).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // 대화방이 없을 경우 기존 값들을 자동으로 입력 후 넘어갑니다.
                if (dataSnapshot.getValue() == null) {
                    ChatModel newRoom = new ChatModel();
                    newRoom.users.put(uid, true);
                    newRoom.users.put(destinationUid, true);
                    FirebaseDatabase.getInstance().getReference().child("chatrooms").push().setValue(newRoom)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    checkChatRoom();
                                }
                            });
                    return;
                }

                for (DataSnapshot item : dataSnapshot.getChildren()) {
                    ChatModel chatModel = item.getValue(ChatModel.class);
                    // 채팅방이 기존에 있을 경우
                    if (chatModel.users.containsKey(destinationUid) && chatModel.users.size() == 2) {
                        chatRoomUid = item.getKey();
                        input_button.setEnabled(true);
                        recyclerView.setLayoutManager(new LinearLayoutManager(MessageActivity.this));
                        recyclerView.setAdapter(new RecyclerViewAdapter());
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void testMethod() {
        FirebaseDatabase.getInstance().getReference().child("chatrooms")
                .orderByChild("users/" + uid).equalTo(true).addListenerForSingleValueEvent(new ValueEventListener() {
            // 현재 내가 들어간 모든 채팅방을 불러온 거니까
            // 사이즈랑 상대 uid 고려하면 될 듯도 함
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ChatModel testChatModel = snapshot.getValue(ChatModel.class);

                    // 기존에 나와 상대방이 포함된 채팅방일 경우
                    if (testChatModel != null && testChatModel.users.size() == 2
                            && testChatModel.users.containsKey(uid) && testChatModel.users.containsKey(destinationUid)) {
                        // 메시지 액티비티로 인텐트 넘기면 될 듯?
                    }

                    // 채팅방이 비었을 경우
                    else {
                        ChatModel newRoom = new ChatModel();
                        newRoom.users.put(uid, true);
                        newRoom.users.put(destinationUid, true);
                        FirebaseDatabase.getInstance().getReference().child("chatrooms").push().setValue(newRoom)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        //
                                    }
                                });
                        return;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        // super.onBackPressed();
        // 메시지가 없을 경우 뒤로가기 버튼을 눌러도 틩기지 않습니다.
        if (valueEventListener != null) {
            databaseReference.removeEventListener(valueEventListener);
        }

        finish();
        // overridePendingTransition(R.anim.fast_fromleft, R.anim.fast_toright);
    }

    class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        List<ChatModel.Comment> comments;

        // 생성자를 통해 초기 데이터 세팅
        public RecyclerViewAdapter() {
            comments = new ArrayList<>();

            // 이전 인텐트에서 넘겨받은 상대방 uid에 해당하는 유저 데이터를 찾습니다.
            FirebaseDatabase.getInstance().getReference().child("users").child(destinationUid)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            // 해당 유저를 담습니다.
                            destinationUserModel = dataSnapshot.getValue(UserModel.class);
                            getMessageList();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
        }

        // 서버에서 채팅 데이터들을 불러옵니다.
        void getMessageList() {
            databaseReference =
                    FirebaseDatabase.getInstance().getReference().child("chatrooms").child(chatRoomUid).child("message_comments");

            valueEventListener = databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    comments.clear();
                    Map<String, Object> readUsersMap = new HashMap<>();

                    for (DataSnapshot item : dataSnapshot.getChildren()) {
                        String key = item.getKey();

                        // 변수 명 수정에 주의합니다.
                        ChatModel.Comment comment_origin = item.getValue(ChatModel.Comment.class);
                        ChatModel.Comment comment_modify = item.getValue(ChatModel.Comment.class);

                        // 태그 입력을 통해 읽었다는 신호를 전달합니다.
                        comment_modify.readUsers.put(uid, true);

                        readUsersMap.put(key, comment_modify);
                        comments.add(comment_origin);
                    }

                    // 채팅이 없으면 틩기는 버그 해결
                    if (comments.size() == 0) {
                        return;
                    }

                    // 내가 읽지 않았나?
                    if (!comments.get(comments.size() - 1).readUsers.containsKey(uid)) {
                        FirebaseDatabase.getInstance().getReference().child("chatrooms").child(chatRoomUid).child("message_comments")
                                .updateChildren(readUsersMap)
                                // 콜백 기능을 제공합니다.
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        // 데이터 갱신 메소드
                                        notifyDataSetChanged();
                                        recyclerView.scrollToPosition(comments.size() - 1);
                                    }
                                });
                    } else {
                        notifyDataSetChanged();
                        recyclerView.scrollToPosition(comments.size() - 1);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.messageactivity_item, parent, false);

            return new MessageViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            MessageViewHolder messageViewHolder = ((MessageViewHolder) holder);

            with_who_text.setText(destinationUserModel.userName);

            // 채팅이 내 메시지일 경우
            if (comments.get(position).uid.equals(uid)) {
                messageViewHolder.textView_message.setText(comments.get(position).message);
                messageViewHolder.textView_message.setBackgroundResource(R.drawable.rightbubble);

                // 내 채팅일 경우 사진 및 이름을 띄우지 않겠습니다.
                messageViewHolder.linearLayout_destination.setVisibility(View.INVISIBLE);

                messageViewHolder.textView_message.setTextSize(15);
                messageViewHolder.linearLayout_main.setGravity(Gravity.RIGHT);
                messageViewHolder.textview_timestamp.setGravity(Gravity.RIGHT);
                setReadCounter(position, messageViewHolder.textView_readCounter_left);
            } else {
                Glide.with(holder.itemView.getContext()).load(destinationUserModel.profileImageUri)
                        // 원형 이미지로 호출
                        .apply(new RequestOptions().circleCrop())
                        // 여기에 넣습니다
                        .into(messageViewHolder.imageview_profile);

                // 이름에 DB 유저 네임을 불러옵니다.
                messageViewHolder.textView_name.setText(destinationUserModel.userName);
                messageViewHolder.linearLayout_destination.setVisibility(View.VISIBLE);
                messageViewHolder.textView_message.setBackgroundResource(R.drawable.leftbubble);
                messageViewHolder.textView_message.setText(comments.get(position).message);
                messageViewHolder.textView_message.setTextSize(15);
                messageViewHolder.linearLayout_main.setGravity(Gravity.LEFT);
                messageViewHolder.textview_timestamp.setGravity(Gravity.START);
                setReadCounter(position, messageViewHolder.textView_readCounter_right);
            }

            // 타임 스탬프 설정
            long unixTime = (long) comments.get(position).timestamp;
            Date date = new Date(unixTime);
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));

            // 데이터 베이스 내 알아먹을 수 없는 코드가 읽을 수 있게 변환됩니다.
            String time = simpleDateFormat.format(date);
            messageViewHolder.textview_timestamp.setText(time);
        }

        // 몇 명이 읽었는지 확인합니다.
        void setReadCounter(final int position, final TextView textView) {
            if (peopleCount == 0) {
                FirebaseDatabase.getInstance().getReference().child("chatrooms").child(chatRoomUid).child("users")
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                // 테이블 정보를 해시맵으로 불러오겠다는 소리
                                Map<String, Boolean> users_maps = (Map<String, Boolean>) dataSnapshot.getValue();

                                // 현재 유저가 몇명인지 세주는 카운트 변수
                                peopleCount = users_maps.size();
                                int count
                                        = peopleCount - comments.get(position).readUsers.size();

                                if (count > 0) {
                                    textView.setVisibility(View.VISIBLE);
                                    textView.setText(String.valueOf(count));
                                } else {
                                    textView.setVisibility(View.INVISIBLE);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
            } else {
                int count
                        = peopleCount - comments.get(position).readUsers.size();

                if (count > 0) {
                    textView.setVisibility(View.VISIBLE);
                    textView.setText(String.valueOf(count));
                } else {
                    textView.setVisibility(View.INVISIBLE);
                }
            }
        }

        @Override
        public int getItemCount() {
            return comments.size();
        }

        private class MessageViewHolder extends RecyclerView.ViewHolder {
            public TextView textView_message;
            public TextView textView_name;
            public ImageView imageview_profile;
            public LinearLayout linearLayout_destination;
            public LinearLayout linearLayout_main;
            public TextView textview_timestamp;
            public TextView textView_readCounter_left;
            public TextView textView_readCounter_right;

            public MessageViewHolder(View view) {
                super(view);
                textView_message = (TextView) view.findViewById(R.id.messageactivity_item_textview);
                textView_name = (TextView) view.findViewById(R.id.messageactivity_item_name);
                imageview_profile = (ImageView) view.findViewById(R.id.messageactivity_item_imageview);
                linearLayout_destination = (LinearLayout) view.findViewById(R.id.messageactivity_item_linearlayout);
                linearLayout_main = (LinearLayout) view.findViewById(R.id.messageactivity_item_linearlayout_main);
                textview_timestamp = (TextView) view.findViewById(R.id.messageactivity_item_textview_timestamp);
                textView_readCounter_left = (TextView) view.findViewById(R.id.messageactivity_item_textview_readCounter_left);
                textView_readCounter_right = (TextView) view.findViewById(R.id.messageactivity_item_textview_readCounter_right);
            }
        }
    }
}