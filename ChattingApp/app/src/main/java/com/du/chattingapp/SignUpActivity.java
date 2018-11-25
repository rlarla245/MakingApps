package com.du.chattingapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.du.chattingapp.Models.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {
    // 권한 코드
    private static final int PICK_FROM_ALBUM = 10;
    private static final int PICK_IMAGE_FROM_ALBUM = 0;

    // 각각의 UI 변수들을 생성합니다.
    public EditText idEdit;
    public EditText emailEdit;
    public EditText passwordEdit;
    public EditText phoneNumberEdit;
    public Button signupButton;
    public ImageView profileImage;
    public Uri profileImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // 변수 호출
        idEdit = (EditText)findViewById(R.id.signup_edittext_id);
        emailEdit = (EditText)findViewById(R.id.signup_edittext_email);
        passwordEdit = (EditText)findViewById(R.id.signup_edittext_password);
        phoneNumberEdit = (EditText)findViewById(R.id.signup_edittext_phonenumber);
        signupButton = (Button)findViewById(R.id.signupactivity_button_signup);
        profileImage = (ImageView)findViewById(R.id.signupactivity_imageview);

        // 권한 요청 하는 부분 - 외부 저장소를 읽을 수 있는 권한을 요청합니다.
        // 요청 코드를 통해 onActivityResult 메소드에서 처리합니다.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            ActivityCompat.requestPermissions
                    (SignUpActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }

        // 사진 불러오기 - 권한 승인 후 접근합니다.
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(SignUpActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED) {
                    Intent pickIntent = new Intent(Intent.ACTION_PICK);
                    pickIntent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                    startActivityForResult(pickIntent, PICK_IMAGE_FROM_ALBUM);
                }
                else {
                    Toast.makeText(SignUpActivity.this, "권한이 필요합니다.", Toast.LENGTH_SHORT).show();
                }
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

                // 각각의 입력창 중 하나라도 공백일 경우 해당 메시지를 출력합니다.
                if (idEdit.getText().toString().equals("") || emailEdit.getText().toString().equals("")
                        || passwordEdit.getText().toString().equals("") || phoneNumberEdit.getText().toString().equals("")) {
                    Toast.makeText(SignUpActivity.this, "입력을 완료해주세요.", Toast.LENGTH_SHORT).show();

                    // 회원가입에 실패했으니 다시 해당 버튼을 실행할 수 있게 해줘야 합니다.
                    signupButton.setEnabled(true);
                    return;
                }

                // 패스워드의 길이가 6자리가 되지 않을 경우
                if (passwordEdit.getText().toString().length() < 6) {
                    Toast.makeText(SignUpActivity.this, "비밀번호는 6자리 이상입니다.", Toast.LENGTH_SHORT).show();

                    // 마찬가지로 재입력 가능해야 합니다.
                    signupButton.setEnabled(true);
                    return;
                }

                // 이미지를 입력하지 않았을 경우
                if (profileImageUri == null) {
                    Toast.makeText(SignUpActivity.this, "사진을 입력해주세요.", Toast.LENGTH_SHORT).show();
                    // 마찬가지
                    signupButton.setEnabled(true);
                    return;
                }

                else {
                    createUsers();
                }
            }
        });
    }

    /*
    String checkEmail() {
        // 1. 이메일 중복 여부를 확인하기 위해 이메일 데이터들을 데이터베이스에서 불러옵니다.
        FirebaseDatabase.getInstance().getReference().child("userEmails").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // 반복문을 돌면서 하나하나 확인합니다.
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    UserEmailsModel userEmails = snapshot.getValue(UserEmailsModel.class);

                    // 내가 입력한 이메일과 저장된 이메일이 동일할 경우
                    if (emailEdit.getText().toString().trim().equals(userEmails.userEmail)) {
                        Toast.makeText(SignUpActivity.this, "이미 가입된 이메일입니다.", Toast.LENGTH_SHORT).show();

                        // 회원가입에 실패했으니 다시 해당 버튼을 실행할 수 있게 해줘야 합니다.
                        confirmResult = "불가";
                        signupButton.setEnabled(true);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return confirmResult;
    }
    */

    void createUsers() {
        // 이메일, 패스워드로만 확인이 가능 -> 계정 생성합니다.
        FirebaseAuth.getInstance()
                .createUserWithEmailAndPassword(emailEdit.getText().toString().trim(), passwordEdit.getText().toString().trim())
                // 회원가입 성공 시
                .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // 로그인 실패
                        if (!task.isSuccessful()) {
                            try {
                                // 실패할 경우 예외 처리를 실시합니다... 라는 뜻으로 보입니다?
                                throw task.getException();

                            } catch (FirebaseAuthUserCollisionException e) {
                                Toast.makeText(SignUpActivity.this, "이미 존재하는 이메일입니다.", Toast.LENGTH_SHORT).show();
                                signupButton.setEnabled(true);

                            } catch (Exception e2) {
                                Toast.makeText(SignUpActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                                signupButton.setEnabled(true);
                            }
                        }

                        else {
                            Toast.makeText(SignUpActivity.this, "데이터베이스 입력 중 입니다.", Toast.LENGTH_SHORT).show();

                            // 회원가입한 유저의 uid를 바로 불러옵니다.
                            final String uid = task.getResult().getUser().getUid();

                            // 회원가입 시 자신의 아이디를 담기 위한 코드입니다.
                            // 채팅방 진입 시 상대방 이름 불러올 때 활용됩니다.
                            UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest.Builder().setDisplayName(idEdit.getText().toString()).build();
                            task.getResult().getUser().updateProfile(userProfileChangeRequest);

                            FirebaseStorage.getInstance().getReference()
                                    .child("userImages").child(uid).putFile(profileImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                    // 프로필 이미지 다운로드 url
                                    String imageUri = task.getResult().getDownloadUrl().toString();

                                    final UserModel userModel = new UserModel();
                                    userModel.userName = idEdit.getText().toString();
                                    userModel.userPhoneNumber = phoneNumberEdit.getText().toString();
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
                                            /*
                                            // 유저 이메일 데이터 테이블 생성
                                            UserEmailsModel userEmailsModel = new UserEmailsModel();
                                            userEmailsModel.userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();

                                            // 해당 유저 uid에 입력
                                            FirebaseDatabase.getInstance().getReference().child("userEmails").child(uid).setValue(userEmailsModel);
                                            */
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
        // 권한 받고 갑시다.
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_FROM_ALBUM && resultCode == RESULT_OK) {
            Glide.with(this).load(data.getData())
                    .apply(new RequestOptions().circleCrop()).into(profileImage);

            // 이미지 경로 원본 저장
            profileImageUri = data.getData();
        }
    }
}
