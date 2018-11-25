package com.example.user.myapplication1;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final TextView user_name = (TextView)findViewById(R.id.user_name);

        user_name.setText(getIntent().getStringExtra("id"));

        Button logout_button = (Button)findViewById(R.id.logout_button);
        logout_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(LoginActivity.this, "로그아웃 하였습니다.", Toast.LENGTH_SHORT).show();
                Intent logout_intent = new Intent();
                logout_intent.putExtra("logout_user", user_name.getText().toString() + "이 로그아웃 하였습니다.");
                setResult(20, logout_intent);
                finish();
            }
        });
    }
}
