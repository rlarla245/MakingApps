package com.test.testbooktest_service;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    public Button startMusicServiceButton, stopMusicServiceButton;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startMusicServiceButton = (Button)findViewById(R.id.mainactivity_button_startmusicservice);
        stopMusicServiceButton = (Button)findViewById(R.id.mainactivity_button_stopmusicservice);
        intent = new Intent(MainActivity.this, MainActivity_MusicServiceClass.class);

        startMusicServiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            startService(intent);
            }
        });

        stopMusicServiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopService(intent);
            }
        });
    }
}
