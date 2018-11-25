package com.example.user.noti;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button simple_notification = (Button)findViewById(R.id.simple_notification);
        Button specific_notification = (Button)findViewById(R.id.specific_notification);
        Button plus_notification = (Button)findViewById(R.id.plus_notification);
        Button multi_view_notification = (Button)findViewById(R.id.multi_view_notification);

        simple_notification.setOnClickListener(this);
        specific_notification.setOnClickListener(this);
        plus_notification.setOnClickListener(this);
        multi_view_notification.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.simple_notification:
                notification1();
                break;

            case R.id.specific_notification:
                notification2();
                break;

            case R.id.plus_notification:
                notification3();
                break;

            case R.id.multi_view_notification:
                notification4();
                break;
        }
    }

    void notification1() {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.message_icon)
                        .setContentTitle("단순 알람")
                        .setAutoCancel(true)
                        .setContentText("첫 번째 알람입니다.");

        Intent resultIntent = new Intent(this, ResultActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);

        stackBuilder.addParentStack(ResultActivity.class);

        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.notify(0, mBuilder.build());
    }

    void notification2() {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.message_icon)
                .setContentTitle("상세 알람입니다.")
                .setAutoCancel(true)
                .setContentText("밑으로 스크롤하여 확인해주세요");

        NotificationCompat.InboxStyle inboxStyle =
                new NotificationCompat.InboxStyle();

        String[] events = new String[6];
        events[0] = "1. 동해물과 백두산이";
        events[1] = "2. 마르고 닳도록";
        events[2] = "3. 하느님이 보우하사";
        events[3] = "4. 우리나라 만세";
        events[4] = "5. 무궁화 삼천리 화려강산";
        events[5] = "6. 대한사람 대한으로 길이 보전하세";

        inboxStyle.setBigContentTitle("다음과 같습니다.");

        for (int i = 0; i < events.length; i++) {
            inboxStyle.addLine(events[i]);
        }

        mBuilder.setStyle(inboxStyle);

        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.notify(0, mBuilder.build());
    }

    void notification3() {
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        int notifyID = 1;
        NotificationCompat.Builder mNotifyBuilder = new NotificationCompat.Builder(this)
                .setContentTitle("새로운 알람입니다.")
                .setContentText("추가 알람입니다.")
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.message_icon);

        int numMessages = 0;

        mNotifyBuilder.setContentText("추가 알람입니다.")
                .setNumber(++numMessages);

        mNotificationManager.notify(
                notifyID,
                mNotifyBuilder.build());
    }

    void notification4() {
        Intent resultIntent = new Intent(this, ResultActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);

        stackBuilder.addParentStack(ResultActivity.class);

        stackBuilder.addNextIntent(resultIntent);

        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setContentTitle("새로운 알람입니다.")
                .setContentText("추가 알람입니다.")
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.message_icon);

        builder.setContentIntent(resultPendingIntent);

        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.notify(0, builder.build());
    }
}
