package com.du.instagramprototypeproject;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.du.instagramprototypeproject.databinding.ActivityLoginBinding;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.Arrays;

public class LoginActivity extends AppCompatActivity
        implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {

    // Sign In - Google 왜 9001 이었더라
    private static final int RC_SIGN_IN = 9001;

    // Login Listener - 상태 변화에 따라 로그인을 실행하는 리스너입니다.
    FirebaseAuth.AuthStateListener authListener;

    // Data Binding
    private ActivityLoginBinding binding;
    private FirebaseAuth auth;
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 데이터 바인딩 위한 인스턴스 + 바인딩 레이아웃 지정
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);

        // Firebase 로그인 통합 관리하는 Object 만들기 - auth는 현재 '계정'으로 볼 수 있음
        auth = FirebaseAuth.getInstance();

        // 구글 로그인 옵션 설정(요청 토큰, 요청 권한 등)
        GoogleSignInOptions gso = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                // 토큰 및 이메일 정보 불러옴
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // 구글 로그인 API 만들기
        mGoogleApiClient =
                new GoogleApiClient.Builder(this)
                        .enableAutoManage(this, this)
                        // 상단의 구글 로그인 옵션에서 불러온 토큰, 이메일 값을 적용시킵니다.
                        .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                        .build();

        // 구글 로그인 버튼 가져오기
        binding.googleSignInButton.setOnClickListener(this);

        // Email 로그인
        binding.emailLoginButton.setOnClickListener(this);

        // Login Listener - 로그인에 필수적인 요소
        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                // 현재 유저에 대한 변수화를 실시합니다.
                FirebaseUser user = firebaseAuth.getCurrentUser();

                // 로그인된 경우 null 값이 아닙니다.
                if (user != null) {
                    Toast.makeText(LoginActivity.this, getString(R.string.signin_complete), Toast.LENGTH_SHORT).show();
                    // 로그인 되었으면 Intent를 통해 다음 액티비티로 넘어갑니다.
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                    // 다르게 말하면, 다음 액티비티를 불러오기 전까지는 프로그레스 바가 보이게 됩니다.
                    binding.progressBar.setVisibility(View.GONE);
                }
            }
        };
    }

    @Override
    /* 참고 - 액티비티 생명주기 */
    // 액티비티로 전환될 경우 자동으로 불러오게 되는 메소드입니다. 오버라이드를 통해 리스너를 불러옵니다.
    // 리스너를 불러옴에 따라 해당 액티비티로 이동할 때마다 로그인 여부를 확인하게 됩니다.
    protected void onStart() {
        super.onStart();
        auth.addAuthStateListener(authListener);
    }

    @Override
    // 해당 액티비티를 백그라운드로 넘길 때 해당 리스너를 제거합니다.
    // 다시 포그라운드로 넘어올 경우 onStart()가 재실행됩니다.
    protected void onStop() {
        super.onStop();
        if (authListener != null) {
            auth.removeAuthStateListener(authListener);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // 구글 드라이브와 구글+ 중 적어도 하나의 서비스가 연결이 되지 않을 경우.
        Toast.makeText(this, "연결되지 않았습니다. 재시작바랍니다.", Toast.LENGTH_SHORT).show();
        finish();
    }

    // 각 버튼에 맞게 onClick 메소드를 실행시키는 것이 귀찮으니 switch문을 통해 한 번에 처리합시다.
    // 여기서 인텐트를 통해 다음 액티비티로 넘기는 것이 아닙니다.
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            // 구글 로그인 버튼
            case R.id.google_sign_in_button:
                // 로그인 대기 중일 경우 프로그레스바를 호출합니다.
                binding.progressBar.setVisibility(View.VISIBLE);

                // 구글 로그인 api를 실행합니다. 구글 로그인 api는 gso를 불러오게 되겠죠.
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent, RC_SIGN_IN);
                break;

            // 이메일 로그인 버튼
            case R.id.email_login_button:
                // 공란 케이스에 대한 처리입니다. equal("")로 처리해도 됩니다.
                if (TextUtils.isEmpty(binding.emailEdittext.getText().toString()) || TextUtils.isEmpty(binding.passwordEdittext.getText().toString())) {
                    Toast.makeText(this, getString(R.string.signout_fail_null), Toast.LENGTH_SHORT).show();
                } else {
                    binding.progressBar.setVisibility(View.VISIBLE);
                    // 계정 생성 혹은 로그인 메소드를 실행합니다.
                    createAndLoginEmail();
                }
                // switch 문은 break 문이 생명입니다. 다만... 여기선 필요 없는디
                break;
        }
    }

    @Override
    // 다음 액티비티로 넘길 때 쓰는 메소드로 '특정' 조건에 따라 동작을 다르게 하거나 동작을 거부할 수 있습니다.
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 구글에서 승인된 정보를 가지고 오기 + 구글 로그인 인텐트에 맞는 조건과 동일할 경우 바디 부분을 실행합니다.
        if (requestCode == RC_SIGN_IN) {
            // 구글 로그인 api에 담긴 정보를 받아옵니다.
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            // 로그인 성공 시
            if (result.isSuccess()) {
                // 불러온 계정을 담아주고 파이어베이스와 연동시킵니다.
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                /* ??? */
               binding.progressBar.setVisibility(View.GONE);
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        // 토큰 값을 받아와 현재 계정에 연결시킵니다.
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        // 토큰, 이메일 등이 파라미터로 이동합니다.
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // 로그인 실패?
                        if (!task.isSuccessful()) {
                           binding.progressBar.setVisibility(View.GONE);
                        }
                    }
                });
    }

    /*
     * Email 로그인
     */

    // 이메일 회원가입 및 로그인 메소드
    private void createAndLoginEmail() {
        // 계정 생성 메소드
        auth.createUserWithEmailAndPassword(binding.emailEdittext.getText().toString(),
                binding.passwordEdittext.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this,
                                    getString(R.string.signup_complete), Toast.LENGTH_SHORT).show();
                        }

                        //회원가입 에러 - 1. 비밀번호가 6자리 이상 입력이 안됐을 경우
                        else if (binding.passwordEdittext.getText().toString().length() < 6) {
                            binding.progressBar.setVisibility(View.GONE);
                            Toast.makeText(LoginActivity.this,
                                    task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }

                        //회원가입 에러 - 2. 아이디가 있을 경우이며 에러를 발생시키지 않고 바로 로그인 코드로 넘어간다
                        else {
                            signinEmail();
                        }
                    }
                });
    }

    //로그인 메소드
    private void signinEmail() {
        auth.signInWithEmailAndPassword(binding.emailEdittext.getText().toString(),
                binding.passwordEdittext.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //로그인 에러 발생 - 1. 비밀번호가 틀릴 경우
                        if (!task.isSuccessful()) {
                            binding.progressBar.setVisibility(View.GONE);
                            // 영어로 뜹니다. 여러 에러가 뜨는 경우들을 봐야 정할 수 있을 듯 합니다.
                            Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
