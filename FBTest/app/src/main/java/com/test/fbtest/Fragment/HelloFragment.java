package com.test.fbtest.Fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.test.fbtest.R;

public class HelloFragment extends Fragment {
    protected FirebaseAuth auth;
    protected TextView helloTextView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_hellofragment, container, false);
        helloTextView = view.findViewById(R.id.hellofragment_textview);

        String email = getArguments().getString("email");
        helloTextView.setText(email);
        return view;
    }
}
