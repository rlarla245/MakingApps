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
import com.du.chattingapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

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
                } else {
                    upload();
                    getFragmentManager().beginTransaction().replace(R.id.firstActivity_FirstLayout_mainFrame, new BoardFragment())
                            .commit();
                    Toast.makeText(v.getContext(), "리스트를 길게 눌러 게시판에 접속합니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Firebase 관련 변수 생성
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        return view;
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
