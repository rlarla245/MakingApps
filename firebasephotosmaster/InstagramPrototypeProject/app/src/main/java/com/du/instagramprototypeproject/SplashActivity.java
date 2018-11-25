package com.du.instagramprototypeproject;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

public class SplashActivity extends AppCompatActivity {
    protected LinearLayout splashLinearLayout;
    protected ImageView centerImage;
    protected FirebaseRemoteConfig mFirebaseRemoteConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        centerImage = (ImageView)findViewById(R.id.splashactivity_imageview);
        splashLinearLayout = (LinearLayout)findViewById(R.id.SplashActivity_linearlayout);

        // 앱 원격 제어 시스템
        remoteConfig();
    }

    void remoteConfig() {
        // 디버그 값 호출 및 원격 제어 기능 호출
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(BuildConfig.DEBUG)
                .build();
        mFirebaseRemoteConfig.setConfigSettings(configSettings);

        // 디폴트 설정 값 도출
        mFirebaseRemoteConfig.setDefaults(R.xml.default_config);

        // 서버 요청 시간을 의미. 이미 디버깅했기 때문에 0으로 설정
        mFirebaseRemoteConfig.fetch(0)
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            mFirebaseRemoteConfig.activateFetched();
                        } else {

                        }
                        displaymessage();
                    }
                });

    }

    void displaymessage() {
        String splash_background = mFirebaseRemoteConfig.getString("splash_background");
        // Boolean 값과 boolean 값은 다릅니다.
        Boolean caps = mFirebaseRemoteConfig.getBoolean("splash_caps");
        String splash_message = mFirebaseRemoteConfig.getString("splash_message");

        // 백그라운드 색 지정
        splashLinearLayout.setBackgroundColor(Color.parseColor(splash_background));

        // true일 경우 해당 동작 작동
        if (caps) {
            // AlertDialog 실행 + 메시지 출력하고 앱이 꺼지도록 유도합니다.
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(splash_message).setPositiveButton("확인", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            builder.create().show();
        }
        else {
            // Intent 필요
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }
}
