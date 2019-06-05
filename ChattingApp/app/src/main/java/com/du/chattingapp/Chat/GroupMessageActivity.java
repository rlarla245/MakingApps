package com.du.chattingapp.Chat;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.du.chattingapp.Models.ChatModel;
import com.du.chattingapp.Models.NotificationModel;
import com.du.chattingapp.Models.UserModel;
import com.du.chattingapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;
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

public class GroupMessageActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_FROM_ALBUM = 0;
    public ImageView tempImage;

    // 필요한 변수 생성
    Map<String, UserModel> users = new HashMap<>();

    String destinationRoom;
    String uid;

    EditText editText;
    TextView chatUsersName, temporaryUserName;
    Button input_button;
    ImageView openGallery;

    // 메시지 코드 관련 변수 생성
    DatabaseReference databaseReference;
    ValueEventListener valueEventListener;

    RecyclerView recyclerView;

    // 실제 대화에 참여하는 유저들만 불러옵니다.
    List<String> takeUsers;

    // 마찬가지로 몇 명이 대화에 참여하는지 확인하는 메소드입니다.
    int peopleCount = 0;

    // 연도 – 월 – 일 – 시간 – 분
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm");

    // 이미지 uri
    private Uri uploadImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_message);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // 변수 호출
        destinationRoom = getIntent().getStringExtra("destinationRoom");

        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        editText = (EditText) findViewById(R.id.groupmessageactivity_inputtext);
        input_button = (Button) findViewById(R.id.groupmessageactivity_inputbutton);
        chatUsersName = (TextView) findViewById(R.id.groupmessageactivity_textview_who);
        temporaryUserName = (TextView) findViewById(R.id.groupmessageactivity_textview_temporary_who);
        openGallery = (ImageView) findViewById(R.id.messageactivity_imageview_opengallery);

        // 사진 전송하기 위한 갤러리 오픈
        openGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pickIntent = new Intent(Intent.ACTION_PICK);
                pickIntent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                startActivityForResult(pickIntent, PICK_IMAGE_FROM_ALBUM);
            }
        });

        takeUsers = new ArrayList<>();

        // 해당 대화에 참여하는 사람들만 불러와봅시다.
        FirebaseDatabase.getInstance().getReference().child("chatrooms")
                .child(destinationRoom).child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                takeUsers.clear();
                for (final DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // 내 uid는 빼자
                    if (!uid.equals(snapshot.getKey())) {
                        // users 정보
                        FirebaseDatabase.getInstance().getReference().child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot snapshot1 : dataSnapshot.getChildren()) {
                                    UserModel chatUserModel = snapshot1.getValue(UserModel.class);
                                    // 채팅방 속 유저 == 유저 데이터 속 일치하는 유저
                                    if (snapshot.getKey().equals(chatUserModel.uid)) {
                                        // 마지막 ,처리 때문에 임시 변수에 값을 저장합니다.
                                        temporaryUserName.append(chatUserModel.userName + ", ");
                                    }
                                }

                                // 반복문 다 돈 상태에서
                                // 마지막 ,를 삭제합니다.
                                try {
                                    // char 배열로 쪼갭니다.
                                    CharSequence tests = temporaryUserName.getText();
                                    // 제거합니다.
                                    final CharSequence test2 = tests.subSequence(0, tests.length() - 2);
                                    chatUsersName.setText(test2);

                                    // Test
                                    chatUsersName.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Toast.makeText(GroupMessageActivity.this, test2 + " 유저와 채팅하고 있습니다.", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }

                                // 계정 삭제 시
                                catch (StringIndexOutOfBoundsException e) {
                                    chatUsersName.setText("알 수 없음");
                                }
                        }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        // 전체 유저 불러오는 듯
        FirebaseDatabase.getInstance().getReference().child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // 데이터 값들을 UserModel로 캐스팅해서 불러옵니다.
                for (DataSnapshot item : dataSnapshot.getChildren()) {
                    // 결국, key - 유저 uid, value - 해당 유저 모델
                    // 결국 모든 유저들을 다 불러오는 메소드
                    users.put(item.getKey(), item.getValue(UserModel.class));
                }
                input();

                // 리사이클러 뷰 호출, 어댑터 설정, 레이아웃 매니저 설정
                recyclerView = (RecyclerView) findViewById(R.id.groupmessageactivity_recyclerview);
                recyclerView.setAdapter(new GroupMessageRecyclerViewAdapter());
                recyclerView.setLayoutManager(new LinearLayoutManager(GroupMessageActivity.this));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    // 채팅 입력 메소드
    void input() {
        final ArrayList<String> pushTokens = new ArrayList<>();

        input_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 채팅 입력
                final ChatModel.Comment comment = new ChatModel.Comment();
                comment.photoUri = null;
                comment.uid = uid;
                comment.message = editText.getText().toString();
                comment.timestamp = ServerValue.TIMESTAMP;

                // 해당 채팅방 데이터에 "message_comments"라는 이름으로 코멘트가 쌓이게 됩니다.
                FirebaseDatabase.getInstance().getReference().child("chatrooms").child(destinationRoom).child("message_comments")
                        .push().setValue(comment).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // 채팅방 유저들을 불러옵니다.
                        FirebaseDatabase.getInstance().getReference().child("chatrooms")
                                .child(destinationRoom).child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                // 해시맵 형태로 데이터를 불러옵니다.
                                Map<String, Boolean> map = (Map<String, Boolean>) dataSnapshot.getValue();

                                try {
                                    // 유저의 uid 값만 불러옵니다.
                                    for (String item : map.keySet()) {
                                        // 내 uid면 푸시 보내지 않음
                                        if (item.equals(uid)) {
                                            continue;
                                        }
                                        pushTokens.add(users.get(item).pushToken);
                                    }
                                } catch (NullPointerException e) {
                                    return;
                                }

                                // 단체 채팅방 속 유저들에게 푸시 메시지를 전송합니다.
                                for (String tokens : pushTokens) {
                                    sendGcm(tokens, comment.message);
                                }
                                pushTokens.clear();
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

    void sendGcm(String token, String message) {
        Gson gson = new Gson();
        NotificationModel notificationModel = new NotificationModel();

        // 현재 유저 네임으로 메시지를 띄울 때 사용하기 위함입니다.
        String userName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();

        notificationModel.to = token;
        // notificationModel.notification.title = userName;
        // notificationModel.notification.text = editText.getText().toString();

        // 푸시 메시지 출력에 필요한 변수들입니다.
        notificationModel.data.title = userName;
        notificationModel.data.text = message;
        notificationModel.data.caseNumber = "1";
        notificationModel.data.index = destinationRoom;

        // 포스트 맨과 같은 바디를 생성했습니다.
        RequestBody requestBody =
                RequestBody.create(MediaType.parse("application/json; charset=utf8"), gson.toJson(notificationModel));

        // 포스트 맨과 같은 헤더 부분입니다.
        Request request = new Request.Builder()
                .header("Content-Type", "application/json")
                // 해당 서버키를 입력합니다.
                .addHeader("Authorization", "key=AIzaSyCDpcPkE61tZtjVRdO3JoJ9AhWdrqEwzFA")
                // 이것도 맞는지 확인해야 합니다.
                .url("https://fcm.googleapis.com/fcm/send")
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // 넘어온 값이 있을 경우에만
        if (data != null) {
            if (requestCode == PICK_IMAGE_FROM_ALBUM && resultCode == RESULT_OK) {
                // 다이얼로그 뷰 불러오기
                View alertView = View.inflate(this, R.layout.alertdialog_layout_view, null);
                tempImage = new ImageView(this);
                tempImage = alertView.findViewById(R.id.alertdialog_imageview);

                AlertDialog.Builder builder = new AlertDialog.Builder(GroupMessageActivity.this);
                builder.setView(alertView);
                tempImage.setImageURI(data.getData());

                // 사진을 전송합시다.
                builder.setPositiveButton("전송", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 먼저 스토리지에 사진을 저장한 뒤 -> 데이터베이스로 전송합니다.
                        FirebaseStorage.getInstance().getReference()
                                .child("group_message_comments").child(destinationRoom).putFile(uploadImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                // 프로필 이미지 다운로드 url
                                final String imageUri = task.getResult().getDownloadUrl().toString();

                                // 전송 메소드
                                final ArrayList<String> pushTokens = new ArrayList<>();

                                // 채팅 입력
                                ChatModel.Comment comment = new ChatModel.Comment();
                                comment.photoUri = imageUri;
                                comment.uid = uid;
                                comment.message = null;
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
                                                Map<String, Boolean> map
                                                        = (Map<String, Boolean>) dataSnapshot.getValue();

                                                // 유저의 uid 값만 불러옵니다.
                                                for (String item : map.keySet()) {
                                                    // 내 uid면 푸시 보내지 않음
                                                    if (item.equals(uid)) {
                                                        continue;
                                                    }
                                                    pushTokens.add(users.get(item).pushToken);
                                                }

                                                for (String tokens : pushTokens) {
                                                    // System.out.println("토큰 푸시 메시지 전송 중입니다.....");
                                                    // System.out.println("토큰 확인: " + tokens);
                                                    sendGcm(tokens, "사진을 전송했습니다.");
                                                }
                                                pushTokens.clear();
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
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(GroupMessageActivity.this, "업로드에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                // checkChatRoom();

                AlertDialog alertDialog = builder.create();
                alertDialog.show();

                // 이미지 경로 원본 저장
                uploadImageUri = data.getData();
            }
        }
    }

    // 어댑터 설정
    class GroupMessageRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        // 채팅들 담는 리스트 생성
        List<ChatModel.Comment> comments = new ArrayList<>();

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
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
            GroupMessageViewHolder messageViewHolder = ((GroupMessageViewHolder) holder);

            try {
                messageViewHolder.textView_name.setText(users.get(comments.get(position).uid).userName);
            } catch (NullPointerException e) {
                messageViewHolder.textView_name.setText("알 수 없음");
                editText.setEnabled(false);
                openGallery.setEnabled(false);
                input_button.setText("");
                input_button.setEnabled(false);
            }

            // 채팅이 내 메시지일 경우
            if (comments.get(position).uid.equals(uid)) {
                // 내 채팅일 경우 사진 및 이름을 띄우지 않겠습니다.
                messageViewHolder.linearLayout_destination.setVisibility(View.INVISIBLE);

                // 사진 파일일 경우
                if (comments.get(position).message == null) {
                    messageViewHolder.imageview_upload.setVisibility(View.VISIBLE);
                    messageViewHolder.textView_message.setVisibility(View.GONE);
                    System.out.println("이미지 url: " + comments.get(position).photoUri);
                    Glide.with(GroupMessageActivity.this).load(comments.get(position).photoUri).into(messageViewHolder.imageview_upload);

                    messageViewHolder.imageview_upload.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // 사진을 누를 경우 AlertDialog를 띄웁니다.
                            // 다이얼로그 뷰 불러오기
                            View alertView = View.inflate(GroupMessageActivity.this, R.layout.alertdialog_layout_view, null);
                            tempImage = new ImageView(GroupMessageActivity.this);
                            tempImage = alertView.findViewById(R.id.alertdialog_imageview);

                            AlertDialog.Builder builder = new AlertDialog.Builder(GroupMessageActivity.this);
                            builder.setView(alertView);
                            Glide.with(GroupMessageActivity.this).load(comments.get(position).photoUri).into(tempImage);

                            AlertDialog alertDialog = builder.create();
                            alertDialog.show();
                        }
                    });
                }

                // 단순 메시지 채팅일 경우
                if (comments.get(position).message != null) {
                    messageViewHolder.imageview_upload.setVisibility(View.GONE);
                    messageViewHolder.textView_message.setVisibility(View.VISIBLE);

                    // 텍스트 설정
                    messageViewHolder.textView_message.setText(comments.get(position).message);
                    messageViewHolder.textView_message.setBackgroundResource(R.drawable.rightbubble);
                }

                // 내 채팅이니 오른쪽 정렬
                messageViewHolder.linearLayout_main.setGravity(Gravity.RIGHT);
                messageViewHolder.textview_timestamp.setGravity(Gravity.RIGHT);
                setReadCounter(position, messageViewHolder.textView_readCounter_left);
            }

            // 내 채팅이 아닌 경우
            else {
                try {
                    Glide.with(holder.itemView.getContext()).load(users.get(comments.get(position).uid).profileImageUri)
                            // 원형 이미지로 호출
                            .apply(new RequestOptions().circleCrop())
                            // 여기에 넣습니다
                            .into(messageViewHolder.imageview_profile);

                    // 이름에 DB 유저 네임을 불러옵니다.
                    messageViewHolder.textView_name.setText(users.get(comments.get(position).uid).userName);
                    messageViewHolder.linearLayout_destination.setVisibility(View.VISIBLE);
                }
                // 삭제 유저
                catch (NullPointerException e) {
                    Glide.with(holder.itemView.getContext()).load(R.drawable.academy_logo)
                            // 원형 이미지로 호출
                            .apply(new RequestOptions().circleCrop())
                            // 여기에 넣습니다
                            .into(messageViewHolder.imageview_profile);
                    // 이름에 DB 유저 네임을 불러옵니다.
                    messageViewHolder.textView_name.setText("알 수 없음");
                }

                    // 사진 파일일 경우
                    if (comments.get(position).message == null) {
                        messageViewHolder.imageview_upload.setVisibility(View.VISIBLE);
                        messageViewHolder.textView_message.setVisibility(View.GONE);
                        Glide.with(GroupMessageActivity.this).load(comments.get(position).photoUri).into(messageViewHolder.imageview_upload);

                        messageViewHolder.imageview_upload.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // 사진을 누를 경우 AlertDialog를 띄웁니다.
                                // 다이얼로그 뷰 불러오기
                                View alertView = View.inflate(GroupMessageActivity.this, R.layout.alertdialog_layout_view, null);
                                tempImage = new ImageView(GroupMessageActivity.this);
                                tempImage = alertView.findViewById(R.id.alertdialog_imageview);

                                AlertDialog.Builder builder = new AlertDialog.Builder(GroupMessageActivity.this);
                                builder.setView(alertView);
                                Glide.with(GroupMessageActivity.this).load(comments.get(position).photoUri).into(tempImage);

                                AlertDialog alertDialog = builder.create();
                                alertDialog.show();
                            }
                        });
                    }

                    // 단순 채팅 메시지일 경우
                    if (comments.get(position).message != null) {
                        messageViewHolder.imageview_upload.setVisibility(View.GONE);
                        messageViewHolder.textView_message.setVisibility(View.VISIBLE);

                        // 텍스트 설정
                        messageViewHolder.textView_message.setText(comments.get(position).message);
                        messageViewHolder.textView_message.setBackgroundResource(R.drawable.leftbubble);
                    }

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
                                // TODO: 새로운 리스트 만들고 유저들 담은 다음에 반복문 돌리면서 해당 유저 데이터베이스 접근한 뒤
                                // TODO : 나오는 값들 append로 더하면 됩니다.
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
            public ImageView imageview_upload;

            public GroupMessageViewHolder(View view) {
                super(view);
                textView_message = (TextView) view.findViewById(R.id.messageactivity_item_textview);
                textView_name = (TextView) view.findViewById(R.id.messageactivity_item_name);
                imageview_profile = (ImageView) view.findViewById(R.id.messageactivity_item_imageview);
                linearLayout_destination = (LinearLayout) view.findViewById(R.id.messageactivity_item_linearlayout);
                linearLayout_main = (LinearLayout) view.findViewById(R.id.messageactivity_item_linearlayout_main);
                textview_timestamp = (TextView) view.findViewById(R.id.messageactivity_item_textview_timestamp);
                textView_readCounter_left = (TextView) view.findViewById(R.id.messageactivity_item_textview_readCounter_left);
                textView_readCounter_right = (TextView) view.findViewById(R.id.messageactivity_item_textview_readCounter_right);
                imageview_upload = view.findViewById(R.id.messageactivity_item_imageview_uploadimageview);
            }
        }
    }

}
