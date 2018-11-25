package com.du.chattingapp.Fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Build;
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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.du.chattingapp.Models.BoardModel;
import com.du.chattingapp.Models.UserModel;
import com.du.chattingapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class BoardFragment extends Fragment {
    // 게시판, 게시판 uid, 유저 정보들을 불러옵니다.
    private List<BoardModel> boardModelLists = new ArrayList<>();
    private List<UserModel> boardUserModelLists = new ArrayList<>();
    private List<String> boardUidsLists = new ArrayList<>();

    // Firebase 관련 변수들 불러오기
    private FirebaseDatabase database;
    private FirebaseAuth auth;

    // TimeTable 찍기
    @SuppressLint("SimpleDateFormat")
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd");

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.boardfragment, container, false);

        // Firebase 관련 변수들 생성
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        // 리사이클러 뷰 호출, 레이아웃 매니저 설정, 어댑터 설정
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.board_fragment_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(inflater.getContext()));
        final RecyclerViewAdapter recyclerViewAdapter = new RecyclerViewAdapter();
        recyclerView.setAdapter(recyclerViewAdapter);

        // 유저 모델 불러오기
        database.getReference().child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boardUserModelLists.clear();

                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    UserModel userModel = userSnapshot.getValue(UserModel.class);
                    boardUserModelLists.add(userModel);
                }
                // 갱신
                recyclerViewAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    Toast.makeText(getContext(), "데이터를 불러오지 못했습니다.\n관리자에게 문의 부탁드립니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // 게시판 데이터 불러오기 - 없을 경우 에러 낼 것 같아서 예외처리 해놓습니다.
        try {
            database.getReference().child("board").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    boardModelLists.clear();
                    boardUidsLists.clear();

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        // 게시판 데이터들을 불러옵니다.
                        BoardModel boardModel = snapshot.getValue(BoardModel.class);
                        boardModelLists.add(boardModel);

                        // 해당 데이터 테이블의 키(uid) 값
                        String uidKeys = snapshot.getKey();
                        boardUidsLists.add(uidKeys);
                    }
                    recyclerViewAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        Toast.makeText(getContext(), "데이터를 불러오지 못했습니다.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } catch (NullPointerException e) {
            Toast.makeText(getActivity(), "게시판 데이터가 없습니다. 업로드 바랍니다.", Toast.LENGTH_SHORT).show();
        }

        // 플로팅 버튼을 통해 게시판 업로드 프레그먼트로 이동합니다.
        FloatingActionButton floatingActionButton = view.findViewById(R.id.board_fragment_uploadfloatingbutton);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 getFragmentManager().beginTransaction()
                        .replace(R.id.firstActivity_FirstLayout_mainFrame, new BoardUploadFragment()).addToBackStack(BoardUploadFragment.class.getName()).commit();
            }
        });
        return view;
    }

    // 어댑터 설정
    class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.boardfragment_item, parent, false);
            return new CustomViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
            // 시간 불러오기
            long unixTime = (long) boardModelLists.get(position).timeStamp;
            Date date = new Date(unixTime);
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
            String time = simpleDateFormat.format(date);

            ((CustomViewHolder) holder).timeTextView.setText(time);

            // 게시판 제목, 내용 불러오기
            ((CustomViewHolder) holder).titleTextView.setText(boardModelLists.get(position).boardTitle);
            ((CustomViewHolder) holder).contentTextView.setText(boardModelLists.get(position).boardDescription);

            // 게시판에 이미지 및 사진 불러오기
            for (UserModel userModel : boardUserModelLists) {
                // 게시판 작성자 uid를 기준으로 일치할 경우 프로필 이미지 및 작성자 이름을 불러옵니다.
                if (userModel.uid.equals(boardModelLists.get(position).boardUid)) {
                    try {
                        // 프로필 이미지 불러오기
                        Glide.with(holder.itemView.getContext())
                                .load(userModel.profileImageUri)
                                .apply(new RequestOptions().circleCrop())
                                .into(((CustomViewHolder) holder).userImageView);

                        // 작성자 이름 불러오기
                        ((CustomViewHolder) holder).writerTextView.setText(userModel.userName);
                    } catch (Exception e) {
//
                    }
                }
            }

            // 좋아요 기능
            ((CustomViewHolder) holder).starImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onStarClicked(database.getReference().child("board")
                            // 각 게시물에 접근한다는 뜻입니다.
                            .child(boardUidsLists.get(position)));
                }
            });

            // 해당 게시물에 현재 유저의 uid 값이 있으면 가득 찬 이미지를
            if (boardModelLists.get(position).boardStars.containsKey(auth.getCurrentUser().getUid())) {
                ((CustomViewHolder) holder).starImageView
                        .setImageResource(R.drawable.star);
                // 없으면 빈 이미지를 출력합니다.
            } else {
                ((CustomViewHolder) holder).starImageView
                        .setImageResource(R.drawable.no_star);
            }

            // 좋아요 갯수 불러오기
            int count = boardModelLists.get(position).boardStarCount;
            ((CustomViewHolder) holder).countTextView.setText(String.valueOf(count));

            // 휴지통 이미지를 누를 경우 삭제 dialog를 띄웁니다.
            // 물론, 작성자 본인일 경우에만 띄워줘야 겠죠?
            if (boardModelLists.get(position).boardUid.equals(auth.getCurrentUser().getUid())) {
                ((CustomViewHolder) holder).deleteImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
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
                });
            }
        }

        @Override
        public int getItemCount() {
            return boardModelLists.size();
        }

        private class CustomViewHolder extends RecyclerView.ViewHolder {
            TextView titleTextView, writerTextView, timeTextView, countTextView, contentTextView;
            ImageView starImageView, deleteImageView, userImageView;

            public CustomViewHolder(View view) {
                super(view);
                titleTextView = (TextView)view.findViewById(R.id.boardfragment_textview_title);
                writerTextView = (TextView) view.findViewById(R.id.boardfragment_textview_writername);
                timeTextView = (TextView) view.findViewById(R.id.boardfragment_textView_time);
                countTextView = (TextView) view.findViewById(R.id.boardfragment_textview_count);
                contentTextView = (TextView) view.findViewById(R.id.boardfragment_textview_content);
                starImageView = (ImageView) view.findViewById(R.id.boardfragment_imageview_star);
                deleteImageView = (ImageView) view.findViewById(R.id.boardfragment_imageview_delete);
                userImageView = (ImageView) view.findViewById(R.id.boardfragment_item_uploaderImageView);
            }
        }
    }

    // 파라미터 값은 해당 게시판 데이터 테이블 값
    private void onStarClicked(DatabaseReference postRef) {
        postRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                BoardModel boardModel = mutableData.getValue(BoardModel.class);
                // 근데 좋아요 버튼을 눌렀다는 건 게시판 데이터가 있다는 건데 에러가 날까?
                if (boardModel == null) {
                    return Transaction.success(mutableData);
                }

                // 좋아요 취소 기능
                if (boardModel.boardStars.containsKey(auth.getCurrentUser().getUid())) {
                    boardModel.boardStarCount = boardModel.boardStarCount - 1;
                    boardModel.boardStars.remove(auth.getCurrentUser().getUid());

                    // 좋아요 기능
                } else {
                    boardModel.boardStarCount = boardModel.boardStarCount + 1;
                    boardModel.boardStars.put(auth.getCurrentUser().getUid(), true);
                }

                // 데이터 업로드
                mutableData.setValue(boardModel);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b,
                                   DataSnapshot dataSnapshot) {
                // Transaction completed
            }
        });
    }

    // 어차피 boardLists를 통해 모든 데이터를 불러왔으므로
    // 몇 번째 인덱스 값인지만 파라미터로 넘겨줘 쉽게 삭제할 수 있습니다.
    private void delete_content(int position) {
        database.getReference().child("board").child(boardUidsLists.get(position))
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
}
