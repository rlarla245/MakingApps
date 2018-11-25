package com.test.testbooktest_10;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.RatingBar;
import android.widget.TextView;

public class SecondActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        // 8. 인텐트 값을 불러옵니다.
        int[] voteResult = getIntent().getIntArrayExtra("투표 수");
        String[] nameOfImages = getIntent().getStringArrayExtra("사진 이름");

        // 9. 두 번째 레이아웃의 요소들을 배열로 불러옵니다.
        TextView[] names = {(TextView)findViewById(R.id.secondactivity_firsttextview), (TextView)findViewById(R.id.secondactivity_secondtextview), (TextView)findViewById(R.id.secondactivity_thirdtextview),
                (TextView)findViewById(R.id.secondactivity_fourthtextview), (TextView)findViewById(R.id.secondactivity_fifthttextview), (TextView)findViewById(R.id.secondactivity_sixthtextview),
                (TextView)findViewById(R.id.secondactivity_seventhtextview), (TextView)findViewById(R.id.secondactivity_eigthtextview), (TextView)findViewById(R.id.secondactivity_ninethtextview)};

        RatingBar[] ratings = {(RatingBar)findViewById(R.id.mainactivity_firstratebar), (RatingBar)findViewById(R.id.mainactivity_secondratebar), (RatingBar)findViewById(R.id.mainactivity_thirdratebar),
                (RatingBar)findViewById(R.id.mainactivity_fourthratebar), (RatingBar)findViewById(R.id.mainactivity_fifthratebar), (RatingBar)findViewById(R.id.mainactivity_sixthratebar),
                (RatingBar)findViewById(R.id.mainactivity_seventhratebar), (RatingBar)findViewById(R.id.mainactivity_eighthratebar), (RatingBar)findViewById(R.id.mainactivity_ninethratebar)};

        // 10. 입력해줍니다.
        for (int i = 0; i < names.length; i++) {
            int index = i;
            names[index].setText(nameOfImages[index]);
            ratings[index].setRating((float)voteResult[index]);
        }
    }
}
