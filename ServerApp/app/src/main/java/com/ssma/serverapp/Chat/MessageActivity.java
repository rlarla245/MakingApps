package com.ssma.serverapp.Chat;

import android.annotation.SuppressLint;
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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
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

public class MessageActivity extends AppCompatActivity {
    // 필요한 변수 생성
    public Button input_button;
    public EditText input_text;

    // 내 uid
    public String uid;

    // 상대방 uid
    public static String destinationUid;

    // 해당 채팅방의 고유 uid입니다. - 1:1에선 안쓰지 않았던가?
    public String chatRoomUid;

    // 채팅을 띄울 변수들을 생성합니다.
    public RecyclerView recyclerView;
    public TextView withWhoText;

    // 연도 – 월 – 일 – 시간 – 분
    @SuppressLint("SimpleDateFormat")
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm");

    // 상대방 유저 정보를 담을 모델 생성
    private UserModel destinationUserModel;

    // 데이터베이스에 접근 및 호출하기 위한 변수들입니다.
    private DatabaseReference databaseReference;
    private ValueEventListener valueEventListener;

    // 그룹 채팅방을 생성할 시 3명 이상의 인원으로 채팅방을 만들도록 하기 위한 카운트 변수입니다.
    int peopleCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_message_activity);

        // 상태창 제거
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // 이전 화면에서 채팅창으로 보낸 상대방 uid값을 불러옵니다.
        // 해당 유저와의 대화가 DB에 쌓여야 하므로 필요합니다.
        destinationUid = getIntent().getStringExtra("destinationUid");

        // 변수 호출
        input_button = (Button) findViewById(R.id.messageactivity_inputbutton);
        input_text = (EditText) findViewById(R.id.messageactivity_inputtext);
        withWhoText = (TextView) findViewById(R.id.messageactivity_textview_who);

        // 현재 유저 uid 불러오기
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // recyclerview 불러오기
        recyclerView = (RecyclerView) findViewById(R.id.messageactivity_recyclerview);

        // 전송
        input_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChatModel chatModel = new ChatModel();

                // 여기서 users는 해쉬맵입니다. 대화에 참여하는 uid를 담습니다.
                // 참여하고 있을 경우 true로 표시됩니다.
                chatModel.users.put(uid, true);
                chatModel.users.put(destinationUid, true);

                // 대화방이 없을 경우 생성합니다.
                if (chatRoomUid == null) {
                    // 데이터 입력하는 도중 중복 실행 방지
                    input_button.setEnabled(false);

                    // 채팅방 uid를 입력합니다. Push 메소드를 통해 데이터베이스 내 채팅방 uid를 임의적으로 생성되게 합니다.
                    FirebaseDatabase.getInstance().getReference().child("chatrooms").push().setValue(chatModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            checkChatRoom();
                            input_text.setText("");
                        }
                    });
                } else {
                    // 이미 채팅방이 생성되었을 경우, 그 채팅방의 값을 불러와 채팅 데이터를 입력합니다.
                    // Comment 클래스는 ChatModel의 내부 클래스이므로 아래와 같이 선언합니다.
                    ChatModel.Comment comment = new ChatModel.Comment();
                    comment.uid = uid;
                    comment.message = input_text.getText().toString();
                    comment.timestamp = ServerValue.TIMESTAMP;

                    // 데이터 입력 -> 해당 채팅방 uid에 "message_comment"라는 새로운 데이터 테이블을 만들어 쌓습니다.
                    FirebaseDatabase.getInstance().getReference().child("chatrooms").child(chatRoomUid).child("message_comments").push().setValue(comment).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // 푸시 메시지 전송합니다.
                            sendGcm();
                            input_text.setText("");
                        }
                    });
                }
            }
        });
        checkChatRoom();
    }

    // 푸시 메시지 보내야 합니다.
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

        // 포스트 맨과 같은 바디를 생성했습니다.
        RequestBody requestBody =
                RequestBody.create(MediaType.parse("application/json; charset=utf8"), gson.toJson(notificationModel));

        // 포스트 맨과 같은 헤더 부분입니다.
        Request request = new Request.Builder()
                .header("Content-Type", "application/json")
                // 해당 서버키를 입력합니다.
                .addHeader("Authorization", "key=AIzaSyB1p32ZQJsShRX3hvvw8qmu13Gc7HCSKaM")
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

    // 리사이클러뷰를 생성했으니 어댑터도 설정해줍니다.
    class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        // 코멘트를 보여주기 위한 리스트입니다.
        List<ChatModel.Comment> comments;

        // 생성자를 통해 출력할 초기 데이터들을 불러옵니다.
        public RecyclerViewAdapter() {
            comments = new ArrayList<>();

            // 해당 대화에 참여한 유저들의 정보를 불러옵니다.
            FirebaseDatabase.getInstance().getReference().child("users").child(destinationUid)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            destinationUserModel = dataSnapshot.getValue(UserModel.class);
                            getMessageList();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

        }

        // 어댑터 생성자에 넣었었으나 너무 길어져 코멘트만 불러오는 메소드입니다.
        // 메시지들을 불러옵니다.
        void getMessageList() {
            // 해당 채팅방 uid의 메시지들을 불러옵니다.
            databaseReference =
                    FirebaseDatabase.getInstance().getReference().child("chatrooms").child(chatRoomUid).child("message_comments");

            valueEventListener = databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    comments.clear();
                    // 읽었는지 안읽었는지 확인하기 위한 해시맵입니다.
                    Map<String, Object> readUsersMap = new HashMap<>();

                    for (DataSnapshot item : dataSnapshot.getChildren()) {
                        // 각 메시지의 uid 값입니다.
                        String key = item.getKey();

                        // 변수 명 수정에 주의합니다.
                        ChatModel.Comment comment_origin = item.getValue(ChatModel.Comment.class);
                        ChatModel.Comment comment_modify = item.getValue(ChatModel.Comment.class);

                        // 태그 입력을 통해 읽었다는 신호를 전달합니다.
                        // 해당 액티비티에 들어왔을 경우 유저가 읽었다는 표시가 됩니다.
                        comment_modify.readUsers.put(uid, true);

                        // 해당 해시맵에는 해당 채팅방 uid, 내 uid - true 값이 저장됩니다.
                        // 해당 채팅방의 내가 채팅을 읽었다는 의미입니다.
                        readUsersMap.put(key, comment_modify);
                        comments.add(comment_origin);
                    }

                    // 채팅이 없으면 틩기는 버그 해결
                    if (comments.size() == 0) {
                        return;
                    }

                    // 내가 읽지 않았을 경우... 인데 상단 코드를 해당 조건문에 넣는게 낫지 않은가?
                    if (!comments.get(comments.size() - 1).readUsers.containsKey(uid)) {
                        FirebaseDatabase.getInstance().getReference().child("chatrooms").child(chatRoomUid).child("message_comments")
                                .updateChildren(readUsersMap)
                                // 콜백 기능을 제공합니다.
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        // 데이터 갱신 메소드
                                        notifyDataSetChanged();

                                        // 최근 메시지로 스크롤 이동합니다.
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

            withWhoText.setText("<" + destinationUserModel.userName + ">");

            // 채팅이 내 메시지일 경우
            if (comments.get(position).uid.equals(uid)) {
                messageViewHolder.textView_message.setText(comments.get(position).message);
                messageViewHolder.textView_message.setBackgroundResource(R.drawable.rightbubble);

                // 내 채팅일 경우 사진 및 이름을 띄우지 않겠습니다.
                // TODO: 확인후 Gone으로 바꿔보자.
                messageViewHolder.linearLayout_destination.setVisibility(View.INVISIBLE);

                messageViewHolder.textView_message.setTextSize(15);

                // 액티비티에서 UI 조정이 가능합니다.
                messageViewHolder.linearLayout_main.setGravity(Gravity.RIGHT);
                messageViewHolder.textview_timestamp.setGravity(Gravity.RIGHT);

                setReadCounter(position, messageViewHolder.textView_readCounter_left);

            } else {
                // 내 채팅이 아닐경우 상대방 프로필 이미지를 띄웁니다.
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

            // TimeStamp 찍기
            long unixTime = (long) comments.get(position).timestamp;
            Date date = new Date(unixTime);
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
            // 데이터 베이스 내 알아먹을 수 없는 코드가 읽을 수 있게 변환됩니다.
            String time = simpleDateFormat.format(date);

            // 입력
            messageViewHolder.textview_timestamp.setText(time);
        }

        // 몇명 읽었는지 확인하는 메소드
        void setReadCounter(final int position, final TextView textView) {
            if (peopleCount == 0) {
                FirebaseDatabase.getInstance().getReference().child("chatrooms").child(chatRoomUid).child("users")
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                // 테이블 정보를 해시맵으로 불러오겠다는 소리
                                Map<String, Boolean> users_maps = (Map<String, Boolean>) dataSnapshot.getValue();

                                // 대화에 몇 명있는지 확인하는 변수
                                peopleCount = users_maps.size();

                                // 총 명수 - 채팅 읽은 사람 수
                                int count
                                        = peopleCount - comments.get(position).readUsers.size();

                                // 아직 안읽은 사람이 있음
                                if (count > 0) {
                                    textView.setVisibility(View.VISIBLE);
                                    textView.setText(String.valueOf(count));
                                }

                                // 다 읽었을 경우 생략해줍니다.
                                else {
                                    textView.setVisibility(View.INVISIBLE);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
            }

            // 채팅방에 한 번 들어갔을 경우 peopleCount 변수가 생성되어있습니다.
            else {
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
            public RelativeLayout linearLayout_main;
            public TextView textview_timestamp;
            public TextView textView_readCounter_left;
            public TextView textView_readCounter_right;

            public MessageViewHolder(View view) {
                super(view);
                textView_message = (TextView) view.findViewById(R.id.messageactivity_item_textview);
                textView_name = (TextView) view.findViewById(R.id.messageactivity_item_name);
                imageview_profile = (ImageView) view.findViewById(R.id.messageactivity_item_imageview);
                linearLayout_destination = (LinearLayout) view.findViewById(R.id.messageactivity_item_linearlayout);
                linearLayout_main = (RelativeLayout) view.findViewById(R.id.messageactivity_item_linearlayout_main);
                textview_timestamp = (TextView) view.findViewById(R.id.messageactivity_item_textview_timestamp);
                textView_readCounter_left = (TextView) view.findViewById(R.id.messageactivity_item_textview_readCounter_left);
                textView_readCounter_right = (TextView) view.findViewById(R.id.messageactivity_item_textview_readCounter_right);
            }
        }
    }

    // 똑같은 애랑 채팅하는데 채팅방이 계속해서 생성됩니다.
    // 이를 막기 위해, 기존에 존재하는 채팅방인지 확인하는 메소드를 생성합니다.
    public void checkChatRoom() {
        FirebaseDatabase.getInstance().getReference().child("chatrooms")
                // 다음 코드가 중복을 확인하는 코드입니다.
                // 이미 내가 채팅방에 존재하는 경우만 불러옵니다.
                // 여기서 users는 위에서 생성한 chatModel의 해쉬맵입니다.
                .orderByChild("users/" + uid).equalTo(true).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // 대화방이 없을 경우 기존 값들을 자동으로 입력 후 넘어갑니다.
                // 현재 채팅방의 아이디가 존재하지 않을 경우...
                if (dataSnapshot.getValue() == null) {
                    ChatModel newRoom = new ChatModel();
                    newRoom.users.put(uid, true);
                    newRoom.users.put(destinationUid, true);
                    FirebaseDatabase.getInstance().getReference().child("chatrooms").push().setValue(newRoom)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    // 리사이클러 뷰 레이아웃 매니저랑 어댑터 설정하는 듯
                                    checkChatRoom();
                                }
                            });
                    return;
                }

                for (DataSnapshot item : dataSnapshot.getChildren()) {
                    ChatModel chatModel = item.getValue(ChatModel.class);
                    // 채팅방이 기존에 있을 경우
                    // 다음 코드가 데이터베이스를 확인합니다.
                    // 내가 존재하고, 상대방 uid가 존재하며 1:1 채팅일 경우 채팅방을 재생성하지 않습니다.
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

    @Override
    public void onBackPressed() {
        // super.onBackPressed();
        // 메시지가 없을 경우 뒤로가기 버튼을 눌러도 틩기지 않습니다.
        if (valueEventListener != null) {
            databaseReference.removeEventListener(valueEventListener);
        }

        finish();
        overridePendingTransition(R.anim.fromleft, R.anim.toright);
    }
}
