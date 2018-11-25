package com.test.testbooktest_spinner;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;

public class MainActivity extends AppCompatActivity {
    // 1. 레이아웃에 필요한 변수들 생성 후 호출합니다.
    public Spinner jisuSpinner;
    public ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        jisuSpinner = (Spinner)findViewById(R.id.mainactivity_spineer);
        imageView = (ImageView)findViewById(R.id.mainactivity_imageview);

        // 2. 그림 이름을 담을 배열을 생성합니다.
        final String[] imageNames = {"그림 1", "그림 2", "그림 3", "그림 4", "그림 5", "그림 6", "그림 7", "그림 8", "그림 9"};

        // 3. 어댑터를 설정합니다. 기존 레이아웃이 기능할 수 없는 부분들을 설정할 때 어댑터를 설정합니다.
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, imageNames);
        jisuSpinner.setAdapter(spinnerAdapter);

        // 4. 그림을 담을 배열을 설정합니다.
        final Integer[] images = {R.drawable.i_1, R.drawable.i_2, R.drawable.i_3, R.drawable.i_4, R.drawable.i_5,
                R.drawable.i_6, R.drawable.i_7, R.drawable.i_8, R.drawable.i_9};

        for (int i = 0; i < imageNames.length; i++) {
            jisuSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    imageView.setImageResource(images[position]);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }
    }
}
