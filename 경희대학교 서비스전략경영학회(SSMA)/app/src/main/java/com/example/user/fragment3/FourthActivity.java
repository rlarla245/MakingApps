package com.example.user.fragment3;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.user.fragment3.GoogleMapActivities.googleMapActivity;
import com.example.user.fragment3.MainFragments.Fragment1;
import com.example.user.fragment3.MainFragments.Fragment2;
import com.example.user.fragment3.MainFragments.Fragment3;
import com.example.user.fragment3.MainFragments.Fragment4;
import com.example.user.fragment3.NavigationFragments.Navigation_fragment1;
import com.example.user.fragment3.NavigationFragments.Navigation_fragment2;
import com.example.user.fragment3.NavigationFragments.Navigation_fragment3;
import com.example.user.fragment3.NavigationFragments.Navigation_fragment4;
import com.example.user.fragment3.NavigationFragments.Navigation_fragment5;

public class FourthActivity extends AppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fourth);

        // 툴바 설정 및 액션바, 프로젝트 이름 제거
        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // 이메일 버튼 누를 시 Toast 기능 활용
        toolbar.findViewById(R.id.toolbar_email).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(FourthActivity.this, "서버와의 연결이 필요합니다.", Toast.LENGTH_SHORT).show();
            }
        });

        // 툴바, 사이드 바 활용 위한 drawerlayout 설정
        final DrawerLayout drawerLayout = (DrawerLayout)findViewById(R.id.second_drawerlayout);

        // 툴바에 사이드 바 입력하는 단계
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, 0, 0);

        drawerLayout.setDrawerListener(actionBarDrawerToggle);

        // 사이드 바 동기화
        actionBarDrawerToggle.syncState();

        // 사이드 바에 들어갈 레이아웃 설정
        final NavigationView navigationView = (NavigationView)findViewById(R.id.second_navigationview);

        navigationView.findViewById(R.id.email_address).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(FourthActivity.this, "서버와의 연결이 필요합니다.", Toast.LENGTH_SHORT).show();
            }
        });

        // 각 내비게이션 프레그먼트 마다 레이아웃 설정 - addToBackStavk(이름.class.getName() 주의!
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                if (item.getItemId() == R.id.first_item) {
                    getFragmentManager().beginTransaction().replace(R.id.main_frame, new Navigation_fragment1()).addToBackStack(Navigation_fragment1.class.getName()).commit();
                }

                if (item.getItemId() == R.id.second_item) {
                    Intent intent = new Intent(navigationView.getContext(), Navigation_fragment2.class);
                    startActivity(intent);
                }

                if (item.getItemId() == R.id.third_item) {
                    getFragmentManager().beginTransaction().replace(R.id.main_frame, new Navigation_fragment3()).addToBackStack(Navigation_fragment3.class.getName()).commit();
                }

                if (item.getItemId() == R.id.fouth_item) {
                    getFragmentManager().beginTransaction().replace(R.id.main_frame, new Navigation_fragment4()).addToBackStack(Navigation_fragment4.class.getName()).commit();
                }

                if (item.getItemId() == R.id.fifth_item) {
                    getFragmentManager().beginTransaction().replace(R.id.main_frame, new Navigation_fragment5()).addToBackStack(Navigation_fragment5.class.getName()).commit();
                }
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });

        // 내비게이션 뷰에 있는 이메일 버튼 누를 시 Toast 활성화
        navigationView.findViewById(R.id.email_address).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(FourthActivity.this, "서버와의 연결이 필요합니다.", Toast.LENGTH_SHORT).show();
            }
        });

        // 프레그먼트 생성(둘러싼 레이아웃이 LinearLayout)
        LinearLayout button1 = (LinearLayout)findViewById(R.id.button1);
        LinearLayout button2 = (LinearLayout)findViewById(R.id.button2);
        LinearLayout button3 = (LinearLayout)findViewById(R.id.button3);
        LinearLayout button4 = (LinearLayout)findViewById(R.id.button4);
        LinearLayout button5 = (LinearLayout)findViewById(R.id.button5);

        // 각 레이아웃 뷰를 누르면 FramgLayout을 각 프레그먼트로 바꿈(애니메이션 효과도 줌)
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().beginTransaction().setCustomAnimations(R.animator.show_effect_enter, R.animator.show_effect_exit).replace(R.id.main_frame, new Fragment1()).addToBackStack(Fragment1.class.getName()).commit();
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().beginTransaction().setCustomAnimations(R.animator.flip_effect_enter, R.animator.flip_effect_exit).replace(R.id.main_frame, new Fragment2()).addToBackStack(Fragment2.class.getName()).commit();
            }
        });

        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().beginTransaction().setCustomAnimations(R.animator.zoom_effect_enter, R.animator.zoom_effect_exit).replace(R.id.main_frame, new Fragment3()).addToBackStack(Fragment3.class.getName()).commit();
            }
        });

        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().beginTransaction().replace(R.id.main_frame, new Fragment4()).addToBackStack(Fragment4.class.getName()).commit();
            }
        });

        button5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), googleMapActivity.class);
                startActivity(intent);
            }
        });

        Button simple_notification = (Button)findViewById(R.id.simple_notification);
        Button specific_notification = (Button)findViewById(R.id.specific_notification);
        Button plus_notification = (Button)findViewById(R.id.plus_notification);
        Button multi_view_notification = (Button)findViewById(R.id.multi_view_notification);
        Button individual_notification = (Button)findViewById(R.id.individual_notification);
        Button loading1_notification = (Button)findViewById(R.id.loading);
        Button loading2_notification = (Button)findViewById(R.id.fragment_loading);
        Button headup_notification = (Button)findViewById(R.id.head_up);

        simple_notification.setOnClickListener(this);
        specific_notification.setOnClickListener(this);
        plus_notification.setOnClickListener(this);
        multi_view_notification.setOnClickListener(this);
        individual_notification.setOnClickListener(this);
        loading1_notification.setOnClickListener(this);
        loading2_notification.setOnClickListener(this);
        headup_notification.setOnClickListener(this);

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

            case R.id.individual_notification:
                notification5();
                break;

            case R.id.loading:
                notification6();
                break;

            case R.id.fragment_loading:
                notification7();
                break;

            case R.id.head_up:
                notification8();
                break;
        }
    }

    void notification1() {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
            // 아이콘을 다운받아 집어넣어도 되고, mipmap 폴더를 활용해도 됩니다.
            // autocancel은 알림을 누를 경우 알림이 사라지는 효과를 줍니다.
                        .setSmallIcon(R.drawable.message_icon)
                        .setContentTitle("공지사항")
                        .setAutoCancel(true)
                        .setContentText("반갑습니다, 앱 테스트 중입니다.");

        // resultIntent = 클릭하면 띄우는 창
        // ResultActivity는 없으므로 새로 생성해줘야합니다
        // 유의해야 할 점은 클래스가 아니라 액티비티입니다.
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
        // peddingIntent가 인텐트 설정값(세팅값)으로 들어갑니다.
        mBuilder.setContentIntent(resultPendingIntent);

        NotificationManager mNotificationManager =
                (NotificationManager)
                        getSystemService(Context.NOTIFICATION_SERVICE);

        // notify 메소드로 인해 실행됩니다.
        mNotificationManager.notify(0, mBuilder.build());
    }

    void notification2() {
        // 상세 알림에는 peddingIntent가 없습니다.

        // 원격으로 앱을 실행하는 게 아니라 필요한 정보를 알림에서 보여주기
        // 때문이기도 합니다.

        // 이 대신에 events라는 문자열 배열에 정보 요소를 넣어줍니다.
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
        // NotificationManager이 설정되어 있지 않습니다.
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        int notifyID = 1;
        // 이 또한 설정되어 있지 않습니다.
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

        // 여기서 원 코드는 id이나, 오류가 발생하므로 임의로 0으로 지정했습니다.
        // 원 코드에서는 오류가 발생하지 않으므로 미심쩍은 부분이 존재하는 구간
        mNotificationManager.notify(10, builder.build());
    }

    void notification5() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setContentTitle("새로운 공지사항입니다.")
                .setContentText("알림을 눌러 확인하세요.")
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.message_icon);
        // 인텐트의 결과 액티비티는 fitth로 변경되어 있기 때문에 수정
        Intent notifyIntent =
                new Intent(this, FifthActivity.class);

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
        // 고정 기간 진행 상태 표시기 코드 불러오기
        final NotificationManager mNotifyManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setContentTitle("Loading...")
                .setContentText("잠시만 기다려주세요...")
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.message_icon);

        new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        int incr;
                        // 시간을 의미합니다. 100을 몇으로 나눈 시간동안 실행시킬 것인가에 대한 의미입니다.
                        for (incr = 0; incr <= 100; incr+=10) {

                            mBuilder.setProgress(100, incr, false);

                            mNotifyManager.notify(0, mBuilder.build());

                            try {
                                // 몇초마다 진행 바가 늘어나는 지 설정하는 값입니다.
                                Thread.sleep(1 * 1000);
                            } catch (InterruptedException e) {

                            }
                        }
                        // 다운로드 완료 시 새로운 메시지가 출력됩니다.
                        mBuilder.setContentText("다운로드가 완료되었습니다.")
                                // 진행바를 삭제하는 코드입니다.
                                .setProgress(0,0,false);
                        mNotifyManager.notify(6, mBuilder.build());
                    }
                }
