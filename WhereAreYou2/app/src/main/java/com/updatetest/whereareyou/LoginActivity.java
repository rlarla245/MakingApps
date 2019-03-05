package com.updatetest.whereareyou;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.FirebaseDatabase;
import com.updatetest.whereareyou.Models.UserModel;

public class LoginActivity extends AppCompatActivity {
    // 각 위젯 변수들을 불러옵니다.
    EditText editTextEmail;
    EditText editTextPassword;
    Button buttonLogin;
    TextView textViewToSignUp;

    // 다이얼로그 변수들을 불러옵니다.
    EditText dialog_editTextEmail;
    EditText dialog_editTextPassword;
    EditText dialog_editTextPhoneNumber;

    // auth 호출합니다.
    FirebaseAuth firebaseAuth;

    // 로그인 listener 호출합니다.
    FirebaseAuth.AuthStateListener authStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // EditText 변수들을 호출합니다.
        editTextEmail = findViewById(R.id.loginactivity_edittext_email);
        editTextPassword = findViewById(R.id.loginactivity_edittext_password);

        // Button 변수를 호출합니다.
        buttonLogin = findViewById(R.id.loginactivity_button_login);

        // SignUpActivity로 넘어가는 텍스트 뷰를 호출합니다.
        textViewToSignUp = findViewById(R.id.loginactivity_textview_to_signupactivity);

        // 계정 인스턴스 호출합니다.
        firebaseAuth = FirebaseAuth.getInstance();

        // 로그인 버튼 활성화
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 아이디 칸이 공백이고, 패스워드 칸이 공백일 경우 return 합니다.
                if (editTextEmail.getText().toString().trim().equals("")
                        && editTextPassword.getText().toString().trim().equals("")) {
                    Toast.makeText(LoginActivity.this, "계정 입력이 필요합니다.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // 패스워드를 입력하지 않았을 경우
                if (!editTextEmail.getText().toString().trim().equals("") && editTextPassword.getText().toString().trim().equals("")) {
                    Toast.makeText(LoginActivity.this, "비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // 아이디를 입력하지 않았을 경우
                if (editTextEmail.getText().toString().trim().equals("") && !editTextPassword.getText().toString().trim().equals("")) {
                    Toast.makeText(LoginActivity.this, "아이디를 입력해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // 실제로 로그인을 진행합니다. -> "여부"만 불러옵니다.
                else {
                    loginEvent();
                }
            }
        });

        textViewToSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 회원가입을 위해 dialog창을 띄웁니다.
                View alertView = View.inflate(LoginActivity.this, R.layout.signup_dialog_view, null);

                // dialog 변수들을 불러옵니다.
                dialog_editTextEmail = new EditText(LoginActivity.this);
                dialog_editTextPassword = new EditText(LoginActivity.this);
                dialog_editTextPhoneNumber = new EditText(LoginActivity.this);

                dialog_editTextEmail = alertView.findViewById(R.id.dialog_edittext_email);
                dialog_editTextPassword = alertView.findViewById(R.id.dialog_edittext_password);
                dialog_editTextPhoneNumber = alertView.findViewById(R.id.dialog_edittext_phonenumber);

                // 생성합니다.
                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                builder.setView(alertView);

                builder.setPositiveButton("회원가입", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // 실제로 회원가입을 진행하는 부분입니다.
                                // 각각의 입력창 중 하나라도 공백일 경우 해당 메시지를 출력합니다.
                                if (dialog_editTextEmail.getText().toString().trim().equals("")
                                        || dialog_editTextPhoneNumber.getText().toString().trim().equals("")
                                        || dialog_editTextPassword.getText().toString().trim().equals("")) {
                                    Toast.makeText(LoginActivity.this, "입력을 완료해주세요.", Toast.LENGTH_SHORT).show();

                                    return;
                                }

                                // 패스워드의 길이가 6자리가 되지 않을 경우
                                if (dialog_editTextPassword.getText().toString().length() < 6) {
                                    Toast.makeText(LoginActivity.this, "비밀번호는 6자리 이상입니다.", Toast.LENGTH_SHORT).show();

                                    return;
                                } else {
                                    createUsers();
                                    dialog.cancel();
                                }
                            }
                        }

                        // 취소합니다.
                ).setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

        // 로그인 인터페이스 리스너(아이디와 비밀번호가 일치할 시 다음 화면으로 넘어갑니다)
        // 실제로 로그인을 담당하는 부분입니다.
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                // user 변수는 서버에 있는 유저를 받아옵니다.
                final FirebaseUser user = firebaseAuth.getCurrentUser();

                // user가 null이 아닐 경우 로그인을 진행합니다.
                if (user != null) {
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                } else {
                    //
                }
            }
        };
    }

    // 로그인 여부를 불러오는 메소드입니다.
    void loginEvent() {
        firebaseAuth.signInWithEmailAndPassword(editTextEmail.getText().toString().trim(), editTextPassword.getText().toString().trim())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            // 로그인 실패한 부분
                            Toast.makeText(LoginActivity.this, "이메일 또는 비밀번호를 확인해주세요.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    void createUsers() {
        // 실제로 계정을 생성하는 메소드입니다.
        FirebaseAuth.getInstance()
                .createUserWithEmailAndPassword(dialog_editTextEmail.getText().toString().trim(), dialog_editTextPassword.getText().toString().trim())
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // 로그인 실패
                        if (!task.isSuccessful()) {
                            try {
                                // 실패할 경우 예외 처리를 실시합니다... 라는 뜻으로 보입니다?
                                throw task.getException();

                            } catch (FirebaseAuthUserCollisionException e) {
                                Toast.makeText(LoginActivity.this, "이미 존재하는 이메일입니다.", Toast.LENGTH_SHORT).show();

                            } catch (Exception e2) {
                                Toast.makeText(LoginActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(LoginActivity.this, "데이터베이스 입력 중 입니다.", Toast.LENGTH_SHORT).show();

                            // 회원가입한 유저의 uid를 바로 불러옵니다.
                            final String uid = task.getResult().getUser().getUid();

                            // 회원가입 시 자신의 아이디를 담기 위한 코드입니다.
                            // 채팅방 진입 시 상대방 이름 불러올 때 활용됩니다.
                            UserProfileChangeRequest userProfileChangeRequest
                                    = new UserProfileChangeRequest.Builder().setDisplayName(dialog_editTextEmail.getText().toString()).build();
                            task.getResult().getUser().updateProfile(userProfileChangeRequest);

                            UserModel userModel = new UserModel();
                            userModel.userEmail = dialog_editTextEmail.getText().toString().trim();
                            userModel.userPhoneNumber = dialog_editTextPhoneNumber.getText().toString().trim();
                            userModel.uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                            // 매칭된 유저가 있는지 확인하는 int 변수입니다.
                            userModel.caseNumber = 0;

                            FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(userModel)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Toast.makeText(LoginActivity.this, "회원가입이 완료되었습니다!", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    }
                });
    }

    // 앱 화면이 다시 켜질 때
    @Override
    protected void onResume() {
        super.onResume();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    // 앱 종료 혹은 가려질 때
    @Override
    protected void onPause() {
        super.onPause();
        firebaseAuth.removeAuthStateListener(authStateListener);
    }
}
