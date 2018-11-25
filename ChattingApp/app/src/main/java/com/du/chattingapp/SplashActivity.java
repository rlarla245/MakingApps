package com.du.chattingapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

public class SplashActivity extends AppCompatActivity {

    private LinearLayout SplashActivityLinearLayout;
    private FirebaseRemoteConfig mFirebaseRemoteConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // 상태 창 제거
        // 잘 안하긴 함
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // 레이아웃 호출
        SplashActivityLinearLayout = (LinearLayout) findViewById(R.id.SplashActivity_linearLayout);

        // 디버그 값 호출
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(BuildConfig.DEBUG)
                .build();
        mFirebaseRemoteConfig.setConfigSettings(configSettings);

        // 서버 요청 시간을 의미. 이미 디버깅했기 때문에 0으로 설정
        mFirebaseRemoteConfig.fetch(0)
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            mFirebaseRemoteConfig.activateFetched();
                        } else {

                        }
                        // alertBuilder 메시지를 입력합니다.
                        displayMessage();
                    }
                });
    }

    // 메시지 띄우기
    void displayMessage() {
        String splash_background = mFirebaseRemoteConfig.getString("splash_background");
        boolean caps = mFirebaseRemoteConfig.getBoolean("splash_caps");
        String splash_message = mFirebaseRemoteConfig.getString("splash_message");

        // 백그라운드 색 지정
        SplashActivityLinearLayout.setBackgroundColor(Color.parseColor(splash_background));

        // 서버 점검 시 유저의 앱 사용을 막습니다.
        // 보완 방법 중 하나로 유저가 확인 버튼 외에 화면 클릭이 불가하도록 만들면 좋을 것 같습니다.
        // 오버라이딩 가능한지 확인하면 좋을 듯
        if (caps) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(splash_message).setPositiveButton("확인", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            builder.create().show();
        }

        // 유저 입력을 막을 필요가 없으니 로그인 화면으로 넘겨줘야 합니다.
        else {
            // Intent 필요
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }
}

