package com.du.chattingapp;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.du.chattingapp.Chat.MessageActivity;
import com.du.chattingapp.Fragments.BoardFragment;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    // "ChattingApp"이라는 채널 id 하에 각각의 요소들이 담깁니다.
    String ChannerId = "ChattingApp";

    // 서버에서 메시지 수신 시 작동하는 메소드입니다.
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage.getData().size() > 0) {
            String title = remoteMessage.getData().get("title");
            String text = remoteMessage.getData().get("text");
            String caseNumber = remoteMessage.getData().get("caseNumber");
            String index = remoteMessage.getData().get("index");

            // 1:1 채팅방으로 떨어뜨립니다.
            if (caseNumber.equals("0")) {
                sendNotification0(title, text, index);
            }

            // 1:多 채팅방으로 떨어뜨립니다.
            if (caseNumber.equals("1")) {
                sendNotification1(title, text, index);
            }

            // 게시판으로 떨어뜨립니다.
            if (caseNumber.equals("2")) {
                sendNotification2(title, text);
            }
        }
    }

    // 1:1 채팅방으로 이동
    private void sendNotification0(String title, String text, String index) {
        // 채널 생성 - sdk 26? 부터
        // todo
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            // 푸시메시지를 누를 경우 1:1 채팅방으로 이동합니다.
            Log.d("Service Act - 오레오 버전 이상", "1:1 채팅방으로 이동합니다.");
            Intent intent = new Intent(this, LoginActivity.class);
            intent.putExtra("caseNumber", "0");
            intent.putExtra("destinationUid", index);

            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                    PendingIntent.FLAG_ONE_SHOT);

            Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            NotificationCompat.Builder notificationBuilder =
                    new NotificationCompat.Builder(this)
                            .setSmallIcon(R.drawable.ic_share_black_24dp)
                            .setContentTitle(title)
                            .setContentText(text)
                            .setAutoCancel(true)
                            .setSound(defaultSoundUri)
                            .setVibrate(new long[]{100, 200, 100, 200})
                            .setPriority(Notification.PRIORITY_HIGH)
                            // 채널 적용
                            .setChannelId(ChannerId)
                            .setContentIntent(pendingIntent);

            // 채널 적용
            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            NotificationChannel notificationChannel
                    = new NotificationChannel(ChannerId, "ChattingApp", NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setDescription("서비스 전략경영학회(SSMA)입니다.");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.GREEN);
            notificationChannel.enableVibration(true);
            notificationChannel.setVibrationPattern(new long[]{100, 200, 100, 200});
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

            if (notificationManager != null) {
                notificationManager.createNotificationChannel(notificationChannel);
            }

            if (notificationManager != null) {
                notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
            }
        }

        // 채널 생성 - < 26
        else {
            Log.d("Service Act - 오레오 버전 이하", "1:1 채팅방으로 이동합니다.");
            // 푸시메시지를 누를 경우 1:1 채팅방으로 이동합니다.
            Intent intent = new Intent(this, LoginActivity.class);
            intent.putExtra("caseNumber", "0");
            intent.putExtra("destinationUid", index);

            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                    PendingIntent.FLAG_ONE_SHOT);

            Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            long [] pattern = {100, 200, 100, 200};

            NotificationCompat.Builder notificationBuilder =
                    new NotificationCompat.Builder(this)
                            .setSmallIcon(R.drawable.ic_share_black_24dp)
                            .setContentTitle(title)
                            .setContentText(text)
                            .setAutoCancel(true)
                            .setSound(defaultSoundUri)
                            .setVibrate(pattern)
                            .setPriority(Notification.PRIORITY_HIGH)
                            .setContentIntent(pendingIntent);

            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
        }
    }

    // 1:多 채팅방으로 이동
    private void sendNotification1(String title, String text, String index) {
        // 채널 생성 - skd 26? 부터
        // todo
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            Log.d("Service Act - 오레오 버전 이상", "1:多 채팅방으로 이동합니다.");
            // 푸시메시지를 누를 경우 게시판 프레그먼트로 이동합니다.
            Intent intent = new Intent(this, LoginActivity.class);
            intent.putExtra("caseNumber", "1");
            intent.putExtra("destinationRoomUid", index);

            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                    PendingIntent.FLAG_ONE_SHOT);

            Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder notificationBuilder =
                    new NotificationCompat.Builder(this)
                            .setSmallIcon(R.drawable.ic_share_black_24dp)
                            .setContentTitle(title)
                            .setContentText(text)
                            .setAutoCancel(true)
                            .setSound(defaultSoundUri)
                            .setVibrate(new long[]{100, 200, 100, 200})
                            .setPriority(Notification.PRIORITY_HIGH)
                            // 채널 적용
                            .setChannelId(ChannerId)
                            .setContentIntent(pendingIntent);

            // 채널 적용
            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            NotificationChannel notificationChannel
                    = new NotificationChannel(ChannerId, "ChattingApp", NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setDescription("서비스 전략경영학회(SSMA)입니다.");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.GREEN);
            notificationChannel.enableVibration(true);
            notificationChannel.setVibrationPattern(new long[]{100, 200, 100, 200});
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

            if (notificationManager != null) {
                notificationManager.createNotificationChannel(notificationChannel);
            }

            if (notificationManager != null) {
                notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
            }
        }

        // 채널 생성 - < 26
        else {
            Log.d("Service Act - 오레오 버전 이하", "1:多 채팅방으로 이동합니다.");
            // 푸시메시지를 누를 경우 게시판 프레그먼트로 이동합니다.
            Intent intent = new Intent(this, LoginActivity.class);
            intent.putExtra("caseNumber", "1");
            intent.putExtra("destinationRoomUid", index);

            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                    PendingIntent.FLAG_ONE_SHOT);

            Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder notificationBuilder =
                    new NotificationCompat.Builder(this)
                            .setSmallIcon(R.drawable.ic_share_black_24dp)
                            .setContentTitle(title)
                            .setContentText(text)
                            .setAutoCancel(true)
                            .setSound(defaultSoundUri)
                            .setPriority(Notification.PRIORITY_HIGH)
                            .setContentIntent(pendingIntent);

            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
        }
    }

    // 게시판으로 이동
    private void sendNotification2(String title, String text) {
        // 채널 생성 - skd 26? 부터
        // todo
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            Log.d("Service Act - 오레오 버전 이상", "게시판으로 이동합니다.");
            // 푸시메시지를 누를 경우 게시판 프레그먼트로 이동합니다.
            Intent intent = new Intent(this, LoginActivity.class);
            intent.putExtra("caseNumber", "2");

            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                    PendingIntent.FLAG_ONE_SHOT);

            Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder notificationBuilder =
                    new NotificationCompat.Builder(this)
                            .setSmallIcon(R.drawable.ic_share_black_24dp)
                            .setContentTitle(title)
                            .setContentText(text)
                            .setAutoCancel(true)
                            .setSound(defaultSoundUri)
                            .setVibrate(new long[]{100, 200, 100, 200})
                            .setPriority(Notification.PRIORITY_HIGH)
                            // 채널 적용
                            .setChannelId(ChannerId)
                            .setContentIntent(pendingIntent);

            // 채널 적용
            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            NotificationChannel notificationChannel
                    = new NotificationChannel(ChannerId, "ChattingApp", NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setDescription("서비스 전략경영학회(SSMA)입니다.");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.GREEN);
            notificationChannel.enableVibration(true);
            notificationChannel.setVibrationPattern(new long[]{100, 200, 100, 200});
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

            if (notificationManager != null) {
                notificationManager.createNotificationChannel(notificationChannel);
            }

            if (notificationManager != null) {
                notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
            }
        }

        // 채널 생성 - < 26
        else {
            Log.d("Service Act - 오레오 버전 이하", "게시판으로 이동합니다.");
            // 푸시메시지를 누를 경우 게시판 프레그먼트로 이동합니다.
            Intent intent = new Intent(this, LoginActivity.class);
            intent.putExtra("caseNumber", "2");

            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                    PendingIntent.FLAG_ONE_SHOT);

            Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder notificationBuilder =
                    new NotificationCompat.Builder(this)
                            .setSmallIcon(R.drawable.ic_share_black_24dp)
                            .setContentTitle(title)
                            .setContentText(text)
                            .setAutoCancel(true)
                            .setSound(defaultSoundUri)
                            .setPriority(Notification.PRIORITY_HIGH)
                            .setContentIntent(pendingIntent);

            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
        }
    }
}
