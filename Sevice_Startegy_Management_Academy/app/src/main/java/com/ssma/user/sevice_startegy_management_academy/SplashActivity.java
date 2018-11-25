package com.ssma.user.sevice_startegy_management_academy;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.LinearLayout;

public class SplashActivity extends AppCompatActivity {

    private LinearLayout SplashActivityLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_splash_activity);

        // 액션 바 및 상태창 제거
        getSupportActionBar().hide();

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // 레이아웃 불러오기
        SplashActivityLayout = (LinearLayout)findViewById(R.id.SplashActivit_Linearlayout);
    }
}
