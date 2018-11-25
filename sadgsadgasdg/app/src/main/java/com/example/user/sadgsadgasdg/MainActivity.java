package com.example.user.sadgsadgasdg;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setTitle("그럴듯한 앱");

        final ImageView imageView = (ImageView)findViewById(R.id.imageSwitcher);

        final EditText edit = (EditText)findViewById(R.id.editText);

        Button button1 = (Button)findViewById(R.id.button1);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edit.getText().toString().equals("")) {
                    Toast.makeText(MainActivity.this, "글자를 입력해주세요!", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(MainActivity.this, edit.getText().toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        Button button2 = (Button)findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent internet_intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://"+edit.getText().toString()));
                startActivity(internet_intent);
            }
        });

        RadioButton radioButton = (RadioButton)findViewById(R.id.radioButton1);
        radioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageView.setImageResource(R.drawable.ic_launcher_background);
            }
        });

        RadioButton radioButton2 = (RadioButton)findViewById(R.id.radioButton2);
        radioButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageView.setImageResource(R.drawable.ic_launcher_foreground);
            }
        });
    }
}
