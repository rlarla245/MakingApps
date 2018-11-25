package com.example.user.intent;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class SecondActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        final TextView textView = (TextView)findViewById(R.id.text_id);
        textView.setText(getIntent().getStringExtra("이름"));

        // 로그아웃 버튼 활성화
        Button logout_button = (Button)findViewById(R.id.logout_button);
        logout_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 결과 코드값을 입력합니다.
                Toast.makeText(SecondActivity.this, "로그아웃 하였습니다.", Toast.LENGTH_SHORT).show();
                Intent logout_intent = new Intent();
                logout_intent.putExtra("logout_user", textView.getText().toString() + "님이 로그아웃하였습니다.");
                setResult(20, logout_intent);
                // 해당 액티비티를 닫습니다.
                finish();
            }
        });

        // 카메라 버튼 활성화
        Button camera_button = (Button)findViewById(R.id.camera_button);
        camera_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requirePermission();
                boolean camera = ContextCompat.checkSelfPermission(view.getContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
                boolean write = ContextCompat.checkSelfPermission(view.getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
                if (camera && write) {
                    Intent camera_intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(camera_intent, 0);
                }
                else {
                    Toast.makeText(SecondActivity.this, "권한 대기 중...", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    void requirePermission() {
        String[] permissions = new String[] {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        ArrayList<String> listPermissionNeeded = new ArrayList<>();

        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_DENIED) {
                listPermissionNeeded.add(permission);
            }
            if (!listPermissionNeeded.isEmpty()) {
                ActivityCompat.requestPermissions(this, listPermissionNeeded.toArray(new String[listPermissionNeeded.size()]), 1);
            }
        }
    }
}
