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
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.ssma.serverapp.NonMembers.NonMemberFirstActivity;

public class LoginActivity extends AppCompatActivity {

    public static EditText idTextView;
    EditText passwordTextview;
    Button loginButton;
    Button signUpButton;
    public FirebaseRemoteConfig mFirebaseRemoteConfig;
    // 로그인 관리 변수로 실제 로그인 담당
    FirebaseAuth firebaseAuth;
    // 로그인 확인 메소드
    FirebaseAuth.AuthStateListener authStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_login_activity);

        // 버튼 및 텍스트 뷰 불러오기
        loginButton = (Button)findViewById(R.id.loginactivity_button_login);
        signUpButton = (Button)findViewById(R.id.loginactivity_button_signup);
        idTextView = (EditText) findViewById(R.id.loginactivity_edittext_email);
        passwordTextview = (EditText) findViewById(R.id.loginactivity_edittext_password);
        firebaseAuth = FirebaseAuth.getInstance();

        // 로그아웃 메소드를 잠깐 불러옵시다.
        firebaseAuth.signOut();

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
                if (idTextView.getText().toString().equals("") && passwordTextview.getText().toString().equals("")) {
                    Intent nonMemberIntent = new Intent(view.getContext(), NonMemberFirstActivity.class);
                    startActivity(nonMemberIntent);
                    Toast.makeText(LoginActivity.this, "비회원으로 접속합니다.\n서비스 전략경영학회에 오신 것을 환영합니다!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (idTextView.getText().toString().equals("ㅣ") && passwordTextview.getText().toString().equals("1")) {
                    Intent firstactivity_intent = new Intent(view.getContext(), FirstActivity.class);
                    firstactivity_intent.putExtra("id", idTextView.getText().toString());
                    startActivity(firstactivity_intent);

                    Toast.makeText(LoginActivity.this, "테스트 계정으로 접속합니다.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!idTextView.getText().toString().equals("") && passwordTextview.getText().toString().equals("")) {
                    Toast.makeText(LoginActivity.this, "비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (idTextView.getText().toString().equals("") && !passwordTextview.getText().toString().equals("")) {
                    Toast.makeText(LoginActivity.this, "아이디를 입력해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // 실제로 로그인을 진행합니다.
                else {
                    loginEvent();
                }
            }
        });

        // 로그인 인터페이스 리스너(아이디와 비밀번호가 일치할 시 다음 화면으로 넘어갑니다)
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                // user 변수는 서버에 있는 유저를 받아옵니다.
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // 로그인
                    Intent firstactivity_intent2 = new Intent(LoginActivity.this, FirstActivity.class);
                    startActivity(firstactivity_intent2);
                    finish();
                }
                else {
                    // 로그아웃
                }
            }
        };

        // 회원가입 버튼 활성화
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(LoginActivity.this, "중복된 계정 입력 시 앱이 종료됩니다.", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    // 로그인 여부를 파악 후 '실행'하는 메소드입니다.
    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        firebaseAuth.removeAuthStateListener(authStateListener);
    }


}