// Starts the thread by calling the run() method in its Runnable
        ).start();
    }

    void notification7() {
        // 고정 기간 진행 상태 표시기 코드 불러오기
        final NotificationManager mNotifyManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setContentTitle("Loading...")
                .setContentText("잠시만 기다려주세요.")
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.message_icon);
// Start a lengthy operation in a background thread
        new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        int incr;
                        // 시간을 의미합니다. 100을 몇으로 나눈 시간동안 실행시킬 것인가에 대한 의미입니다.
                        for (incr = 0; incr <= 100; incr+=10) {

                            mBuilder.setProgress(0, 0, true);

                            mNotifyManager.notify(0, mBuilder.build());

                            try {
                                // 몇초마다 진행 바가 늘어나는 지 설정하는 값입니다.
                                Thread.sleep(1 * 1000);
                            } catch (InterruptedException e) {

                            }
                        }
                        // 다운로드가 끝나면 새로운 메시지를 출력합니다.
                        mBuilder.setContentText("다운로드가 완료되었습니다.")
                                // true로 변경하면 다운로드 진행바가 무한히 돌아갑니다.
                                .setProgress(0,0,false);
                        mNotifyManager.notify(6, mBuilder.build());
                    }
                }
// Starts the thread by calling the run() method in its Runnable
        ).start();
    }

    void notification8() {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                // 아이콘을 다운받아 집어넣어도 되고, mipmap 폴더를 활용해도 됩니다.
                // autocancel은 알림을 누를 경우 알림이 사라지는 효과를 줍니다.
                .setSmallIcon(R.drawable.message_icon)
                .setContentTitle("공지사항")
                // 알람이 발생할 시 진동을 발생시키는 코드입니다.
                .setPriority(Notification.PRIORITY_HIGH).setDefaults(Notification.DEFAULT_VIBRATE)
                .setContentText("반갑습니다, 앱 테스트 중입니다.");

        // resultIntent = 클릭하면 띄우는 창
        // ResultActivity는 없으므로 새로 생성해줘야합니다
        // 유의해야 할 점은 클래스가 아니라 액티비티입니다.
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
        // peddingIntent가 인텐트 설정값(세팅값)으로 들어갑니다.
        mBuilder.setContentIntent(resultPendingIntent);

        NotificationManager mNotificationManager =
                (NotificationManager)
                        getSystemService(Context.NOTIFICATION_SERVICE);

        // notify 메소드로 인해 실행됩니다.
        mNotificationManager.notify(0, mBuilder.build());
    }
}
