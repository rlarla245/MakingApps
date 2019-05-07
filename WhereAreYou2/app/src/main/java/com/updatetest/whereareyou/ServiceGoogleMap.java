package com.updatetest.whereareyou;

import android.Manifest;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.updatetest.whereareyou.Models.LocationModel;

public class ServiceGoogleMap extends Service implements LocationListener {
    // 위도, 경도
    // 자료형이 더블이라는 걸 명심합니다.
    Double latitude = 0.0;
    Double longitude = 0.0;

    // 내 uid
    String myUid;

    // 위치 입력하기 위한 변수 설정
    LocationManager locationManager;

    // start
    @Override
    public void onCreate() {
        super.onCreate();
    }

    // 다시 시작될 때
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // 내 uid 불러오기
        try {
            myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            System.out.println("service uid 확인: " + myUid);
            System.out.println("service 확인: onStartCommand 접근 완료");

            new Thread(new Runnable() {
                @Override
                public void run() {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            while (true) {
                                // 앱이 포그라운드 상태에 있을 때
                                if (!GoogleMapActivity.isAppIsInBackground(getApplicationContext())) {
                                    break;
                                }

                                if (!LoginActivity.isAppIsInBackground(getApplicationContext())) {
                                    break;
                                }

                                // locationManager 설정
                                locationManager = (LocationManager) ServiceGoogleMap.this.getSystemService(Context.LOCATION_SERVICE);

                                // 허가 확인을 하지 않으면 해당 메소드들을 사용할 수 없습니다.
                                if (ContextCompat.checkSelfPermission(ServiceGoogleMap.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                                        && ContextCompat.checkSelfPermission(ServiceGoogleMap.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                                    // 어떤 방식으로 위치를 불러올지, 시간 간격, 몇 미터마다 갱신할 것인지, 리스너를 파라미터로 받습니다,
                                    // 어디에 연결되었니? GPS || NETWORD
                                    // locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 100f, this);
                                    if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                                        if (locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER) == null) {
                                            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, ServiceGoogleMap.this);
                                        } else {
                                            // 위치 변경 시 호출
                                            onLocationChanged(locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER));
                                            System.out.println("service 확인 gps 위치: " + locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER));
                                        }
                                    }
                                    // 네트워크 방식을 통한 위치 호출은 정확도가 급격히 떨어진다는 단점이 존재합니다.
                                    else {
                                        if (locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER) == null) {
                                            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, ServiceGoogleMap.this);
                                        } else {
                                            // 위치 변경 시 호출
                                            onLocationChanged(locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER));
                                            System.out.println("service 확인 기지국 위치: " + locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER));
                                        }
                                    }
                                } else {
                                    Toast.makeText(ServiceGoogleMap.this, "권한이 필요합니다.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });
                }
            }).start();
            return START_STICKY;
        } catch (NullPointerException e) {
            //
            return START_NOT_STICKY;
        }
    }

    // 서비스 제거
    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    // 위치 변경 시
    @Override
    public void onLocationChanged(Location location) {
        System.out.println("service 확인: onLocationChangerd 메소드 접근 완료");

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
                        //
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ServiceGoogleMap.this, "위치 입력에 실패했습니다.", Toast.LENGTH_SHORT).show();
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
