package com.updatetest.firestoretest;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.multidex.MultiDex;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.updatetest.firestoretest.Models.UserModel;

public class MainActivity extends AppCompatActivity {
    TextView textView_ReadFullDB;
    TextView textView_UseQuery;
    TextView textView_OrderByChildLimit;
    TextView textView_ChatBot;
    EditText editText_name;
    EditText editText_age;
    EditText editText_major;
    Button uploadToDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 각각 위젯 호출
        textView_ReadFullDB = (TextView)findViewById(R.id.mainactivity_textview_readfulldb);
        textView_UseQuery = findViewById(R.id.mainactivity_textview_usequery);
        textView_OrderByChildLimit = findViewById(R.id.mainactivity_textview_query_orderbychildlimit);
        textView_ChatBot = findViewById(R.id.mainactivity_textview_chatbot);
        editText_name = findViewById(R.id.mainactivity_edittext_name);
        editText_age = findViewById(R.id.mainactivity_edittext_age);
        editText_major= findViewById(R.id.mainactivity_edittext_major);
        uploadToDB = findViewById(R.id.mainactivity_button_upload);

        // put data to DB
        uploadToDB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserModel userModel = new UserModel();
                userModel.name = editText_name.getText().toString();
                userModel.age = Integer.parseInt(editText_age.getText().toString().trim());
                userModel.major = editText_major.getText().toString();
                if (userModel.name != null) {
                    FirebaseFirestore.getInstance().collection("users").document(userModel.name)
                            .set(userModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(MainActivity.this, "업로드 성공", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(MainActivity.this, "업로드 실패", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

        // read Full DB
        textView_ReadFullDB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, ReadFullDB.class));
            }
        });

        textView_UseQuery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, UseQuery.class));
            }
        });

        textView_OrderByChildLimit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, OrderByChildLimit.class));
            }
        });

        textView_ChatBot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, ChatBot.class));
            }
        });
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
