package com.updatetest.whereareyou.Fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.updatetest.whereareyou.GoogleMapActivity;
import com.updatetest.whereareyou.Models.RoomModel;
import com.updatetest.whereareyou.Models.UserModel;
import com.updatetest.whereareyou.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CheckRoomMapFragment extends Fragment {
    // 룸 모델을 담는 리스트
    List<RoomModel> roomModels = new ArrayList<>();

    // 상대방 uid 불러옵니다.
    String counterPartUid;

    // 내 uid
    String myUid;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, Bundle savedInstanceState) {
        // 전체 레이아웃
        final View view = inflater.inflate(R.layout.fragment_checkroommapfragment, container, false);

        // 내 uid 호출
        myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // 상대방 uid를 불러오기 위해 db 데이터를 활용합니다.
        FirebaseDatabase.getInstance().getReference().child("users")
                // 내 uid의 정보를 불러옵니다.
                .child(myUid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        counterPartUid = dataSnapshot.getValue(UserModel.class).counterPartUid;

                        // 룸 uid를 조합하기 위한 새로운 문자열입니다.
                        final String roomUid;

                        // 내 uid의 문자열이 상대방 uid의 문자열보다 클 경우
                        // 내 uid + 상대방 uid로 room을 생성합니다.
                        if (myUid.compareTo(counterPartUid) > 0) {
                            roomUid = myUid + counterPartUid;
                        } else {
                            // 그렇지 않으면 반대로
                            roomUid = counterPartUid + myUid;
                        }

                        // 데이터베이스 돌면서 rooms 데이터들 전부 호출
                        FirebaseDatabase.getInstance().getReference().child("rooms")
                                // 내 uid가 있는 경우
                                .orderByChild("users/" + myUid)
                                .addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        roomModels.clear();

                                        // 반복문 돌면서 데이터 긁어오기
                                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                            try {
                                                RoomModel roomModel = snapshot.getValue(RoomModel.class);

                                                // 리스트 삽입
                                                roomModels.add(roomModel);
                                            }
                                            // db에 null값 데이터가 존재합니다.
                                            catch (NullPointerException e) {
                                                Toast.makeText(view.getContext(), "데이터베이스 에러: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        }

                                        // 룸을 생성할 때 2가지의 경우가 존재합니다.
                                        // 1. 이미 존재하는 경우 해당 룸으로 진입해야 합니다.
                                        // 2. 존재하지 않는 경우 새로 룸을 생성해야 합니다.

                                        // 방이 기존에 존재하는지 확인하는 정수형 변수
                                        // 들어올 때마다 리셋되니까 상관없지 않나?
                                        int criteriaNumber = 0;

                                        // 실질적 실행 코드
                                        // 반복문 돌면서 해당 두 유저 uid 값을 포함하고 있는지 확인
                                        for (RoomModel roomModel : roomModels) {
                                            // null 값이 아니고 + 내 uid 있고 + 상대방 uid 있는 경우
                                            if (roomModel != null && roomModel.users.containsKey(myUid)
                                                    && roomModel.users.containsKey(counterPartUid)) {
                                                // 결국 방이 존재한다는 경우
                                                criteriaNumber = 1;

                                                // System.out.println("확인. roomModel 값: " + roomModel.users);
                                                // System.out.println("확인. 조건문 들어오는지?");

                                                // 내 밸류 값이 false일 경우
                                                // 즉, 내가 아직 매칭하지 않은 경우
                                                if (!roomModel.users.get(myUid)) {
                                                    System.out.println("내 값 확인. false입니다. true로 변환합니다.");
                                                    Map<String, Object> updateMap = new HashMap<>();
                                                    updateMap.put(myUid, true);

                                                    // DB 업데이트
                                                    FirebaseDatabase.getInstance().getReference()
                                                            .child("rooms").child(roomUid).child("users")
                                                            // 내 uid, true로 변환합니다.
                                                            .updateChildren(updateMap);

                                                    // 코드는 순차적으로 불러오기 때문에 다다음 조건문과 겹치게 되어
                                                    // 아래 코드는 주석처리합니다. 중복되어 필요없는 코드입니다.
                                                    // 상대방 - true && 나 - true 이므로 fragment 이동
                                                    /*
                                                    getFragmentManager().beginTransaction()
                                                            .replace(R.id.mainactivity_fragment, new MapFragment())
                                                            .commit();
                                                    */
                                                }

                                                // 상대방 밸류 값이 false일 경우
                                                if (!roomModel.users.get(counterPartUid)) {
                                                    Toast.makeText(view.getContext(), "아직 상대방이 위치 제공에 동의하지 않았습니다 :)", Toast.LENGTH_SHORT).show();
                                                }

                                                // 둘 다 true일 경우 프레그먼트 이동합니다.
                                                if (roomModel.users.get(myUid) && roomModel.users.get(counterPartUid)) {
                                                    System.out.println("확인. 프레그먼트 이동");

                                                    // 인텐트 설정. 실제로 위치를 보여주는 액티비티로 이동합니다.
                                                    // 인텐트에 상대방 uid 정보를 담아줍니다.
                                                    Intent mapIntent = new Intent(view.getContext(), GoogleMapActivity.class);
                                                    mapIntent.putExtra("counterPartUid", counterPartUid);

                                                    // 지도 액티비티로 이동
                                                    startActivity(mapIntent);

                                                    // 기존 앱 닫기
                                                    // 프레그먼트이기 때문에 finish()를 바로 사용할 수 없습니다.
                                                    getActivity().finish();
                                                }
                                            }
                                        }

                                        // 인덱스 확인
                                        // System.out.println("기준 값 확인: " + criteriaNumber);

                                        // 방이 없을 경우
                                        // 새로 생성해야 합니다.
                                        if (criteriaNumber == 0) {
                                            // 룸 모델 설정
                                            // 기존 생성된 방이 없을 경우
                                            RoomModel roomModel = new RoomModel();
                                            // 나는 true, 상대방은 false로 지정합니다.
                                            roomModel.users.put(myUid, true);
                                            roomModel.users.put(counterPartUid, false);

                                            // 룸 생성 in DB
                                            FirebaseDatabase.getInstance().getReference().child("rooms")
                                                    .child(roomUid).setValue(roomModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Toast.makeText(view.getContext(), "상대방이 회원님의 휴대전화 번호를 입력하면 위치를 볼 수 있습니다 :)", Toast.LENGTH_SHORT).show();
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(view.getContext(), "데이터베이스 입력 오류", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

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
