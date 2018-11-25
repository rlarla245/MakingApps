package com.example.user.intent;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button login_button = (Button)findViewById(R.id.login_button);

        final EditText id = (EditText)findViewById(R.id.edit_id);

        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent login_intent = new Intent(view.getContext(), SecondActivity.class);
                login_intent.putExtra("id", id.getText().toString());
                startActivityForResult(login_intent, 10);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 10 && resultCode == 20) {
            Toast.makeText(this, data.getStringExtra("logout_id"), Toast.LENGTH_SHORT).show();
        }
    }
}
