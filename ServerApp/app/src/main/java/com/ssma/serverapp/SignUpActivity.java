package com.ssma.serverapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;
import com.ssma.serverapp.Model.UserEmailsModel;
import com.ssma.serverapp.Model.UserModel;

import java.util.ArrayList;

public class SignUpActivity extends AppCompatActivity {
    // 유저 모델들을 담는 리스트 생성
    public static  ArrayList<UserModel> userModels = new ArrayList<>();

    // 권한 코드
    private static final int PICK_FROM_ALBUM = 10;

    // 각각의 UI 변수들을 생성합니다.
    public EditText id;
    public EditText email;
    public EditText password;
    public EditText phonenumber;
    public Button signupButton;
    public ImageView profile;
    public Uri imageUri;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_signup_activity);

        // 변수 호출
        id = (EditText)findViewById(R.id.signup_edittext_id);
        email = (EditText)findViewById(R.id.signup_edittext_email);
        password = (EditText)findViewById(R.id.signup_edittext_password);
        phonenumber = (EditText)findViewById(R.id.signup_edittext_phonenumber);
        signupButton = (Button)findViewById(R.id.signupactivity_button_signup);
        profile = (ImageView)findViewById(R.id.signupactivity_imageview);

        // 사진 불러오기 - 권한은..?
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pickIntent = new Intent(Intent.ACTION_PICK);
                pickIntent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                startActivityForResult(pickIntent, PICK_FROM_ALBUM);
            }
        });

        /*
        // 버튼을 원격으로 색 변경할 시
        FirebaseRemoteConfig mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        String splash_background = mFirebaseRemoteConfig.getString(getString(R.string.rc_color));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.parseColor(splash_background));
        }
        signupButton.setBackgroundColor(Color.parseColor(splash_background));
        */

        // 상태 창 제거
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // 회원가입 버튼 활성화
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 중복 입력 방지를 위해 메소드 실행 중 동작을 강제로 멈춥니다.
                signupButton.setEnabled(false);

                FirebaseDatabase.getInstance().getReference().child("userEmails").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            UserEmailsModel userEmails = snapshot.getValue(UserEmailsModel.class);

                            if (email.getText().toString().equals(userEmails.userEmail)) {
                                Toast.makeText(SignUpActivity.this, "이미 가입된 이메일입니다.", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                // 각각의 입력창 중 하나라도 공백일 경우 해당 메시지를 출력합니다.
                if (id.getText().toString().equals("") || email.getText().toString().equals("")
                        || password.getText().toString().equals("") || phonenumber.getText().toString().equals("")) {
                    Toast.makeText(SignUpActivity.this, "입력을 완료해주세요.", Toast.LENGTH_SHORT).show();
                    // 회원가입에 실패했으니 다시 해당 버튼을 실행할 수 있게 해줘야 합니다.
                    signupButton.setEnabled(true);
                    return;
                }

                // 패스워드의 길이가 6자리가 되지 않을 경우
                if (password.getText().toString().length() < 6) {
                    Toast.makeText(SignUpActivity.this, "비밀번호는 6자리 이상입니다.", Toast.LENGTH_SHORT).show();
                    // 마찬가지로 재입력 가능해야 합니다.
                    signupButton.setEnabled(true);
                    return;
                }

                // 이미지를 입력하지 않았을 경우
                if (imageUri == null) {
                    Toast.makeText(SignUpActivity.this, "사진을 입력해주세요.", Toast.LENGTH_SHORT).show();
                    // 마찬가지
                    signupButton.setEnabled(true);
                    return;
                }

                else {
                    // 이메일, 패스워드로만 확인이 가능한 듯..?
                    FirebaseAuth.getInstance()
                            .createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                            // 회원가입 성공 시
                            .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    Toast.makeText(SignUpActivity.this, "데이터베이스 입력 중 입니다.", Toast.LENGTH_SHORT).show();

                                    /*----------------------------------------------------------------------------------------------*/
                                    // final String uid = task.getResult().getUser().getUid();
                                    final String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

                                    // 회원가입 시 자신의 아이디를 담기 위한 코드입니다.
                                    // UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest.Builder().setDisplayName(id.getText().toString()).build();
                                    // task.getResult().getUser().updateProfile(userProfileChangeRequest);

                                    FirebaseStorage.getInstance().getReference().child("userImages").child(uid).putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                            // 프로필 이미지 다운로드 url
                                            String imageUri = task.getResult().getDownloadUrl().toString();

                                            final UserModel userModel = new UserModel();
                                            userModel.userName = id.getText().toString();
                                            userModel.userPhoneNumber = phonenumber.getText().toString();
                                            userModel.profileImageUri = imageUri;
                                            userModel.uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

                                            // 데이터베이스에 입력해봅시다.
                                            // 데이터가 확실히 서버에 넘어갔을 때 진행합니다.
                                            // "users"의 데이터 테이블에 유저 각각의 uid에 맞게 데이터를 저장하게 됩니다.
                                            FirebaseDatabase.getInstance().getReference().child("users").child(uid).setValue(userModel)
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            Toast.makeText(SignUpActivity.this, "회원가입이 완료되었습니다!", Toast.LENGTH_SHORT).show();
                                                            finish();
                                                        }
                                                    }).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    UserEmailsModel userEmailsModel = new UserEmailsModel();
                                                    userEmailsModel.userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                                                    FirebaseDatabase.getInstance().getReference().child("userEmails").child(uid).setValue(userEmailsModel);
                                                }
                                            });
                                        }
                                    });
                                }
                            });
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_FROM_ALBUM && resultCode == RESULT_OK) {
            // 회원가입 시 사진을 선택하면 가운데 뷰를 바꿔줍니다.
            profile.setImageURI(data.getData());
            // 이미지 경로 원본 저장
            imageUri = data.getData();
        }
    }
}