package com.test.testbook1;

import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

        final EditText editText = (EditText)findViewById(R.id.message_edittext);
        Button textButton = (Button)findViewById(R.id.mainactivity_toast_button);
        Button urlButton = (Button)findViewById(R.id.mainactivity_url_button);
        RadioButton nugaRadioButton = (RadioButton)findViewById(R.id.mainactivity_nuga_radiobutton);
        RadioButton oreoRadioButton = (RadioButton)findViewById(R.id.mainactivity_oreo_radiobutton);
        final ImageView showImageView = (ImageView)findViewById(R.id.mainactivity_change_imageview);

        urlButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent facebookIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.facebook.com"));
                startActivity(facebookIntent);
            }
        });

        textButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, editText.getText(), Toast.LENGTH_SHORT).show();
            }
        });

        nugaRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImageView.setImageResource(R.drawable.ic_launcher_background);
            }
        });

        oreoRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImageView.setImageResource(R.drawable.ic_launcher_foreground);
            }
        });

    }
}
