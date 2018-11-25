package com.ssma.serverapp.Fragments;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ssma.serverapp.Chat.MessageActivity;
import com.ssma.serverapp.Model.ChatModel;
import com.ssma.serverapp.Model.UserModel;
import com.ssma.serverapp.R;

import java.util.ArrayList;
import java.util.List;

public class SelectPeopleActivity extends AppCompatActivity {
    ChatModel chatModel = new ChatModel();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_select_people);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.selectpeopleactivity_recyclerview);
        recyclerView.setAdapter(new SelectPeopleRecyclerViewAdapter());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        FloatingActionButton floatingActionButton = (FloatingActionButton)findViewById(R.id.selectpeopleactivity_floatingbutton);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SelectPeopleActivity.this, "채팅방이 생성되었습니다.", Toast.LENGTH_SHORT).show();
                String myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                // 내 uid도 입력해야합니다.
                chatModel.users.put(myUid,true);
                // push가 데이터베이스에 값을 입력하겠다는 메소드입니다.
                FirebaseDatabase.getInstance().getReference().child("chatrooms").push().setValue(chatModel);

                finish();
            }
        });
    }

    class SelectPeopleRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        List<UserModel> userModels;

        public SelectPeopleRecyclerViewAdapter() {
            userModels = new ArrayList<>();
            // 내 아이디
            final String myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

            FirebaseDatabase.getInstance().getReference().child("users").addValueEventListener(new ValueEventListener() {

                @Override
                // 서버에서 넘어온 데이터를 의미합니다.
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // 친구 목록 추가 시 데이터를 불러오게 되는데 다음과 같은 코드가 없으면 똑같은 친구들을 또 불러옵니다.
                    userModels.clear();

                    // dataSnapshot이 데이터이므로 반복문을 통해 리스트에 넣어줍시다.
                    for (DataSnapshot snapshot :dataSnapshot.getChildren()){
                        UserModel userModel = snapshot.getValue(UserModel.class);

                        if (userModel.uid.equals(myUid)) {
                            continue;
                        }
                        userModels.add(userModel);

                    }

                    // 새로고침 버튼입니다. 이게 없으면 친구 목록이 뜨지 않습니다.
                    notifyDataSetChanged();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.peoples_fragment_item_select, parent, false);

            return new CustomViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
            Glide.with
                    (holder.itemView.getContext())
                    .load(userModels.get(position).profileImageUri)
                    .apply(new RequestOptions().circleCrop())
                    .into(((CustomViewHolder)holder).imageView);

            ((CustomViewHolder)holder).textView.setText(userModels.get(position).userName);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent chat_intent = new Intent(view.getContext(), MessageActivity.class);
                    chat_intent.putExtra("destinationUid", userModels.get(position).uid);
                    // 젤리빈 이상부터 적용합니다.
                    ActivityOptions activityOptions = null;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                        activityOptions = ActivityOptions.makeCustomAnimation(view.getContext(), R.anim.fromright, R.anim.toleft);
                        startActivity(chat_intent, activityOptions.toBundle());
                    }
                }
            });

            if (userModels.get(position).status_message != null) {
                ((CustomViewHolder)holder).comment.setText(userModels.get(position).status_message);
            }

            ((CustomViewHolder) holder).
            // 체크 여부에 따라 반응합니다.
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    //체크 된상태, 즉 불러온 상태
                    if (b) {
                        // 체크된 유저의 uid를 불러옵시다. 이를 true 값으로 표시합니다.
                        // users 해시맵에 담습니다.
                        chatModel.users.put(userModels.get(position).uid,true);
                        //체크 취소 상태
                    } else {
                        chatModel.users.remove(userModels.get(position));
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return userModels.size();
        }

        private class CustomViewHolder extends RecyclerView.ViewHolder {

            public ImageView imageView;
            public TextView textView;
            public TextView comment;
            public CheckBox checkBox;

            public CustomViewHolder(View view) {
                super(view);
                imageView = (ImageView)view.findViewById(R.id.peoples_fragment_item_imageview);
                textView = (TextView)view.findViewById(R.id.peoples_fragment_item_textview);
                comment = (TextView)view.findViewById(R.id.peoples_fragment_item_textview_message);
                checkBox = (CheckBox)view.findViewById(R.id.peoples_fragment_item_select_checkbox);
            }
        }
    }
}
