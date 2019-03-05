package com.ssma.serverapp.NonMemberGoogleMap;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.ssma.serverapp.NonMemberSidebarFragments.SidebarFifthFragment;
import com.ssma.serverapp.NonMemberSidebarFragments.SidebarFirstFragment;
import com.ssma.serverapp.NonMemberSidebarFragments.SidebarFourthFragment;
import com.ssma.serverapp.NonMemberSidebarFragments.SidebarSecondFragment;
import com.ssma.serverapp.NonMemberSidebarFragments.SidebarSixthFragment;
import com.ssma.serverapp.NonMemberSidebarFragments.SidebarThirdFragment;
import com.ssma.serverapp.R;

public class NonMemberGoogleMap extends AppCompatActivity implements OnMapReadyCallback {

    // 바텀 레이아웃 불러오기
    private TextView bottom_textview;

    // 3d(확대) 버튼 불러오기
    private Button google_map_3D_button;

    GoogleMap gMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nonmember_googlemap);

        bottom_textview = (TextView) findViewById(R.id.googlemap_bottom_textview);
        google_map_3D_button = (Button)findViewById(R.id.nonmember_googlemap_button);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // 툴바 설정 및 액션바, 프로젝트 이름 제거
        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar)findViewById(R.id.nonMemberToolbar2);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // 툴바, 사이드 바 활용 위한 drawerlayout 설정
        final DrawerLayout drawerLayout = (DrawerLayout)findViewById(R.id.nonmember_googlemap_drawerlayout);

        // 툴바에 사이드 바 입력하는 단계
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, 0, 0);

        drawerLayout.setDrawerListener(actionBarDrawerToggle);

        // 사이드 바 동기화
        actionBarDrawerToggle.syncState();

        // 사이드 바에 들어갈 레이아웃 설정
        final NavigationView navigationView = (NavigationView)findViewById(R.id.nonmember_googlemap_navigationview);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                drawerLayout.closeDrawer(GravityCompat.START);

                // 특정 item 클릭 시 fragment 실행 코드)
                if (item.getItemId() == R.id.nonmember_first_sidebar_item) {
                    getFragmentManager().beginTransaction().
                            replace(R.id.nonmember_googlemap_mainframe, new SidebarFirstFragment()).addToBackStack(SidebarFirstFragment.class.getName()).commit();
                    google_map_3D_button.setVisibility(View.INVISIBLE);
                    bottom_textview.setVisibility(View.INVISIBLE);
                }

                if (item.getItemId() == R.id.nonmember_second_sidebar_item) {
                    getFragmentManager().beginTransaction().
                            replace(R.id.nonmember_googlemap_mainframe, new SidebarSecondFragment()).addToBackStack(SidebarSecondFragment.class.getName()).commit();
                    google_map_3D_button.setVisibility(View.INVISIBLE);
                    bottom_textview.setVisibility(View.INVISIBLE);
                }

                if (item.getItemId() == R.id.nonmember_third_sidebar_item) {
                    getFragmentManager().beginTransaction().
                            replace(R.id.nonmember_googlemap_mainframe, new SidebarThirdFragment()).addToBackStack(SidebarThirdFragment.class.getName()).commit();
                    google_map_3D_button.setVisibility(View.INVISIBLE);
                    bottom_textview.setVisibility(View.INVISIBLE);
                }

                if (item.getItemId() == R.id.nonmember_fourth_sidebar_item) {
                    getFragmentManager().beginTransaction().
                            replace(R.id.nonmember_googlemap_mainframe, new SidebarFourthFragment()).addToBackStack(SidebarFourthFragment.class.getName()).commit();
                    google_map_3D_button.setVisibility(View.INVISIBLE);
                    bottom_textview.setVisibility(View.INVISIBLE);
                }

                if (item.getItemId() == R.id.nonmember_fifth_sidebar_item) {
                    getFragmentManager().beginTransaction().
                            replace(R.id.nonmember_googlemap_mainframe, new SidebarFifthFragment()).addToBackStack(SidebarFifthFragment.class.getName()).commit();
                    google_map_3D_button.setVisibility(View.INVISIBLE);
                    bottom_textview.setVisibility(View.INVISIBLE);
                }

                if (item.getItemId() == R.id.nonmember_sixth_sidebar_item) {
                    getFragmentManager().beginTransaction().
                            replace(R.id.nonmember_googlemap_mainframe, new SidebarSixthFragment()).addToBackStack(SidebarSixthFragment.class.getName()).commit();
                    bottom_textview.setVisibility(View.INVISIBLE);
                    google_map_3D_button.setVisibility(View.INVISIBLE);
                    bottom_textview.setVisibility(View.INVISIBLE);
                }

                if (item.getItemId() == R.id.nonmember_seventh_sidebar_item) {
                    Intent website_intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://ssma.strikingly.com"));
                    startActivity(website_intent);
                }
                return true;
            }
        });

        // 구글 맵 불러오기
        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.nonmember_fragment_google_map);
        supportMapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        gMap = googleMap;
        final LatLng kyunghee_university = new LatLng(37.595359, 127.051071);

        gMap.getUiSettings().setZoomControlsEnabled(true);
        gMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        gMap.addMarker(new MarkerOptions().position(kyunghee_university).title("서비스 전략경영학회(SSMA)"));

        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(kyunghee_university, 10));

        google_map_3D_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Construct a CameraPosition focusing on Mountain View and animate the camera to that position.
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(kyunghee_university)      // Sets the center of the map to Mountain View
                        .zoom(17)                   // Sets the zoom
                        .bearing(10)                // Sets the orientation of the camera to east
                        .tilt(5)                   // Sets the tilt of the camera to 30 degrees
                        .build();                   // Creates a CameraPosition from the builder
                gMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));


            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0, 1, 0, "위성 지도");
        menu.add(0, 2, 0, "일반 지도");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 1:
                gMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                return true;
            case 2:
                gMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                return true;
        }
        return false;
    }


    @Override
    public void onBackPressed() {
        // super.onBackPressed();
        finish();
    }
}

