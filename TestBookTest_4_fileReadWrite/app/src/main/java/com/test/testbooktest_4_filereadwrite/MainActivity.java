package com.test.testbooktest_4_filereadwrite;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    // 1. 레이아웃에서 쓰고 읽을 버튼을 생성합니다.
    // 2. 메인 액티비티에서 버튼 생성 및 호출합니다.
    // 3, 쓰기 코드를 먼저 작성합니다. FileOutputStream writeFile = openFileOutput("file.txt", Context.MODE_PRIVATE);

    protected Button fileWriteButton, fileReadButton;
    private TextView fileTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fileWriteButton = (Button)findViewById(R.id.mainactivty_button_writebutton);
        fileReadButton = (Button)findViewById(R.id.mainactivty_button_readbutton);
        fileTextView = (TextView)findViewById(R.id.mainactivity_textview_filetextview);

        fileWriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    FileOutputStream writeFile = openFileOutput("file.txt", Context.MODE_PRIVATE);
                    // 4. 쓰고 싶은 내용을 설정합시다.
                    String context = "반갑습니다. 테스트 파일입니다";

                    // 5. 바이트 형태로 변환해야 합니다. 파이썬과 동일합니다.
                    writeFile.write(context.getBytes());

                    // 6. 파일을 읽거나 썼을 경우 닫아줘야 됩니다.
                    writeFile.close();

                    // 7. 완료 메시지를 출력합니다.
                    Toast.makeText(MainActivity.this, "파일 생성이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                } catch (FileNotFoundException e) {

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        fileReadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    // 8. 파일을 읽어오는 인스턴스를 호출합니다. 파라미터 값에 해당하는 이름의 파일을 불러옵니다.
                    FileInputStream readFile = openFileInput("file.txt");

                    // 9. 문자열의 내용을 바로 불러올 수 없으므로 다시 바이트 형태로 변환해야 합니다.
                    byte[] txtFile = new byte[readFile.available()];

                    // 10. 바이트 형태로 파일의 내용을 불러옵니다.
                    readFile.read(txtFile);

                    // 11. 해당 내용을 다시 문자열로 변환해옵니다.
                    String readContext = new String(txtFile);

                    // 12. TextView 등을 활용해도 됩니다.
                    Toast.makeText(MainActivity.this, readContext, Toast.LENGTH_SHORT).show();
                    fileTextView.setText(readContext);

                    // 13. 파일을 읽거나 썼으면 닫아줍니다.
                    readFile.close();

                } catch (FileNotFoundException e) {
                    Toast.makeText(MainActivity.this, "해당하는 파일이 없습니다.", Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
