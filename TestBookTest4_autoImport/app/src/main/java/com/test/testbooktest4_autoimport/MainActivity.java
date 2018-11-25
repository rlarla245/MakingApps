package com.test.testbooktest4_autoimport;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.MultiAutoCompleteTextView;

public class MainActivity extends AppCompatActivity {
    public AutoCompleteTextView autoImport;
    public MultiAutoCompleteTextView multiAutoImport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("자동 완성");

        autoImport = (AutoCompleteTextView) findViewById(R.id.mainacticity_edittext_autoimport);
        multiAutoImport = (MultiAutoCompleteTextView) findViewById(R.id.mainacticity_edittext_multiautoimport);

        String[] items = {"학점", "가군", "나군", "다군", "정시", "수시", "취업", "학자금 대출", "취준", "취준생", "삼포세대"};

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, items);

        autoImport.setAdapter(adapter);

        MultiAutoCompleteTextView.CommaTokenizer token = new MultiAutoCompleteTextView.CommaTokenizer();
        multiAutoImport.setTokenizer(token);
        multiAutoImport.setAdapter(adapter
        );
    }
}
