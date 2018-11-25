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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.myapplication1.MainFragments.MainFragment1;
import com.example.user.myapplication1.MainFragments.MainFragment2;
import com.example.user.myapplication1.MainFragments.MainFragment3;
import com.example.user.myapplication1.MainFragments.MainFragment4;
import com.example.user.myapplication1.MainFragments.MainFragment5;
import com.example.user.myapplication1.SidebarFragments.SidebarFragment1;
import com.example.user.myapplication1.SidebarFragments.SidebarFragment2;
import com.example.user.myapplication1.SidebarFragments.SidebarFragment3;
import com.example.user.myapplication1.SidebarFragments.SidebarFragment4;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RelativeLayout fragment_button1 = (RelativeLayout) findViewById(R.id.main_fragment1);
        RelativeLayout fragment_button2 = (RelativeLayout) findViewById(R.id.main_fragment2);
        RelativeLayout fragment_button3 = (RelativeLayout) findViewById(R.id.main_fragment3);
        RelativeLayout fragment_button4 = (RelativeLayout) findViewById(R.id.main_fragment4);
        RelativeLayout fragment_button5 = (RelativeLayout) findViewById(R.id.main_fragment5);

        fragment_button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().beginTransaction().setCustomAnimations(R.animator.showeffect_enter_anim, R.animator.showeffect_exit_anim).replace(R.id.main_frame, new MainFragment1()).addToBackStack(MainFragment1.class.getName()).commit();
            }
        });

        fragment_button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().beginTransaction().setCustomAnimations(R.animator.filpeffect_enter_anim, R.animator.filpeffect_exit_anim).replace(R.id.main_frame, new MainFragment2()).addToBackStack(MainFragment2.class.getName()).commit();
            }
        });

        fragment_button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().beginTransaction().setCustomAnimations(R.animator.zoomeffect_enter_anim, R.animator.zoomeffect_exit_anim).replace(R.id.main_frame, new MainFragment3()).addToBackStack(MainFragment3.class.getName()).commit();
            }
        });

        fragment_button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().beginTransaction().setCustomAnimations(R.animator.filpeffect_enter_anim, R.animator.filpeffect_exit_anim).replace(R.id.main_frame, new MainFragment4()).addToBackStack(MainFragment4.class.getName()).commit();
            }
        });

        fragment_button5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().beginTransaction().setCustomAnimations(R.animator.showeffect_enter_anim, R.animator.showeffect_exit_anim).replace(R.id.main_frame, new MainFragment5()).addToBackStack(MainFragment5.class.getName()).commit();
            }
        });

        // 툴바 생성
        Toolbar toolbar = (Toolbar)findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);

        toolbar.findViewById(R.id.toolbar_email).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "서버 접속이 필요합니다.", Toast.LENGTH_SHORT).show();
            }
        });

        // 사이드바 설정
        final DrawerLayout drawerLayout = (DrawerLayout)findViewById(R.id.main_drawerlayout);

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, 0, 0);
        drawerLayout.setDrawerListener(actionBarDrawerToggle);

        actionBarDrawerToggle.syncState();

        // 내비게이션 뷰 호출
        NavigationView navigationView = (NavigationView)findViewById(R.id.main_navigationview);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                drawerLayout.closeDrawer(GravityCompat.START);

                if (item.getItemId() == R.id.first_sidebar_item) {
                    getFragmentManager().beginTransaction().replace(R.id.main_frame, new SidebarFragment1()).addToBackStack(SidebarFragment1.class.getName()).commit();
                }

                if (item.getItemId() == R.id.second_sidebar_item) {
                    getFragmentManager().beginTransaction().replace(R.id.main_frame, new SidebarFragment2()).addToBackStack(SidebarFragment2.class.getName()).commit();
                }

                if (item.getItemId() == R.id.third_sidebar_item) {
                    getFragmentManager().beginTransaction().replace(R.id.main_frame, new SidebarFragment3()).addToBackStack(SidebarFragment3.class.getName()).commit();
                }

                if (item.getItemId() == R.id.picture_of_academy) {
                    getFragmentManager().beginTransaction().replace(R.id.main_frame, new SidebarFragment4()).addToBackStack(SidebarFragment4.class.getName()).commit();
                }
                return true;
            }
        });
        
        final Button login_button = (Button)findViewById(R.id.login_button);
        final EditText id_check = (EditText)findViewById(R.id.input_id);
        final EditText password_check = (EditText)findViewById(R.id.input_password);
        
        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (id_check.getText().toString().equals("asd") && password_check.getText().toString().equals("1")) {
                    Toast.makeText(MainActivity.this, "로그인에 성공하셨습니다.", Toast.LENGTH_SHORT).show();
                    Intent login_intent = new Intent(view.getContext(), LoginActivity.class);
                    login_intent.putExtra("id", id_check.getText().toString() + "님");
                    startActivityForResult(login_intent, 10);
                }
                
                if (id_check.getText().toString().equals("asd") && !password_check.getText().toString().equals("1")) {
                    Toast.makeText(MainActivity.this, "비밀번호가 맞지 않습니다.", Toast.LENGTH_SHORT).show();
                }

                if (!id_check.getText().toString().equals("asd") && password_check.getText().toString().equals("1")) {
                    Toast.makeText(MainActivity.this, "아이디가 맞지 않습니다.", Toast.LENGTH_SHORT).show();
                }
                
                if (!id_check.getText().toString().equals("asd") && !password_check.getText().toString().equals("1")) {
                    Toast.makeText(MainActivity.this, "로그인에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 10 && resultCode == 20) {
            TextView logout_user_name = (TextView)findViewById(R.id.logout_user_name);
            logout_user_name.setText(data.getStringExtra("logout_user"));
        }
    }
}
