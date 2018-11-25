package com.ssma.serverapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.ssma.serverapp.Fragments.AccountFragment;
import com.ssma.serverapp.Fragments.ChatFragment;
import com.ssma.serverapp.Fragments.PeoplesFragment;
import com.ssma.serverapp.NavigationViewFragments.SidebarFifthMembers;
import com.ssma.serverapp.NavigationViewFragments.SidebarFirstMembers;
import com.ssma.serverapp.NavigationViewFragments.SidebarFourthMembers;
import com.ssma.serverapp.NavigationViewFragments.SidebarSecondMembers;
import com.ssma.serverapp.NavigationViewFragments.SidebarSixthMembers;
import com.ssma.serverapp.NavigationViewFragments.SidebarThirdMembers;

import java.util.HashMap;
import java.util.Map;

public class FirstActivity extends AppCompatActivity {
    // UI 변수들 생성
    public ImageButton emailIcon;
    public TextView emailAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_first_activity);

        // 변수 생성
        emailIcon = (ImageButton) findViewById(R.id.firstActivityrToolbarEmailIcon);
        emailAddress = (TextView) findViewById(R.id.firstActivity_sidebar_emailaddress);

        // 상태 창 제거
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // 이메일 아이콘 삽입
        emailIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(FirstActivity.this, "Firebase 연동 중 입니다.", Toast.LENGTH_SHORT).show();
            }
        });

        // 툴바 불러오기
        Toolbar toolbar = findViewById(R.id.nonMemberToolbar);
        setSupportActionBar(toolbar);

        // 프로젝트 명 삭제
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Drawer 레이아웃 호출
        final DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.firstactivity_drawerlayout);

        // 토글 기능 불러오기
        ActionBarDrawerToggle actionBarDrawerToggle =
                new ActionBarDrawerToggle(this, drawerLayout, toolbar, 0, 0);

        // 설정한 토글 값 입력시켜주고 동기화시킵니다.
        drawerLayout.setDrawerListener(actionBarDrawerToggle);

        actionBarDrawerToggle.syncState();

        // 좌측 내비게이션 바 불러오기
        final NavigationView navigationView = (NavigationView) findViewById(R.id.firstActivity_firstlayout_navigationview);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                drawerLayout.closeDrawer(GravityCompat.START);

                // 특정 item 클릭 시 fragment 실행 코드)
                if (item.getItemId() == R.id.firstactivity_first_sidebar_item) {
                    getFragmentManager().beginTransaction().
                            replace(R.id.firstActivity_FirstLayout_main_frame, new SidebarFirstMembers()).addToBackStack(SidebarFirstMembers.class.getName()).commit();
                }

                if (item.getItemId() == R.id.firstactivity_second_sidebar_item) {
                    getFragmentManager().beginTransaction().
                            replace(R.id.firstActivity_FirstLayout_main_frame, new SidebarSecondMembers()).addToBackStack(SidebarSecondMembers.class.getName()).commit();
                }

                if (item.getItemId() == R.id.firstactivity_third_sidebar_item) {
                    getFragmentManager().beginTransaction().
                            replace(R.id.firstActivity_FirstLayout_main_frame, new SidebarThirdMembers()).addToBackStack(SidebarThirdMembers.class.getName()).commit();
                }

                if (item.getItemId() == R.id.firstactivity_fourth_sidebar_item) {
                    getFragmentManager().beginTransaction().
                            replace(R.id.firstActivity_FirstLayout_main_frame, new SidebarFourthMembers()).addToBackStack(SidebarFourthMembers.class.getName()).commit();
                }

                if (item.getItemId() == R.id.firstactivity_fifth_sidebar_item) {
                    getFragmentManager().beginTransaction().
                            replace(R.id.firstActivity_FirstLayout_main_frame, new SidebarFifthMembers()).addToBackStack(SidebarFifthMembers.class.getName()).commit();
                }

                if (item.getItemId() == R.id.firstactivity_sixth_sidebar_item) {
                    getFragmentManager().beginTransaction().
                            replace(R.id.firstActivity_FirstLayout_main_frame, new SidebarSixthMembers()).addToBackStack(SidebarSixthMembers.class.getName()).commit();
                }

                if (item.getItemId() == R.id.firstactivity_seventh_sidebar_item) {
                    Intent website_intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://ssma.strikingly.com"));
                    startActivity(website_intent);
                }
                return true;
            }
        });

        // 메인 화면 조정
        // peoples 프레그먼트로 자동 전환됩니다.
        getFragmentManager().beginTransaction().replace(R.id.firstActivity_FirstLayout_main_frame, new PeoplesFragment()).commit();

        // Bottom NavigationView 호출 및 설정
        final BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.firstLayout_bottomnavigationview);

        // 하단의 내비게이션바를 누를 경우
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.firstLayout_Chat:
                        getFragmentManager().beginTransaction().replace(R.id.firstActivity_FirstLayout_main_frame, new ChatFragment()).addToBackStack(ChatFragment.class.getName()).commit();
                        return true;

                    case R.id.firstLayout_Account:
                        getFragmentManager().beginTransaction().replace(R.id.firstActivity_FirstLayout_main_frame, new AccountFragment()).addToBackStack(AccountFragment.class.getName()).commit();
                        return true;

                    case R.id.firstLayout_Board:
                        Toast.makeText(FirstActivity.this, "개발 중 입니다.", Toast.LENGTH_SHORT).show();
                        return true;
                }
                return false;
            }
        });

        if (FirebaseAuth.getInstance().getCurrentUser().getUid() != null) {
            // 현재 로그인한 유저의 토큰 값을 서버에 적용합니다.
            passPushTokenToServer();
        }
    }

    void passPushTokenToServer() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String token = FirebaseInstanceId.getInstance().getToken();

        // 키 - 문자열, value - 실제 토큰을 집어 넣습니다.
        Map<String, Object> map = new HashMap<>();
        map.put("pushToken", token);

        // 해당 유저의 uid의 하위 데이터로 토큰 값을 입력합니다.
        FirebaseDatabase.getInstance().getReference().child("users").child(uid).updateChildren(map);
    }

    // 액티비티가 띄워질 경우 자동으로 실행하는 메소드입니다.
    @Override
    protected void onStart() {
        super.onStart();
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            finish();
        }
    }
}