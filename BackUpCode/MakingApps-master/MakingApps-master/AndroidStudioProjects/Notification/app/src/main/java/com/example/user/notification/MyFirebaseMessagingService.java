package com.example.user.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by user on 2018-03-02.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        String messageFormServer = remoteMessage.getNotification().getBody();

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.message_icon)
                        .setContentTitle("공지사항")
                        .setPriority(Notification.PRIORITY_HIGH).setDefaults(Notification.DEFAULT_VIBRATE)
                        .setContentText(messageFormServer);

        // 클릭하면 띄우는 창
        Intent resultIntent = new Intent(this, ResultActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);

        stackBuilder.addParentStack(ResultActivity.class);

        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                // pedding이라는 것은 원격으로 어떤 동작을 실행시킨다는 뜻입니다.
                // 고로, 앱이 꺼져있어도 알람을 누르면 원격으로 앱을 실행시킵니다.
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        // peddingIntent가 인텐트 설정값으로 들어갑니다.
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // notify 메소드로 인해 실행됩니다.
        mNotificationManager.notify(0, mBuilder.build());
    }
}
