package com.example.user.intent;

import android.content.Intent;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button = (Button)findViewById(R.id.login_button);

        final EditText id = (EditText)findViewById(R.id.edit_id);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {                
                if (id.getText().toString().equals("111")) {
                    Toast.makeText(MainActivity.this, "다음 화면으로 넘어갑니다.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(view.getContext(), SecondActivity.class);
                    intent.putExtra("이름", id.getText().toString());
                    // requestCode가 10일 경우에 intent를 실행시킵니다.
                    startActivityForResult(intent, 10);
                }else {
                    Toast.makeText(MainActivity.this, "다시 입력해주세요.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 10 && resultCode == 20) {
            // request = 요청 코드번호, result = 원하는 결과 코드번호 일치해야 원하는 값을 구동
            TextView textView = (TextView)findViewById(R.id.logout_use_name);
            // getIntent인데 data가 Intent의 인스턴스이므로 data를 호출
            textView.setText(data.getStringExtra("logout_user"));
        }
    }
}
