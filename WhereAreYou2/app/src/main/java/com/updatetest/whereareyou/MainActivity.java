package com.updatetest.whereareyou;

import android.app.Fragment;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.updatetest.whereareyou.Fragment.CheckRoomMapFragment;
import com.updatetest.whereareyou.Models.UserModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    // 메뉴를 띄울 툴바 내 이미지 버튼입니다.
    protected ImageButton imageButtonMenu;

    // 매칭된 유저의 존재 여부를 확인하는 정수형 변수입니다.
    int caseNumber;

    // 위젯들 선언합니다.
    EditText editText_UserPhoneNumber;

    // 유저들을 담을 리스트입니다.
    List<UserModel> userModels = new ArrayList<>();

    // 내 정보들입니다.
    UserModel myUserModel = new UserModel();

    // 내 uid와 상대방 uid입니다.
    String myUid;
    String counterpartUid;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 내 uid를 입력합니다.
        // 회원가입 후 task.getResult().getUser().getUid() 입니다.
        try {
            myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        } catch (NullPointerException e) {
            Toast.makeText(this, "uid 에러 발생. 관리자에게 문의바랍니다.", Toast.LENGTH_SHORT).show();
        }

        // DB에서 유저들 정보를 불러옵시다.
        FirebaseDatabase.getInstance().getReference().child("users")
                // 해당 액티비티가 실행될 때 '한 번만' 데이터들을 불러옵니다.
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userModels.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // UserModel 데이터 형식으로 db 내 데이터들을 담습니다.
                    // 리스트에 담습니다.
                    userModels.add(snapshot.getValue(UserModel.class));

                    try {
                        // 내 정보를 담기 위한 조건문입니다.
                        // 현재 계정의 uid값과 db내 계정의 uid값이 같을 경우 == 내 계정일 때
                        if (FirebaseAuth.getInstance().getCurrentUser().getUid()
                                .equals(snapshot.getValue(UserModel.class).uid)) {
                            myUserModel = snapshot.getValue(UserModel.class);
                            // 매칭된 상대방이 있는지 확인하는 정수형 변수입니다.
                            // 없으면 0, 있으면 1입니다.
                            caseNumber = myUserModel.caseNumber;
                        }
                    }
                    // db내 계정 정보가 null 값을 내뱉을 경우 -> 유령 계정
                    catch (NullPointerException e) {
                        Toast.makeText(MainActivity.this, "유저 정보가 없는 계정이 존재합니다.\n관리자에게 문의바랍니다.", Toast.LENGTH_SHORT).show();
                    }
                }

                // 테스트
                // 원하는 상대방의 휴대폰 번호를 입력한 경우
                // 바로 위치를 알려주는 프레그먼트로 이동합니다.
                if (caseNumber == 1) {
                    // 프레그먼트 값을 원하는 프레그먼트로 변환해줍니다.
                    Fragment toMapFragment = new CheckRoomMapFragment();

                    // 위치를 보여주는 프레그먼트로 넘깁니다.
                    getFragmentManager().beginTransaction().replace(R.id.mainactivity_fragment,toMapFragment)
                            .commit();
                }

                // caseNumber가 0일 경우 다이얼로그 창을 띄웁니다.
                // 위치를 알고 싶어하는 상대방의 휴대폰 번호를 입력해야 합니다.
                if (caseNumber == 0) {
                    // 다이얼로그에 활용할 뷰를 설정합니다.
                    View alertView = View.inflate(MainActivity.this, R.layout.matching_user_dialog_view, null);

                    // 위젯들 호출합니다.
                    // 해당 액티비티 소속의 EditText라는 것을 밝혀줘야 합니다.
                    // 해당 액티비티 레이아웃에 있는 위젯이 아니라 다이얼로그에 활용되는 다른 레이아웃의 위젯이기 때문입니다.
                    editText_UserPhoneNumber = new EditText(MainActivity.this);
                    editText_UserPhoneNumber = alertView.findViewById(R.id.matchingUser_DialogView_editText_phoneNumber);

                    // 다이얼로그 빌더를 해당 액티비티에 매칭시킵니다.
                    final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    // 화면을 설정해줍니다.
                    builder.setView(alertView);

                    // 외부 화면 클릭 금지
                    builder.setCancelable(false);

                    // 연결을 누를 경우 작업입니다.
                    builder.setPositiveButton("연결", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // 현재 해당 메소드는 비동기 메소드(데이터 불러오기) 내부에서 실행됩니다.
                            // db 값 불러와야 됩니다.
                            for (UserModel userModel : userModels) {
                                // 굳이 null 값을 초기값으로 설정할 필요는 없어 보이는데.
                                String myPhoneNumber = null;

                                // 내 휴대폰 번호 불러오기
                                // db내 uid와 내 uid가 일치할 경우
                                if (userModel.uid.equals(myUid)) {
                                    myPhoneNumber = userModel.userPhoneNumber;
                                }

                                // 내 휴대폰 번호를 입력한 경우
                                if (editText_UserPhoneNumber.getText().toString().equals(myPhoneNumber)) {
                                    Toast.makeText(MainActivity.this, "본인의 휴대폰 번호를 입력할 수 없습니다.", Toast.LENGTH_SHORT).show();

                                    // 임시방편으로 종료 후 계속 다시 띄웁니다.
                                    // 그렇지 않으면 현재 자동 로그인 기능이 구축되어 있어 유저가 로그아웃 후 재 로그인해야 하는 문제가 있습니다.
                                    finish();
                                    startActivity(new Intent(MainActivity.this, MainActivity.class));

                                    return;
                                }

                                // 휴대폰 번호가 일치할 경우
                                // 즉 db내 휴대폰 번호가 내가 입력한 번호가 아닌 경우 + 내 휴대폰 번호가 아닌 경우
                                if (editText_UserPhoneNumber.getText().toString().trim().equals(userModel.userPhoneNumber)) {
                                    try {
                                        // 상대방 uid 입니다.
                                        counterpartUid = userModel.uid;
                                        System.out.println("확인. 휴대폰 번호가 일치합니다.");

                                        // 다음 로그인 시 다이얼로그 창이 뜨지 않게 수정합니다.
                                        // caseNumber를 1로, 상대방 uid를 입력해줍니다.
                                        // Firebase의 UpdateChildren은 Map을 활용해 입력 가능합니다.
                                        Map<String, Object> updateCaseNumber = new HashMap<>();
                                        updateCaseNumber.put("caseNumber", 1);
                                        updateCaseNumber.put("counterPartUid", counterpartUid);

                                        // DB 값 수정
                                        // 데이터를 업데이트 합니다.
                                        FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                .updateChildren(updateCaseNumber);

                                        // 상대방이 매칭되었는지 확인하는 프레그먼트로 이동합니다.
                                        Fragment toMapFragment = new CheckRoomMapFragment();
                                        getFragmentManager().beginTransaction().replace(R.id.mainactivity_fragment, toMapFragment)
                                                .commit();

                                        // Toast.makeText(CheckRoomMapFragment.this, "상대방도 나의 휴대폰 번호를 입력하면\n위치가 보여집니다 :)", Toast.LENGTH_SHORT).show();
                                    } catch (NullPointerException e) {
                                        Toast.makeText(MainActivity.this, "상대방 uid Null 에러. 관리자에게 문의바랍니다.", Toast.LENGTH_SHORT).show();
                                        finish();
                                    }

                                    // 찾았으니 반복문 종료
                                    // 조금이라도 시간을 아낍시다.
                                    break;
                                }
                            }

                            // 상대방 계정을 불러오지 못했을 경우
                            // 반복문 돌면서 상대방 계정을 찾지 못한. 즉, db에 가입된 유저가 아닙니다.
                            if (counterpartUid == null) {
                                Toast.makeText(MainActivity.this, "상대방의 계정이 없습니다.", Toast.LENGTH_SHORT).show();

                                // 임시방편으로 계속 다시 띄웁니다.
                                // 마찬가지로 자동 로그인 기능으로 인해 강제로 다시 띄우지 않으면 유저가 로그아웃을 한 뒤 재접속해야 합니다.
                                startActivity(new Intent(MainActivity.this, MainActivity.class));
                                finish();
                            }
                        }
                        // 마찬가지로 취소가 아닌 로그아웃을 실행합니다.
                    }).setNegativeButton("로그아웃", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // dialog.cancel();

                            Toast.makeText(MainActivity.this, "로그아웃됩니다. 감사합니다 :)", Toast.LENGTH_SHORT).show();

                            // 로그아웃 및 로그인 화면 이동
                            FirebaseAuth.getInstance().signOut();
                            startActivity(new Intent(MainActivity.this, LoginActivity.class));
                            finish();
                        }
                    });

                    // 실제로 띄웁니다.
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        // 툴바 내 이미지 버튼 누를 시 메뉴 생성
        imageButtonMenu = findViewById(R.id.main_toolbar_imagebutton);
        imageButtonMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 메뉴가 띄워질 view를 해당 위젯으로 설정해줍니다.
                View button = findViewById(R.id.main_toolbar_imagebutton);
                // 팝업처럼 생성되는 메뉴입니다. 해당 클래스의 해당 위젯에 팝업 메뉴가 생성됩니다.
                PopupMenu menu = new PopupMenu(MainActivity.this, button);

                // 메뉴를 호출(매칭)합니다.
                menu.getMenuInflater().inflate(R.menu.settings, menu.getMenu());
                menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        // 로그아웃 버튼을 누를 경우
                        if (item.getItemId() == R.id.action_sign_out) {
                            FirebaseAuth.getInstance().signOut();
                            Toast.makeText(MainActivity.this, "로그아웃 됩니다.", Toast.LENGTH_SHORT).show();
                            finish();
                            startActivity(new Intent(MainActivity.this, LoginActivity.class));
                        }
                        return true;
                    }
                });
                // 메뉴를 띄웁니다.
                menu.show();
            }
        });

        // 상태창 제거
        // getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }
}
