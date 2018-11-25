package com.example.user.navigationfragment;

import android.annotation.SuppressLint;
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
import android.widget.Toast;

import com.example.user.navigationfragment.navigation_fragment.FirstFragment1;
import com.example.user.navigationfragment.navigation_fragment.FirstFragment2;
import com.example.user.navigationfragment.navigation_fragment.FirstFragment3;
import com.example.user.navigationfragment.navigation_fragment.FirstFragment4;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar)findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        
        toolbar.findViewById(R.id.email_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "서버에 연결되지 않았습니다.", Toast.LENGTH_SHORT).show();
            }
        });

        final DrawerLayout drawerLayout = (DrawerLayout)findViewById(R.id.main_drawerlayout);

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, 0, 0);
        drawerLayout.setDrawerListener(actionBarDrawerToggle);

        actionBarDrawerToggle.syncState();

        NavigationView navigationView = (NavigationView)findViewById(R.id.main_navigationview);

        navigationView.findViewById(R.id.navi_email_text).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "서버에 연결되지 않았습니다.", Toast.LENGTH_SHORT).show();
            }
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.first_navi_set) {
                    getFragmentManager().beginTransaction().replace(R.id.main_frame, new FirstFragment1()).commit();
                }

                if (item.getItemId() == R.id.first_navi_set) {
                    getFragmentManager().beginTransaction().replace(R.id.main_frame, new FirstFragment2()).commit();
                }

                if (item.getItemId() == R.id.first_navi_set) {
                    getFragmentManager().beginTransaction().replace(R.id.main_frame, new FirstFragment3()).commit();
                }

                if (item.getItemId() == R.id.first_navi_set) {
                    getFragmentManager().beginTransaction().replace(R.id.main_frame, new FirstFragment4()).commit();
                }
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });
    }
}
