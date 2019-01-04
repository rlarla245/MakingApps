package com.du.chattingapp;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.du.chattingapp.Chat.GroupMessageActivity;
import com.du.chattingapp.Chat.MessageActivity;
import com.du.chattingapp.Fragments.AccountFragment;
import com.du.chattingapp.Fragments.BoardFragment;
import com.du.chattingapp.Fragments.ChatFragment;
import com.du.chattingapp.Fragments.PeoplesFragment;
import com.du.chattingapp.Sidebars.SidebarFifthMembers;
import com.du.chattingapp.Sidebars.SidebarFirstMembers;
import com.du.chattingapp.Sidebars.SidebarFourthMembers;
import com.du.chattingapp.Sidebars.SidebarSecondMembers;
import com.du.chattingapp.Sidebars.SidebarSixthMembers;
import com.du.chattingapp.Sidebars.SidebarThirdMembers;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class FirstActivity extends AppCompatActivity {
    // Firebase 계정 변수 생성
    FirebaseAuth auth;

    // 변수 설정
    public ImageButton emailIcon;
    public TextView emailaddress;

    // 이제 MessageActivity로 넘깁시다.
    String destinationUid;

    // 이제 GroupMessageActivity로 넘깁시다.
    String destinationRoomUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);

        destinationUid = getIntent().getStringExtra("destinationUid");
        destinationRoomUid = getIntent().getStringExtra("destinationRoomUid");

        // 변수 불러오고 로그아웃
        auth = FirebaseAuth.getInstance();
        // auth.signOut();

        // 변수 생성
        emailIcon = (ImageButton)findViewById(R.id.firstActivityrToolbarEmailIcon);
        emailaddress = (TextView)findViewById(R.id.firstActivity_sidebar_emailaddress);

        // 상태 창 제거
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // 이메일 아이콘 삽입
        emailIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(FirstActivity.this, "Firebase 연동 중 입니다.", Toast.LENGTH_SHORT).show();
            }
        });

        // 툴바 불러오기 + 아이디 변경 필요
        Toolbar toolbar = (Toolbar)findViewById(R.id.nonMemberToolbar);
        setSupportActionBar(toolbar);

        // 프로젝트 명 삭제
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Drawer 레이아웃 호출
        final DrawerLayout drawerLayout = (DrawerLayout)findViewById(R.id.firstActivity_drawerLayout);

        // 토글 기능 불러오기
        ActionBarDrawerToggle actionBarDrawerToggle =
                new ActionBarDrawerToggle(this, drawerLayout, toolbar, 0, 0);

        // 설정한 토글 값 입력시켜주고 동기화시킵니다.
        drawerLayout.setDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        // 내비게이션 바 불러오기
        final NavigationView navigationView = (NavigationView)findViewById(R.id.firstActivity_firstLayout_navigationView);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                drawerLayout.closeDrawer(GravityCompat.START);

                // TODO
                // 특정 item 클릭 시 fragment 실행 코드 - 1기
                if (item.getItemId() == R.id.firstActivity_first_sideBar_item) {
                    getFragmentManager().beginTransaction().
                            replace(R.id.firstActivity_FirstLayout_mainFrame, new SidebarFirstMembers())
                            .addToBackStack(SidebarFirstMembers.class.getName()).commit();
                }

                // TODO
                // 2기
                if (item.getItemId() == R.id.firstActivity_second_sideBar_item) {
                    getFragmentManager().beginTransaction().
                            replace(R.id.firstActivity_FirstLayout_mainFrame, new SidebarSecondMembers())
                            .addToBackStack(SidebarSecondMembers.class.getName()).commit();
                }

                // TODO
                // 3기
                if (item.getItemId() == R.id.firstActivity_third_sideBar_item) {
                    getFragmentManager().beginTransaction().
                            replace(R.id.firstActivity_FirstLayout_mainFrame, new SidebarThirdMembers())
                            .addToBackStack(SidebarThirdMembers.class.getName()).commit();
                }

                // TODO
                // 4기
                if (item.getItemId() == R.id.firstActivity_fourth_sideBar_item) {
                    getFragmentManager().beginTransaction().
                            replace(R.id.firstActivity_FirstLayout_mainFrame, new SidebarFourthMembers())
                            .addToBackStack(SidebarFourthMembers.class.getName()).commit();
                }

                // TODO
                // 5기
                if (item.getItemId() == R.id.firstActivity_fifth_sideBar_item) {
                    getFragmentManager().beginTransaction().
                            replace(R.id.firstActivity_FirstLayout_mainFrame, new SidebarFifthMembers())
                            .addToBackStack(SidebarFifthMembers.class.getName()).commit();
                }

                // TODO
                // 6기
                if (item.getItemId() == R.id.firstActivity_sixth_sideBar_item) {
                    getFragmentManager().beginTransaction().
                            replace(R.id.firstActivity_FirstLayout_mainFrame, new SidebarSixthMembers())
                            .addToBackStack(SidebarSixthMembers.class.getName()).commit();
                }

                // 학회 홈페이지로 이동
                if (item.getItemId() == R.id.firstActivity_seventh_sideBar_item) {
                    Intent website_intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://ssma.strikingly.com"));
                    startActivity(website_intent);
                }

                // TODO
                // 비회원 페이지로 이동
                /*
                if (item.getItemId() == R.id.firstActivity_eighth_sideBar_item) {
                    startActivity(new Intent(navigationView.getContext(), NonmemberFirstActivity.class));
                }
                */

                // 로그아웃 기능 - 확인 요망
                if (item.getItemId() == R.id.firstActivity_nineth_sideBar_item) {
                    auth.signOut();
                    Toast.makeText(FirstActivity.this, "로그아웃 됩니다.", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(navigationView.getContext(), LoginActivity.class));
                    finish();
                }
                return true;
            }
        });

        // TODO
        // 메인 화면 조정
        getFragmentManager().beginTransaction()
                .replace(R.id.firstActivity_FirstLayout_mainFrame, new PeoplesFragment()).commit();

        // 푸시 메시지 통한 1:1 채팅방 전환
        if (getIntent().getStringExtra("caseNumber") != null && getIntent().getStringExtra("caseNumber").equals("0")) {
            Log.d("First Act", "1:1 채팅방 이동");
            Intent intent = new Intent(this, MessageActivity.class);
            intent.putExtra("destinationUid", destinationUid);
            startActivity(intent);
        }

        // 푸시 메시지 통한 1:多 채팅방 전환
        if (getIntent().getStringExtra("caseNumber") != null && getIntent().getStringExtra("caseNumber").equals("1")) {
            Log.d("First Act", "1:多 채팅방 이동");
            Intent intent = new Intent(this, GroupMessageActivity.class);
            intent.putExtra("destinationRoom", destinationRoomUid);
            startActivity(intent);
        }

        // 푸시 메시지 통한 게시판 전환
        if (getIntent().getStringExtra("caseNumber") != null && getIntent().getStringExtra("caseNumber").equals("2")) {
            Log.d("First Act", "게시판 이동");
            getFragmentManager().beginTransaction().replace(R.id.firstActivity_FirstLayout_mainFrame,
                    new BoardFragment()).addToBackStack(BoardFragment.class.getName()).commit();
        }

        // TODO
        // Bottom NavigationView 호출 및 설정
        final BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.firstLayout_bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.firstLayout_bottom_chat:
                        getFragmentManager().beginTransaction().replace(R.id.firstActivity_FirstLayout_mainFrame,
                                new ChatFragment()).addToBackStack(ChatFragment.class.getName()).commit();
                        return true;

                    case R.id.firstLayout_bottom_account:
                        getFragmentManager().beginTransaction().replace(R.id.firstActivity_FirstLayout_mainFrame,
                                new AccountFragment()).addToBackStack(AccountFragment.class.getName()).commit();
                        return true;

                    case R.id.firstLayout_bottom_board:
                        getFragmentManager().beginTransaction().replace(R.id.firstActivity_FirstLayout_mainFrame,
                                new BoardFragment()).addToBackStack(BoardFragment.class.getName()).commit();
                        return true;
                }
                return false;
            }
        });

        // 현재 계정이 없을 경우 로그인 액티비티로 넘깁니다.
        if (FirebaseAuth.getInstance().getCurrentUser().getEmail() == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }

        else {
            passPushTokenToServer();
        }
    }

    // 푸시 토큰을 불러옵니다.
    void passPushTokenToServer() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String token = FirebaseInstanceId.getInstance().getToken();

        // Firebase는 현재 해쉬맵에 최적화되어있습니다.
        Map<String,Object> map = new HashMap<>();
        map.put("pushToken", token);

        FirebaseDatabase.getInstance().getReference().child("users").child(uid).updateChildren(map);
    }
}
