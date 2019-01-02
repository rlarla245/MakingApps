package com.du.chattingapp.Fragments;

import android.app.Fragment;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.du.chattingapp.Models.BoardModel;
import com.du.chattingapp.Models.NotificationModel;
import com.du.chattingapp.Models.UserModel;
import com.du.chattingapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class BoardUploadFragment extends Fragment {
    // 필요한 변수들 생성
    public EditText titleEditText, descriptionEditText;
    public Button uploadButton;

    // Firebase 관련 변수 생성
    public FirebaseAuth auth;
    public FirebaseDatabase database;

    // 작성자 uid 불러오기 - 왜 필요한지 모르겠음
    // public static String writerUid;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.boarduploadfragment, container, false);

        // 필요한 변수 생성
        titleEditText = (EditText) view.findViewById(R.id.boarduploadfragment_edittext_title);
        descriptionEditText = (EditText) view.findViewById(R.id.boarduploadfragment_edittext_description);
        uploadButton = (Button) view.findViewById(R.id.boarduploadfragment_button_uploadbutton);

        // 업로드 버튼을 누를 경우의 동작
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (titleEditText.getText().toString().trim().equals("") || descriptionEditText.getText().toString().trim().equals("")) {
                    Toast.makeText(v.getContext(), "입력이 필요합니다.", Toast.LENGTH_SHORT).show();
                    return;
                }

                else {
                    // 모든 유저들에게 푸시 메시지를 전송합니다.
                    database.getReference().child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                UserModel userModel = snapshot.getValue(UserModel.class);
                                sendGcm(userModel.pushToken);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    // 업로드 후 푸시 메시지 전송
                    upload();
                    // getFragmentManager().beginTransaction().replace(R.id.firstActivity_FirstLayout_mainFrame, new BoardFragment())
                    //        .commit();
                }
            }
        });

        // Firebase 관련 변수 생성
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        return view;
    }

    // 푸시 메시지를 보내야 합니다.
    void sendGcm(String pushToken) {
        Gson gson = new Gson();
        NotificationModel notificationModel = new NotificationModel();

        // 현재 유저 네임으로 메시지를 띄울 때 사용하기 위함입니다.
        String userName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();

        notificationModel.to = pushToken;
        notificationModel.notification.title = titleEditText.getText().toString();
        notificationModel.notification.text = userName + "님이 새로운 게시글을 업로드했습니다.";

        // 푸시 메시지 출력에 필요한 변수들입니다.
        notificationModel.data.title = titleEditText.getText().toString();
        notificationModel.data.text = userName + "님이 새로운 게시글을 업로드했습니다.";
        notificationModel.data.caseNumber = "2";

        // 포스트 맨과 같은 바디를 생성했습니다.
        RequestBody requestBody =
                RequestBody.create(MediaType.parse("application/json; charset=utf8"), gson.toJson(notificationModel));

        // 포스트 맨과 같은 헤더 부분입니다.
        Request request = new Request.Builder()
                .header("Content-Type","application/json")
                // 해당 서버키를 입력합니다.
                .addHeader("Authorization","key=AIzaSyCDpcPkE61tZtjVRdO3JoJ9AhWdrqEwzFA")
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

    // 업로드 메소드
    public void upload() {
        // 게시판 모델 설정
        final BoardModel boardModel = new BoardModel();
        boardModel.boardUid = auth.getCurrentUser().getUid();

        // 전역 변수화... 인데 이거 왜 쓰지?
        // writerUid = boardModel.boardUid;

        boardModel.boardTitle = titleEditText.getText().toString();
        boardModel.boardDescription = descriptionEditText.getText().toString();
        boardModel.boardUserId = auth.getCurrentUser().getEmail();

        // SimpleDataFormat은 파이어베이스에 담을 수 없기 때문에 변환이 필요합니다.
        boardModel.timeStamp = ServerValue.TIMESTAMP;

        // "board" 데이터 테이블에 값을 넣습니다.
        database.getReference().child("board")
                .push().setValue(boardModel).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    Toast.makeText(getContext(), "업로드에 성공했습니다.", Toast.LENGTH_SHORT).show();
                }
            }

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    Toast.makeText(getContext(), "업로드에 실패했습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
