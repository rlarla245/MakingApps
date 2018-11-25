package com.test.testbooktest_10;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    public Button finishButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        finishButton = (Button)findViewById(R.id.mainactivty_finishbutton);

        // 3. 모든 레이아웃을 만들어 준 뒤, 투표 수를 기록할 정수형 배열을 생성해 초기값을 생성해줍니다.
        final int[] voteCount = new int[9];
        for (int i = 0; i < voteCount.length; i++) {
            voteCount[i] = 0;
        }

        // 4. 각각의 이미지를 담는 이미지 뷰 배열을 생성합니다.
        ImageView[] images = {(ImageView)findViewById(R.id.mainactivity_firstimage), (ImageView)findViewById(R.id.mainactivity_secondimage), (ImageView)findViewById(R.id.mainactivity_thirdimage),
                (ImageView)findViewById(R.id.mainactivity_fourthimage), (ImageView)findViewById(R.id.mainactivity_fifthimage),(ImageView)findViewById(R.id.mainactivity_sixthimage),
                (ImageView)findViewById(R.id.mainactivity_seventhimage), (ImageView)findViewById(R.id.mainactivity_eighthimage),(ImageView)findViewById(R.id.mainactivity_ninethimage)};

        // 5. 각 이미지의 이름들입니다.
        final String[] nameOfImages = {"지수 1", "지수 2", "지수 3", "지수 4", "지수 5", "지수 6", "지수 7", "지수 8", "지수 9"};

        // 6. 각 이미지들을 누를 경우 카운트 증가시킵니다.
        for (int i = 0; i < images.length; i++) {
            final int index = i;
            images[index].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    voteCount[index]++;
                    // 출력시킵니다.
                    Toast.makeText(MainActivity.this, nameOfImages[index] + ": " + Integer.toString(voteCount[index]) + "표", Toast.LENGTH_SHORT).show();
                }
            });

            // 7. 버튼 클릭 시 인텐트 값을 두 번째 액티비티로 전달합니다.
            finishButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), SecondActivity.class);
                    intent.putExtra("투표 수", voteCount);
                    intent.putExtra("사진 이름", nameOfImages);
                    startActivity(intent);
                }
            });
        }
    }
}
