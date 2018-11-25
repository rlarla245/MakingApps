package com.test.testbooktest1_activityresult;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    public EditText firstNumberEdit, secondNumberEdit;
    public Button plusButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firstNumberEdit = (EditText)findViewById(R.id.mainactivty_firstedittext);
        secondNumberEdit = (EditText)findViewById(R.id.mainactivty_secondedittext);

        plusButton = (Button)findViewById(R.id.mainactivty_button);

        plusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent plusIntent = new Intent(v.getContext(), SecondActivity.class);
                    plusIntent.putExtra("첫 번째 수", Integer.parseInt(firstNumberEdit.getText().toString()));
                    plusIntent.putExtra("두 번째 수", Integer.parseInt( secondNumberEdit.getText().toString()));
                    // 1. 두 번째 액티비티에서 값을 받아와 활용할 것이므로 startActivityForResult 메소드를 이용합니다.
                    // 요청 값을 0으로 잡습니다.
                    startActivityForResult(plusIntent, 0);

                } catch (Exception e) {
                    Toast.makeText(MainActivity.this, "숫자를 입력해주세요.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // 2. 값을 받아올 때 쓰는 메소드입니다.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // 3. 세컨드 액티비티에서 넘어오는 코드 값이 -1일 경우 작동시킵니다.
        // 넘어오는 값은 data라는 인텐트 변수입니다.
        if (resultCode == RESULT_OK) {
            Toast.makeText(this, String.valueOf(data.getIntExtra("합", 0)), Toast.LENGTH_SHORT).show();
        }
    }
}
