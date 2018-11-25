package com.test.testbooktest4_filereadwrite_diarytest;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    // 1. 마찬가지로 레이아웃 설정 후 메인 액티비티에 생성 및 호출해줍니다.
    // 파일 이름을 전역 변수로 선언해주는게 특징입니다. 버튼은 버그를 방지하기 위해 잠시 꺼줍니다.
    protected DatePicker datePicker;
    protected EditText writeDiary;
    protected Button fileButton;
    protected String fileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("일기장");

        datePicker = (DatePicker)findViewById(R.id.mainactivity_datepicker_datepick);
        writeDiary = (EditText) findViewById(R.id.mainacitvty_edittext_textview);
        fileButton = (Button)findViewById(R.id.mainactivity_button_filebutton);
        fileButton.setEnabled(false);

        // 2. Calendar 클래스를 활용해 인스턴스를 호출한 뒤 현재 시간을 불러옵니다.
        Calendar cal = Calendar.getInstance();
        final int cYear = cal.get(Calendar.YEAR);
        final int cMonth = cal.get(Calendar.MONTH);
        final int cDay = cal.get(Calendar.DAY_OF_MONTH);

        try {
            // 오늘 날짜의 일기를 일단 불러옵시다.
            String defaultDiary = readDiary(Integer.toString(cYear) + "_" + Integer.toString(cMonth + 1) + "_" + Integer.toString(cDay) +"일" + ".txt");
            writeDiary.setText(defaultDiary);
            fileButton.setEnabled(true);
            fileButton.setText("수정하기");

        } catch (NullPointerException e) {
            writeDiary.setHint("일기 없음");
            fileButton.setText("일기 쓰기");
        }


        // 3. 시간 초기화 메소드를 호출한 뒤 파라미터를 해당 날짜로 설정해줍니다.
        datePicker.init(cYear, cMonth, cDay, new DatePicker.OnDateChangedListener() {
            @Override
            // 4. 데이터 변경 시 작동하는 메소드를 호출합니다.
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                // 5. 정수형을 문자형으로 변환해줘야 합니다. 변경된 날짜로 지정해줘야 하며 월 단위의 경우 0부터 시작하기 때문에 1을 더해줘야 합니다.
                fileName = Integer.toString(year) + "_" + Integer.toString(monthOfYear + 1) + "_" + Integer.toString(dayOfMonth) +"일" + ".txt";

                // 6. 해당 파일의 텍스트를 문자열로 변환해줍니다. readDiary 메소드를 생성하여 파일을 불러옵니다.
                String fileContext = readDiary(fileName);

                // 7. 불러온 텍스트를 텍스트 뷰에 입력합니다.
                writeDiary.setText(fileContext);

                // 8. 버튼을 다시 활성화합니다.
                fileButton.setEnabled(true);
            }
        });

        // 17. 쓰기 기능(수정/새로 저장)을 활성화 합니다.
        fileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    // 쓰기 기능을 불러올 때엔 MODE_PRIVATE이 중요합니다.
                    FileOutputStream writeFile = openFileOutput(fileName, Context.MODE_PRIVATE);

                    // 18. 수정 또는 새로 작성한 일기를 문자열로 불러와 바이트 형태로 변환합니다.
                    String diaryContext = writeDiary.getText().toString();
                    writeFile.write(diaryContext.getBytes());

                    // 19. 닫아줍니다.
                    writeFile.close();

                    Toast.makeText(MainActivity.this, fileName + "가 저장되었습니다.", Toast.LENGTH_SHORT).show();
                    writeDiary.setText("");
                    writeDiary.setHint("작성이 완료되었습니다.");

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    String readDiary(String fileName) {
        // 9. 문자열의 일기 내용을 리턴값으로 지정해줘야 하므로 변수를 선언해줍니다.
        String diaryContext = null;

        try {
            // 10. 파일을 읽어오기 위해 변수를 생성합니다.
            FileInputStream readFile = openFileInput(fileName);

            // 11. 불러오기 위해 바이트 배열로 변환해줍니다.
            byte[] readFileByte = new byte[readFile.available()];

            // 12. 바이트 배열로 변환했으니 파일을 불러옵니다.
            readFile.read(readFileByte);

            // 13. 불러왔으니 닫습니다.
            readFile.close();

            // 14. 리턴해 올 내용을 다시 문자열로 담아주고 정돈합니다.
            diaryContext = (new String(readFileByte)).trim();

            // 15. 버튼을 활성화시키고 기존 일기를 수정할 경우로 바꿔줍니다.
            fileButton.setText("수정하기");

        } catch (IOException e) {
            // 16. 파일이 없을 경우 텍스트 뷰에 없다는 문자열을 호출해주고 버튼도 변경해줍니다.
            writeDiary.setHint("일기 없음");
            fileButton.setText("일기 쓰기");
        }
        return diaryContext;
    }

}
