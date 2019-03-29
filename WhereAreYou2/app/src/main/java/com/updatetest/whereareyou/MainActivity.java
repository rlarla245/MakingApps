package com.updatetest.whereareyou;

import android.app.Fragment;
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
    // 메뉴를 띄울 이미지 버튼입니다.
    protected ImageButton imageButtonMenu;

    // 매칭된 유저가 존재하는지 확인하는 정수형 변수입니다.
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
        try {
            myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        } catch (NullPointerException e) {
            Toast.makeText(this, "uid 에러 발생. 관리자에게 문의바랍니다.", Toast.LENGTH_SHORT).show();
        }

        // DB에서 유저들 정보를 불러옵시다.
        FirebaseDatabase.getInstance().getReference().child("users")
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userModels.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // 리스트에 담습니다.
                    userModels.add(snapshot.getValue(UserModel.class));

                    try {
                        // 내 정보를 담기 위한 조건문입니다.
                        if (FirebaseAuth.getInstance().getCurrentUser().getUid().equals(snapshot.getValue(UserModel.class).uid)) {
                            myUserModel = snapshot.getValue(UserModel.class);
                            caseNumber = myUserModel.caseNumber;
                        }
                    } catch (NullPointerException e) {
                        Toast.makeText(MainActivity.this, "유저 정보가 없는 계정이 존재합니다.\n관리자에게 문의바랍니다.", Toast.LENGTH_SHORT).show();
                    }
                }

                // 테스트
                if (caseNumber == 1) {
                    Fragment toMapFragment = new CheckRoomMapFragment();

                    // 위치를 보여주는 프레그먼트로 넘깁니다.
                    getFragmentManager().beginTransaction().replace(R.id.mainactivity_fragment,toMapFragment)
                            .commit();
                }

                // caseNumber가 0일 경우 다이얼로그 창을 띄웁니다.
                if (caseNumber == 0) {
                    // 회원가입을 위해 dialog창을 띄웁니다.
                    View alertView = View.inflate(MainActivity.this, R.layout.matching_user_dialog_view, null);

                    // 위젯들 호출합니다.
                    editText_UserPhoneNumber = new EditText(MainActivity.this);
                    editText_UserPhoneNumber = alertView.findViewById(R.id.matchingUser_DialogView_editText_phoneNumber);

                    final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setView(alertView);
                    // 외부 화면 클릭 금지
                    builder.setCancelable(false);

                    builder.setPositiveButton("연결", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // db 값 불러와야 됩니다.
                            for (UserModel userModel : userModels) {
                                String myPhoneNumber = null;

                                // 내 휴대폰 번호 불러오기
                                if (userModel.uid.equals(myUid)) {
                                    myPhoneNumber = userModel.userPhoneNumber;
                                }

                                // 내 휴대폰 번호를 입력한 경우
                                if (editText_UserPhoneNumber.getText().toString().equals(myPhoneNumber)) {
                                    Toast.makeText(MainActivity.this, "본인의 휴대폰 번호를 입력할 수 없습니다.", Toast.LENGTH_SHORT).show();

                                    // 임시방편으로 계속 다시 띄웁니다.
                                    finish();
                                    startActivity(new Intent(MainActivity.this, MainActivity.class));

                                    return;
                                }

                                // 휴대폰 번호가 일치할 경우
                                if (editText_UserPhoneNumber.getText().toString().trim().equals(userModel.userPhoneNumber)) {
                                    try {
                                        // 상대방 uid 입니다.
                                        counterpartUid = userModel.uid;
                                        System.out.println("확인. 휴대폰 번호가 일치합니다.");

                                        // 다음 로그인 시 다이얼로그 창이 뜨지 않게 수정합니다.
                                        Map<String, Object> updateCaseNumber = new HashMap<>();
                                        updateCaseNumber.put("caseNumber", 1);
                                        updateCaseNumber.put("counterPartUid", counterpartUid);

                                        // DB 값 수정
                                        FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                .updateChildren(updateCaseNumber);

                                        Fragment toMapFragment = new CheckRoomMapFragment();

                                        // 위치를 보여주는 프레그먼트로 넘깁니다.
                                        getFragmentManager().beginTransaction().replace(R.id.mainactivity_fragment, toMapFragment)
                                                .commit();

                                        // Toast.makeText(CheckRoomMapFragment.this, "상대방도 나의 휴대폰 번호를 입력하면\n위치가 보여집니다 :)", Toast.LENGTH_SHORT).show();
                                    } catch (NullPointerException e) {
                                        Toast.makeText(MainActivity.this, "상대방 uid Null 에러. 관리자에게 문의바랍니다.", Toast.LENGTH_SHORT).show();
                                        finish();
                                    }

                                    // 찾았으니 반복문 종료
                                    break;
                                }
                            }

                            // 상대방 계정을 불러오지 못했을 경우
                            if (counterpartUid == null) {
                                Toast.makeText(MainActivity.this, "상대방의 계정이 없습니다.", Toast.LENGTH_SHORT).show();

                                // 임시방편으로 계속 다시 띄웁니다.
                                startActivity(new Intent(MainActivity.this, MainActivity.class));
                                finish();
                            }
                        }
                    }).setNegativeButton("로그아웃", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // dialog.cancel();

                            Toast.makeText(MainActivity.this, "로그아웃됩니다. 감사합니다 :)", Toast.LENGTH_SHORT).show();

                            // 로그인 화면 이동
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

        // 이미지 버튼 누를 시 메뉴 생성
        imageButtonMenu = findViewById(R.id.main_toolbar_imagebutton);
        imageButtonMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 메뉴가 띄워질 view를 설정해줍니다.
                View button = findViewById(R.id.main_toolbar_imagebutton);
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
                menu.show();
            }
        });

        // 상태창 제거
        // getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }
}
