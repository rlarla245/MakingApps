package com.du.instagramprototypeproject.bottmNavigations;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.du.instagramprototypeproject.MainActivity;
import com.du.instagramprototypeproject.R;
import com.du.instagramprototypeproject.model.AlarmDTO;
import com.du.instagramprototypeproject.model.ContentDTO;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CommentActivity extends AppCompatActivity {
    String destinationUidToken;

    // 댓글을 담는 텍스트와 전송 버튼
    private EditText message;
    private Button sendButton;

    // 이미지 uid
    private String imageUid;

    // 해당 이미지 표시 + 데이터 테이블
    private ImageView glideImage;
    private ArrayList<ContentDTO> glideImageList;

    // 리사이클러 뷰 생성
    private RecyclerView commentRecyclerView;

    private FirebaseUser user;
    private String destinationUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        // 현재 유저를 지정합니다.
        user = FirebaseAuth.getInstance().getCurrentUser();

        // 해당 이미지 주인의 uid 값
        destinationUid = getIntent().getStringExtra("destinationUid");

        // 해당 이미지에 대한 데이터 테이블을 불러오는 것
        imageUid = getIntent().getStringExtra("imageUid");

        // 해당 이미지 표시
        glideImage = (ImageView)findViewById(R.id.commentactivity_imageview_image);

        glideImageList = new ArrayList<>();

        // 데이터 베이스에서 해당 이미지 url을 가져와야 합니다.
        FirebaseDatabase.getInstance().getReference().child("images")
                .child(imageUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                glideImageList.clear();
                glideImageList.add(dataSnapshot.getValue(ContentDTO.class));

                // 액티비티가 끝났을 경우에도 Glide 메소드를 불러와 에러가 발생합니다.
                commentRecyclerView.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                        // 해당 이미지를 불러와야합니다. 인덱스 값으로 0을 설정합니다.
                        Glide.with(commentRecyclerView.getContext()).load(glideImageList.get(glideImageList.size() - 1).imageUrl)
                                .into(glideImage);
                        } catch (NullPointerException e) {
                            //
                        }
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        message = (EditText) findViewById(R.id.comment_edit_message);

        // 댓글 입력 버튼
        sendButton = (Button) findViewById(R.id.comment_btn_send);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // ContentDTO의 내부 Comment 클래스를 담습니다.
                // 사실상 댓글의 경우 imageUrl에 담기 때문에 + 좋아요 시 update로 인해 골치 아파질 수 있으므로
                // 내부 클래스 선언은 좀.....
                ContentDTO.Comment comment = new ContentDTO.Comment();

                // 댓글을 다는 사람, 즉 현재 유저의 정보들을 담습니다.
                comment.userId = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                comment.comment = message.getText().toString();
                comment.uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

                // 아무것도 입력하지 않을 경우 막습니다.
                if (message.getText().toString().equals("")) {
                    Toast.makeText(CommentActivity.this, "메시지를 입력해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // 댓글을 입력합니다. 현재 수정된 코드입니다.
                FirebaseDatabase.getInstance().getReference()
                        // 해당 이미지의 종속 데이터로 댓글 데이터들을 담습니다.
                        .child("images").child("comments")
                        .child(imageUid).child("comments")
                        .push().setValue(comment).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // 댓글을 서버에 올릴 경우 메시지를 공백처리합니다.
                        message.setText("");
                    }
                });

                // Comment 입력 시 commentAlarm을 호출 -> 푸시 메시지 출력해야 합니다.
                commentAlarm(destinationUid, comment.comment);
            }
        });

        // 댓글 리사이클러 뷰 호출, 어댑터 및 레이아웃 매니저 설정
        commentRecyclerView = (RecyclerView) findViewById(R.id.comment_recyclerview);
        commentRecyclerView.setAdapter(new CommentRecyclerViewAdapter());
        commentRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    // 댓글을 남겼을 경우 데이터 베이스에 넘겨줘야 합니다.
    // 파라미터 1 - 상대 uid, 파라미터 2 - 메시지
    private void commentAlarm(final String destinationUid, final String message) {
        AlarmDTO alarmDTO = new AlarmDTO();

        alarmDTO.destinationUid = destinationUid;
        // 내 이메일, id
        alarmDTO.userId = user.getEmail();
        alarmDTO.uid = user.getUid();
        // 나중에 종류 구분하는 숫자로 동작을 구분해야 합니다.
        alarmDTO.kind = 1;
        alarmDTO.message = message;
        alarmDTO.imageUid = imageUid;

        // 푸시메시지를 보내봅시다. 토큰값을 불러와야 합니다.
        FirebaseDatabase.getInstance().getReference().child("alarms").push().setValue(alarmDTO)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                // 댓글을 남겼을 경우
                FirebaseDatabase.getInstance().getReference().child("pushToken")
                        .child(destinationUid).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            destinationUidToken = snapshot.getValue().toString();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                String sendGcmMessage
                        = FirebaseAuth.getInstance().getCurrentUser().getEmail() + getString(R.string.alarm_who)
                        + message + getString(R.string.alarm_comment);

                // 푸시 알람을 전송합니다.
                MainActivity.sendGcm(destinationUidToken, "알림 메시지입니다.", sendGcmMessage);
            }
        });
    }

    class CommentRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private ArrayList<ContentDTO.Comment> comments;
        private ArrayList<String> commentsUids;

        public CommentRecyclerViewAdapter() {
            comments = new ArrayList<>();
            commentsUids = new ArrayList<>();
            FirebaseDatabase.getInstance().getReference()
                    .child("images")
                    // 해당 이미지의 댓글 값을 불러옵니다.
                    // 이전에 버튼을 누를 경우 해당 이미지 테이블에 코멘츠 데이터를 쌓게 되었습니다.
                    .child("comments").child(imageUid).child("comments")
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            comments.clear();
                            commentsUids.clear();

                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                comments.add(snapshot.getValue(ContentDTO.Comment.class));
                                // 댓글 데이터의 uid 값을 불러옵니다.
                                commentsUids.add(snapshot.getKey());
                            }
                            notifyDataSetChanged();
                            // 최신 댓글로 뷰가 이동됩니다.
                            commentRecyclerView.scrollToPosition(comments.size() - 1);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            // 아이템 레이아웃 호출
            // 이전 GridView도 아래와 같은 방식으로 처리함이 맞습니다.
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_commentview, parent, false);

            return new CustomViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
            // 댓글 유저의 프로필이미지 불러오기
            FirebaseDatabase.getInstance().getReference()
                    .child("profileImages")
                    .child(comments.get(position).uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        // url는 해당 유저 프로필 이미지의 다운로드 url입니다.
                        @SuppressWarnings("VisibleForTests")
                        String url = dataSnapshot.getValue().toString();
                        ImageView profileImageView = ((CustomViewHolder) holder).profileImageView;
                        Glide.with(holder.itemView.getContext())
                                .load(url)
                                .apply(new RequestOptions().circleCrop())
                                .into(profileImageView);
                    }

                    // 프로필 사진 없을 경우 기본 이미지를 삽입합니다.
                    else {
                        ImageView profileImageView = ((CustomViewHolder) holder).profileImageView;
                        profileImageView.setImageResource(R.mipmap.ic_launcher_foreground);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            // 댓글 유저 이름, 댓글을 표시합니다.
            ((CustomViewHolder) holder).profileTextView
                    .setText(comments.get(position).userId);
            ((CustomViewHolder) holder).commentTextView
                    .setText(comments.get(position).comment);
        }

        @Override
        public int getItemCount() {
            return comments.size();
        }

        private class CustomViewHolder extends RecyclerView.ViewHolder {
            public ImageView profileImageView;
            public TextView profileTextView;
            public TextView commentTextView;

            public CustomViewHolder(View itemView) {
                super(itemView);
                profileImageView = (ImageView) itemView.findViewById(R.id.commentviewitem_imageview_profile);
                profileTextView = (TextView) itemView.findViewById(R.id.commentviewitem_textview_profile);
                commentTextView = (TextView) itemView.findViewById(R.id.commentviewitem_textview_comment);
            }
        }
    }
}
