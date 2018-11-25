package com.example.user.a5test;

import android.content.DialogInterface;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

public class SplashActivity extends AppCompatActivity {

    public LinearLayout SplashActivityLinearLayout;
    public FirebaseRemoteConfig mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // 상테 창 제거
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // 레이아웃 불러오기
        SplashActivityLinearLayout = (LinearLayout)findViewById(R.id.splash_activity_linearlayout);

        // Remote 기능 디버그 값 불러오기
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(BuildConfig.DEBUG)
                .build();
        mFirebaseRemoteConfig.setConfigSettings(configSettings);

        // 디폴트 코드 값 불러오기
        mFirebaseRemoteConfig.setDefaults(R.xml.defaultlayout);

        // 이미 디버깅했기 때문에 요청 시간 값을 0으로 입력합니다.
        mFirebaseRemoteConfig.fetch(0)
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            mFirebaseRemoteConfig.activateFetched();
                        } else {
                        }
                        displayMessage();
                    }
                });
    }

    void displayMessage() {
        String splash_background = mFirebaseRemoteConfig.getString("splash_background");
        Boolean caps = mFirebaseRemoteConfig.getBoolean("splash_caps");
        String splash_message = mFirebaseRemoteConfig.getString("splash_message");

        // 백그라운드 색을 지정합니다.
        SplashActivityLinearLayout.setBackgroundColor(Color.parseColor(splash_background));

        if (caps) {
            // 대화창 생성
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            // 대화창 내 텍스트 입력
            // 확인을 누르면 대화창이 닫힙니다. 점검이므로 대화창을 닫게 하는 것이 목표입니다.
            builder.setMessage(splash_message).setPositiveButton("확인", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            builder.create().show();
        }
    }

}
