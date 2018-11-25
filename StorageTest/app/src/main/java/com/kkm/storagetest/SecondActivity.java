package com.kkm.storagetest;

import android.annotation.SuppressLint;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.kkm.storagetest.databinding.ActivitySecondBinding;

public class SecondActivity extends AppCompatActivity {
    protected ActivitySecondBinding binding;
    protected FirebaseAuth auth;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_second);
        auth = FirebaseAuth.getInstance();

        String email = auth.getCurrentUser().getEmail();

        binding.secondactivityTextviewHellotextview.setText(email + "님 환영합니다!");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        auth.signOut();
        // finish();`
    }
}
