package com.test.howlfirebasetest1_1;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
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
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;

public class BoardActivity extends AppCompatActivity {
    private RecyclerView board_recyclerview;
    private List<ImageDTO> imagesDTOLists = new ArrayList<>();
    private List<String> userUidsLists = new ArrayList<>();
    private FirebaseDatabase database;
    private FirebaseAuth auth;
    private FirebaseStorage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.board_activity_main);

        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();

        board_recyclerview = (RecyclerView) findViewById(R.id.boardactivity_recyclerview);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        board_recyclerview.setLayoutManager(layoutManager);

        final BoardRecyclerViewAdapter boardRecyclerViewAdapter = new BoardRecyclerViewAdapter();
        board_recyclerview.setAdapter(boardRecyclerViewAdapter);

        database.getReference().child("images").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                imagesDTOLists.clear();
                userUidsLists.clear();
                for (DataSnapshot snapShot: dataSnapshot.getChildren()) {
                    ImageDTO imageDTO = snapShot.getValue(ImageDTO.class);
                    imagesDTOLists.add(imageDTO);

                    // 해당 데이터 테이블의 키 값
                    String uidKeys = snapShot.getKey();
                    userUidsLists.add(uidKeys);
                }
                boardRecyclerViewAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(BoardActivity.this, "데이터를 읽어오지 못했습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    class BoardRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.board_activity_recyclerview_item, parent, false);

            return new CustomViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
            ((CustomViewHolder)holder).titleTextView.setText(imagesDTOLists.get(position).title);
            ((CustomViewHolder)holder).descriptionTextView.setText(imagesDTOLists.get(position).description);
            Glide.with(holder.itemView.getContext()).load(imagesDTOLists.get(position).imageUri)
                    .into(((CustomViewHolder)holder).pickImageView);

            ((CustomViewHolder)holder).starImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onStarClicked(database.getReference().child("images")
                            // 각 게시물에 접근한다는 뜻입니다.
                            .child(userUidsLists.get(position)));
                }
            });
            // 해당 게시물에 현재 유저의 uid 값이 있으면 가득 찬 이미지를
            if (imagesDTOLists.get(position).stars.containsKey(auth.getCurrentUser().getUid())) {
                ((CustomViewHolder)holder).starImageView
                        .setImageResource(R.drawable.star);
            // 없으면 빈 이미지를 출력합니다.
            } else {
                ((CustomViewHolder)holder).starImageView
                        .setImageResource(R.drawable.no_star);
            }
            int count = imagesDTOLists.get(position).starCount;
            ((CustomViewHolder)holder).countTextView.setText(String.valueOf(count));


            ((CustomViewHolder)holder).deleteImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    delete_content(position);
                }
            });
        }

        @Override
        public int getItemCount() {
            return imagesDTOLists.size();
        }

        private void onStarClicked(DatabaseReference postRef) {
            postRef.runTransaction(new Transaction.Handler() {
                @Override
                public Transaction.Result doTransaction(MutableData mutableData) {
                    ImageDTO imageDTO = mutableData.getValue(ImageDTO.class);
                    if (imageDTO == null) {
                        return Transaction.success(mutableData);
                    }
                    // 좋아요 취소 기능
                    if (imageDTO.stars.containsKey(auth.getCurrentUser().getUid())) {
                        // Unstar the post and remove self from stars
                        imageDTO.starCount = imageDTO.starCount - 1;
                        imageDTO.stars.remove(auth.getCurrentUser().getUid());
                        // 좋아요 기능
                    } else {
                        // Star the post and add self to stars
                        imageDTO.starCount = imageDTO.starCount + 1;
                        imageDTO.stars.put(auth.getCurrentUser().getUid(), true);
                    }
                    // Set value and report transaction success
                    mutableData.setValue(imageDTO);
                    return Transaction.success(mutableData);
                }

                @Override
                public void onComplete(DatabaseError databaseError, boolean b,
                                       DataSnapshot dataSnapshot) {
                    // Transaction completed

                }
            });
        }

        // 이미지 삭제 성공 시 데이터 베이스 값 삭제합니다.
        private void delete_content(final int position){
            storage.getReference().child("images")
                    .child(imagesDTOLists.get(position).imageName)
                    .	delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                // 데이터 베이스 값 삭제
                @Override
                public void onSuccess(Void aVoid) {
                    database.getReference().child("images")
                            .child(userUidsLists.get(position)).removeValue()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(BoardActivity.this, "삭제가 완료 되었습니다.", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(BoardActivity.this, "삭제 실패",
                            Toast.LENGTH_SHORT).show();
                }
            });
        }

        private class CustomViewHolder extends RecyclerView.ViewHolder {
            ImageView pickImageView, starImageView, deleteImageView;
            TextView titleTextView, descriptionTextView, countTextView;
            public CustomViewHolder(View view) {
                super(view);
                // 여기서 중요한 것은 view.find...라는 것
                pickImageView = view.findViewById(R.id.boardactivity_recyclerview_item_imageview);
                titleTextView = view.findViewById(R.id.boardactivity_recyclerview_item_titletextview);
                descriptionTextView = view.findViewById(R.id.boardactivity_recyclerview_item_descriptiontextview);
                starImageView = view.findViewById(R.id.boardactivity_recyclerview_item_imageview_star);
                countTextView = view.findViewById(R.id.boardactivity_recyclerview_item_textview_count);
                deleteImageView = view.findViewById(R.id.boardactivity_recyclerview_item_imageview_delete);
            }
        }
    }
}
