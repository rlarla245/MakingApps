package com.updatetest.whereareyou.Fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.updatetest.whereareyou.Models.RoomModel;
import com.updatetest.whereareyou.R;

import java.util.ArrayList;
import java.util.List;

public class MapFragment extends Fragment{
    List<RoomModel> roomModels = new ArrayList<>();

    // 상대방 uid 불러옵니다.
    String counterPartUid = getArguments().getString("counterPartUid");

    // 내 uid
    String myUid;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        // 전체 레이아웃
        final View view = inflater.inflate(R.layout.fragment_mapfragment, container, false);

        // 내 uid 호출
        myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // 데이터베이스 돌면서 rooms 데이터들 전부 호출
        FirebaseDatabase.getInstance().getReference().child("rooms")
                .orderByChild("users/" + myUid).equalTo(true).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                roomModels.clear();

                // 반복문 돌면서 데이터 긁어오기
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    try {
                        RoomModel roomModel = snapshot.getValue(RoomModel.class);

                        // 리스트 삽입
                        roomModels.add(roomModel);
                    } catch (NullPointerException e) {
                        Toast.makeText(view.getContext(), "데이터베이스 에러: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

                // 실질적 실행 코드
                // 반복문 돌면서 해당 두 유저 uid 값을 포함하고 있는지 확인
                for (RoomModel roomModel : roomModels) {
                    // null 값이 아니고 + 내 uid 있고 + 상대방 uid 있는 경우
                    if (roomModel != null && roomModel.users.containsKey(myUid)
                            && roomModel.users.containsKey(counterPartUid)) {

                        if (roomModel.users.get(counterPartUid) == false) {
                            Toast.makeText(view.getContext(), "아직 상대방이 위치 제공에 동의하지 않았습니다 :)", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                // 룸 모델 설정
                // 기존 생성된 방이 없을 경우
                RoomModel roomModel = new RoomModel();
                roomModel.users.put(myUid, true);
                roomModel.users.put(counterPartUid, false);
                FirebaseDatabase.getInstance().getReference().child("rooms")
                        .push().setValue(roomModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(view.getContext(), "데이터베이스 업로드 완료", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(view.getContext(), "데이터베이스 입력 오류", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return view;
    }
}
