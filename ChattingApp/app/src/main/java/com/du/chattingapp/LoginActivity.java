package com.du.chattingapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.du.chattingapp.Models.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends AppCompatActivity {
    // 위젯 호출
    public static EditText idTextView;
    EditText passwordTextView;
    Button loginButton;
    Button signUpButton;

    // test
    String mainUid;

    // 로그인 관리 변수로 실제 로그인 담당
    FirebaseAuth firebaseAuth;

    // 로그인 확인 메소드
    FirebaseAuth.AuthStateListener authStateListener;

    // Firebase 데이터베이스 호출
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = database.getReference();

    // FirstActivity로 상대방 uid와 룸 uid를 넘겨줍니다.
    String destiUid;
    String destiRoomUid;

    // 원격으로 버튼 및 배경화면 색 지정할 때 활용할 수 있습니다.
    // public FirebaseRemoteConfig mFirebaseRemoteConfig;

    // 없는 계정인지, 있는 계정인지 확인합니다.
    int criteriaNumber;

    List<UserModel> testUsers = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // SplashActivity에서 넘어온 uid 값을 입력합니다.
        if (getIntent().getStringExtra("mainUid") != null) {
            mainUid = getIntent().getStringExtra("mainUid");
        }

        // caseNumber는 접속 루트
        if (getIntent().getStringExtra("mainUid") == null
                // ||이 맞는 표현인데? -> 둘 중에 하나라도 없으면 종료시켜야지.
                && getIntent().getStringExtra("caseNumber") == null) {
            // 마찬가지로 signOut이 아니라 finish()가 맞지.
            // 계정이 없는데 어케 로그아웃을 함
            FirebaseAuth.getInstance().signOut();
        }

        System.out.println("LoginActivity.mainUid 확인: " + mainUid);

        // 상대방 uid
        destiUid = getIntent().getStringExtra("destinationUid");

        // 룸 uid 불러 옴. 단체 채팅방에 활용됨
        destiRoomUid = getIntent().getStringExtra("destinationRoomUid");

        // 버튼 및 텍스트 뷰 불러오기
        loginButton = (Button) findViewById(R.id.loginactivity_button_login);
        signUpButton = (Button) findViewById(R.id.loginactivity_button_signup);
        idTextView = (EditText) findViewById(R.id.loginactivity_edittext_email);
        passwordTextView = (EditText) findViewById(R.id.loginactivity_edittext_password);

        /*----------------------------------------------------------------------------------*/
        // 로그아웃 메소드를 잠깐 불러옵시다.
        // firebaseAuth.signOut();

        // 상태 창 색 변경
        // getWindow().setStatusBarColor(Color.parseColor(splash_background));

        // 원격 기능 활용 시
        /*
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        String splash_background = mFirebaseRemoteConfig.getString("splash_background");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.parseColor(splash_background));
        }
        loginButton.setBackgroundColor(Color.parseColor(splash_background));
        signUpButton.setBackgroundColor(Color.parseColor(splash_background));
        */

        // 상태 창 제거
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // 로그인 버튼 활성화
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 아이디 칸이 공백이고, 패스워드 칸이 공백일 경우
                // "계정 입력이 필요합니다." 출력
                if (idTextView.getText().toString().trim().equals("")
                        && passwordTextView.getText().toString().trim().equals("")) {
                    Toast.makeText(LoginActivity.this, "계정 입력이 필요합니다.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // 패스워드를 입력하지 않았을 경우
                if (!idTextView.getText().toString().trim().equals("")
                        && passwordTextView.getText().toString().trim().equals("")) {
                    Toast.makeText(LoginActivity.this, "비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // 아이디를 입력하지 않았을 경우
                if (idTextView.getText().toString().trim().equals("") && !passwordTextView.getText().toString().trim().equals("")) {
                    Toast.makeText(LoginActivity.this, "아이디를 입력해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // 실제로 로그인을 진행합니다. -> 로그인 된 "여부"만 불러옵니다.
                // 실제로 인텐트를 넘기는 건 리스너가 합니다.
                else {
                    loginEvent();
                }
            }
        });

        // Firebase 계정 변수입니다.
        firebaseAuth = FirebaseAuth.getInstance();

        // 로그인 인터페이스 리스너(아이디와 비밀번호가 일치할 시 다음 화면으로 넘어갑니다)
        // 실제로 로그인을 담당하는 부분입니다.
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                // user 변수는 로그인된 현재 유저를 불러옵니다.
                final FirebaseUser user = firebaseAuth.getCurrentUser();

                // db 값에서 유저 데이터를 불러옵니다.
                databaseReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        testUsers.clear();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            testUsers.add(snapshot.getValue(UserModel.class));
                        }

                        // 1:1 대화로 넘기기
                        if (getIntent().getStringExtra("caseNumber") != null &&
                                user != null && getIntent().getStringExtra("caseNumber").equals("0")) {
                            System.out.println("LoginActivity 로그인 경로 확인: 1:1 채팅");

                            // 로그인
                            Toast.makeText(LoginActivity.this, "로그인에 성공하였습니다.", Toast.LENGTH_SHORT).show();
                            Intent firstActivityIntent = new Intent(LoginActivity.this, FirstActivity.class);
                            // 상대방 uid 및 경로 여부를 인텐트에 넘깁니다.
                            firstActivityIntent.putExtra("destinationUid", destiUid);
                            firstActivityIntent.putExtra("caseNumber", "0");
                            startActivity(firstActivityIntent);
                            finish();
                        }

                        // 1:多 대화로 넘기기
                        else if (getIntent().getStringExtra("caseNumber") != null &&
                                user != null && getIntent().getStringExtra("caseNumber").equals("1")) {
                            System.out.println("LoginActivity 로그인 경로 확인: 1:多 채팅");

                            // 로그인
                            Toast.makeText(LoginActivity.this, "로그인에 성공하였습니다.", Toast.LENGTH_SHORT).show();
                            Intent firstActivityIntent = new Intent(LoginActivity.this, FirstActivity.class);
                            // 채팅방 룸 uid(단체 채팅방) 및 경로 여부를 인텐트에 넘깁니다.
                            firstActivityIntent.putExtra("destinationRoomUid", destiRoomUid);
                            firstActivityIntent.putExtra("caseNumber", "1");
                            startActivity(firstActivityIntent);
                            finish();
                        }

                        // 게시판으로 넘기기
                        else if (getIntent().getStringExtra("caseNumber") != null &&
                                user != null && getIntent().getStringExtra("caseNumber").equals("2")) {
                            System.out.println("LoginActivity 로그인 경로 확인: 게시판 이동");

                            // 로그인
                            Toast.makeText(LoginActivity.this, "로그인에 성공하였습니다.", Toast.LENGTH_SHORT).show();
                            Intent firstActivityIntent = new Intent(LoginActivity.this, FirstActivity.class);
                            // 로그인 경로만 넘김
                            firstActivityIntent.putExtra("caseNumber", "2");
                            startActivity(firstActivityIntent);
                            finish();
                        }

                        // 일반 접속
                        else if (user != null) {
                            System.out.println("LoginActivity 로그인 경로 확인: 일반 접속");

                            // 지금 유저의 uid 값을 불러옵니다.
                            mainUid = user.getUid();

                            // 왜 서버에서 계정 존재 여부를 확인하는 거지?
                            for (UserModel userModel : testUsers) {
                                System.out.println("LoginActivity criteriaNumber 확인 1: " + criteriaNumber);
                                // System.out.println("LoginActivity.mainUid 반복문 확인: " + mainUid);
                                // System.out.println("LoginActivity.userModel 반복문 확인: " + userModel.uid);

                                // 서버 내 데이터와 접속 계정의 uid가 일치할 경우
                                // 이 또한, 계정 삭제 시 발생하는 NullPointerException 에러를 방지하기 위함인 듯?
                                if (userModel.uid.equals(mainUid)) {
                                    criteriaNumber = 1;
                                    // System.out.println("criteriaNumber 확인 2: " + criteriaNumber);
                                }
                            }

                            // 존재하는 계정인 경우
                            if (criteriaNumber == 1) {
                                Toast.makeText(LoginActivity.this, "로그인에 성공하였습니다.", Toast.LENGTH_SHORT).show();

                                // 인텐트를 실질적으로 넘겨줍니다.
                                Intent firstActivityIntent = new Intent(LoginActivity.this, FirstActivity.class);
                                startActivity(firstActivityIntent);
                                finish();

                                // 넘겨줬으니 기준값은 다시 0으로
                                criteriaNumber = 0;
                                System.out.println("LoginActivity criteriaNumber 확인 4: " + criteriaNumber);
                            }

                            else if (criteriaNumber == 0) {
                                Toast.makeText(LoginActivity.this, "존재하지 않는 계정입니다.\n관리자에게 문의바랍니다.", Toast.LENGTH_SHORT).show();
                                // 마찬가지로 로그아웃이 아니라 인텐트를 넘겨야 겠는데?
                                FirebaseAuth.getInstance().signOut();
                            }
                        } else {
                            //
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        };

        // 중복 계정을 막기 위해 새로운 데이터베이스 테이블을 만들 필요가 있습니다.
        // 회원가입 액티비티로 이동됩니다.
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
            }
        });
    }

    /*
    int getCriteriaNumber(List<UserModel> userModels) {
        int testcriteriaNumber = 0;

        String testMainUid;
        testMainUid  = getIntent().getStringExtra("mainUid");

        System.out.println("testMainUid 확인: " + testMainUid);
        System.out.println("getTestUsers 확인: " + userModels);

        for (UserModel userModel : userModels) {
            System.out.println("userModel Uid 확인: " + userModel.uid);
            System.out.println("criteriaNumber 확인 1: " + criteriaNumber);
            if (userModel.uid.equals(testMainUid)) {
                testcriteriaNumber = 1;
                System.out.println("criteriaNumber 확인 2: " + criteriaNumber);
            }
        }

        if (testcriteriaNumber == 1) {
            return 1;
        } else {
            return 0;
        }
    }
    */

    // 아이디, 비밀번호가 일치하는지 '확인'하는 메소드입니다.
    void loginEvent() {
        firebaseAuth.signInWithEmailAndPassword(idTextView.getText().toString().trim(),
                passwordTextView.getText().toString().trim())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            // 로그인이 실패했을 경우 메시지를 '영어'로 출력합니다.
                            Toast.makeText(LoginActivity.this, "이메일 또는 비밀번호를 확인해주세요.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    // 액티비티가 띄워질 경우 자동으로 실행하는 메소드입니다.
    // 액티비티 생명주기 참조
    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    // 타 액티비티 전환 시 동작입니다.
    // 없으면 onStart()에 걸려서 중복 동작됨
    @Override
    protected void onPause() {
        super.onPause();
        if (firebaseAuth != null) {
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
    }
}
