package com.test.testbooktest_battery;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {
    public ImageView batteryImageview;
    public EditText batteryStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        batteryImageview = (ImageView)findViewById(R.id.mainactivity_imageview_batteryimage);
        batteryStatus = (EditText)findViewById(R.id.mainactivity_edittext_batterystatus);
    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @SuppressLint("SetTextI18n")
        @Override
        // 데이터를 받아오는 리시브기능
        public void onReceive(Context context, Intent intent) {
            // 데이터를 전송해주는 인텐트 값 받아오기
            String action = intent.getAction();

            // 충전 중인지 아닌지 판단합니다. EXTRA_PLUGGED의 값을 불러옵니다.
            int plug = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0);

            if (action.equals(Intent.ACTION_BATTERY_CHANGED)) {
                // 배터리 잔여량을 나타냅니다. EXTRA_LEVEL에 해당하는 값을 가져옵니다.
                int remainBattry = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
                batteryStatus.setText("현재 충전량: " + remainBattry + "%\n");

                if (remainBattry >= 90 && plug == 0) {
                    batteryImageview.setImageResource(R.drawable.battery_100p);
                }
                else if (remainBattry >= 70 && plug == 0) {
                    batteryImageview.setImageResource(R.drawable.battery_80p);
                }
                else if (remainBattry >= 50 && plug == 0) {
                    batteryImageview.setImageResource(R.drawable.battery_60p);
                }
                else if (remainBattry >= 10 && plug == 0) {
                    batteryImageview.setImageResource(R.drawable.battery_20p);
                }
                else if (remainBattry < 10 && plug == 0){
                    batteryImageview.setImageResource(R.drawable.battery_0p);
                }

                switch (plug) {
                    case 0:
                        batteryStatus.append("전원 연결이 되지 않았습니다.");
                        break;

                    case BatteryManager.BATTERY_PLUGGED_AC:
                        batteryStatus.append("어댑터에 연결되었습니다.");
                        batteryImageview.setImageResource(R.drawable.charging);
                        break;

                    case BatteryManager.BATTERY_PLUGGED_USB:
                        batteryStatus.append("USB에 연결되었습니다.");
                        batteryImageview.setImageResource(R.drawable.charging);
                        break;
                }
            }
        }
    };

    // 앱 중지 시 등록된 리시버를 해제합니다.
    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }

    // 브로드캐스트 리시버를 실행시킵니다. ACTION_BATTERY_CHANGED 액션을 리시버에 등록합니다.
    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(broadcastReceiver, intentFilter);
    }
}
