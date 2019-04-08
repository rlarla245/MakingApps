package com.du.instagramprototypeproject;

import android.Manifest;
import android.app.Fragment;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.du.instagramprototypeproject.bottmNavigations.AddPhotoActivity;
import com.du.instagramprototypeproject.bottmNavigations.AlarmFragment;
import com.du.instagramprototypeproject.bottmNavigations.DetailViewFragment;
import com.du.instagramprototypeproject.bottmNavigations.GridFragment;
import com.du.instagramprototypeproject.bottmNavigations.UserFragment;
import com.du.instagramprototypeproject.databinding.ActivityMainBinding;
import com.du.instagramprototypeproject.model.NotificationModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.du.instagramprototypeproject.util.StatusCode.FRAGMENT_ARG;
import static com.du.instagramprototypeproject.util.StatusCode.PICK_IMAGE_FROM_ALBUM;
import static com.du.instagramprototypeproject.util.StatusCode.PICK_PROFILE_FROM_ALBUM;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    // Data Binding
    public static ActivityMainBinding binding;

    // notificationChannel - 채널을 쓰는 걸로 봐서 서비스 기능을 활용하는 듯
    private static final int NOTIFICATION_ID = 1;
    private static final String NOTIFICATION_CHANNEL_ID = "my_notification_channel";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 데이터 바인딩
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        // 데이터가 없을 경우 계속해서 보인다는게 단점으로 볼 수도 있을 듯함.
        // 디테일 뷰로 넘어가지 못하면 계속해서 프로그레스 바가 보입니다.
        binding.progressBar.setVisibility(View.VISIBLE);

        // Bottom Navigation View - 하단 네비게이션 바를 활용합니다.
        binding.bottomNavigation.setOnNavigationItemSelectedListener(this);

        // 자동으로 홈화면(디테일 뷰)로 이동합니다.
        // 즉, 메인 액티비티 전환 시 디테일 뷰 프레그먼트를 자동으로 불러오게 됩니다.
        binding.bottomNavigation.setSelectedItemId(R.id.action_home);

        // Firebase 서버에 토큰 등록하기 + 로그인 되어 있을 경우(혹시나 서버 문제로 인한 에러 방지 위함)
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            passPushTokenToServer();
        }

        // 오레오 이상 푸시 메시지 전송 -> 오레오 버전 부터는 무조건 '채널'을 생성해야 푸시 메시지를 전송할 수 있습니다.
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

            String id = "ProtoType";
            CharSequence name = "ProtoType";

            NotificationChannel notificationChannel = new NotificationChannel(id, name,
                    NotificationManager.IMPORTANCE_DEFAULT);

            // 푸시 알람 설정
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setVibrationPattern(new long[] {100, 200, 300, 400, 500, 400, 300, 200, 400});
            notificationChannel.setShowBadge(true);

            if (notificationManager != null) {
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }
    }

    @Override
    // 하단 뷰가 클릭될 경우
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_home:
                // 하단에 정의한 메소드
                setToolbarDefault();

                // 새로운 프레그먼트를 지정해서 번들 클래스를 활용해 데이터를 넘겨줍니다.
                // v4 버전은 활용할 수 없습니다.
                Fragment detailViewFragment = new DetailViewFragment();

                Bundle bundle_0 = new Bundle();
                // util 패키지 디렉토리에 정리한 인스턴스 변수 value 값이 모두 다릅니다.
                // 다른 value 값을 통해 다른 동작을 수행합니다.
                bundle_0.putInt(FRAGMENT_ARG, 0);

                // 번들값을 지정해줍니다.
                detailViewFragment.setArguments(bundle_0);

                // 애니메이션 효과를 넣으면 좋을 듯
                getFragmentManager().beginTransaction()
                        .replace(R.id.main_content, detailViewFragment)
                        .commit();

                return true;

            case R.id.action_search:
                // 마찬가지로 새로운 프레그먼트 지정해 번들 기능을 활용, 데이터를 넘겨줌
                Fragment gridFragment = new GridFragment();

                Bundle bundle_1 = new Bundle();
                bundle_1.putInt(FRAGMENT_ARG, 1);

                gridFragment.setArguments(bundle_1);

                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.main_content, gridFragment)
                        .commit();

                return true;

            // 사진을 업로드 하는 버튼입니다.
            case R.id.action_add_photo:
                setToolbarDefault();

                // 권한 요청 하는 부분 - 외부 저장소를 읽을 수 있는 권한을 요청합니다.
                // 요청 코드를 통해 onActivityResult 메소드에서 처리합니다.
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    ActivityCompat.requestPermissions
                            (this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                }

                /*
                * 확인 요망
                *
                * */

                // 권한을 받았다는 걸 확인하고 앨범을 열어야 하지 않나?
                // 앨범을 열어 이미지를 선택합니다.
                startActivityForResult(new Intent(MainActivity.this, AddPhotoActivity.class),
                        PICK_IMAGE_FROM_ALBUM);

                return true;

            case R.id.action_favorite_alarm:
                setToolbarDefault();

                Fragment alarmFragment = new AlarmFragment();

                Bundle bundle_3 = new Bundle();
                bundle_3.putInt(FRAGMENT_ARG, 3);

                alarmFragment.setArguments(bundle_3);

                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.main_content, alarmFragment)
                        .commit();

                return true;

            // 계정 프레그먼트로 이동합니다.
            // 그러기 위해선 계정에 관한 정보를 해당 프레그먼트에 넘겨줘야 합니다.
            case R.id.action_account:
                setToolbarDefault();

                Fragment userFragment = new UserFragment();

                // 현재 유저의 uid를 담습니다.
                String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

                Bundle bundle = new Bundle();
                // 목적지의 uid에 현재 유저의 uid를 담습니다.
                // 다른 프레그먼트에서는 다른 유저로 넘기기 위한 조건입니다.
                bundle.putString("destinationUid", uid);
                bundle.putInt(FRAGMENT_ARG, 4);

                userFragment.setArguments(bundle);

                getFragmentManager().beginTransaction()
                        .replace(R.id.main_content, userFragment)
                        .commit();

                return true;
        }
        return false;
    }

    public void setToolbarDefault() {
        // 타이틀 이미지 보이게 하기(1), 백버튼 삭제(2), 유저이름 삭제(3)
        binding.toolbarTitleImage.setVisibility(View.VISIBLE);
        binding.toolbarBtnBack.setVisibility(View.GONE);
        binding.toolbarUsername.setVisibility(View.GONE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /* getPath() 메소드 참고
        * public String getPath(Uri uri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        CursorLoader cursorLoader = new CursorLoader(this, uri, proj, null, null, null);

        Cursor cursor = cursorLoader.loadInBackground();
        int index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

        cursor.moveToFirst();
        return cursor.getString(index);
        }
        */

        // 앨범에서 Profile Image 사진 선택시 호출 되는 부분
        if (requestCode == PICK_PROFILE_FROM_ALBUM && resultCode == RESULT_OK) {
            // 원래 getPath() 메소드를 만드는게 편하긴 함
            String[] proj = {MediaStore.Images.Media.DATA};
            CursorLoader cursorLoader = new CursorLoader
                    (this, data.getData(), proj, null, null, null);
            Cursor cursor = cursorLoader.loadInBackground();
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();

            // 이미지 경로 == return cursor.getString(index);
            String photoPath = cursor.getString(column_index);

            // 유저 Uid
            final String uid = FirebaseAuth.getInstance().getCurrentUser().getUid(); //파일 업로드

            // 앨범에서 고른 사진을 새로운 파일로 생성합니다.
            File f = new File(photoPath);
            FirebaseStorage
                    .getInstance()
                    .getReference()
                    // 앨범을 통해 열린 이미지가
                    // 해당 스토리지의 "userProfileImages"에 이미지들을 담게 됩니다.
                    .child("userProfileImages")
                    // 그 이미지 데이터 테이블의 '내' uid 데이터베이스에 담깁니다.
                    .child(uid)
                    // 해당 파일의 이미지 경로를 서버에 입력합니다(Url = 경로);
                    .putFile(Uri.fromFile(f))
                    // 성공 시
                    .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            // 다운로드 uri(링크)를 지정합니다. Firebase 최신 버전에서는 사용할 수 없습니다.
                            @SuppressWarnings("VisibleForTests")
                            String url = task.getResult().getDownloadUrl().toString();
                            // 해시 맵을 생성해 해당 유저 - 다운로드 이미지 uri를 지정해줍니다.
                            Map<String, Object> map = new HashMap<String, Object>();
                            // 해시 맵에 key 값으로 유저를, value 값으로 해당 이미지의 다운로드 경로를 입력합니다.
                            map.put(uid, url);
                            // 업로드 할 때마다 value 값의 이미지 uri가 수정(updateChrldren)되므로 유저 아이콘을 수정할 수 있습니다.
                            // 대신 하나의 이미지만 업로드가 될 수 밖에 없습니다.
                            FirebaseDatabase.getInstance().getReference().child("profileImages").updateChildren(map);
                        }
                    });

        } else if (requestCode == PICK_IMAGE_FROM_ALBUM && resultCode == RESULT_OK) {
            // 계정을 눌렀을 때의 화면으로 넘어갑니다.
            binding.bottomNavigation.setSelectedItemId(R.id.action_account);
        }
    }

    // 백 키를 오버라이딩 합니다.
    @Override
    public void onBackPressed() {
        Fragment fragment = getFragmentManager().findFragmentById(R.id.main_content);
        int fragmentNum = fragment.getArguments().getInt(FRAGMENT_ARG, 0);

        if (fragmentNum == 5) {
            // 프레그먼트 번들 값에서 정수를 불러오는데 그 수가 5일 경우 메인 화면으로 이동시킵니다.
            binding.bottomNavigation.setSelectedItemId(R.id.action_home);

        } else {
            // 그렇지 않으면 원래 백 키의 기능을 되살립니다.
            super.onBackPressed();
        }
    }


    // 서버 에러 방지를 위해 NULL 값이 아닐 경우 토큰을 서버에 전송해줘야 합니다.
    public static void passPushTokenToServer() {
        // 토큰 입력하기
        String token = FirebaseInstanceId.getInstance().getToken();

        // uid 생성
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Map<String, Object> map = new HashMap<>();
        // 키 - 문자열, value - 실제 토큰을 집어 넣습니다.
        map.put("pushToken", token);

        FirebaseDatabase.getInstance().getReference().child("pushToken").child(uid).updateChildren(map);
    }

    // 포그라운드 푸시 메시지 출력입니다. 백그라운드는 '채널'을 활용합니다.
    public static void sendGcm(String destinationUidToken, String title, String message) {
        Gson gson = new Gson();

        NotificationModel notificationModel = new NotificationModel();

        notificationModel.to = destinationUidToken;
        notificationModel.notification.title = title;
        notificationModel.notification.text = message;

        // 포그라운드 푸시 메시지 출력에 필요한 변수들입니다.
        notificationModel.data.title = title;
        notificationModel.data.text = message;

        // 포스트 맨과 같은 바디를 생성했습니다.
        RequestBody requestBody =
                RequestBody.create(MediaType.parse("application/json; charset=utf8"), gson.toJson(notificationModel));

        // 포스트 맨과 같은 헤더 부분입니다.
        Request request = new Request.Builder()
                .header("Content-Type", "application/json")
                // 해당 서버키를 입력합니다.
                .addHeader("Authorization", "key=AIzaSyCGWCdEHi9S3__1b6_p89Uchsei9rYDxYM")
                // 이것도 맞는지 확인해야 합니다.
                .url("https://gcm-http.googleapis.com/gcm/send")
                .post(requestBody)
                .build();

        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

            }
        });
    }
}
