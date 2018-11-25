package com.example.user.fragment3;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.fragment3.GoogleMapActivities.googleMapActivity;
import com.example.user.fragment3.MainFragments.CurriculumFragment;
import com.example.user.fragment3.MainFragments.MissionFragment;
import com.example.user.fragment3.MainFragments.ProfessorFragment;
import com.example.user.fragment3.MainFragments.VisionFragment;
import com.example.user.fragment3.NavigationFragments.FirstPeoples;
import com.example.user.fragment3.NavigationFragments.Secondpeoples;
import com.example.user.fragment3.NavigationFragments.developer_information;

public class SecondActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        // 이미지를 통한 Intent 이동
        ImageView academy_notification = (ImageView)findViewById(R.id.first_effect);
        academy_notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent notification = new Intent(view.getContext(), AcademyNotificationClass.class);
                startActivity(notification);
            }
        });

        // 두 번째 이미지
        ImageView academy_events = (ImageView)findViewById(R.id.second_effect);
        academy_events.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent notification = new Intent(view.getContext(), AcademyEventsActivity.class);
                startActivity(notification);
            }
        });

        // 여섯번째 이미지
        ImageView web_image = (ImageView)findViewById(R.id.sixth_effect);
        web_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent web_intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://ssma.strikingly.com"));
                startActivity(web_intent);
            }
        });

        // 텍스트를 통한 Intent 이동
        TextView academy_textview = (TextView)findViewById(R.id.academy_noti);
        academy_textview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent notification = new Intent(view.getContext(), AcademyNotificationClass.class);
                startActivity(notification);
            }
        });

        // 두 번째 텍스트 통한 Intent 이동
        TextView academy_events_textview = (TextView)findViewById(R.id.academy_event_textview);
        academy_events_textview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent notification = new Intent(view.getContext(), AcademyEventsActivity.class);
                startActivity(notification);
            }
        });

        // 여섯번째 텍스트 활용
        TextView web_text = (TextView)findViewById(R.id.textView6);
        web_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent web_intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://ssma.strikingly.com"));
                startActivity(web_intent);
            }
        });

        // 상태 바 제거
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // 내비게이션 뷰 내 이메일 주소를 넣어봅시다.
        TextView navigaionview_emailaddress = (TextView)findViewById(R.id.email_address);
        navigaionview_emailaddress.setText(getIntent().getStringExtra("id") + " 반갑습니다!");

        // 툴바 생성 및 액션바에 툴바 입력
        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // 툴바에 프로젝트 이름 제거
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // 툴바내의 이메일 버튼을 누를 경우 서버 연결이 필요하다는 메시지를 출력합니다.
        toolbar.findViewById(R.id.toolbar_email).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(SecondActivity.this, "서버와의 연결이 필요합니다.", Toast.LENGTH_SHORT).show();
            }
        });

        // 새로운 drawerlayout을 생성 및 연결합니다.
        final DrawerLayout drawerLayout = (DrawerLayout)findViewById(R.id.second_drawerlayout);

        // 툴바에 내비게이션 토글을 생성합니다.
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, 0, 0);

        // 토글을 입력한 뒤 동기화합니다.
        drawerLayout.setDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        // 내비게이션 바를 생성합니다.
        final NavigationView navigationView = (NavigationView)findViewById(R.id.second_navigationview);

        // 내비게이션 바 내 이메일 주소를 입력하면 메시지를 출력합니다.
        navigationView.findViewById(R.id.email_address).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(SecondActivity.this, "서버와의 연결이 필요합니다.", Toast.LENGTH_SHORT).show();
            }
        });

        // 내비게이션 바 내 프레그먼트 전환입니다.
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                if (item.getItemId() == R.id.first_peoples) {
                    Intent first_peoples = new Intent(navigationView.getContext(), FirstPeoples.class);
                    startActivity(first_peoples);
                }

                if (item.getItemId() == R.id.second_peoples) {
                    Intent second_peoples = new Intent(navigationView.getContext(), Secondpeoples.class);
                    startActivity(second_peoples);                }

                if (item.getItemId() == R.id.third_peoples) {
                    Toast.makeText(SecondActivity.this, "개발중입니다.", Toast.LENGTH_SHORT).show();
                }

                if (item.getItemId() == R.id.fourth_peoples) {
                    Toast.makeText(SecondActivity.this, "개발중입니다.", Toast.LENGTH_SHORT).show();
                }

                if (item.getItemId() == R.id.fifth_peoples) {
                    Toast.makeText(SecondActivity.this, "개발중입니다.", Toast.LENGTH_SHORT).show();
                }

                if (item.getItemId() == R.id.fun_activity) {
                    Intent fun_intent = new Intent(navigationView.getContext(), FunActivity.class);
                    startActivity(fun_intent);
                }

                if (item.getItemId() == R.id.developr_information) {
                    Intent developer_intent = new Intent(navigationView.getContext(), developer_information.class);
                    startActivity(developer_intent);
                }
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });

        // 하단 부 프레그먼트 생성(둘러싼 레이아웃이 LinearLayout)
        LinearLayout professor_button1 = (LinearLayout)findViewById(R.id.button1);
        LinearLayout vision_button2 = (LinearLayout)findViewById(R.id.button2);
        LinearLayout mission_button3 = (LinearLayout)findViewById(R.id.button3);
        LinearLayout curriculum_button4 = (LinearLayout)findViewById(R.id.button4);
        LinearLayout googlemap_button5 = (LinearLayout)findViewById(R.id.button5);

        // 각 레이아웃 뷰를 누르면 FramgLayout을 각 프레그먼트로 바꿈(애니메이션 효과도 줌)
        professor_button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().beginTransaction().replace(R.id.main_frame, new ProfessorFragment()).addToBackStack(ProfessorFragment.class.getName()).commit();
            }
        });

        vision_button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().beginTransaction().replace(R.id.main_frame, new VisionFragment()).addToBackStack(VisionFragment.class.getName()).commit();
            }
        });

        mission_button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().beginTransaction().replace(R.id.main_frame, new MissionFragment()).addToBackStack(MissionFragment.class.getName()).commit();
            }
        });

        curriculum_button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().beginTransaction().replace(R.id.main_frame, new CurriculumFragment()).addToBackStack(CurriculumFragment.class.getName()).commit();
            }
        });

        googlemap_button5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), googleMapActivity.class);
                startActivity(intent);
            }
        });

        // 로그아웃 버튼 활성화
        final Button logout_button = (Button)findViewById(R.id.logout_button);
        logout_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent logout_intent = new Intent();

                // 로그아웃 값을 입력합니다.
                setResult(20, logout_intent);
                finish();
            }
        });

        /*
        // 사진 촬영 버튼
        final Button permission_button = (Button)findViewById(R.id.permission_button);
        permission_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent permission_intent = new Intent(view.getContext(), ThirdActivity.class);
                startActivity(permission_intent);
            }
        });*/

        // 푸시 알람 버튼 불러오기
        /*
        Button alarm_button = (Button)findViewById(R.id.push_alarm);
        alarm_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent alarm_intent = new Intent(view.getContext(), FourthActivity.class);
                startActivity(alarm_intent);
            }
        });*/

        // 구글 맵 버튼 불러오기
        /*Button google_map_button = (Button)findViewById(R.id.google_map_button);
        google_map_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent google_map_intent = new Intent(view.getContext(), googleMapActivity.class);
                startActivity(google_map_intent);
            }
        });*/
    }
}
