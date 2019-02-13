package com.du.chattingapp.Fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import com.du.chattingapp.Models.BoardModel;
import com.du.chattingapp.Models.UserModel;
import com.du.chattingapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

import static android.support.constraint.Constraints.TAG;

public class BoardCommentFragment extends Fragment {
    public ImageView writerImageView, starImageView, deleteImageView;
    public TextView titleTextView, writerNameTextView, contentsTextView, dateTextView, starCountTextView;
    public EditText commentEditText;
    public Button commentButton;
    public String writerUid, boardPositionUid, myName;
    public int likeImageCountNumber, boardPositionNumber;
    // 리사이클러 뷰
    RecyclerView recyclerView;
    // Firebase 인스턴스 불러오기
    private FirebaseDatabase database;
    private FirebaseAuth auth;
    // TimeTable 찍기
    @SuppressLint("SimpleDateFormat")
    private SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("yyyy.MM.dd HH:mm");

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.boardcommentfragment, container, false);

        // Firebase
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        // todo
        writerImageView = view.findViewById(R.id.boardcomment_fragment_ImageView);
        starImageView = view.findViewById(R.id.boardcomment_fragment_imageview_star);
        deleteImageView = view.findViewById(R.id.boardcomment_fragment_imageview_delete);

        // getArgument로 값 불러오기
        titleTextView = view.findViewById(R.id.boardcomment_fragment_textview_title);
        writerNameTextView = view.findViewById(R.id.boardcomment_fragment_textview_writername);
        contentsTextView = view.findViewById(R.id.boardcomment_fragment_textview_content);
        dateTextView = view.findViewById(R.id.boardcomment_fragment_textView_time);
        commentEditText = view.findViewById(R.id.boardcomment_fragment_edittext_comment);
        commentButton = view.findViewById(R.id.boardcomment_fragment_button_comment);
        starCountTextView = view.findViewById(R.id.boardcomment_fragment_textview_count);

        // 해당 댓글의 포지션 값 불러오기
        boardPositionNumber = getArguments().getInt("boardPositionNumber");

        // 댓글 uid 불러오기
        boardPositionUid = getArguments().getString("boardPositionUid");

        // 작성자 uid 불러오기
        writerUid = getArguments().getString("boardWriterUid");

        // 좋아요 여부 값 불러오기
        likeImageCountNumber = getArguments().getInt("likeImageCountNumber");

        // 값 불러오기
        titleTextView.setText(getArguments().getString("boardTitle"));
        writerNameTextView.setText(getArguments().getString("boardWriterName"));
        contentsTextView.setText(getArguments().getString("boardContent"));
        dateTextView.setText(getArguments().getString("date"));
        starCountTextView.setText(getArguments().getString("likeCount"));

        // 내 이름 불러옵시다.
        database.getReference().child("users").child(auth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserModel userModel = dataSnapshot.getValue(UserModel.class);
                try {
                    myName = userModel.userName;
                } catch (NullPointerException e) {
                    myName = "알 수 없음";
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(view.getContext(), "오류가 발생했습니다. 관리자에게 문의 바랍니다.", Toast.LENGTH_SHORT).show();
            }
        });

        // 댓글 입력 후 db에 저장하기
        commentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BoardModel.Comment comment = new BoardModel.Comment();

                // 데이터 모델에 담아 실제 DB에 넘겨봅시다.
                comment.uid = auth.getCurrentUser().getUid();

                if (myName != null) {
                    comment.userName = myName;
                }

                comment.comment = commentEditText.getText().toString();
                // 아무것도 입력하지 않을 경우 막습니다.
                if (commentEditText.getText().toString().equals("")) {
                    Toast.makeText(view.getContext(), "메시지를 입력해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // 타임스탬프 찍기
                comment.timestamp = ServerValue.TIMESTAMP;

                database.getReference().child("board").child("comments")
                        .child(boardPositionUid).push().setValue(comment).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        commentEditText.setText("");
                    }
                });
            }
        });

        // 색 씌우기
        if (likeImageCountNumber == 1) {
            starImageView.setImageResource(R.drawable.star);
        } else {
            starImageView.setImageResource(R.drawable.no_star);
        }

        // 아이템 삭제
        // 휴지통 이미지를 누를 경우 삭제 dialog를 띄웁니다.
        // 물론, 작성자 본인일 경우에만 띄워줘야 겠죠?
        if (writerUid.equals(auth.getCurrentUser().getUid())) {
            deleteImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                    builder.setTitle("확인")
                            .setMessage("삭제하시겠습니까?")
                            .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // 아예 메소드처리를 해버림
                                    delete_content();
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
            });
        }

        // 댓글 담는 리사이클러 뷰
        recyclerView = view.findViewById(R.id.boardcomment_fragment_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(inflater.getContext()));

        RecyclerViewAdapter recyclerViewAdapter = new RecyclerViewAdapter();
        recyclerView.setAdapter(recyclerViewAdapter);

        // 작성자 사진 불러오기
        database.getReference().child("users").child(writerUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserModel userModel = dataSnapshot.getValue(UserModel.class);
                try {
                    String url = userModel.profileImageUri;
                    Glide.with(view.getContext()).load(url).apply(new RequestOptions().circleCrop())
                            .into(writerImageView);
                } catch (NullPointerException e) {
                    Glide.with(view.getContext()).load(R.drawable.academy_logo
                    ).apply(new RequestOptions().circleCrop())
                            .into(writerImageView);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return view;
    }

    // 삭제 메소드
    private void delete_content() {
        database.getReference().child("board").child(boardPositionUid)
                .removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    Toast.makeText(getContext(), "삭제되었습니다.", Toast.LENGTH_SHORT).show();
                }
            }

            // 삭제 실패할 경우
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    Toast.makeText(getContext(), "삭제되지 않았습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        // 리스트 생성
        ArrayList<BoardModel.Comment> commentsDTOs;
        ArrayList<String> commentsUids;

        // 생성자 생성
        public RecyclerViewAdapter() {
            commentsDTOs = new ArrayList<>();
            commentsUids = new ArrayList<>();

            System.out.println("해당 게시글 uid " + boardPositionUid);

            // 데이터 불러오기
            database.getReference().child("board").child("comments").child(boardPositionUid).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    commentsDTOs.clear();
                    commentsUids.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        commentsDTOs.add(snapshot.getValue(BoardModel.Comment.class));
                        commentsUids.add(snapshot.getKey());
                    }
                    notifyDataSetChanged();

                    // 최신 댓글로 이동
                    recyclerView.scrollToPosition(commentsDTOs.size() - 1);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.boardcommentfragment_item, parent, false);
            return new CustomViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
            // 프로필 이미지 불러오기
            database.getReference().child("users").child(commentsDTOs.get(position).uid)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            UserModel userModel = dataSnapshot.getValue(UserModel.class);
                            try {
                                String url = userModel.profileImageUri;
                                Glide.with(holder.itemView.getContext()).load(url).apply(new RequestOptions().circleCrop())
                                        .into(((CustomViewHolder) holder).profileImageView);

                                ((CustomViewHolder) holder).writerNameTextView.setText(commentsDTOs.get(position).userName);
                                ((CustomViewHolder) holder).commentTextView.setText(commentsDTOs.get(position).comment);
                            } catch (NullPointerException e) {
                                Glide.with(holder.itemView.getContext()).load(R.drawable.academy_logo)
                                        .apply(new RequestOptions().circleCrop())
                                        .into(((CustomViewHolder) holder).profileImageView);
                                ((CustomViewHolder) holder).writerNameTextView.setText("탈퇴 유저\n(" + commentsDTOs.get(position).userName + ")");
                                ((CustomViewHolder) holder).commentTextView.setText(commentsDTOs.get(position).comment);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            // 불러오기 실패 or 없을 경우
                        }
                    });

            // 시간 불러오기
            long unixTime = (long) commentsDTOs.get(position).timestamp;
            Date date = new Date(unixTime);
            simpleDateFormat2.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
            String time = simpleDateFormat2.format(date);

            // 적용하기
            ((CustomViewHolder) holder).timeStampTextView.setText(time);

            // 삭제 이미지 클릭 시
            ((CustomViewHolder)holder).deleteImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (commentsDTOs.get(position).uid.equals(auth.getCurrentUser().getUid())) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                        builder.setTitle("확인")
                                .setMessage("삭제하시겠습니까?")
                                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        // 아예 메소드처리를 해버림
                                        delete_content(position);
                                        dialog.cancel();
                                    }
                                });
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                    }
                }
            });

            // 좋아요 버튼 누를 시
            ((CustomViewHolder)holder).starImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onStarClicked(database.getReference().child("board").child("comments").child(boardPositionUid)
                            // 각 댓글에 접근한다는 뜻입니다.
                            .child(commentsUids.get(position)));
                }
            });

            // 해당 게시물에 현재 유저의 uid 값이 있으면 가득 찬 이미지를
            if (commentsDTOs.get(position).boardStars.containsKey(auth.getCurrentUser().getUid())) {
                ((CustomViewHolder) holder).starImageView
                        .setImageResource(R.drawable.star);
                // 없으면 빈 이미지를 출력합니다.
            } else {
                ((CustomViewHolder) holder).starImageView
                        .setImageResource(R.drawable.no_star);
            }

            // 좋아요 카운트 수 불러오기
            try {
                int count = commentsDTOs.get(position).boardStarCount;
                ((CustomViewHolder)holder).starLikeTextView.setText(String.valueOf(count));
            } catch (NullPointerException e) {

            }
        }

        // 삭제 메소드 - 오버 라이딩
        private void delete_content(int position) {
            database.getReference().child("board").child("comments").child(boardPositionUid).child(commentsUids.get(position))
                    .removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        Toast.makeText(getContext(), "삭제되었습니다.", Toast.LENGTH_SHORT).show();
                    }
                }

                // 삭제 실패할 경우
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        Toast.makeText(getContext(), "삭제되지 않았습니다.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        // 파라미터 값은 해당 게시판 데이터 테이블 값
        private void onStarClicked(DatabaseReference postRef) {
            postRef.runTransaction(new Transaction.Handler() {
                @Override
                public Transaction.Result doTransaction(MutableData mutableData) {
                    BoardModel.Comment comment = mutableData.getValue(BoardModel.Comment.class);
                    // 근데 좋아요 버튼을 눌렀다는 건 게시판 데이터가 있다는 건데 에러가 날까?
                    if (comment == null) {
                        return Transaction.success(mutableData);
                    }

                    // 좋아요 취소 기능
                    if (comment.boardStars.containsKey(auth.getCurrentUser().getUid())) {
                        comment.boardStarCount = comment.boardStarCount - 1;
                        comment.boardStars.remove(auth.getCurrentUser().getUid());

                        // 좋아요 기능
                    } else {
                        comment.boardStarCount = comment.boardStarCount + 1;
                        comment.boardStars.put(auth.getCurrentUser().getUid(), true);
                    }

                    // 데이터 업로드
                    mutableData.setValue(comment);
                    return Transaction.success(mutableData);
                }

                @Override
                public void onComplete(DatabaseError databaseError, boolean b,
                                       DataSnapshot dataSnapshot) {
                    // Transaction completed
                }
            });
        }

        @Override
        public int getItemCount() {
            return commentsDTOs.size();
        }

        private class CustomViewHolder extends RecyclerView.ViewHolder {
            ImageView profileImageView, starImageView, deleteImageView;
            TextView writerNameTextView, commentTextView, timeStampTextView, starLikeTextView;

            public CustomViewHolder(View view) {
                super(view);
                profileImageView = view.findViewById(R.id.boardcommenfragment_item_imageview);
                starImageView = view.findViewById(R.id.boardcommentfragment_imageview_star);
                deleteImageView = view.findViewById(R.id.boardcommentfragment_imageview_delete);

                writerNameTextView = view.findViewById(R.id.boardcommenfragment_item_textview_name);
                commentTextView = view.findViewById(R.id.boardcommentfragment_item_textview);
                timeStampTextView = view.findViewById(R.id.boardcommentfragment_item_textview_timestamp);
                starLikeTextView = view.findViewById(R.id.boardcommentfragment_textview_count);
            }
        }
    }
}
