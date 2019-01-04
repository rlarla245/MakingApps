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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    public static EditText idTextView;
    EditText passwordTextView;
    Button loginButton;
    Button signUpButton;

    // 로그인 관리 변수로 실제 로그인 담당
    FirebaseAuth firebaseAuth;

    // 로그인 확인 메소드
    FirebaseAuth.AuthStateListener authStateListener;

    // FirstActivity로 상대방 uid를 넘겨줍니다.
    String destiUid;
    String destiRoomUid;

    // 원격으로 버튼 및 배경화면 색 지정할 때 활용할 수 있습니다.
    // public FirebaseRemoteConfig mFirebaseRemoteConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // 상대방 uid
        destiUid = getIntent().getStringExtra("destinationUid");

        // 단체 채팅방
        destiRoomUid = getIntent().getStringExtra("destinationRoomUid");

        // 버튼 및 텍스트 뷰 불러오기
        loginButton = (Button) findViewById(R.id.loginactivity_button_login);
        signUpButton = (Button) findViewById(R.id.loginactivity_button_signup);
        idTextView = (EditText) findViewById(R.id.loginactivity_edittext_email);
        passwordTextView = (EditText) findViewById(R.id.loginactivity_edittext_password);

        // 파이어베이스 계정 불러오기
        firebaseAuth = FirebaseAuth.getInstance();

        /*----------------------------------------------------------------------------------*/
        // 로그아웃 메소드를 잠깐 불러옵시다.
        // firebaseAuth.signOut();

        /*
        // 버튼을 원격으로 색 변경할 시
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
                // 비회원 페이지로 이동됩니다.

                if (idTextView.getText().toString().trim().equals("")
                        && passwordTextView.getText().toString().trim().equals("")) {
                    Toast.makeText(LoginActivity.this, "입력이 필요합니다.", Toast.LENGTH_SHORT).show();
                    return;
                }


                // 패스워드를 입력하지 않았을 경우
                if (!idTextView.getText().toString().trim().equals("") && passwordTextView.getText().toString().trim().equals("")) {
                    Toast.makeText(LoginActivity.this, "비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // 아이디를 입력하지 않았을 경우
                if (idTextView.getText().toString().trim().equals("") && !passwordTextView.getText().toString().trim().equals("")) {
                    Toast.makeText(LoginActivity.this, "아이디를 입력해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // 실제로 로그인을 진행합니다. -> "여부"만 불러옵니다.
                else {
                    loginEvent();
                }
            }
        });

        // 로그인 인터페이스 리스너(아이디와 비밀번호가 일치할 시 다음 화면으로 넘어갑니다)
        // 실제로 로그인을 담당하는 부분입니다.
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                // user 변수는 서버에 있는 유저를 받아옵니다.
                FirebaseUser user = firebaseAuth.getCurrentUser();

                // 1:1 대화로 넘기기
                if (getIntent().getStringExtra("caseNumber") != null &&
                        user != null && getIntent().getStringExtra("caseNumber").equals("0")) {
                    Log.d("Login Act", "1:1 채팅");
                    // 로그인
                    Toast.makeText(LoginActivity.this, "로그인에 성공하였습니다.", Toast.LENGTH_SHORT).show();
                    Intent firstActivityIntent = new Intent(LoginActivity.this, FirstActivity.class);
                    firstActivityIntent.putExtra("destinationUid", destiUid);
                    firstActivityIntent.putExtra("caseNumber", "0");
                    startActivity(firstActivityIntent);
                    finish();
                }

                // 1:多 대화로 넘기기
                else if (getIntent().getStringExtra("caseNumber") != null &&
                        user != null && getIntent().getStringExtra("caseNumber").equals("1")) {
                    Log.d("Login Act", "1:多 채팅");
                    // 로그인
                    Toast.makeText(LoginActivity.this, "로그인에 성공하였습니다.", Toast.LENGTH_SHORT).show();
                    Intent firstActivityIntent = new Intent(LoginActivity.this, FirstActivity.class);
                    firstActivityIntent.putExtra("destinationRoomUid", destiRoomUid);
                    firstActivityIntent.putExtra("caseNumber", "1");
                    startActivity(firstActivityIntent);
                    finish();
                }

                // 게시판으로 넘기기
                else if (getIntent().getStringExtra("caseNumber") != null &&
                        user != null && getIntent().getStringExtra("caseNumber").equals("2")) {
                    Log.d("Login Act", "게시판 이동");
                    // 로그인
                    Toast.makeText(LoginActivity.this, "로그인에 성공하였습니다.", Toast.LENGTH_SHORT).show();
                    Intent firstActivityIntent = new Intent(LoginActivity.this, FirstActivity.class);
                    firstActivityIntent.putExtra("caseNumber", "2");
                    startActivity(firstActivityIntent);
                    finish();
                }

                else if (user != null) {
                    Log.d("Login Act", "일반 접속");
                    // 로그인
                    Toast.makeText(LoginActivity.this, "로그인에 성공하였습니다.", Toast.LENGTH_SHORT).show();
                    Intent firstActivityIntent = new Intent(LoginActivity.this, FirstActivity.class);
                    startActivity(firstActivityIntent);
                    finish();
                }

                // 현재 유저가 없을 경우
                else {
                    // 로그아웃된 상태
                }
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
    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    // 타 액티비티 전환 시 동작입니다.
    @Override
    protected void onPause() {
        super.onPause();
        if (firebaseAuth != null) {
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
    }
}
