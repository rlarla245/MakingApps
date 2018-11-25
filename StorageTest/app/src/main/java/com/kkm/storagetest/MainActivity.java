package com.kkm.storagetest;

import android.Manifest;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.kkm.storagetest.databinding.ActivityMainBinding;

import java.io.File;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    private static final int GALLERY_CODE = 11;
    private static final int RC_SIGN_IN = 10;
    protected String imagePath;
    protected FirebaseStorage storage;
    protected GoogleApiClient mGoogleApiClient;
    protected FirebaseAuth mAuth;
    protected FirebaseAuth.AuthStateListener authStateListener;
    protected CallbackManager callbackManager;
    protected LoginButton facebookLoginButton;

    protected ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();

        storage = FirebaseStorage.getInstance();

        // 이미지를 선택할 경우 그 이미지를 띄웁니다.
        binding.mainactivityImageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 권한 읽어오기
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, MODE_PRIVATE);
                }

                // 권한이 부여되었을 경우에만 앨범을 엽니다.
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                        && ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    Intent pickIntent = new Intent(Intent.ACTION_PICK);
                    pickIntent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                    startActivityForResult(pickIntent, GALLERY_CODE);
                } else {
                    Toast.makeText(MainActivity.this, "권한이 필요합니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // 버튼을 누를 경우 FirebaseStorage에 업로드합니다.
        binding.mainactivityButtonUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 파일 업로드 코드
                if (imagePath != null) {
                    upload(imagePath);
                } else {
                    Toast.makeText(MainActivity.this, "사진 선택이 필요합니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Firebase로 구글 인증하기(요청 토큰, 요청 권한 등)
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        // 구글 로그인 버튼 불러오기
        binding.mainactivityButtonGooglelogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 로그인 버튼을 누를 경우 구글 로그인 api에서 로그인 기능을 불러옵니다.
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });

        // 로그인 될 경우 다음 액티비티로 이동합니다.
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    startActivity(new Intent(MainActivity.this, SecondActivity.class));
                }
            }
        };

        // 페이스북 로그인
        callbackManager = CallbackManager.Factory.create();
        facebookLoginButton = (LoginButton) findViewById(R.id.mainactivity_button_facebooklogin);
        facebookLoginButton.setReadPermissions("email");

        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
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


    // Facebook 토큰을 Firebase로 넘겨주는 코드
    private void handleFacebookAccessToken(AccessToken token) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            //
                        } else {
                            Toast.makeText(MainActivity.this, "아이디 생성이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (authStateListener != null) {
            mAuth.removeAuthStateListener(authStateListener);
        }
    }

    public String getPath(Uri uri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        CursorLoader cursorLoader = new CursorLoader
                (this, uri, proj, null, null, null);

        Cursor cursor = cursorLoader.loadInBackground();
        int index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

        cursor.moveToFirst();
        return cursor.getString(index);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Facebook SDK로 값 넘겨주기
        callbackManager.onActivityResult(requestCode, resultCode, data);

        try {
            if (requestCode == GALLERY_CODE) {
                imagePath = getPath(data.getData());
                File file = new File(imagePath);
                binding.mainactivityImageview.setImageURI(Uri.fromFile(file));
            }
        } catch (Exception e) {
            Toast.makeText(this, "사진 입력이 필요합니다.", Toast.LENGTH_SHORT).show();
        }

        // 구글 로그인 인텐트 값 불러오기
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                // Google Sign In failed, update UI appropriately
                // ...
            }
        }
    }

    // 구글 계정과 Firebase 연동, 구글 계정을 파라미터 값으로 받습니다.
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        // 해당 계정의 토큰값을 입력합니다.
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);

        // 로그인 합니다.
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "로그인 되었습니다.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MainActivity.this, "로그인에 실패했습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void upload(String uri) {
        // 스토리지 주소 입력
        // 해당 스토리지는 FirebaseStorage를 불러오는 것입니다.
        StorageReference storageReference
                = storage.getReferenceFromUrl("gs://storagetest-25895.appspot.com");

        // getData() 메소드를 통해 Uri 파일로 변경합니다.
        Uri file = Uri.fromFile(new File(uri));

        // 데이터 테이블 id를 images로 접근합니다.
        StorageReference storageReference2
                = storageReference.child("images/" + file.getLastPathSegment());

        // 해당 스토리지 경로에 파일을 추가합니다. 어떻게 보면 실행 메소드라고 볼 수 있습니다.
        // 아래 코드와 동일합니다.
        // UploadTask uploadTask = storageReference.child("images/" + file.getLastPathSegment()).putFile(file);
        UploadTask uploadTask = storageReference2.putFile(file);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this, "업로드에 실패하였습니다.", Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(MainActivity.this, "업로드에 성공했습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // 실패 시
    }
}
