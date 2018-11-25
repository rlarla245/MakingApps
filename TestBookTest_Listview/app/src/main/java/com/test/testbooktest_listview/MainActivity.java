package com.test.testbooktest_listview;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    public EditText plusEditText;
    public Button plusButton;
    public ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("리스트 추가");

        plusEditText = (EditText)findViewById(R.id.mainactivty_edittext_firstedittext);
        plusButton = (Button)findViewById(R.id.mainactivity_button_plusbutton);
        listView = (ListView)findViewById(R.id.mainactivity_listview);

        // 1. 리스트에 담을 문자열 리스트를 생성합니다.
        final ArrayList<String> items = new ArrayList<String>();

        // 2. 리스트 뷰를 레이아웃이 담을 수 있게 어댑터를 설정합니다.
        // 단순히 아이템들을 리스트 정렬시키겠다는 뜻입니다.
        final ArrayAdapter<String> adapter
                = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items);
        listView.setAdapter(adapter);

        // 3. 버튼 누를 시 추가합니다.
        plusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (plusEditText.getText().toString().trim().equals("")) {
                    Toast.makeText(MainActivity.this, "입력이 필요합니다.", Toast.LENGTH_SHORT).show();
                } else {
                    items.add(plusEditText.getText().toString());
                    // 리스트 추가로 인한 데이터 변동 시 새로 출력합니다.
                    adapter.notifyDataSetChanged();
                }
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(MainActivity.this, String.valueOf(items.get(position)), Toast.LENGTH_SHORT).show();
            }
        });

        // 4. 리스트 요소를 길게 누르면 삭제됩니다.
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                // 5. 대화상자 생성 후 쿼리 기능을 제공합니다.
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage("삭제하시겠습니까?")
                        .setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(MainActivity.this, String.valueOf(items.get(position)) + "가 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                                // 리스트 삭제로 인한 데이터 변동 시 새로 출력합니다.
                                items.remove(position);
                                adapter.notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                builder.show();
                return false;
            }
        });
    }
}
