package com.example.user.fragment3;

import android.content.Intent;
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
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.user.fragment3.GoogleMapActivities.googleMapActivity;
import com.example.user.fragment3.MainFragments.CurriculumFragment;
import com.example.user.fragment3.MainFragments.MissionFragment;
import com.example.user.fragment3.MainFragments.ProfessorFragment;
import com.example.user.fragment3.MainFragments.VisionFragment;

public class NotificationResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        // 툴바 설정 및 액션바, 프로젝트 이름 제거
        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // 이메일 버튼 누를 시 Toast 기능 활용
        toolbar.findViewById(R.id.toolbar_email).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(NotificationResultActivity.this, "서버와의 연결이 필요합니다.", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(NotificationResultActivity.this, "서버와의 연결이 필요합니다.", Toast.LENGTH_SHORT).show();
            }
        });

        // 각 내비게이션 프레그먼트 마다 레이아웃 설정 - addToBackStavk(이름.class.getName() 주의!
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                if (item.getItemId() == R.id.notification) {
                    Toast.makeText(NotificationResultActivity.this, "로그인이 필요합니다.", Toast.LENGTH_SHORT).show();
                }

                if (item.getItemId() == R.id.academy_event) {
                    Toast.makeText(NotificationResultActivity.this, "로그인이 필요합니다.", Toast.LENGTH_SHORT).show();
                }

                if (item.getItemId() == R.id.first_peoples) {
                    Toast.makeText(NotificationResultActivity.this, "로그인이 필요합니다.", Toast.LENGTH_SHORT).show();
                }

                if (item.getItemId() == R.id.second_peoples) {
                    Toast.makeText(NotificationResultActivity.this, "로그인이 필요합니다.", Toast.LENGTH_SHORT).show();
                }

                if (item.getItemId() == R.id.third_peoples) {
                    Toast.makeText(NotificationResultActivity.this, "로그인이 필요합니다.", Toast.LENGTH_SHORT).show();
                }

                if (item.getItemId() == R.id.fourth_peoples) {
                    Toast.makeText(NotificationResultActivity.this, "로그인이 필요합니다.", Toast.LENGTH_SHORT).show();
                }

                if (item.getItemId() == R.id.fifth_peoples) {
                    Toast.makeText(NotificationResultActivity.this, "로그인이 필요합니다.", Toast.LENGTH_SHORT).show();
                }

                if (item.getItemId() == R.id.fun_activity) {
                    Toast.makeText(NotificationResultActivity.this, "로그인이 필요합니다.", Toast.LENGTH_SHORT).show();
                }

                if (item.getItemId() == R.id.developr_information) {
                    Toast.makeText(NotificationResultActivity.this, "로그인이 필요합니다.", Toast.LENGTH_SHORT).show();
                }

                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });

        // 내비게이션 뷰에 있는 이메일 버튼 누를 시 Toast 활성화
        navigationView.findViewById(R.id.email_address).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(NotificationResultActivity.this, "서버와의 연결이 필요합니다.", Toast.LENGTH_SHORT).show();
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
    }
}
