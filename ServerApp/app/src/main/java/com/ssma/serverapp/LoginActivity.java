package com.ssma.serverapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
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
import com.ssma.serverapp.NonMembers.NonMemberFirstActivity;

public class LoginActivity extends AppCompatActivity {

    public static EditText idTextView;
    EditText passwordTextview;
    Button loginButton;
    Button signUpButton;

    // 로그인 관리 변수로 실제 로그인 담당
    FirebaseAuth firebaseAuth;

    // 로그인 확인 메소드
    FirebaseAuth.AuthStateListener authStateListener;

    // 원격으로 버튼 및 배경화면 색 지정할 때 활용할 수 있습니다.
    // public FirebaseRemoteConfig mFirebaseRemoteConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_login_activity);

        // 버튼 및 텍스트 뷰 불러오기
        loginButton = (Button)findViewById(R.id.loginactivity_button_login);
        signUpButton = (Button)findViewById(R.id.loginactivity_button_signup);
        idTextView = (EditText) findViewById(R.id.loginactivity_edittext_email);
        passwordTextview = (EditText) findViewById(R.id.loginactivity_edittext_password);

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
                if (idTextView.getText().toString().equals("") && passwordTextview.getText().toString().equals("")) {
                    Intent nonMemberIntent = new Intent(view.getContext(), NonMemberFirstActivity.class);
                    startActivity(nonMemberIntent);
                    Toast.makeText(LoginActivity.this, "비회원으로 접속합니다.\n서비스 전략경영학회에 오신 것을 환영합니다!", Toast.LENGTH_SHORT).show();
                }

                // 패스워드를 입력하지 않았을 경우
                if (!idTextView.getText().toString().equals("") && passwordTextview.getText().toString().equals("")) {
                    Toast.makeText(LoginActivity.this, "비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
                }

                // 아이디를 입력하지 않았을 경우
                if (idTextView.getText().toString().equals("") && !passwordTextview.getText().toString().equals("")) {
                    Toast.makeText(LoginActivity.this, "아이디를 입력해주세요.", Toast.LENGTH_SHORT).show();
                }

                // 실제로 로그인을 진행합니다.
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
                if (user != null) {
                    // 실질적으로 로그인을 실행하게됩니다.
                    Intent firstActivityIntent = new Intent(LoginActivity.this, FirstActivity.class);
                    startActivity(firstActivityIntent);
                    finish();
                }

                // 현재 유저가 없을 경우
                else {
                    // 로그아웃
                    Toast.makeText(LoginActivity.this, "로그아웃 됩니다.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        };

        /*-------------------------------------------------------------------------------------------------------*/
        // 중복 계정을 막기 위해 새로운 데이터베이스 테이블을 만들 필요가 있습니다.
        // 회원가입 액티비티로 이동됩니다.
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 회원가입 액티비티 이동
                startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
            }
        });
    }

    // 아이디, 비밀번호가 일치하는지 '확인'하는 메소드입니다.
    void loginEvent() {
        firebaseAuth.signInWithEmailAndPassword(idTextView.getText().toString(), passwordTextview.getText().toString())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            // 로그인이 실패했을 경우 메시지를 '영어'로 출력합니다./
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