package com.updatetest.whereareyou;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

public class SplashActivity extends AppCompatActivity {
    // 스플래쉬 레이아웃을 선언합니다.
    private LinearLayout SplashActivityLinearLayout;

    // 원격 조정을 할 수 있는 인스턴스 변수를 선언합니다.
    private FirebaseRemoteConfig mFirebaseRemoteConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 레이아웃 xml 파일 설정 및 호출
       setContentView(R.layout.activity_splash);

        // 상태창 제거 -> FullScreen 설정
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // 레이아웃 호출 - 원격으로 조정해야 하기 때문에 레이아웃 호출이 필요합니다.
        SplashActivityLinearLayout = (LinearLayout)findViewById(R.id.splashactivity_linearlayout);

        // 원격 기능 호출합니다.
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        // 세팅 설정 및 개발자 디버깅 모드? 활성화
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(BuildConfig.DEBUG)
                .build();
        // 세팅 설정
        mFirebaseRemoteConfig.setConfigSettings(configSettings);

        // 디폴트 설정 값 설정
        mFirebaseRemoteConfig.setDefaults(R.xml.default_config);

        // 서버 요청 시간을 의미. 설정 값 변경 뒤 호출하는 시간을 의미하는 듯?
        mFirebaseRemoteConfig.fetch(0)
                // 성공했을 경우
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // 성공 시 활성화
                            mFirebaseRemoteConfig.activateFetched();
                        } else {
                            // 실패 시 동작 작동
                        }
                        // 메시지 띄우는 메소드 실행
                        displayMessage();
                    }
                });
    }

    // 원격 창에 들어갈 메시지들을 입력합니다.
    // xml 파일 내 설정 값 호출합니다.
    void displayMessage() {
        // 배경 색 입니다.
        String splash_background = mFirebaseRemoteConfig.getString("splash_background");
        // true, false를 통해 유저의 접근을 허용할 지, 막을 지 설정해야 합니다.
        boolean caps = mFirebaseRemoteConfig.getBoolean("splash_caps");
        // 다이얼로그 창에 띄울 메시지 입니다.
        String splash_message = mFirebaseRemoteConfig.getString("splash_message");

        // 백그라운드 색 지정
        SplashActivityLinearLayout.setBackgroundColor(Color.parseColor(splash_background));

        // 서버 점검 시 유저의 앱 사용을 막습니다.
        // 보완 방법 중 하나로 유저가 확인 버튼 외에 화면 클릭이 불가하도록 만들면 좋을 것 같습니다.
        // CheckRoomMapFragment 프레그먼트 내 다이얼로그 코드 참조
        if (caps) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            // 확인 버튼 외 화면 클릭이 불가합니다.
            builder.setCancelable(false);
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
