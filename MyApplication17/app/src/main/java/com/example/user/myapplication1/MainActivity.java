package com.example.user.myapplication1;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.myapplication1.Fragments.MainFragment1;
import com.example.user.myapplication1.Fragments.MainFragment2;
import com.example.user.myapplication1.Fragments.MainFragment3;
import com.example.user.myapplication1.Fragments.MainFragment4;
import com.example.user.myapplication1.Fragments.MainFragment5;
import com.example.user.myapplication1.SidebarFragments.Fragment1;
import com.example.user.myapplication1.SidebarFragments.Fragment2;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final RelativeLayout fragment1 = (RelativeLayout) findViewById(R.id.main_fragment1);
        RelativeLayout fragment2 = (RelativeLayout) findViewById(R.id.main_fragment2);
        RelativeLayout fragment3 = (RelativeLayout) findViewById(R.id.main_fragment3);
        RelativeLayout fragment4 = (RelativeLayout) findViewById(R.id.main_fragment4);
        RelativeLayout fragment5 = (RelativeLayout) findViewById(R.id.main_fragment5);

        fragment1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().beginTransaction().setCustomAnimations(R.animator.show_enter_anim, R.animator.show_exit_anim).replace(R.id.main_frame, new MainFragment1()).addToBackStack(MainFragment1.class.getName()).commit();
            }
        });

        fragment2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().beginTransaction().setCustomAnimations(R.animator.filp_enter_anim, R.animator.filp_exit_anim).replace(R.id.main_frame, new MainFragment2()).addToBackStack(MainFragment2.class.getName()).commit();
            }
        });

        fragment3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().beginTransaction().setCustomAnimations(R.animator.zoom_enter_anim, R.animator.zoom_exit_anim).replace(R.id.main_frame, new MainFragment3()).addToBackStack(MainFragment3.class.getName()).commit();
            }
        });

        fragment4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().beginTransaction().setCustomAnimations(R.animator.filp_enter_anim, R.animator.filp_exit_anim).replace(R.id.main_frame, new MainFragment4()).addToBackStack(MainFragment4.class.getName()).commit();
            }
        });

        fragment5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().beginTransaction().setCustomAnimations(R.animator.show_enter_anim, R.animator.show_exit_anim).replace(R.id.main_frame, new MainFragment5()).addToBackStack(MainFragment5.class.getName()).commit();
            }
        });

        // 툴바 불러오기
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // 프로젝트 이름 삭제
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        toolbar.findViewById(R.id.toolbar_email).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "서버 연결이 필요합니다.", Toast.LENGTH_SHORT).show();
            }
        });

        // 툴바 및 사이드 바 호출
       final DrawerLayout drawerLayout = (DrawerLayout)findViewById(R.id.main_drawerlayout);

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, 0, 0);
        drawerLayout.setDrawerListener(actionBarDrawerToggle);

        actionBarDrawerToggle.syncState();

        // 사이드 바 옆 내비게이션 뷰 호출
        NavigationView navigationView = (NavigationView)findViewById(R.id.main_navigationview);

        // 내비게이션 내 아이템을 눌렀을 때 반응하게 하기 위함
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                // 내비게이션 바 내 아이템을 눌렀을 시 아이템을 없애는 코드
                drawerLayout.closeDrawer(GravityCompat.START);

                if(item.getItemId() == R.id.first_sidebar_item) {
                    getFragmentManager().beginTransaction().replace(R.id.sidebar_frame, new Fragment1()).commit();
                }

                if(item.getItemId() == R.id.second_sidebar_item) {
                    getFragmentManager().beginTransaction().replace(R.id.sidebar_frame, new Fragment2()).commit();
                }
                return true;
            }
        });

        final Button login_button = (Button)findViewById(R.id.login_button);
        final EditText id_checker = (EditText)findViewById(R.id.input_id);
        final EditText password_checker = (EditText)findViewById(R.id.input_password);

        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (id_checker.getText().toString().equals("asd") && password_checker.getText().toString().equals("1")) {
                    Toast.makeText(MainActivity.this, "로그인에 성공하였습니다", Toast.LENGTH_SHORT).show();
                    Intent login_intent = new Intent(view.getContext(), LoginActivity.class);
                    login_intent.putExtra("id",id_checker.getText().toString() + "님");
                    // 요청 값이 10이 되고, 10에 맞는 특정 하드코드를 작성하겠습니다.
                    startActivityForResult(login_intent, 10);
                }
                else {
                    Toast.makeText(MainActivity.this, "로그인에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // 요청 값이 10 - 로그인, 결과 값이 20 - 로그 아웃일 경우에만 작동합니다.
        // 즉, 정상적으로 로그인하고 로그아웃 했을 시 user_name이 표시됩니다.
        if (requestCode == 10 && resultCode == 20) {
            TextView logout_user = (TextView)findViewById(R.id.logout_user);
            logout_user.setText(data.getStringExtra("logout_uesr"));
        }
    }
}
