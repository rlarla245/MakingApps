package com.ssma.serverapp.NonMembers;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
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

import com.ssma.serverapp.NonMemberBottomNavigationViewFragments.ButtomCurriculumFragment;
import com.ssma.serverapp.NonMemberBottomNavigationViewFragments.ButtomMissionFragment;
import com.ssma.serverapp.NonMemberBottomNavigationViewFragments.ButtomProfessorFragment;
import com.ssma.serverapp.NonMemberBottomNavigationViewFragments.ButtomVisionFragment;
import com.ssma.serverapp.NonMemberGoogleMap.NonMemberGoogleMap;
import com.ssma.serverapp.NonMemberSidebarFragments.SidebarFifthFragment;
import com.ssma.serverapp.NonMemberSidebarFragments.SidebarFirstFragment;
import com.ssma.serverapp.NonMemberSidebarFragments.SidebarFourthFragment;
import com.ssma.serverapp.NonMemberSidebarFragments.SidebarSecondFragment;
import com.ssma.serverapp.NonMemberSidebarFragments.SidebarSixthFragment;
import com.ssma.serverapp.NonMemberSidebarFragments.SidebarThirdFragment;
import com.ssma.serverapp.R;

public class NonMemberFirstActivity extends AppCompatActivity {
    
    public ImageButton emailIcon;
    public TextView emailaddress;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nonmember_firstlayout);

        // 변수 생성
        emailIcon = (ImageButton)findViewById(R.id.firstActivityrToolbarEmailIcon);
        emailaddress = (TextView)findViewById(R.id.nonmember_sidebar_emailaddress);

        // 상태 창 제거
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // 이메일 아이콘 삽입
        emailIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(NonMemberFirstActivity.this, "로그인이 필요합니다.", Toast.LENGTH_SHORT).show();
            }
        });

        // 툴바 불러오기
        Toolbar toolbar = (Toolbar)findViewById(R.id.nonMemberToolbar);
        setSupportActionBar(toolbar);

        // 프로젝트 명 삭제
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Drawer 레이아웃 호출
        final DrawerLayout drawerLayout = (DrawerLayout)findViewById(R.id.nonmber_firstlayout_drawerlayout);

        // 토글 기능 불러오기
        ActionBarDrawerToggle actionBarDrawerToggle =
                new ActionBarDrawerToggle(this, drawerLayout, toolbar, 0, 0);

        // 설정한 토글 값 입력시켜주고 동기화시킵니다.
        drawerLayout.setDrawerListener(actionBarDrawerToggle);

        actionBarDrawerToggle.syncState();

        // 내비게이션 바 불러오기
        final NavigationView navigationView = (NavigationView)findViewById(R.id.nonmber_firstlayout_navigationview);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                drawerLayout.closeDrawer(GravityCompat.START);

                // 특정 item 클릭 시 fragment 실행 코드)
                if (item.getItemId() == R.id.nonmember_first_sidebar_item) {
                    getFragmentManager().beginTransaction().
                            replace(R.id.nonMember_FirstLayout_main_frame, new SidebarFirstFragment()).addToBackStack(SidebarFirstFragment.class.getName()).commit();
                }

                if (item.getItemId() == R.id.nonmember_second_sidebar_item) {
                    getFragmentManager().beginTransaction().
                            replace(R.id.nonMember_FirstLayout_main_frame, new SidebarSecondFragment()).addToBackStack(SidebarSecondFragment.class.getName()).commit();
                }

                if (item.getItemId() == R.id.nonmember_third_sidebar_item) {
                    getFragmentManager().beginTransaction().
                            replace(R.id.nonMember_FirstLayout_main_frame, new SidebarThirdFragment()).addToBackStack(SidebarThirdFragment.class.getName()).commit();
                }

                if (item.getItemId() == R.id.nonmember_fourth_sidebar_item) {
                    getFragmentManager().beginTransaction().
                            replace(R.id.nonMember_FirstLayout_main_frame, new SidebarFourthFragment()).addToBackStack(SidebarFourthFragment.class.getName()).commit();
                }

                if (item.getItemId() == R.id.nonmember_fifth_sidebar_item) {
                    getFragmentManager().beginTransaction().
                            replace(R.id.nonMember_FirstLayout_main_frame, new SidebarFifthFragment()).addToBackStack(SidebarFifthFragment.class.getName()).commit();
                }

                if (item.getItemId() == R.id.nonmember_sixth_sidebar_item) {
                    getFragmentManager().beginTransaction().
                            replace(R.id.nonMember_FirstLayout_main_frame, new SidebarSixthFragment()).addToBackStack(SidebarSixthFragment.class.getName()).commit();
                }

                if (item.getItemId() == R.id.nonmember_seventh_sidebar_item) {
                    Intent website_intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://ssma.strikingly.com"));
                    startActivity(website_intent);
                }
                return true;
            }
        });

        // Bottom NavigationView 호출 및 설정
        final BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.nonMember_FirstLayout_bottomnavigationview);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nonMember_FirstLayout_Professor:
                        getFragmentManager().beginTransaction().replace(R.id.nonMember_FirstLayout_main_frame, new ButtomProfessorFragment()).addToBackStack(ButtomProfessorFragment.class.getName()).commit();
                        return true;

                    case R.id.nonMember_FirstLayout_Vision:
                        getFragmentManager().beginTransaction().replace(R.id.nonMember_FirstLayout_main_frame, new ButtomVisionFragment()).addToBackStack(ButtomVisionFragment.class.getName()).commit();
                        return true;

                    case R.id.nonMember_FirstLayout_Mission:
                        getFragmentManager().beginTransaction().replace(R.id.nonMember_FirstLayout_main_frame, new ButtomMissionFragment()).addToBackStack(ButtomMissionFragment.class.getName()).commit();
                        return true;

                    case R.id.nonMember_FirstLayout_Curriculum:
                        getFragmentManager().beginTransaction().replace(R.id.nonMember_FirstLayout_main_frame, new ButtomCurriculumFragment()).addToBackStack(ButtomCurriculumFragment.class.getName()).commit();
                        return true;

                    case R.id.nonMember_FirstLayout_Location:
                        Intent googlemap_intent = new Intent(bottomNavigationView.getContext(), NonMemberGoogleMap.class);
                        startActivity(googlemap_intent);
                        return true;
                }
                return false;
            }
        });

        /* 이메일 주소 입력하기
        emailaddress.setText(LoginActivity.emailTextview.getText().toString());
        */
    }
}
