package com.test.fbtest;

import android.Manifest;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    private static final int GALLERY_CODE = 10;
    protected EditText emailEditText, passwordEditText;
    protected Button signInButton, loginButton, pickUpImageButton;

    protected FirebaseAuth mAuth;
    protected FirebaseAuth.AuthStateListener mAuthListener;
    protected FirebaseStorage storage;

    protected CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();

        emailEditText = (EditText) findViewById(R.id.mainactivity_edittext_email);
        passwordEditText = (EditText) findViewById(R.id.mainactivity_edittext_password);

        signInButton = (Button) findViewById(R.id.mainactivity_button_loginbutton);
        loginButton = (Button) findViewById(R.id.mainactivity_button_loginbutton2);
        pickUpImageButton = (Button) findViewById(R.id.mainactivity_button_pickuppicture);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
        }

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (emailEditText.getText().toString().trim().equals("") && !passwordEditText.getText().toString().trim().equals("")) {
                    Toast.makeText(MainActivity.this, "이메일을 입력해주세요.", Toast.LENGTH_SHORT).show();
                }

                if (passwordEditText.getText().toString().trim().equals("") && !emailEditText.getText().toString().trim().equals("")) {
                    Toast.makeText(MainActivity.this, "비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
                }

                if (emailEditText.getText().toString().trim().equals("") && passwordEditText.getText().toString().trim().equals("")) {
                    Toast.makeText(MainActivity.this, "계정 입력이 필요합니다.", Toast.LENGTH_SHORT).show();
                } else {
                    createUser(emailEditText.getText().toString(), passwordEditText.getText().toString());
                }
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (emailEditText.getText().toString().trim().equals("") && !passwordEditText.getText().toString().trim().equals("")) {
                    Toast.makeText(MainActivity.this, "이메일을 입력해주세요.", Toast.LENGTH_SHORT).show();
                }

                if (passwordEditText.getText().toString().trim().equals("") && !emailEditText.getText().toString().trim().equals("")) {
                    Toast.makeText(MainActivity.this, "비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
                }

                if (emailEditText.getText().toString().trim().equals("") && passwordEditText.getText().toString().trim().equals("")) {
                    Toast.makeText(MainActivity.this, "계정 입력이 필요합니다.", Toast.LENGTH_SHORT).show();
                } else {
                    loginUser(emailEditText.getText().toString().trim(), passwordEditText.getText().toString().trim());
                }
            }
        });

        // 사진 올리는 버튼
        pickUpImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "사진 업로드 중입니다.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                // 상수 입력이 필요합니다. 10으로 넣습니다.
                startActivityForResult(intent, GALLERY_CODE);
            }
        });

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Intent intent = new Intent(MainActivity.this, SecondActivity.class);
                    startActivity(intent);
                } else {

                }
            }
        };

        callbackManager = CallbackManager.Factory.create();
        final LoginButton loginButton = findViewById(R.id.mainactivity_loginbutton_facebook);
        loginButton.setReadPermissions("email", "public_profile");

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });
    }

    private void handleFacebookAccessToken(AccessToken token) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());

        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (!task.isSuccessful()) {
                    Toast.makeText(MainActivity.this, "에러 발생", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "로그인에 성공하였습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    protected void createUser(final String email, final String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "계정 생성 완료. 반갑습니다!", Toast.LENGTH_SHORT).show();
                        }

                        if (!task.isSuccessful() && passwordEditText.getText().toString().trim().length() < 6) {
                            Toast.makeText(MainActivity.this, "비밀번호는 최소 6자리 이상입니다.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MainActivity.this, "계정 생성에 실패했습니다.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void loginUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "존재하는 계정이 아닙니다.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MainActivity.this, "이메일 로그인 완료", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    public String getPath(Uri uri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        CursorLoader cursorLoader = new CursorLoader(this, uri, proj, null, null, null);

        Cursor cursor = cursorLoader.loadInBackground();
        int index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

        cursor.moveToFirst();
        return cursor.getString(index);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_CODE) {
            StorageReference storageRef
                    = storage.getReferenceFromUrl("gs://fbtest-24460.appspot.com");

            // 4. 다음과 같이 정상적으로 경로를 받아옵니다.
            Uri file = Uri.fromFile(new File(getPath(data.getData())));
            StorageReference riversRef
                    = storageRef.child("images/" + file.getLastPathSegment());
            UploadTask uploadTask = riversRef.putFile(file);

            // Register observers to listen for when the download is done or if it fails
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                }
            }).addOnSuccessListener
                    (new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(MainActivity.this, "업로드 성공", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}


