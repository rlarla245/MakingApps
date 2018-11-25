package com.example.user.camera2;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private static final int CAMERA_CODE = 0;
    private static final int GET_PHOTO_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requirePermission();

        Button camera_button = (Button) findViewById(R.id.camera_button);
        camera_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    boolean use_camera = ContextCompat.checkSelfPermission(view.getContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
                    boolean write_camera = ContextCompat.checkSelfPermission(view.getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;

                    if (use_camera && write_camera) {
                        takePicture();
                    } else {
                        Toast.makeText(MainActivity.this, "권한 대기중입니다.", Toast.LENGTH_SHORT).show();
                    }
                }
        });

        Button save_button = (Button)findViewById(R.id.save_photo_camera);
        save_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                galleryAddPic();
            }
        });

        Button get_photo_button = (Button)findViewById(R.id.get_photo_camera);
        get_photo_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickUpPicture();
            }
        });
    }

    void pickUpPicture() {
        Intent get_photo = new Intent(Intent.ACTION_PICK);
        get_photo.setType(MediaStore.Images.Media.CONTENT_TYPE);
        get_photo.setType("image/*");
        startActivityForResult(get_photo, GET_PHOTO_CODE);
    }

    void takePicture() {
        Intent camera_intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            File photoFile = createImageFile();
            Uri photoUri = FileProvider.getUriForFile(this,"com.example.user.camera2.fileprovider", photoFile);
            camera_intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            startActivityForResult(camera_intent, GET_PHOTO_CODE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_CODE) {
            ImageView take_imageview = (ImageView)findViewById(R.id.photo_imageview);
            take_imageview.setImageBitmap(BitmapFactory.decodeFile(mCurrentPhotoPath));
        }

        if (requestCode == GET_PHOTO_CODE && resultCode == RESULT_OK) {
            Uri photoUri = data.getData();
            ImageView get_imageview = (ImageView)findViewById(R.id.get_photo_imageview);
            get_imageview.setImageURI(photoUri);
        }
    }

    void requirePermission() {
        String[] permissions = new String[] {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        ArrayList<String> listPermissionNeeded = new ArrayList<>();

        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_DENIED) {
                listPermissionNeeded.add(permission);
            }

            if (!listPermissionNeeded.isEmpty()) {
                ActivityCompat.requestPermissions(this, listPermissionNeeded.toArray(new String[listPermissionNeeded.size()]), 10);
            }
        }
     }

    private String mCurrentPhotoPath;

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        // File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }
    
    // 사진을 갤러리에서 불러오는 메소드
    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
        Toast.makeText(this, "사진을 저장했습니다.", Toast.LENGTH_SHORT).show();
    }
}
