package com.example.user.navigationview;

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

import com.example.user.navigationview.sidebar.Fragment1;
import com.example.user.navigationview.sidebar.Fragment2;
import com.example.user.navigationview.sidebar.Fragment3;
import com.example.user.navigationview.sidebar.Fragment4;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        
        toolbar.findViewById(R.id.email_button_toolbar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "서버에 연결되지 않았습니다.", Toast.LENGTH_SHORT).show();
            }
        });

        final DrawerLayout drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout_main);

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, 0, 0);
        drawerLayout.setDrawerListener(actionBarDrawerToggle);

        actionBarDrawerToggle.syncState();

        NavigationView navigationView = (NavigationView)findViewById(R.id.navigationview_main);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.first_setting_sidegar) {
                    getFragmentManager().beginTransaction().replace(R.id.main_frame, new Fragment1()).commit();
                }

                if (item.getItemId() == R.id.second_setting_sidegar) {
                    getFragmentManager().beginTransaction().replace(R.id.main_frame, new Fragment2()).commit();
                }

                if (item.getItemId() == R.id.third_setting_sidegar) {
                    getFragmentManager().beginTransaction().replace(R.id.main_frame, new Fragment3()).commit();
                }

                if (item.getItemId() == R.id.fourth_setting_sidegar) {
                    getFragmentManager().beginTransaction().replace(R.id.main_frame, new Fragment4()).commit();
                }
                
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });
        
        navigationView.findViewById(R.id.email_address_sidebar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "서버에 연결되지 않았습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
