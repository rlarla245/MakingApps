package com.test.testbooktest_sdcardfilereadwrite;

import android.Manifest;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    Button prevButton, nextButton;
    TextView curNumberTextView, wholeNumberTextView;
    myPictureView myPictureView;

    // 5. 필요한 변수 생성. 순서대로 현재 이미지 순서, 이미지 파일 배열, 파일 이름
    int numberOfImage;
    File[] imageFiles;
    String imageFname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("ImageViewer");

        ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, MODE_PRIVATE);

        // 6. 각 변수들을 호출해줍니다.
        prevButton = (Button) findViewById(R.id.mainactivity_button_previouspicture);
        nextButton = (Button) findViewById(R.id.mainacitivty_button_nextpicture);

        curNumberTextView = (TextView) findViewById(R.id.mainactivity_textview_numberofcurrentpicture);
        wholeNumberTextView = (TextView) findViewById(R.id.mainactivity_textview_numberofwholepicture);

        myPictureView = (myPictureView) findViewById(R.id.mainactivity_mypictureview);

        // 7. 여기서 해당 폴더를 지정해줍니다.
        imageFiles = new File(Environment.DIRECTORY_DCIM).listFiles();

        // 8. 해당 이미지 파일의 이름입니다.
        imageFname = imageFiles[0].toString();

        // 9. 경로 설정해줍니다.
        myPictureView.imagePath = imageFname;
        wholeNumberTextView.setText(Integer.toString(imageFiles.length));
        curNumberTextView.setText(Integer.toString(numberOfImage + 1));

        // 10. 각 버튼을 눌렀을 경우 반응하도록 만듭니다.
        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (numberOfImage <= 0) {
                    Toast.makeText(MainActivity.this, "첫번째 그림입니다.", Toast.LENGTH_SHORT).show();

                } else {
                    numberOfImage--;
                    imageFname = imageFiles[numberOfImage].toString();
                    myPictureView.imagePath = imageFname;
                    // 11. invalidate 메소드를 실행해야 onDraw 메소드가 실행됩니다.
                    myPictureView.invalidate();
                }
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (numberOfImage >= imageFiles.length - 1) {
                    Toast.makeText(MainActivity.this, "마지막 그림입니다.", Toast.LENGTH_SHORT).show();
                } else {
                    numberOfImage++;
                    imageFname = imageFiles[numberOfImage].toString();
                    myPictureView.imagePath = imageFname;
                    myPictureView.invalidate();
                }
            }
        });
    }
}
