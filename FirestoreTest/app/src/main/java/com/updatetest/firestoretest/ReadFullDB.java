package com.updatetest.firestoretest;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.updatetest.firestoretest.Models.UserModel;

import java.util.ArrayList;
import java.util.List;

public class ReadFullDB extends AppCompatActivity {
    List<UserModel> getUserModels = new ArrayList<>();
    List<UserModel> snapshotUserModels = new ArrayList<>();
    List<String> snapshotKeysUserModels = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_full_db);

        // get 리사이클러 뷰
        RecyclerView recyclerView_get = findViewById(R.id.readfulldb_recyclerview_get);
        recyclerView_get.setAdapter(new GetRecyclerViewAdapter());
        recyclerView_get.setLayoutManager(new LinearLayoutManager(this));

        // snapshot 리사이클러 뷰
        RecyclerView recyclerView_snapshot = findViewById(R.id.readfulldb_recyclerview_snapshot);
        recyclerView_snapshot.setAdapter(new SnapshotRecyclerViewAdapter());
        recyclerView_snapshot.setLayoutManager(new LinearLayoutManager(this));
    }

    class GetRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        // db 읽어오기
        public GetRecyclerViewAdapter() {
            FirebaseFirestore.getInstance().collection("users").get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    getUserModels.clear();
                    for (DocumentSnapshot snapshots : queryDocumentSnapshots.getDocuments()) {
                        UserModel userModel = snapshots.toObject(UserModel.class);
                        getUserModels.add(userModel);
                    }
                    notifyDataSetChanged();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(ReadFullDB.this, "데이터를 불러오지 못했습니다.", Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.get_readdb_item, parent, false);

            return new CustomViewHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ((CustomViewHolder) holder).name.setText(getUserModels.get(position).name);
            ((CustomViewHolder) holder).age.setText(String.valueOf(getUserModels.get(position).age));
            ((CustomViewHolder) holder).major.setText(getUserModels.get(position).major);
        }

        @Override
        public int getItemCount() {
            return getUserModels.size();
        }

        private class CustomViewHolder extends RecyclerView.ViewHolder {
            TextView name;
            TextView age;
            TextView major;

            public CustomViewHolder(View view) {
                super(view);
                name = view.findViewById(R.id.getReadDBItem_textview_name);
                age = view.findViewById(R.id.getReadDBItem_textview_age);
                major = view.findViewById(R.id.getReadDBItem_textview_major);
            }
        }
    }

    class SnapshotRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        // 생성자
        public SnapshotRecyclerViewAdapter() {
            FirebaseFirestore.getInstance().collection("users")
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
                            for (DocumentChange snapshot : queryDocumentSnapshots.getDocumentChanges()) {
                                switch (snapshot.getType()) {
                                    case ADDED:
                                        snapshotUserModels.add(snapshot.getDocument().toObject(UserModel.class));
                                        snapshotKeysUserModels.add(snapshot.getDocument().getId());
                                        notifyDataSetChanged();
                                        break;

                                    case MODIFIED:
                                        modifyItem(snapshot.getDocument().getId(), snapshot.getDocument().toObject(UserModel.class));
                                        notifyDataSetChanged();
                                        break;

                                    case REMOVED:
                                        removeItem(snapshot.getDocument().getId());
                                        notifyDataSetChanged();
                                        break;
                                }
                            }
                        }
                    });
        }

        void modifyItem(String modifyItem, UserModel userModel) {
            int index = 0;
            while (index < snapshotKeysUserModels.size()) {
                if (snapshotKeysUserModels.get(index).equals(modifyItem)) {
                    snapshotUserModels.set(index, userModel);
                }
                index++;
            }
        }

        void removeItem(String deleteItemKey) {
            int index = 0;
            while (index < snapshotKeysUserModels.size()) {
                if (snapshotKeysUserModels.get(index).equals(deleteItemKey)) {
                    snapshotKeysUserModels.remove(index);
                    snapshotUserModels.remove(index);
                }
                index++;
            }
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.snapshot_readdb_item, parent, false);

            return new CustomViewHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ((CustomViewHolder) holder).name.setText(snapshotUserModels.get(position).name);
            ((CustomViewHolder) holder).age.setText(String.valueOf(snapshotUserModels.get(position).age));
            ((CustomViewHolder) holder).major.setText(snapshotUserModels.get(position).major);
        }

        @Override
        public int getItemCount() {
            return snapshotUserModels.size();
        }

        private class CustomViewHolder extends RecyclerView.ViewHolder {
            TextView name;
            TextView age;
            TextView major;

            public CustomViewHolder(View view) {
                super(view);

                name = view.findViewById(R.id.snapshotReadDBItem_textview_name);
                age = view.findViewById(R.id.snapshotReadDBItem_textview_age);
                major = view.findViewById(R.id.snapshotReadDBItem_textview_major);
            }
        }
    }
}
