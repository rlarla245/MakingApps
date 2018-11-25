package com.example.user.navigationfragment.navigation_fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.user.navigationfragment.R;

/**
 * Created by user on 2018-02-19.
 */

public class FirstFragment3 extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.navigation_fragment_layout3, container, false);
    }
}
