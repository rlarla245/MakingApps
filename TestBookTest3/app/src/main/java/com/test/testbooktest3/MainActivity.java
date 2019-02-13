package com.test.testbooktest3;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    public TextView selectionTextView;
    public Switch startSwitch;
    public LinearLayout androidLayout;
    public LinearLayout buttonsLayout;
    public RadioButton lollipop;
    public RadioButton mashmellow;
    public RadioButton nuga;
    public ImageView androidImageView;
    public Button exitButton;
    public Button returnButton;
    public Button rotateButton;
    public RadioGroup radioGroup;
    public EditText autoImport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Test >.<");

        selectionTextView = (TextView) findViewById(R.id.mainactivitiy_textview_androidversion);
        startSwitch = (Switch) findViewById(R.id.mainactivity_switch_androidversion);
        androidLayout = (LinearLayout) findViewById(R.id.mainactivity_linearlayout_androidversion);
        buttonsLayout = (LinearLayout) findViewById(R.id.mainactivity_linearlayout_buttons);

        lollipop = (RadioButton) findViewById(R.id.mainactivity_radiobutton_lollypop);
        mashmellow = (RadioButton) findViewById(R.id.mainactivity_radiobutton_mashimellow);
        nuga = (RadioButton) findViewById(R.id.mainactivity_radiobutton_nuga);
        androidImageView = (ImageView) findViewById(R.id.mainactivity_imageview_androidimage);
        exitButton = (Button) findViewById(R.id.mainactivity_button_exit);
        returnButton = (Button) findViewById(R.id.mainactivity_button_return);
        rotateButton = (Button) findViewById(R.id.mainactivity_button_rotation);

        radioGroup = (RadioGroup) findViewById(R.id.mainactivity_radiogroup_radiobuttons);

        startSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (startSwitch.isChecked()) {
                    androidLayout.setVisibility(View.VISIBLE);
                    buttonsLayout.setVisibility(View.VISIBLE);

                    lollipop.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            androidImageView.setImageResource(R.drawable.i_1);
                        }
                    });

                    mashmellow.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            androidImageView.setImageResource(R.drawable.i_2);
                        }
                    });

                    nuga.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            androidImageView.setImageResource(R.drawable.i_3);
                        }
                    });

                    exitButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(MainActivity.this, "종료합니다.", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });

                    returnButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(MainActivity.this, "처음으로 돌아갑니다.", Toast.LENGTH_SHORT).show();
                            startSwitch.setChecked(false);
                        }
                    });

                    // 각도(0 - 360도)
                    final int[] rotationNumber = {0};

                    // 버튼을 누르면 수동으로 10도 씩 그림이 회전합니다.
                    rotateButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            rotationNumber[0] += 10;
                            androidImageView.setRotation(rotationNumber[0]);
                        }
                    });

                } else {
                    androidLayout.setVisibility(View.INVISIBLE);
                    buttonsLayout.setVisibility(View.INVISIBLE);
                    androidImageView.setRotation(0);
                }
            }
        });
    }
}
