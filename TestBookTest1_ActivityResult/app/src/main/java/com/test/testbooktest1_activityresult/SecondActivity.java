package com.test.testbooktest1_activityresult;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class SecondActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        try {
            int hap = getIntent().getIntExtra("첫 번째 수", 0) + getIntent().getIntExtra("두 번째 수", 0);
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("합", hap);
            setResult(RESULT_OK, intent);

        } catch (Exception e) {
            Toast.makeText(this, "더할 수 없습니다.", Toast.LENGTH_SHORT).show();
        }
    }
}
