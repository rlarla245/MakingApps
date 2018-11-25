package com.du.instagramprototypeproject.bottmNavigations;

import android.Manifest;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.du.instagramprototypeproject.R;
import com.du.instagramprototypeproject.databinding.ActivityAddPhotoBinding;
import com.du.instagramprototypeproject.model.ContentDTO;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.du.instagramprototypeproject.util.StatusCode.PICK_IMAGE_FROM_ALBUM;

public class AddPhotoActivity extends AppCompatActivity implements View.OnClickListener {
    // Data Binding
    private ActivityAddPhotoBinding binding;

    // 이미지 주소를 담기 위한 변수입니다.
    private String photoUrl;

    // Firebase Storage, Database, Auth
    private FirebaseStorage firebaseStorage;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_photo);

        // 업로드 버튼 찾아오고 버튼 세팅하기
        binding.addphotoBtnUpload.setOnClickListener(this);

        // 권한 요청 하는 부분
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            ActivityCompat.requestPermissions
                    (this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }


        // 앨범 오픈
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(photoPickerIntent, PICK_IMAGE_FROM_ALBUM);

        // Firebase storage
        firebaseStorage = FirebaseStorage.getInstance();

        // Firebase Database
        firebaseDatabase = FirebaseDatabase.getInstance();

        // Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance();

        // 이미지를 클릭할 경우 앨범을 열게 됩니다.
        binding.addphotoImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                startActivityForResult(photoPickerIntent, PICK_IMAGE_FROM_ALBUM);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // 앨범에서 사진 선택시 호출 되는 부분
        if (requestCode == PICK_IMAGE_FROM_ALBUM && resultCode == RESULT_OK) {

            // 이 또한 마찬가지로 getPath() 메소드를 생성해 나눌 수 있습니다.
            String[] proj = {MediaStore.Images.Media.DATA};
            CursorLoader cursorLoader = new CursorLoader(this, data.getData(), proj,
                    null, null, null);
            Cursor cursor = cursorLoader.loadInBackground();
            int column_index =
                    cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();

            // 이미지 경로를 불러옵니다.
            photoUrl = cursor.getString(column_index);

            // 이미지뷰에 이미지를 세팅합니다.
            binding.addphotoImage.setImageURI(data.getData());
        }
    }

    @Override
    public void onClick(View v) {
        binding.addphotoBtnUpload.setEnabled(false);
        // 업로드 버튼을 누르고, 이미지가 이미지 뷰에 올라가 있을 경우(이미지가 실재할경우)
        if (v.getId() == R.id.addphoto_btn_upload && photoUrl != null) {
            // 업로드 동안은 프로그레스 바를 띄웁니다.
            binding.progressBar.setVisibility(View.VISIBLE);

            File file = new File(photoUrl);
            Uri contentUri = Uri.fromFile(file);
            // 스토리지에 이미지를 업로드합니다. 프로필 이미지와는 다르게 계속해서 쌓이게 됩니다.
            StorageReference storageRef =
                    firebaseStorage.getReferenceFromUrl("gs://instagramprototypeproject.appspot.com").child("images").child(contentUri.getLastPathSegment());

            UploadTask uploadTask = storageRef.putFile(contentUri);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            binding.progressBar.setVisibility(View.GONE);

                            Toast.makeText(AddPhotoActivity.this, getString(R.string.upload_success),
                                    Toast.LENGTH_SHORT).show();

                            @SuppressWarnings("VisibleForTests")
                            Uri uri = taskSnapshot.getDownloadUrl();

                            // 데이터베이스에 데이터 바인딩 할 위치 생성 및 컬렉션(테이블)에 데이터 집합 생성
                            DatabaseReference images = firebaseDatabase.getReference().child("images").push();

                            // 시간 생성
                            Date date = new Date();
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                            // 컨텐츠 데이터베이스를 생성합니다.
                            ContentDTO contentDTO = new ContentDTO();

                            // 이미지 주소, 아까 입력한 다운로드 uri 주소입니다.
                            contentDTO.imageUrl = uri.toString();
                            // 유저의 UID - 현재 유저의 uid입니다.
                            contentDTO.uid = firebaseAuth.getCurrentUser().getUid();
                            // 게시물의 설명
                            contentDTO.explain = binding.addphotoEditExplain.getText().toString();
                            // 유저의 아이디
                            contentDTO.userId = firebaseAuth.getCurrentUser().getEmail();
                            // 게시물 업로드 시간, TimeStamp를 알아볼 수 있게 변환합니다.
                            contentDTO.timestamp = simpleDateFormat.format(date);

                            // 게시물 데이터를 생성 및 엑티비티 종료
                            images.setValue(contentDTO);

                            setResult(RESULT_OK);
                            finish();
                            binding.addphotoBtnUpload.setEnabled(true);
                        }
                    })
                    // 실패 시
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            binding.progressBar.setVisibility(View.GONE);
                            Toast.makeText(AddPhotoActivity.this, getString(R.string.upload_fail),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        }

        if (v.getId() == R.id.addphoto_btn_upload && photoUrl == null) {
            Toast.makeText(this, "사진 선택이 필요합니다.", Toast.LENGTH_SHORT).show();
            binding.addphotoBtnUpload.setEnabled(true);
        }
    }
}
