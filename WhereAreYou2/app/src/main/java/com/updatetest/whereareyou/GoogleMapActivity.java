package com.updatetest.whereareyou;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.updatetest.whereareyou.Models.LocationModel;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// 구글 맵과 현재 유저의 위치 정보를 불러오는 인터페이스 2개를 불러옵니다.
public class GoogleMapActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener {
    // 변수들 선언
    FirebaseAuth auth = FirebaseAuth.getInstance();
    String myUid;
    String counterPartUid;

    // 매칭 이후 액티비티 이므로 과거 시간대를 보여주는 새로운 메뉴가 필요합니다.
    ImageButton imageButtonMenu;

    // 위치 입력하기 위한 변수 설정
    LocationManager locationManager;

    // 위도, 경도
    // 자료형이 더블이라는 걸 명심합니다.
    Double latitude = 0.0;
    Double longitude = 0.0;

    // 최근 위치
    Location recentLocation;

    // 상대방 위치들을 모으는 리스트
    List<LocationModel> locationModels = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_googlemap);

        // 상대방 uid 불러오기
        counterPartUid = getIntent().getStringExtra("counterPartUid");

        // locationManager 설정
        locationManager = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);

        // 허가 확인을 하지 않으면 해당 메소드들을 사용할 수 없습니다.
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // 어떤 방식으로 위치를 불러올지, 시간 간격, 몇 미터마다 갱신할 것인지, 리스너를 파라미터로 받습니다,
            // 어디에 연결되었니? GPS || NETWORD
            // locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 100f, this);
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500, 0, this);

                // 최근 위치 입력
                recentLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            }
            // 네트워크 방식을 통한 위치 호출은 정확도가 급격히 떨어진다는 단점이 존재합니다.
            else {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 500, 0, this);

                // 최근 위치 입력
                recentLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }
        } else {
            Toast.makeText(this, "권한이 필요합니다.", Toast.LENGTH_SHORT).show();
        }

        // 이미지 버튼 누를 시 메뉴 생성
        imageButtonMenu = findViewById(R.id.main_toolbar_imagebutton);
        imageButtonMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 메뉴가 띄워질 view를 설정해줍니다.
                View button = findViewById(R.id.main_toolbar_imagebutton);
                PopupMenu menu = new PopupMenu(GoogleMapActivity.this, button);

                // 메뉴를 호출(매칭)합니다.
                menu.getMenuInflater().inflate(R.menu.googlemapactivity_settings, menu.getMenu());
                menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        // 로그아웃 버튼을 누를 경우
                        if (item.getItemId() == R.id.action_sign_out) {
                            FirebaseAuth.getInstance().signOut();
                            Toast.makeText(GoogleMapActivity.this, "로그아웃 됩니다.", Toast.LENGTH_SHORT).show();
                            finish();

                            startActivity(new Intent(GoogleMapActivity.this, LoginActivity.class));
                        }

                        // 과거 위치를 불러주는 액티비티로 넘깁니다.
                        // if ()
                        return true;
                    }
                });
                // 메뉴를 띄웁니다.
                menu.show();
            }
        });

        // 내 uid 불러오기
        myUid = auth.getCurrentUser().getUid();

        // 지도 불러오기
        // 맵 프레그먼트를 불러옵니다.
        SupportMapFragment supportMapFragment
                = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapfragment_mapfragment);

        // 해당 맵 프레그먼트를 인터페이스와 동기화 시킵니다.
        supportMapFragment.getMapAsync(this);
    }

    // 지도가 준비되었을 경우
    // 지도를 불러올 때 상태를 보여줍니다.
    @Override
    public void onMapReady(final GoogleMap googleMap) {
        FirebaseDatabase.getInstance().getReference().child("locations")
                //.child(counterPartUid)
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                locationModels.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (snapshot.child("uid").getValue().equals(counterPartUid)) {
                        LocationModel locationModel = new LocationModel();
                        // 위도 불러오기
                        double test = 0;
                        // db에서 데이터를 불러올 땐 long 값으로 불러오므로
                        // double형으로 다시 형변환 해줘야 합니다.
                        Number num = (Number) snapshot.child("latitude").getValue();
                        // doubleValue()를 실행합니다.
                        test = num.doubleValue();

                        // 경도 불러오기
                        double testLng = 0;
                        Number num2 = (Number) snapshot.child("longitude").getValue();
                        testLng = num2.doubleValue();

                        // db 내 위도와, 경도를 받아옵니다.
                        locationModel.latitude = test;
                        locationModel.longitude = testLng;
                        locationModel.uid = counterPartUid;

                        locationModels.add(locationModel);
                    }
                }

                // 위도, 경도를 받는 변수
                LatLng counterPartLocation;

                if (locationModels.size() != 0) {
                    // 좌표는 maps.google.com에서 구할 수 있음
                    counterPartLocation = new LatLng(locationModels.get(0).latitude,
                            locationModels.get(0).longitude);

                }
                // 상대방 위치 값이 저장되어 있지 않은 경우우
                else {
                   counterPartLocation = new LatLng(0, 0);
                }

                // 마커를 찍을 장소와 이름을 지정합니다.
                // 시간으로 설정하면 더 좋을 듯 합니다.
                googleMap.addMarker(new MarkerOptions().position(counterPartLocation).title("현재 위치"));

                // 마커를 누를 경우 그 장소를 확대해서 보여줍니다.
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(counterPartLocation, 17));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    // LocationListener에 필요한 메소드입니다.
    @Override
    public void onLocationChanged(final Location location) {
        // 현재 위도와 경도 값을 받아옵니다.
        latitude = location.getLatitude();
        longitude = location.getLongitude();

        // 데이터 모델에 맞게 포맷합니다.
        final LocationModel locationModel = new LocationModel();
        locationModel.latitude = latitude;
        locationModel.longitude = longitude;
        locationModel.uid = myUid;

        // 위치 데이터를 입력합니다.
        FirebaseDatabase.getInstance().getReference().child("locations").child(myUid)
                .setValue(locationModel)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        //
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(GoogleMapActivity.this, "위치 입력에 실패했습니다.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
