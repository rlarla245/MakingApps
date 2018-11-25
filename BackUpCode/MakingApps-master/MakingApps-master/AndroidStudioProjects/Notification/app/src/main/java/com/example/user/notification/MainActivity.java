package com.example.user.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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
        Button individual_notification = (Button)findViewById(R.id.individual_notification);
        Button loading1 = (Button)findViewById(R.id.loading1);
        Button loading2 = (Button)findViewById(R.id.loading2);
        Button headup = (Button)findViewById(R.id.headup);

        // this는 메인 클래스를 의미하는데, 메인 클래스는 onClickListner를 상속(인터페이스)하기 때문에
        // this만 입력해도 자동으로 버튼을 누를 시 해당 기능을 작동시킵니다.
        simple_notification.setOnClickListener(this);
        specific_notification.setOnClickListener(this);
        plus_notification.setOnClickListener(this);
        multi_view_notification.setOnClickListener(this);
        individual_notification.setOnClickListener(this);
        loading1.setOnClickListener(this);
        loading2.setOnClickListener(this);
        headup.setOnClickListener(this);
    }

    @Override
    // 각각의 버튼 마다 onClick 메소드를 생성하는 것이 아니라 implement를 통해 미리 생성하여
    // switch문으로 간편히 사용할 수 있게 합니다.
    public void onClick(View view) {
        // view가 할당받는 id에 따라 작동하는 기능을 달리합니다.
        switch (view.getId()) {
            case R.id.simple_notification:
                // 단순 알림 버튼을 누르면 해당 메소드를 실행합니다.
                notification1();
                break;

            case R.id.specific_notification:
                // 단순 알림 버튼을 누르면 해당 메소드를 실행합니다.
                notification2();
                break;

            case R.id.plus_notification:
                // 단순 알림 버튼을 누르면 해당 메소드를 실행합니다.
                notification3();
                break;

            case R.id.multi_view_notification:
                // 단순 알림 버튼을 누르면 해당 메소드를 실행합니다.
                notification4();
                break;

            case R.id.individual_notification:
                // 단순 알림 버튼을 누르면 해당 메소드를 실행합니다.
                notification5();
                break;

            case R.id.loading1:
                // 단순 알림 버튼을 누르면 해당 메소드를 실행합니다.
                notification6();
                break;

            case R.id.loading2:
                // 단순 알림 버튼을 누르면 해당 메소드를 실행합니다.
                notification7();
                break;

            case R.id.headup:
                // 단순 알림 버튼을 누르면 해당 메소드를 실행합니다.
                notification8();
                break;
        }
    }
    void notification1() {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.message_icon)
                        .setContentTitle("공지사항")
                        .setAutoCancel(true)
                        .setContentText("반갑습니다, 앱 테스트 중입니다.");

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

    void notification2() {
        // 상세 알림에는 peddingIntent가 없습니다.
        // 이 대신에 events라는 문자열 배열에 요소를 넣어줍니다.
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.message_icon)
                .setContentTitle("상세 알림입니다.")
                .setAutoCancel(true)
                .setContentText("알림을 당겨서 확인하세요!");

        NotificationCompat.InboxStyle inboxStyle =
                new NotificationCompat.InboxStyle();

        String[] events = new String[6];
        events[0] = "1. 첫 번째 공지사항입니다.";
        events[1] = "2. 두 번째 공지사항입니다.";
        events[2] = "3. 세 번째 공지사항입니다.";
        events[3] = "4. 네 번째 공지사항입니다.";
        events[4] = "5. 다섯번째 공지사항입니다.";
        events[5] = "6. 여섯번째 공지사항입니다.";

        // 당겼을 때 뜨는 메인 텍스트
        inboxStyle.setBigContentTitle("알람을 확인하세요.");

        for (int i=0; i < events.length; i++) {
            inboxStyle.addLine(events[i]);
        }
        mBuilder.setStyle(inboxStyle);

        // 현재 실행 코드가 없으므로, 상단의 noti1 메소드에서 불러옵니다.
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // notify 메소드로 인해 실행됩니다.
        mNotificationManager.notify(0, mBuilder.build());
    }

    void notification3() {
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        int notifyID = 1;
        NotificationCompat.Builder mNotifyBuilder = new NotificationCompat.Builder(this)
                .setContentTitle("새로운 공지사항입니다.")
                .setContentText("두 번째 알람입니다.")
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.message_icon);
        int numMessages = 0;

        mNotifyBuilder.setContentText("두 번째 알람입니다.")
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
                .setContentTitle("새로운 공지사항입니다.")
                .setContentText("알림을 눌러 확인하세요.")
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.message_icon);

        builder.setContentIntent(resultPendingIntent);

        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.notify(0
                , builder.build());
    }

    void notification5() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setContentTitle("새로운 공지사항입니다.")
                .setContentText("알림을 눌러 확인하세요.")
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.message_icon);

        Intent notifyIntent =
                new Intent(this, Individual_notification.class);

        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent notifyPendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        notifyIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        builder.setContentIntent(notifyPendingIntent);

        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.notify(5, builder.build());
    }

    void notification6() {
        final NotificationManager mNotifyManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);

        mBuilder.setContentTitle("다운로드 중입니다...")
                .setContentText("잠시만 기다려주세요...")
                .setSmallIcon(R.drawable.message_icon);

        new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        int incr;
                        // Do the "lengthy" operation 20 times
                        for (incr = 0; incr <= 100; incr += 20) {
                            // Sets the progress indicator to a max value, the
                            // current completion percentage, and "determinate"
                            // state
                            mBuilder.setProgress(100, incr, false);
                            // Displays the progress bar for the first time.
                            mNotifyManager.notify(0, mBuilder.build());
                            // Sleeps the thread, simulating an operation
                            // that takes time
                            try {
                                // Sleep for 5 seconds
                                Thread.sleep(2 * 1000);
                            } catch (InterruptedException e) {

                            }
                        }
                        // When the loop is finished, updates the notification
                        mBuilder.setContentText("Download complete")
                                // Removes the progress bar
                                .setProgress(0,0,false);
                        mNotifyManager.notify(6, mBuilder.build());
                    }
                }
        ).start();
    }

    void notification7() {
        final NotificationManager mNotifyManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);

        mBuilder.setContentTitle("다운로드 중입니다...")
                .setContentText("잠시만 기다려주세요...")
                .setSmallIcon(R.drawable.message_icon);

        new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        int incr;
                        // Do the "lengthy" operation 20 times
                        for (incr = 0; incr <= 100; incr += 20) {
                            // Sets the progress indicator to a max value, the
                            // current completion percentage, and "determinate"
                            // state
                            mBuilder.setProgress(0, 0, true);
                            // Displays the progress bar for the first time.
                            mNotifyManager.notify(0, mBuilder.build());
                            // Sleeps the thread, simulating an operation
                            // that takes time
                            try {
                                // Sleep for 5 seconds
                                Thread.sleep(2 * 1000);
                            } catch (InterruptedException e) {

                            }
                        }
                        // When the loop is finished, updates the notification
                        mBuilder.setContentText("Download complete")
                                // Removes the progress bar
                                .setProgress(0,0,false);
                        mNotifyManager.notify(6, mBuilder.build());
                    }
                }
        ).start();
    }

    void notification8() {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.message_icon)
                        .setContentTitle("공지사항")
                        .setPriority(Notification.PRIORITY_HIGH).setDefaults(Notification.DEFAULT_VIBRATE)
                        .setContentText("반갑습니다, 앱 테스트 중입니다.");

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
