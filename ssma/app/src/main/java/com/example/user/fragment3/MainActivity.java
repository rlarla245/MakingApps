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
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.user.fragment3.GoogleMapActivities.googleMapActivity;
import com.example.user.fragment3.MainFragments.CurriculumFragment;
import com.example.user.fragment3.MainFragments.MissionFragment;
import com.example.user.fragment3.MainFragments.ProfessorFragment;
import com.example.user.fragment3.MainFragments.VisionFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        // 툴바 연결
        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar)findViewById(R.id.toolbar);

        // 액션바 대신에 toolbar를 설정함(이미 style 파일에서 noActionBar로 설정)
        setSupportActionBar(toolbar);

        // 프로젝트 이름이 툴바 좌측에 보이는데 그것을 지워줌
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // 휴지통 이미지를 누르면 메시지를 발생하게 함
        toolbar.findViewById(R.id.toolbar_email).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "서버에 연결되지 않았습니다.", Toast.LENGTH_SHORT).show();
            }
        });

        // 사이드바 불러오기
        final DrawerLayout drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);

        // 토글 키를 생성
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, 0, 0);

        // 내비게이션 바 내에 토글 키 생성 및 동기화
        drawerLayout.setDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        // 내비게이션 뷰 설정
        final NavigationView navigationView = (NavigationView)findViewById(R.id.main_navigationview);

        // sidebar 내 각 메뉴를 눌렀을 경우 프레그먼트 이동
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.first_peoples) {
                    Toast.makeText(MainActivity.this, "로그인이 필요합니다.", Toast.LENGTH_SHORT).show();
                }

                if (item.getItemId() == R.id.second_peoples) {
                    Toast.makeText(MainActivity.this, "로그인이 필요합니다.", Toast.LENGTH_SHORT).show();
                }

                if (item.getItemId() == R.id.third_peoples) {
                    Toast.makeText(MainActivity.this, "로그인이 필요합니다.", Toast.LENGTH_SHORT).show();
                }

                if (item.getItemId() == R.id.fourth_peoples) {
                    Toast.makeText(MainActivity.this, "로그인이 필요합니다.", Toast.LENGTH_SHORT).show();
                }

                if (item.getItemId() == R.id.fifth_peoples) {
                    Toast.makeText(MainActivity.this, "로그인이 필요합니다.", Toast.LENGTH_SHORT).show();
                }

                if (item.getItemId() == R.id.fun_activity) {
                    Toast.makeText(MainActivity.this, "로그인이 필요합니다.", Toast.LENGTH_SHORT).show();
                }

                if (item.getItemId() == R.id.developr_information) {
                    Toast.makeText(MainActivity.this, "로그인이 필요합니다.", Toast.LENGTH_SHORT).show();
                }
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });

        // 내비게이션 뷰 내 이메일 주소를 클릭하면 연결이 필요하다는 메시지 출력
        navigationView.findViewById(R.id.email_address).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "서버에 연결되지 않았습니다.", Toast.LENGTH_SHORT).show();
            }
        });

        // 아이디, 비밀번호 체크1
        final EditText id = (EditText)findViewById(R.id.id_edit);
        final EditText password = (EditText)findViewById(R.id.password_edit);

        // 상태 바 제거
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // 로그인 버튼 생성
        final Button LoginButton = (Button)findViewById(R.id.login_button);

        // 로그인 합니다.
        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (id.getText().toString().equals("SSMA") || id.getText().toString().equals("ssma") && password.getText().toString().equals("2013")) {
                    // 로그인 버튼을 누를 경우
                    Toast.makeText(MainActivity.this, "로그인에 성공하였습니다.", Toast.LENGTH_SHORT).show();

                    // 다음 화면으로 넘어갑니다
                    Intent intent = new Intent(view.getContext(), SecondActivity.class);
                    intent.putExtra("id", id.getText().toString() + "(관리자)님");

                    // 특정 결과값 코드를 입력합니다.
                    startActivityForResult(intent, 10);
                } else {
                    Toast.makeText(MainActivity.this, "로그인에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // 요청 코드 10, 결과 코드 20일 경우 로그아웃합니다.
        if (requestCode == 10 && resultCode == 20) {
            Toast.makeText(this, "로그아웃 하였습니다.", Toast.LENGTH_SHORT).show();
        }
    }
}
