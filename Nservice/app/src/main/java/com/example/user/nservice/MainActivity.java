package com.example.user.nservice;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.user.nservice.MainFragments.Fragment1;
import com.example.user.nservice.MainFragments.Fragment2;
import com.example.user.nservice.MainFragments.Fragment3;
import com.example.user.nservice.MainFragments.Fragment4;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
                getFragmentManager().beginTransaction().replace(R.id.main_frame, new Fragment1()).addToBackStack(Fragment1.class.getName()).commit();
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().beginTransaction().replace(R.id.main_frame, new Fragment2()).addToBackStack(Fragment2.class.getName()).commit();
            }
        });

        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().beginTransaction().replace(R.id.main_frame, new Fragment3()).addToBackStack(Fragment3.class.getName()).commit();
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

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, 0, 0);

        drawerLayout.setDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        // sidebar에 프레그먼트 설정
        final NavigationView navigationView = (NavigationView)findViewById(R.id.main_navigationview);

        // sidebar 내 각 메뉴를 눌렀을 경우 프레그먼트 이동
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                if (item.getItemId() == R.id.first_item) {
                    Toast.makeText(MainActivity.this, "로그인이 필요합니다.", Toast.LENGTH_SHORT).show();
                }

                if (item.getItemId() == R.id.second_item) {
                    Toast.makeText(MainActivity.this, "로그인이 필요합니다.", Toast.LENGTH_SHORT).show();
                }

                if (item.getItemId() == R.id.second_people) {
                    Toast.makeText(MainActivity.this, "로그인이 필요합니다.", Toast.LENGTH_SHORT).show();
                }


                if (item.getItemId() == R.id.third_item) {
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
        navigationView.findViewById(R.id.email_address).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "서버에 연결되지 않았습니다.", Toast.LENGTH_SHORT).show();
            }
        });
        // 이메일 주소 클릭하면 연결이 필요하다는 메시지 출력

        // 아이디, 비밀번호 체크1
        final EditText id = (EditText)findViewById(R.id.id_edit);
        final EditText password = (EditText)findViewById(R.id.password_edit);

        // 액티비티 변경
        Button activityButton = (Button)findViewById(R.id.login_button);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);


        activityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (id.getText().toString().equals("SSMA") || id.getText().toString().equals("ssma") && password.getText().toString().equals("2013")) {
                    navigationView.findViewById(R.id.email_address).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Toast.makeText(MainActivity.this, "로그인에 성공하였습니다.", Toast.LENGTH_SHORT).show();
                        }
                    });
                    Toast.makeText(MainActivity.this, "로그인에 성공하였습니다.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(view.getContext(), SecondActivity.class);
                    intent.putExtra("id", id.getText().toString() + "(관리자)님");
                    startActivityForResult(intent, 10);
                } else {
                    Toast.makeText(MainActivity.this, "로그인에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 10 && resultCode == 20) {
            Toast.makeText(this, data.getStringExtra("logout_id"), Toast.LENGTH_SHORT).show();
        }
    }
}
