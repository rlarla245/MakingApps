package io.github.stack07142.firebase_photos.tabbar;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import io.github.stack07142.firebase_photos.R;
import io.github.stack07142.firebase_photos.model.AlarmDTO;
import io.github.stack07142.firebase_photos.model.ContentDTO;

public class CommentActivity extends AppCompatActivity {
    // 댓글을 담는 텍스트와 전송 버튼
    private EditText message;
    private Button sendButton;

    private String imageUid;

    private RecyclerView commentRecyclerView;

    private FirebaseUser user;
    private String destinationUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        user = FirebaseAuth.getInstance().getCurrentUser();

        // 해당 이미지 주인의 uid 값
        destinationUid = getIntent().getStringExtra("destinationUid");

        Log.d("CommentActivity", "onCreate");
        // 해당 이미지에 대한 데이터 테이블을 불러오는 것
        imageUid = getIntent().getStringExtra("imageUid");

        Log.d("CommentActivity", imageUid == null ? "NULL" : imageUid);

        message = (EditText) findViewById(R.id.comment_edit_message);

        sendButton = (Button) findViewById(R.id.comment_btn_send);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // ContentDTO의 내부 Comment 클래스를 담습니다.
                ContentDTO.Comment comment = new ContentDTO.Comment();

                // 댓글을 다는 사람, 즉 현재 유저의 정보들을 담습니다.
                comment.userId = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                comment.comment = message.getText().toString();
                comment.uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

                FirebaseDatabase.getInstance()
                        .getReference("images")
                        // 해당 이미지의 종속 데이터로 댓글 데이터들을 담습니다.
                        .child(imageUid)
                        .child("comments")
                        .push()
                        .setValue(comment);

                message.setText("");

                // Comment 입력 시 commentAlarm을 호출
                commentAlarm(destinationUid, comment.comment);
            }
        });

        commentRecyclerView = (RecyclerView) findViewById(R.id.comment_recyclerview);
        commentRecyclerView.setAdapter(new CommentRecyclerViewAdapter());
        commentRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    // 댓글을 남겼을 경우 데이터 베이스에 넘겨줘야 합니다.
    // 파라미터 1 - 상대 uid, 파라미터 2 - 메시지
    private void commentAlarm(String destinationUid, String message) {

        AlarmDTO alarmDTO = new AlarmDTO();

        alarmDTO.destinationUid = destinationUid;
        // 내 이메일, id
        alarmDTO.userId = user.getEmail();
        alarmDTO.uid = user.getUid();
        alarmDTO.kind = 1;
        alarmDTO.message = message;

        FirebaseDatabase.getInstance().getReference().child("alarms").push().setValue(alarmDTO);
    }

    class CommentRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private ArrayList<ContentDTO.Comment> comments;

        public CommentRecyclerViewAdapter() {
            comments = new ArrayList<>();
            FirebaseDatabase
                    .getInstance()
                    .getReference()
                    .child("images")
                    // 해당 이미지의 댓글 값을 불러옵니다.
                    .child(imageUid)
                    .child("comments")
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            comments.clear();
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                comments.add(snapshot.getValue(ContentDTO.Comment.class));
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
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
            // Profile Image
            FirebaseDatabase.getInstance()
                    .getReference()
                    .child("profileImages")
                    .child(comments.get(position).uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (dataSnapshot.exists()) {

                        @SuppressWarnings("VisibleForTests")
                        String url = dataSnapshot.getValue().toString();
                        ImageView profileImageView = ((CustomViewHolder) holder).profileImageView;
                        Glide.with(holder.itemView.getContext())
                                .load(url)
                                .apply(new RequestOptions().circleCrop()).into(profileImageView);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });

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
