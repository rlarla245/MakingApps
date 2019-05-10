package com.updatetest.whereareyou;

import android.Manifest;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
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
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.updatetest.whereareyou.Models.LocationModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

// 구글 맵과 현재 유저의 위치 정보를 불러오는 인터페이스 2개를 불러옵니다.
public class GoogleMapActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener {
    // 날짜 찍기
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd HH시 mm분");

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
    public static Location recentLocation;

    // 상대방 위치들을 모으는 리스트
    List<LocationModel> locationModels = new ArrayList<>();

    // 플로팅 버튼 클릭을 통한 현재 위치로 이동
    FloatingActionButton floatingActionButton;

    // 서비스 종료 여부를 판단하기 위한 foreground/background 확인
    public static boolean isAppIsInBackground(Context context) {
        // 초기값은 백그라운드 값으로 설정
        boolean isInBackground = true;

        // 상태 불러오기
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        // 킷캣이상
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                // 포그라운드 상태?
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    // 패키지 리스트 접근
                    for (String activeProcess : processInfo.pkgList) {
                        if (activeProcess.equals(context.getPackageName())) {
                            isInBackground = false;
                        }
                    }
                }
            }
        }
        // 킷캣 이하
        // GET_TASK Permission 필요하지만, depricated된 것으로 알고 있음
        else {
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            if (componentInfo.getPackageName().equals(context.getPackageName())) {
                isInBackground = false;
            }
        }

        // 백그라운드일 때 true 리턴, 포그라운드일 때 false 리턴
        return isInBackground;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_googlemap);

        // 상대방 uid 불러오기
        counterPartUid = getIntent().getStringExtra("counterPartUid");

        // locationManager 설정
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        // System.out.println("GoogleMapActivity 확인. locationManager: " + locationManager);

        // 플로팅 버튼 불러오기
        floatingActionButton = findViewById(R.id.googlemap_floatingbutton);

        // 내 uid 불러오기
        myUid = auth.getCurrentUser().getUid();

        // 위치 제공 확인을 하지 않으면 해당 메소드들을 사용할 수 없습니다.
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // 어떤 방식으로 위치를 불러올지, 시간 간격, 몇 미터마다 갱신할 것인지, 리스너를 파라미터로 받습니다,
            // locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 100f, this);
            // 어디에서 위치값을 받는가? GPS || NETWORK
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                System.out.println("GoogleMapActivity 확인: GPS 위치 접근");
                // locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);

                // 최근 위치 입력
                recentLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                System.out.println("GoogleMapActivity 확인. 최근 위치: " + locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER));

                // 최근 위치가 없을 경우
                if (recentLocation == null) {
                    System.out.println("GoogleMapActivity 확인: recentLocation == null");
                    // 최근 위치가 없으므로 새로운 위치 값을 받아옵니다.
                    // 5초, 10미터
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, this);
                    recentLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    System.out.println("GoogleMapActivity 위치 재호출 확인: " + recentLocation);
                }

                // 최근 위치가 있을 경우
                else {
                    // 위도 및 경도 값 불러오기
                    latitude = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLatitude();
                    longitude = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLongitude();

                    // 최근 위치를 서버에 찍어줍니다.
                    // 데이터 모델에 맞게 포맷합니다.
                    final LocationModel locationModel = new LocationModel();
                    locationModel.latitude = latitude;
                    locationModel.longitude = longitude;
                    locationModel.uid = myUid;
                    // 현재 시간
                    locationModel.timestamp = ServerValue.TIMESTAMP;

                    // 위치 데이터를 입력합니다.
                    FirebaseDatabase.getInstance().getReference().child("locations").child(myUid)
                            .setValue(locationModel)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    // 위치 데이터 입력 성공
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(GoogleMapActivity.this, "위치 입력에 실패했습니다.\n관리자에게 문의바랍니다.", Toast.LENGTH_SHORT).show();
                                }
                            });

                    // 새로운 위치 값을 불러옵니다.
                    // 해당 코드가 없으면 바뀐 위치 값을 불러오지 않는 듯(리스너 자체에 접근이 안되므로)
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, this);
                    // 후...
                    onLocationChanged(locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER));
                }
            }

            // 네트워크 방식을 통한 위치 호출은 정확도가 급격히 떨어진다는 단점이 존재합니다.
            else {
                System.out.println("GoogleMapActivity 확인: 기지국 위치 접근");

                // 최근 위치 입력
                recentLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                // 최근 위치가 없는 경우
                if (recentLocation == null) {
                    // 위치 값 요청
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 10, this);
                    recentLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                }

                // 최근 위치가 있는 경우
                else {
                    // 위도 및 경도 값 불러오기
                    latitude = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER).getLatitude();
                    longitude = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER).getLongitude();

                    // 데이터 모델에 맞게 포맷합니다.
                    final LocationModel locationModel = new LocationModel();
                    locationModel.latitude = latitude;
                    locationModel.longitude = longitude;
                    locationModel.uid = myUid;
                    locationModel.timestamp = ServerValue.TIMESTAMP;

                    // 위치 데이터를 입력합니다.
                    FirebaseDatabase.getInstance().getReference().child("locations").child(myUid)
                            .setValue(locationModel)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    // 위치 데이터 입력 성공
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(GoogleMapActivity.this, "위치 입력에 실패했습니다.\n관리자에게 문의바랍니다.", Toast.LENGTH_SHORT).show();
                                }
                            });
                    // 새로운 위치값을 불러옵니다.
                    // 마찬가지로 해당 코드가 없으면 리스너에 접근하지 않아 변경되는 위치 값을 받아오지 않습니다.
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, this);
                }
            }
        }

        // 위치 확인 권한이 없을 경우
        else {
            System.out.println("GoogleMapActivity 확인: 권한이 필요합니다.");
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

        // 지도 불러오기
        // 맵 프레그먼트를 불러옵니다.
        SupportMapFragment supportMapFragment
                = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapfragment_mapfragment);

        // 해당 맵 프레그먼트를 구글 지도 인터페이스와 동기화 시킵니다.
        supportMapFragment.getMapAsync(this);
    }

    // 지도가 준비되었을 경우
    // 디바이스가 구글 지도를 불러올 때 코드를 입력합니다.
    @Override
    public void onMapReady(final GoogleMap googleMap) {
        FirebaseDatabase.getInstance().getReference().child("locations")
                //.child(counterPartUid)
                // 데이터 변경이 있을 시 계속해서 위치 값을 불러옵니다.
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
                                locationModel.timestamp = snapshot.child("timestamp").getValue();

                                // 리스트에 위치 모델값을 담습니다.
                                locationModels.add(locationModel);
                            }
                        }

                        // 위도, 경도를 받는 변수
                        final LatLng counterPartLocation;

                        // 마커에 시간 값을 담습니다.
                        long timeNow;
                        Date dateNow;

                        // 위치 변경에 따라 찍는 마커들
                        final MarkerOptions newMarker;

                        // 상대방 위치 정보가 있을 경우
                        if (locationModels.size() != 0) {
                            // 좌표는 maps.google.com에서 구할 수 있음
                            counterPartLocation = new LatLng(locationModels.get(0).latitude,
                                    locationModels.get(0).longitude);

                            // unixTime을 dateFormat시킴
                            timeNow = (long) locationModels.get(0).timestamp;
                            dateNow = new Date(timeNow);

                            // 항상 title이 showing됩니다.
                            newMarker = new MarkerOptions().position(counterPartLocation).title(simpleDateFormat.format(dateNow));
                        }

                        // 상대방 위치 값이 저장되어 있지 않은 경우
                        // 초기값을 설정해줍니다. 안해주면 틩기니까
                        else {
                            counterPartLocation = new LatLng(0, 0);

                            // 항상 title이 showing됩니다.
                            newMarker = new MarkerOptions().position(counterPartLocation).title("위치 정보 알 수 없음");
                        }

                        // 항상 마커를 띄웁니다.
                        googleMap.addMarker(newMarker).showInfoWindow();

                        // 마커를 누를 경우 그 장소를 확대해서 보여줍니다.
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(counterPartLocation, 17));

                        // 플로팅 버튼을 통해 입력된 최근 위치로 이동합니다.
                        floatingActionButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // 타겟팅 및 애니메이션 효과 입력
                                CameraPosition cameraPosition = new CameraPosition.Builder()
                                        .target(counterPartLocation)      // Sets the center of the map to Mountain View
                                        .zoom(17)                   // Sets the zoom
                                        .bearing(10)                // Sets the orientation of the camera to east
                                        .tilt(5)                   // Sets the tilt of the camera to 30 degrees
                                        .build();                   // Creates a CameraPosition from the builder
                                // 애니메이션 효과 실행
                                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                                // 눈 속임
                                googleMap.addMarker(newMarker).showInfoWindow();
                            }
                        });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    // LocationListener에 필요한 메소드입니다.
    // 위치 값이 변경되면 자동으로 불러옵니다.
    @Override
    public void onLocationChanged(final Location location) {
        System.out.println("googlemap activity 위치 값 변경 확인: onLocationChanged 접근 완료");

        // 현재 위도와 경도 값을 받아옵니다.
        latitude = location.getLatitude();
        longitude = location.getLongitude();

        // 데이터 모델에 맞게 포맷합니다.
        final LocationModel locationModel = new LocationModel();
        locationModel.latitude = latitude;
        locationModel.longitude = longitude;
        locationModel.uid = myUid;
        locationModel.timestamp = ServerValue.TIMESTAMP;

        // 위치 데이터를 입력합니다.
        FirebaseDatabase.getInstance().getReference().child("locations").child(myUid)
                .setValue(locationModel)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // 위치 데이터 입력 성공
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(GoogleMapActivity.this, "위치 입력에 실패했습니다.\n관리자에게 문의바랍니다.", Toast.LENGTH_SHORT).show();
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

    // 화면이 가려지거나 백그라운드로 넘어갈 때 실행되는 메소드입니다.
    @Override
    protected void onPause() {
        super.onPause();
        // 서비스 매칭
        Intent serviceIntent = new Intent(this, ServiceGoogleMap.class);

        // 서비스 시작
        startService(serviceIntent);
        System.out.println("service 확인: 서비스 시작");
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 서비스 매칭
        Intent serviceIntent = new Intent(this, ServiceGoogleMap.class);

        stopService(serviceIntent);
        System.out.println("service 확인: 서비스 종료");
    }
}
