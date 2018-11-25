package com.test.testbooktest_mp3player;

import android.media.MediaPlayer;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

public class MainActivity extends AppCompatActivity {
    // 레이아웃에 설정한 위젯들을 선언해줍니다.
    protected ListView mp3ListView;
    protected Button playButton, pauseButton, stopButton;
    protected SeekBar mp3Seekbar;
    protected TextView mp3NameTextView, mp3TimeTextView;

    // mp3 곡들을 담을 리스트를 선언합니다.
    ArrayList<String> mp3List;

    // 유저가 선택한 mp3 파일입니다.
    String selectedMP3;

    // 정지, 다른 노래로의 전환을 위해 카운트 변수를 생성합니다.
    int countNumber;

    // 경로 및 mp3 플레이어를 선언합니다.
    String mp3Path = "/sdcard" + "/";
    MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("simple mp3Player");

        // 1. 외부 저장소를 쓸 수 있는 권한을 불러옵니다.
        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission
                .WRITE_EXTERNAL_STORAGE}, MODE_PRIVATE);

        // 선언한 인스턴스 변수들을 호출합니다.
        mp3ListView = findViewById(R.id.mainactivity_listview_mp3listview);
        playButton = (Button) findViewById(R.id.mainactivity_button_playbutton);
        pauseButton = (Button) findViewById(R.id.mainactivity_button_pausebutton);
        stopButton = (Button) findViewById(R.id.mainactivity_button_stopbutton);
        mp3Seekbar = (SeekBar) findViewById(R.id.mainactivity_seekbar);
        mp3NameTextView = (TextView) findViewById(R.id.mainactivity_textview_musictextview);
        mp3TimeTextView = (TextView) findViewById(R.id.mainactivity_textview_musictimetextview);
        mp3List = new ArrayList<String>();

        // 파일 리스트입니다.
        File[] listFiles = new File(mp3Path).listFiles();
        String fileName, extName;

        for (File file : listFiles) {
            fileName = file.getName();

            // 파일 이름 맨 뒤 3글자를 추출합니다. 확장자가 되겠죠? mp3 파일만 불러옵니다.
            extName = fileName.substring(fileName.length() - 3);
            if (extName.equals("mp3")) {
                mp3List.add(fileName);
            }
        }

        // 리스트의 어댑터를 설정합니다.
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, mp3List);

        mp3ListView.setAdapter(adapter);

        // 카운트 변수를 먼저 0으로 설정해줍니다.
        countNumber = 0;

        // 실행할 미디어 플레이어도 설정합니다.
        mediaPlayer = new MediaPlayer();
        mp3ListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    // 일단 한 번 누르면 카운트 변수가 1씩 증가합니다.
                    // 카운트 변수가 필요한 이유는 리스트 클릭 시 중지 및 다른 노래로의 전환을 위함입니다.
                    countNumber = countNumber + 1;

                    // 한 번 클릭 시
                    if (countNumber == 1) {
                        // 미디어 플레이어의 데이터 소스. 즉, 해당 mp3 파일을 설정해줍니다.
                        mediaPlayer.setDataSource(mp3Path + mp3List.get(position));
                        mediaPlayer.prepare();
                        mediaPlayer.start();

                        // 유저가 현재 선택한 mp3 파일입니다.
                        selectedMP3 = mp3List.get(position);

                        playButton.setClickable(false);
                        pauseButton.setClickable(true);
                        stopButton.setClickable(true);
                        mp3NameTextView.setText("실행중인 음악: " + selectedMP3);
                        mp3Seekbar.setVisibility(VISIBLE);

                        // 스레드 실행을 통해 노래가 재생됨에 따른 진행 시간, seekbar 위치 조절을 실행합니다.
                        new Thread() {
                            // 진행 시간을 분:초 단위로 표현합니다.
                            SimpleDateFormat timeFormat = new SimpleDateFormat("mm:ss");

                            public void run() {
                                // seekBar의 최대치를 해당 노래의 끝으로 설정합니다.
                                mp3Seekbar.setMax(mediaPlayer.getDuration());

                                // 노래가 시작되는 동안 seekBar도 진행시켜야 합니다.
                                while (mediaPlayer.isPlaying()) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            // 노래가 진행되는 순간마다 이동합니다.
                                            mp3Seekbar.setProgress(mediaPlayer.getCurrentPosition());

                                            // 그 순간의 진행시간으로 이동합니다.
                                            mp3TimeTextView.setText("진행 시간: " + timeFormat
                                                    .format(mediaPlayer.getCurrentPosition()));
                                        }
                                    });
                                    // 0.2초마다 진행 상태가 변경됩니다.
                                    SystemClock.sleep(200);
                                }
                            }
                        }.start();

                        // seekbar의 일정 지점을 클릭하면 거기서부터 노래가 진행됩니다.
                        mp3Seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                            @Override
                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                // 유저가 선택한 지점으로 이동합니다.
                                if (fromUser) {
                                    mediaPlayer.seekTo(progress);
                                    mediaPlayer.start();
                                }
                            }

                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) {

                            }

                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) {

                            }
                        });
                    }

                    // 두 번 눌렀으며 + 똑같은 걸 또 누름 -> 정지
                    if (countNumber > 1 && selectedMP3.equals(mp3List.get(position))) {
                        mediaPlayer.stop();
                        mediaPlayer.reset();

                        playButton.setClickable(true);
                        pauseButton.setClickable(false);
                        stopButton.setClickable(false);

                        mp3Seekbar.setVisibility(INVISIBLE);
                        // 다시 0으로 재설정하여 초기화시킵니다.
                        countNumber = 0;

                        mp3NameTextView.setText("실행중인 음악이 없습니다.");
                        mp3TimeTextView.setText("진행 시간:");
                    }

                    // 두 번 눌렀으며 + 다른 걸 누름 -> 노래 전환
                    if (countNumber > 1 && !selectedMP3.equals(mp3List.get(position))) {
                        mediaPlayer.stop();
                        mediaPlayer.reset();

                        // 다른 노래를 선택한 것이므로 데이터 소스 및 선택 파일을 변경해주어야 합니다.
                        // 클릭한 position값의 노래입니다.
                        mediaPlayer.setDataSource(mp3Path + mp3List.get(position));
                        selectedMP3 = mp3List.get(position);

                        mediaPlayer.prepare();
                        mediaPlayer.start();

                        playButton.setClickable(false);
                        pauseButton.setClickable(true);
                        stopButton.setClickable(true);
                        mp3NameTextView.setText("실행중인 음악: " + selectedMP3);
                        mp3Seekbar.setVisibility(VISIBLE);

                        // 노래가 재생되고 있으므로 0이 아닌 1의 값이 필요하므로 1을 줄여줍니다.
                        countNumber--;

                        // 스레드 실행을 통해 노래가 재생됨에 따른 진행 시간, seekbar 위치 조절을 실행합니다.
                        new Thread() {
                            SimpleDateFormat timeFormat = new SimpleDateFormat("mm:ss");

                            public void run() {
                                mp3Seekbar.setMax(mediaPlayer.getDuration());

                                while (mediaPlayer.isPlaying()) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            mp3Seekbar.setProgress(mediaPlayer.getCurrentPosition());
                                            mp3TimeTextView.setText("진행 시간: "
                                                    + timeFormat.format(mediaPlayer.getCurrentPosition()));
                                        }
                                    });
                                    SystemClock.sleep(200);
                                }
                            }
                        }.start();
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        // 재생 버튼을 누릅니다.
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mediaPlayer = new MediaPlayer();
                    mediaPlayer.setDataSource(mp3Path + selectedMP3);
                    mediaPlayer.prepare();
                    mediaPlayer.start();

                    playButton.setClickable(false);
                    pauseButton.setClickable(true);
                    stopButton.setClickable(true);

                    if (selectedMP3 == null) {
                        mp3NameTextView.setText("실행중인 음악이 없습니다.");
                        mp3Seekbar.setVisibility(INVISIBLE);
                    } else {
                        mp3NameTextView.setText("실행중인 음악: " + selectedMP3);
                        mp3Seekbar.setVisibility(VISIBLE);
                    }

                    // 스레드 실행을 통해 노래가 재생됨에 따른 진행 시간, seekbar 위치 조절을 실행합니다.
                    new Thread() {
                        SimpleDateFormat timeFormat = new SimpleDateFormat("mm:ss");

                        public void run() {
                            mp3Seekbar.setMax(mediaPlayer.getDuration());

                            while (mediaPlayer.isPlaying()) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mp3Seekbar.setProgress(mediaPlayer.getCurrentPosition());
                                        mp3TimeTextView.setText("진행 시간: " +
                                                timeFormat.format(mediaPlayer.getCurrentPosition()));
                                    }
                                });
                                SystemClock.sleep(200);
                            }
                        }
                    }.start();

                } catch (Exception e) {
                }
            }
        });

        // 중지 버튼을 누를 경우입니다.
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.stop();
                mediaPlayer.reset();

                playButton.setClickable(true);
                pauseButton.setClickable(false);
                stopButton.setClickable(false);

                // 아무것도 선택된 것이 없는 초기 상태입니다.
                if (selectedMP3 == null) {
                    mp3NameTextView.setText("실행중인 음악이 없습니다.");
                    mp3TimeTextView.setText("진행 시간:");
                    mp3Seekbar.setVisibility(INVISIBLE);
                } else {
                    mp3NameTextView.setText("실행중인 음악이 없습니다.");
                    mp3TimeTextView.setText("진행 시간:");
                    mp3Seekbar.setVisibility(INVISIBLE);
                }
            }
        });

        // 일시중지 버튼을 누를 경우입니다.
        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.pause();

                playButton.setClickable(true);
                pauseButton.setClickable(true);
                stopButton.setClickable(true);

                if (selectedMP3 == null) {
                    mp3NameTextView.setText("실행중인 음악이 없습니다.");
                    mp3Seekbar.setVisibility(INVISIBLE);
                } else {
                    mp3NameTextView.setText("실행중인 음악: " + selectedMP3);
                    mp3Seekbar.setVisibility(VISIBLE);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        mediaPlayer.stop();
        super.onBackPressed();
    }
}
