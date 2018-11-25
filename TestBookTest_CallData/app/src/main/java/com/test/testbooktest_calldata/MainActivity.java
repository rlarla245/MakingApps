package com.test.testbooktest_calldata;

import android.Manifest;
import android.annotation.SuppressLint;
import android.arch.core.executor.TaskExecutor;
import android.database.Cursor;
import android.provider.CallLog;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    public Button loadCallDataButton;

    ListView listView;
    ArrayList<String> dates;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loadCallDataButton = (Button) findViewById(R.id.mainactivity_button_loadcalldata);

        listView = findViewById(R.id.mainactivity_listview);

        dates = new ArrayList<String>();

        dates.add(getCallHistory());
        ArrayAdapter<String> adapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_list_item_1, dates);

        listView.setAdapter(adapter);
        listView.setVisibility(View.INVISIBLE);

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CALL_LOG}, MODE_PRIVATE);

        loadCallDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listView.setVisibility(View.VISIBLE);
            }
        });
    }

    public String getCallHistory() {
        // 날짜, 착/발신 여뷰, 번호, 통화 시간을 저장하는 배열입니다.
        String[] callSet = new String[]
                {CallLog.Calls.DATE, CallLog.Calls.TYPE, CallLog.Calls.NUMBER, CallLog.Calls.DURATION};

        // 쿼리 기능을 담당합니다.
        @SuppressLint("MissingPermission")
        Cursor c = getContentResolver().query
                (CallLog.Calls.CONTENT_URI, callSet, null, null, null);

        // 통화 기록이 없을 경우
        if (c == null) {
            return "통화기록이 없습니다.";
        }

        StringBuffer callBuff = new StringBuffer();
        callBuff.append("\n날짜 : 구분 : 전화번호 : 통화시간\n\n");

        // 첫 행으로 이동합니다.
        c.moveToFirst();

        do {
            long callDate = c.getLong(0);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String date_string = dateFormat.format(new Date(callDate));
            callBuff.append(date_string + ": ");

            // 착신일 경우
            if (c.getInt(1) == CallLog.Calls.INCOMING_TYPE) {
                callBuff.append("착신: ");
            } else {
                callBuff.append("발신: ");
            }

            callBuff.append(c.getString(2) + ": ");
            callBuff.append(c.getString(3) + "초\n");
            // 다음 행으로 이동
        } while (c.moveToNext()); {
            // 열었으니 닫아야지
            c.close();
        }
        return callBuff.toString();
    }
}
