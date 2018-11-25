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
import android.widget.LinearLayout;
import android.widget.TextView;
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

public class SecondActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        final TextView textView = (TextView)findViewById(R.id.user_name);
        textView.setText(getIntent().getStringExtra("id"));

        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        
        toolbar.findViewById(R.id.toolbar_email).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(SecondActivity.this, "서버와의 연결이 필요합니다.", Toast.LENGTH_SHORT).show();
            }
        });

        final DrawerLayout drawerLayout = (DrawerLayout)findViewById(R.id.second_drawerlayout);

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, 0, 0);

        drawerLayout.setDrawerListener(actionBarDrawerToggle);

        actionBarDrawerToggle.syncState();

        final NavigationView navigationView = (NavigationView)findViewById(R.id.second_navigationview);

        navigationView.findViewById(R.id.email_address).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(SecondActivity.this, "서버와의 연결이 필요합니다.", Toast.LENGTH_SHORT).show();
            }
        });

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
        navigationView.findViewById(R.id.email_address).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(SecondActivity.this, "서버와의 연결이 필요합니다.", Toast.LENGTH_SHORT).show();
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

        // 로그아웃 버튼 활성화
        final Button logout_button = (Button)findViewById(R.id.logout_button);
        logout_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent logout_intent = new Intent();
                logout_intent.putExtra("logout_id", textView.getText().toString() + "님이 로그아웃 하셨습니다.");
                setResult(20, logout_intent);
                finish();
            }
        });

        final Button permission_button = (Button)findViewById(R.id.permission_button);
        permission_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent permission_intent = new Intent(view.getContext(), ThirdActivity.class);
                startActivity(permission_intent);
            }
        });

        // 푸시 알람 버튼 불러오기
        Button alarm_button = (Button)findViewById(R.id.push_alarm);
        alarm_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent alarm_intent = new Intent(view.getContext(), FourthActivity.class);
                startActivity(alarm_intent);
            }
        });

        // 구글 맵 버튼 불러오기
        Button google_map_button = (Button)findViewById(R.id.google_map_button);
        google_map_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent google_map_intent = new Intent(view.getContext(), googleMapActivity.class);
                startActivity(google_map_intent);
            }
        });
    }
}
