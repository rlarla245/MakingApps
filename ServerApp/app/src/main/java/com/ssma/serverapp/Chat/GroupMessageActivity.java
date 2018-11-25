package com.ssma.serverapp.Chat;

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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.ssma.serverapp.Model.ChatModel;
import com.ssma.serverapp.Model.NotificationModel;
import com.ssma.serverapp.Model.UserModel;
import com.ssma.serverapp.R;

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

public class GroupMessageActivity extends AppCompatActivity {
    // 필요한 변수 생성
    Map<String, UserModel> users = new HashMap<>();
    String destinationRoom;
    String uid;
    EditText editText;
    Button input_button;

    // 메시지 코드 관련 변수 생성
    DatabaseReference databaseReference;
    ValueEventListener valueEventListener;

    RecyclerView recyclerView;

    // 채팅들 담는 리스트 생성
    List<ChatModel.Comment> comments = new ArrayList<>();

    // 연도 – 월 – 일 – 시간 – 분
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm");

    // 마찬가지로 몇 명이 대화에 참여하는지 확인하는 메소드입니다.
    int peopleCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_group_message);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // 변수 호출
        destinationRoom = getIntent().getStringExtra("destinationRoom");
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        editText = (EditText)findViewById(R.id.groupmessageactivity_inputtext);
        input_button = (Button)findViewById(R.id.groupmessageactivity_inputbutton);

        FirebaseDatabase.getInstance().getReference().child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // 데이터 내부 값들을 받아올 수 있는 코드네요.
                // 데이터 값들을 UserModel로 캐스팅해서 불러옵니다.
                for(DataSnapshot item : dataSnapshot.getChildren()) {
                    // 결국, key - 유저 uid, value - 해당 유저 모델
                    users.put(item.getKey(), item.getValue(UserModel.class));
                }
                init();

                // 리사이클러 뷰 호출, 어댑터 설정, 레이아웃 매니저 설정
                recyclerView = (RecyclerView)findViewById(R.id.groupmessageactivity_recyclerview);
                recyclerView.setAdapter(new GroupMessageRecyclerViewAdapter());
                recyclerView.setLayoutManager(new LinearLayoutManager(GroupMessageActivity.this));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    // 채팅 입력 메소드
    void init() {
        input_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 채팅 입력
                ChatModel.Comment comment = new ChatModel.Comment();
                comment.uid = uid;
                comment.message = editText.getText().toString();
                comment.timestamp = ServerValue.TIMESTAMP;

                // 해당 채팅방 데이터에 "message_comments"라는 이름으로 코멘트가 쌓이게 됩니다.
                FirebaseDatabase.getInstance().getReference().child("chatrooms").child(destinationRoom).child("message_comments")
                        .push().setValue(comment).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // 채팅방 유저들을 불러옵니다.
                        FirebaseDatabase.getInstance().getReference().child("chatrooms").child(destinationRoom).child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                // 해시맵 형태로 데이터를 불러옵니다.
                                Map<String, Boolean> map = (Map<String, Boolean>) dataSnapshot.getValue();

                                // 유저의 uid 값만 불러옵니다.
                                for (String item : map.keySet()) {

                                    // 내 uid면 푸시 보내지 않음
                                    if (item.equals(uid)) {
                                        continue;
                                    }
                                    else{
                                        sendGcm(users.get(item).pushToken);
                                    }
                                }
                                editText.setText("");
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                        editText.setText("");
                    }
                });
            }
        });
    }

    // 푸시 메시지 보냅니다.
    void sendGcm(String pushToken) {
        Gson gson = new Gson();
        NotificationModel notificationModel = new NotificationModel();

        // 현재 유저 네임으로 메시지를 띄울 때 사용하기 위함입니다.
        String userName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();

        notificationModel.to = pushToken;
        notificationModel.notification.title = userName;
        notificationModel.notification.text = editText.getText().toString();

        // 푸시 메시지 출력에 필요한 변수들입니다.
        notificationModel.data.title = userName;
        notificationModel.data.text = editText.getText().toString();

        // 포스트 맨과 같은 바디를 생성했습니다.
        RequestBody requestBody =
                RequestBody.create(MediaType.parse("application/json; charset=utf8"), gson.toJson(notificationModel));

        // 포스트 맨과 같은 헤더 부분입니다.
        Request request = new Request.Builder()
                .header("Content-Type","application/json")
                // 해당 서버키를 입력합니다.
                .addHeader("Authorization","key=AIzaSyB1p32ZQJsShRX3hvvw8qmu13Gc7HCSKaM")
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

    // 어댑터 설정
    class GroupMessageRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        public GroupMessageRecyclerViewAdapter() {
            getMessageList();
        }

        // 채팅 메시지 불러옵니다.
        void getMessageList() {
            databaseReference =
                    FirebaseDatabase.getInstance().getReference().child("chatrooms").child(destinationRoom).child("message_comments");

            valueEventListener = databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // 불러온 메시지를 담는 리스트
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

                    if (comments.size() == 0) {
                        return;
                    }

                    // 내가 읽지 않았나?
                    if (!comments.get(comments.size() - 1).readUsers.containsKey(uid)) {
                        FirebaseDatabase.getInstance().getReference().child("chatrooms").child(destinationRoom).child("message_comments")
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

            return new GroupMessageViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            GroupMessageViewHolder messageViewHolder = ((GroupMessageViewHolder)holder);

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
            }

            else {
                Glide.with(holder.itemView.getContext()).load(users.get(comments.get(position).uid).profileImageUri)
                        // 원형 이미지로 호출
                        .apply(new RequestOptions().circleCrop())
                        // 여기에 넣습니다
                        .into(messageViewHolder.imageview_profile);

                // 이름에 DB 유저 네임을 불러옵니다.
                messageViewHolder.textView_name.setText(users.get(comments.get(position).uid).userName);
                messageViewHolder.linearLayout_destination.setVisibility(View.VISIBLE);
                messageViewHolder.textView_message.setBackgroundResource(R.drawable.leftbubble);
                messageViewHolder.textView_message.setText(comments.get(position).message);
                messageViewHolder.textView_message.setTextSize(15);
                messageViewHolder.linearLayout_main.setGravity(Gravity.LEFT);
                messageViewHolder.textview_timestamp.setGravity(Gravity.START);
                setReadCounter(position, messageViewHolder.textView_readCounter_right);

            }
            long unixTime = (long) comments.get(position).timestamp;
            Date date = new Date(unixTime);
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
            // 데이터 베이스 내 알아먹을 수 없는 코드가 읽을 수 있게 변환됩니다.
            String time = simpleDateFormat.format(date);
            messageViewHolder.textview_timestamp.setText(time);
        }

        void setReadCounter(final int position, final TextView textView) {
            if (peopleCount == 0) {
                FirebaseDatabase.getInstance().getReference().child("chatrooms").child(destinationRoom).child("users")
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                // 테이블 정보를 해시맵으로 불러오겠다는 소리
                                Map<String, Boolean> users_maps = (Map<String, Boolean>) dataSnapshot.getValue();

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

        private class GroupMessageViewHolder extends RecyclerView.ViewHolder {
            public TextView textView_message;
            public TextView textView_name;
            public ImageView imageview_profile;
            public LinearLayout linearLayout_destination;
            public LinearLayout linearLayout_main;
            public TextView textview_timestamp;
            public TextView textView_readCounter_left;
            public TextView textView_readCounter_right;
            public GroupMessageViewHolder(View view) {
                super(view);
                textView_message = (TextView)view.findViewById(R.id.messageactivity_item_textview);
                textView_name = (TextView)view.findViewById(R.id.messageactivity_item_name);
                imageview_profile = (ImageView)view.findViewById(R.id.messageactivity_item_imageview);
                linearLayout_destination = (LinearLayout)view.findViewById(R.id.messageactivity_item_linearlayout);
                linearLayout_main = (LinearLayout)view.findViewById(R.id.messageactivity_item_linearlayout_main);
                textview_timestamp = (TextView)view.findViewById(R.id.messageactivity_item_textview_timestamp);
                textView_readCounter_left = (TextView)view.findViewById(R.id.messageactivity_item_textview_readCounter_left);
                textView_readCounter_right = (TextView)view.findViewById(R.id.messageactivity_item_textview_readCounter_right);
            }
        }
    }
}
