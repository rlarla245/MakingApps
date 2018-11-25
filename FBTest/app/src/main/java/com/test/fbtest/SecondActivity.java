package com.test.fbtest;

import android.app.Fragment;
import android.graphics.PorterDuff;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.test.fbtest.Fragment.HelloFragment;

public class SecondActivity extends AppCompatActivity {
    protected Button logoutButton, nextFragmentButton;
    protected FirebaseAuth mAuth;
    protected TextView helloTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        mAuth = FirebaseAuth.getInstance();
        helloTextView = findViewById(R.id.mainactivity_textview_hellomessage);
        helloTextView.setText(mAuth.getCurrentUser().getEmail() + "님 반갑습니다!");

        logoutButton = (Button)findViewById(R.id.secondactivity_button_logoutbutton);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                Toast.makeText(SecondActivity.this, "로그아웃 되었습니다,", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        nextFragmentButton = findViewById(R.id.secondactivity_button_newxtFrameLayout);
        nextFragmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 원하는 프레그먼트 모델에 새로운 프레그먼트 인스턴스를 생성합니다.
                Fragment helloFragment = new HelloFragment();

                // Bundle에 데이터를 입력합니다. 여기선 이메일을 Intent.PutExtra()처럼 넘겨줍니다.
                Bundle uidsBundle = new Bundle();
                uidsBundle.putString("email", mAuth.getCurrentUser().getEmail());
                // 값을 세팅합니다.
                helloFragment.setArguments(uidsBundle);

                // 프레그먼트를 넘겨줍니다.
                getFragmentManager().beginTransaction().replace(R.id.mainactivity_framgelayout, helloFragment).commit();
            }
        });
    }
}
