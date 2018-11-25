package com.test.testbooktest2;

import android.graphics.drawable.Icon;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.style.TabStopSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    public Button plusButton;
    public Button minusButton;
    public Button MultiplyButton;
    public Button DividingButton;
    public Button ExtraNumberButton;

    public EditText firstNumber;
    public EditText secondNumber;

    public TextView resultNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("버튼 계산기");

        plusButton = (Button)findViewById(R.id.mainactivity_button_plus);
        minusButton = (Button)findViewById(R.id.mainactivity_button_minus);
        MultiplyButton = (Button)findViewById(R.id.mainactivity_button_multiply);
        DividingButton = (Button)findViewById(R.id.mainactivity_button_dividing);
        ExtraNumberButton = (Button)findViewById(R.id.mainactivity_button_extranumber);

        firstNumber = (EditText)findViewById(R.id.mainactivity_edittext_number1);
        secondNumber = (EditText)findViewById(R.id.mainactivity_edittext_number2);

        resultNumber = (TextView)findViewById(R.id.mainactivity_textview_resultnumber);

        final Button[] buttons = {(Button)findViewById(R.id.mainactivity_button_onebutton), (Button)findViewById(R.id.mainactivity_button_twobutton), (Button)findViewById(R.id.mainactivity_button_threebutton),
                (Button)findViewById(R.id.mainactivity_button_fourbutton), (Button)findViewById(R.id.mainactivity_button_fifthbutton), (Button)findViewById(R.id.mainactivity_button_sixbutton),
                (Button)findViewById(R.id.mainactivity_button_sevenbutton), (Button)findViewById(R.id.mainactivity_button_eightbutton), (Button)findViewById(R.id.mainactivity_button_ninebutton),
                (Button)findViewById(R.id.mainactivity_button_tenbutton)};

        for (int i = 0; i < buttons.length; i++) {
            final int index = i;
            buttons[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (firstNumber.isFocused()) {
                        // 인덱스 값을 문자열로 변환해서 Edittext에 입력하기(+)
                        firstNumber.setText(firstNumber.getText().toString() + buttons[index].getText().toString());
                    }

                    if (secondNumber.isFocused()) {
                        secondNumber.setText(secondNumber.getText().toString() + buttons[index].getText().toString());
                    }

                    if (!firstNumber.isFocused() && !secondNumber.isFocused()) {
                        Toast.makeText(MainActivity.this, "텍스트 입력창에서 입력가능합니다 :)", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        plusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (firstNumber.getText().toString().equals("") || secondNumber.getText().toString().equals("")) {
                    Toast.makeText(MainActivity.this, "빈 칸 입력은 불가합니다.", Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    Double doubleFirstNumber = Double.valueOf(firstNumber.getText().toString());
                    Double doubleSecondNumber = Double.valueOf(secondNumber.getText().toString());

                    Double doubleResult = (doubleFirstNumber + doubleSecondNumber);

                    resultNumber.setText("계산 결과: " + doubleResult.toString());
                } catch (NumberFormatException e) {
                    Toast.makeText(MainActivity.this, "숫자 입력이 필요합니다.", Toast.LENGTH_SHORT).show();
                    return;
                }

            }
        });

        minusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (firstNumber.getText().toString().equals("") || secondNumber.getText().toString().equals("")) {
                    Toast.makeText(MainActivity.this, "빈 칸 입력은 불가합니다.", Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    Double doubleFirstNumber = Double.valueOf(firstNumber.getText().toString());
                    Double doubleSecondNumber = Double.valueOf(secondNumber.getText().toString());

                    Double result = (doubleFirstNumber - doubleSecondNumber);

                    resultNumber.setText("계산 결과: " + result.toString());
                } catch (NumberFormatException e) {
                    Toast.makeText(MainActivity.this, "숫자 입력이 필요합니다.", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });

        MultiplyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (firstNumber.getText().toString().equals("") || secondNumber.getText().toString().equals("")) {
                    Toast.makeText(MainActivity.this, "빈 칸 입력은 불가합니다.", Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    Double doubleFirstNumber = Double.valueOf(firstNumber.getText().toString());
                    Double doubleSecondNumber = Double.valueOf(secondNumber.getText().toString());

                    Double result = (doubleFirstNumber * doubleSecondNumber);

                    resultNumber.setText("계산 결과: " + result.toString());
                } catch (NumberFormatException e) {
                    Toast.makeText(MainActivity.this, "숫자 입력이 필요합니다.", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });

        DividingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (firstNumber.getText().toString().equals("") || secondNumber.getText().toString().equals("")) {
                    Toast.makeText(MainActivity.this, "빈 칸 입력은 불가합니다.", Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    Double doubleFirstNumber = Double.valueOf(firstNumber.getText().toString());
                    Double doubleSecondNumber = Double.valueOf(secondNumber.getText().toString());

                    if (doubleSecondNumber == 0) {
                        Toast.makeText(MainActivity.this, "0으로 나눌 수 없습니다", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    else {
                        Double result = (doubleFirstNumber / doubleSecondNumber);
                        resultNumber.setText("계산 결과: " + result.toString());
                    }
                } catch (NumberFormatException e) {
                    Toast.makeText(MainActivity.this, "숫자 입력이 필요합니다.", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });

        ExtraNumberButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (firstNumber.getText().toString().equals("") || secondNumber.getText().toString().equals("")) {
                    Toast.makeText(MainActivity.this, "빈 칸 입력은 불가합니다.", Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    Double doubleFirstNumber = Double.valueOf(firstNumber.getText().toString());
                    Double doubleSecondNumber = Double.valueOf(secondNumber.getText().toString());

                    Integer intFirstNumber = Integer.valueOf(firstNumber.getText().toString());
                    Integer intSecondNumber = Integer.valueOf(secondNumber.getText().toString());

                    if (doubleSecondNumber == 0 || intSecondNumber == 0) {
                        Toast.makeText(MainActivity.this, "0으로 나눌 수 없습니다", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    else {
                        Double divideDoubleResult = (doubleFirstNumber / doubleSecondNumber);
                        Integer divideIntResult = (intFirstNumber / intSecondNumber);

                        Double doubleResultNumber = divideDoubleResult - divideIntResult;

                        resultNumber.setText("계산 결과: " + doubleResultNumber.toString());
                    }
                } catch (NumberFormatException e) {
                    Toast.makeText(MainActivity.this, "숫자 입력이 필요합니다.", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });
    }
}
