package com.example.user.fragment3;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
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

public class AcademyEventsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.academy_events);

        // 툴바 설정
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar3);

        // 툴바 입력
        setSupportActionBar(toolbar);

        // 프로젝트 이름 삭제
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // 휴지통 이미지를 누르면 메시지를 발생하게 함
        toolbar.findViewById(R.id.toolbar_email).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(AcademyEventsActivity.this, "서버에 연결되지 않았습니다.", Toast.LENGTH_SHORT).show();
            }
        });

        // 사이드바 불러오기
        final DrawerLayout drawerLayout = (DrawerLayout)findViewById(R.id.academy_event_drawerlayout);

        // 토글 키를 생성
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, 0, 0);

        // 내비게이션 바 내에 토글 키 생성 및 동기화
        drawerLayout.setDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        // 내비게이션 뷰 설정
        final NavigationView navigationView = (NavigationView)findViewById(R.id.academy_event_navigationview);

        // sidebar 내 각 메뉴를 눌렀을 경우 프레그먼트 이동
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
                    Toast.makeText(AcademyEventsActivity.this, "개발중입니다.", Toast.LENGTH_SHORT).show();
                }

                if (item.getItemId() == R.id.fourth_peoples) {
                    Toast.makeText(AcademyEventsActivity.this, "개발중입니다.", Toast.LENGTH_SHORT).show();
                }

                if (item.getItemId() == R.id.fifth_peoples) {
                    Toast.makeText(AcademyEventsActivity.this, "개발중입니다.", Toast.LENGTH_SHORT).show();
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

        // 내비게이션 뷰 내 이메일 주소를 클릭하면 연결이 필요하다는 메시지 출력
        navigationView.findViewById(R.id.email_address).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(AcademyEventsActivity.this, "서버에 연결되지 않았습니다.", Toast.LENGTH_SHORT).show();
            }
        });

        // Recyclerview 설정
        RecyclerView view = (RecyclerView)findViewById(R.id.academy_event_recyclerview);
        view.setAdapter(new AcademyEventsAdapter());

        // 그리드 뷰 설정
        view.setLayoutManager(new GridLayoutManager(this, 3));

        // 상태창 제거
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // 툴바 이메일 클릭 시 메시지 출력
        ImageView imageButton = (ImageView)findViewById(R.id.toolbar_email);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(AcademyEventsActivity.this, "서버 연결이 필요합니다.", Toast.LENGTH_SHORT).show();
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

        // 내비게이션 뷰 내 이메일 주소를 넣어봅시다.
        TextView navigaionview_emailaddress = (TextView)findViewById(R.id.email_address);
        navigaionview_emailaddress.setText("반갑습니다, SSMA입니다");
    }
}
