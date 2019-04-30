package com.updatetest.whereareyou;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.updatetest.whereareyou.Models.UserModel;

import java.util.ArrayList;
import java.util.List;

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

    // 로그인을 관장하는 인스턴스 변수 auth를 호출합니다.
    FirebaseAuth firebaseAuth;

    // 로그인 여부를 확인하는 listener를 호출합니다.
    FirebaseAuth.AuthStateListener authStateListener;

    // 겹치는 휴대폰 번호가 없도록 조정합니다.
    // equal 쿼리를 활용하면 굳이 리스트를 생성해야 할 필요가 있나?
    List<UserModel> userModelToCheckPhoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // 휴대폰 번호 중복 리스트 호출
        // onCreate() 이후에 리스트를 새로 설정하게 됩니다.
        userModelToCheckPhoneNumber = new ArrayList<>();

        // EditText 변수들을 호출합니다.
        editTextEmail = findViewById(R.id.loginactivity_edittext_email);
        editTextPassword = findViewById(R.id.loginactivity_edittext_password);

        // Button 변수를 호출합니다.
        buttonLogin = findViewById(R.id.loginactivity_button_login);

        // SignUpActivity로 넘어가는 텍스트 뷰를 호출합니다.
        textViewToSignUp = findViewById(R.id.loginactivity_textview_to_signupactivity);

        // 계정 인스턴스 호출합니다.
        firebaseAuth = FirebaseAuth.getInstance();

        // 회원가입 텍스트입니다.
        textViewToSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 회원가입 이전에 권한을 받아옵시다.
                // 로그인 시 권한을 불러오도록 변경해야 합니다.
                // 다른 기기에서 로그인 할 수 있는 경우를 생각하지 못했음.
                ActivityCompat.requestPermissions
                        (LoginActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 1);

                // 회원가입을 위해 dialog창을 띄웁니다.
                // dialog 창을 통해 입력한 값들이 서버로 넘어갑니다.
                // 다이얼로그 창에 띄울 레이아웃을 설정해줍니다.
                View alertView = View.inflate(LoginActivity.this, R.layout.signup_dialog_view, null);

                // dialog 변수들을 불러옵니다.
                // 컨텍스트 설정을 해줍니다. 어떤 클래스에서 해당 위젯이 작동하는지 설정해줍니다.
                dialog_editTextEmail = new EditText(LoginActivity.this);
                dialog_editTextPassword = new EditText(LoginActivity.this);
                dialog_editTextPhoneNumber = new EditText(LoginActivity.this);

                // 설정한 다이얼로그 레이아웃 뷰에 있는 위젯들을 실제로 연결시켜줍니다.
                dialog_editTextEmail = alertView.findViewById(R.id.dialog_edittext_email);
                dialog_editTextPassword = alertView.findViewById(R.id.dialog_edittext_password);
                dialog_editTextPhoneNumber = alertView.findViewById(R.id.dialog_edittext_phonenumber);

                // 다이얼로그 빌더를 생성합니다. 빌더 값을 입력해줍니다.
                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                // 위에서 설정한 뷰를 매칭시킵니다.
                builder.setView(alertView);

                builder.setPositiveButton("회원가입", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // 권한 다 받았어?
                                if (ContextCompat.checkSelfPermission
                                        // GPS 및 네트워크를 활용한 위치 추적 권한을 유저로부터 받아옵니다.
                                        (LoginActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION)
                                        == PackageManager.PERMISSION_GRANTED
                                        && ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                                        == PackageManager.PERMISSION_GRANTED) {

                                    // 실제로 회원가입을 진행하는 부분입니다.
                                    // 각각의 입력창 중 하나라도 공백일 경우 해당 메시지를 출력합니다.
                                    if (dialog_editTextEmail.getText().toString().trim().equals("")
                                            || dialog_editTextPhoneNumber.getText().toString().trim().equals("")
                                            || dialog_editTextPassword.getText().toString().trim().equals("")) {
                                        Toast.makeText(LoginActivity.this, "입력을 완료해주세요.", Toast.LENGTH_SHORT).show();

                                        return;
                                    }

                                    // 패스워드의 길이가 6자리가 되지 않을 경우
                                    if (dialog_editTextPassword.getText().toString().trim().length() < 6) {
                                        Toast.makeText(LoginActivity.this, "비밀번호는 6자리 이상입니다.", Toast.LENGTH_SHORT).show();

                                        return;
                                    } else {
                                        // 모든 예외 처리를 했다면 회원가입을 진행합니다.
                                        createUsers();
                                        dialog.cancel();
                                    }
                                }
                                // 권한을 모두 받아오지 못한 경우
                                else {
                                    Toast.makeText(LoginActivity.this, "권한 동의가 필요합니다.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                ).setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 취소합니다.
                        dialog.cancel();
                    }
                });

                // 빌더에 맞는 다이얼로그를 생성 및 보여줍니다.
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

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
                // 즉, 로그인이 되었다면 로그인이 되었다는 값과 정보들만 가지고 있지, 다음 화면으로 넘기지 않습니다.
                // 해당 기능은 리스너가 담당합니다.
                else {
                    loginEvent();
                }
            }
        });

        // 로그인 인터페이스 리스너(아이디와 비밀번호가 일치할 시 다음 화면으로 넘어갑니다)
        // 실제로 로그인을 담당하는 부분입니다.
        // 인간 기준에서는 '귀' 역할을 담당합니다.
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                // user 변수는 로그인된 현재 유저(기존 메소드를 통해 로그인된 계정)를 받아옵니다.
                final FirebaseUser user = firebaseAuth.getCurrentUser();

                // user가 null이 아닐 경우 로그인을 진행합니다.
                // 즉, 로그인 된 계정이 없을 경우 로그인을 진행하지 않습니다.
                if (user != null) {
                    // 다음 화면으로 넘긴 뒤 로그인 액티비티를 종료합니다.
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                } else {
                    //
                }
            }
        };
    }

    // 로그인을 진행합니다.
    // 위에서 언급했 듯, 로그인만 진행하여 그 값을 가지고 있을 뿐이지 화면을 직접적으로 넘기지는 않습니다.
    void loginEvent() {
        // 파베 자체 로그인 메소드입니다. 따로 구현을 할 필요가 없습니다.
        // 입력한 이메일과 비밀번호를 파라미터로 넘겨줘 파베에 입력된 auth와 비교합니다.
        firebaseAuth.signInWithEmailAndPassword(
                editTextEmail.getText().toString().trim(),
                editTextPassword.getText().toString().trim())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            // 로그인 실패한 부분
                            // 입력한 값이 서버 데이터와 일치하지 않습니다.
                            Toast.makeText(LoginActivity.this, "이메일 또는 비밀번호를 확인해주세요.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    void createUsers() {
        // 계정 생성 시 활용하는 메소드입니다.
        // 휴대폰 번호 중복을 막기 위한 DB 작업
        // Single로 메소드가 호출될 때 한 번만 데이터를 불러옵니다.
        FirebaseDatabase.getInstance().getReference().child("users")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // 앱이 꺼지지 않은 상태에서 메소드가 다시 호출될 경우
                        // 데이터가 중복으로 쌓이게 되므로
                        // 메소드를 호출할 때마다 리스트를 비워줍니다.
                        userModelToCheckPhoneNumber.clear();

                        // 반복문 호출
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            try {
                                // 유저 모델 호출
                                UserModel userModelToCheckPhoneNumber = snapshot.getValue(UserModel.class);

                                // 리스트가 비어있지 않고, 입력한 번호 값이 일치할 경우
                                if (userModelToCheckPhoneNumber != null && userModelToCheckPhoneNumber.userPhoneNumber
                                        .equals(dialog_editTextPhoneNumber.getText().toString().trim())) {
                                    Toast.makeText(LoginActivity.this, "휴대폰 번호가 중복됩니다. 다시 입력해주세요 :)", Toast.LENGTH_SHORT).show();

                                    // 실질적으로 계정을 생성하지 않고 리턴됩니다.
                                    return;
                                }
                            }

                            // db에 저장된 데이터가 아예 없을 수도 있습니다.
                            catch (NullPointerException e) {
                                System.out.println("로그인 데이터가 없습니다.");
                                Toast.makeText(LoginActivity.this, "로그인 데이터가 없습니다.", Toast.LENGTH_SHORT).show();
                            }
                        }

                        // 모든 예외 처리를 마쳤으므로
                        // 실제로 계정을 생성합니다.
                        FirebaseAuth.getInstance()
                                // 입력한 이메일, 비밀번호 값으로 계정을 생성합니다.
                                .createUserWithEmailAndPassword(dialog_editTextEmail.getText().toString().trim(), dialog_editTextPassword.getText().toString().trim())
                                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        // 회원가입 실패
                                        // 어떻게 보면 3차례에 걸쳐서 회원가입 예외 처리를 실시합니다.
                                        // 1. 유저가 입력하는 정보들이 양식에 맞지 않을 때
                                        // 2. 유저가 입력한 휴대폰 번호가 중복될 경우
                                        // 3. 이메일 중복 및 기타 경우
                                        if (!task.isSuccessful()) {
                                            try {
                                                // 실패할 경우 예외 처리를 실시합니다.
                                                throw task.getException();

                                            } catch (FirebaseAuthUserCollisionException e) {
                                                Toast.makeText(LoginActivity.this, "이미 존재하는 이메일입니다.", Toast.LENGTH_SHORT).show();

                                            }
                                            // 생각하지 못한 예외가 발생할 경우 에러 메시지를 출력합니다.
                                            catch (Exception e2) {
                                                Toast.makeText(LoginActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                        // 모든 예외 처리를 거친 후 회원가입을 실시합니다.
                                        else {
                                            Toast.makeText(LoginActivity.this, "데이터베이스 입력 중 입니다.", Toast.LENGTH_SHORT).show();

                                            // 회원가입한 유저의 uid를 바로 불러옵니다.
                                            final String uid = task.getResult().getUser().getUid();

                                            // 회원가입 시 자신의 아이디를 담기 위한 코드입니다.
                                            // 채팅방 진입 시 상대방 이름 불러올 때 활용됩니다.
                                            // getDisplayName() 같은 메소드 활용 시
                                            UserProfileChangeRequest userProfileChangeRequest
                                                    // 내가 입력한 이메일 값을 설정합니다.
                                                    = new UserProfileChangeRequest.Builder().setDisplayName(dialog_editTextEmail.getText().toString()).build();

                                            // 해당 값을 입력합니다.
                                            task.getResult().getUser().updateProfile(userProfileChangeRequest);

                                            // 회원가입하는 유저의 정보를 생성하여 DB에 입력합니다.
                                            UserModel userModel = new UserModel();
                                            userModel.userEmail = dialog_editTextEmail.getText().toString().trim();
                                            userModel.userPhoneNumber = dialog_editTextPhoneNumber.getText().toString().trim();
                                            userModel.uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

                                            // 매칭된 유저가 있는지 확인하는 int 변수입니다.
                                            // 새로 회원가입되었으므로 0
                                            userModel.caseNumber = 0;

                                            // 유저 정보 DB 입력
                                            // users - 해당 계정의 uid에 데이터들을 담습니다.
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

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // 데이터베이스 에러
                    }
                });
    }

    // 앱 화면이 가려지거나 백그라운드로 넘어갔다가 다시 켜질 때
    // 계정 리스너에 해당 리스너를 담아줘 로그인 된 계정 정보를 넘겨줍니다.
    @Override
    protected void onResume() {
        super.onResume();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    // 앱 종료 혹은 가려질 때
    // Resume() 되면 중복 로그인이 되어 버림
    // 그래서 종료 및 가려질 때 리스너를 제거해줍니다.
    @Override
    protected void onPause() {
        super.onPause();
        firebaseAuth.removeAuthStateListener(authStateListener);
    }
}
