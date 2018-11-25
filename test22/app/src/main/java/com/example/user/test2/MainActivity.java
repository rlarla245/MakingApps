package com.example.user.test2;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

import com.example.user.test2.classes.AmericanLiterature;
import com.example.user.test2.classes.BigData;
import com.example.user.test2.classes.EnglishLiterature;
import com.example.user.test2.classes.HotelCostManagement;
import com.example.user.test2.classes.ServiceScience;
import com.example.user.test2.classes.WhatIsTheJustice;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

import java.util.ArrayList;
import java.util.zip.Inflater;

public class MainActivity extends YouTubeBaseActivity {

    private String[] class_lists = {"영어 산문", "서비스 사이언스", "정의란 무엇인가", "호텔원가관리", "빅데이터로 세상 바로알기", "근대영미소설"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button make_list_button = (Button)findViewById(R.id.listmake_button);
        final Button exit_button = (Button)findViewById(R.id.exit_button);

        make_list_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("오늘의 수업은 무엇입니까?");
                builder.setView(R.layout.activity_dialog);
                builder.setIcon(R.drawable.i_7);
/*
                builder.setItems(class_lists, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (class_lists[i].toString().equals("영어 산문")) {
                            Intent intent = new Intent(view.getContext(), EnglishLiterature.class);
                            startActivity(intent);
                        }

                        if (class_lists[i].toString().equals("서비스 사이언스")) {
                            Intent intent = new Intent(view.getContext(), ServiceScience.class);
                            startActivity(intent);
                        }

                        if (class_lists[i].toString().equals("정의란 무엇인가")) {
                            Intent intent = new Intent(view.getContext(), WhatIsTheJustice.class);
                            startActivity(intent);
                        }

                        if (class_lists[i].toString().equals("호텔원가관리")) {
                            Intent intent = new Intent(view.getContext(), HotelCostManagement.class);
                            startActivity(intent);
                        }

                        if (class_lists[i].toString().equals("빅데이터로 세상 바로알기")) {
                            Intent intent = new Intent(view.getContext(), BigData.class);
                            startActivity(intent);
                        }

                        if (class_lists[i].toString().equals("근대영미소설")) {
                            Intent intent = new Intent(view.getContext(), AmericanLiterature.class);
                            startActivity(intent);
                        }
                    }
                });*/
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

        exit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder newBuilder = new AlertDialog.Builder(MainActivity.this);
                newBuilder.setMessage("정말로 종료 하시겠습니까?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                finish();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        });
                AlertDialog alertDialog = newBuilder.create();
                alertDialog.show();
            }
        });

        final YouTubePlayerView youTubePlayerView = (YouTubePlayerView)findViewById(R.id.main_youtubeplayerview);

        final YouTubePlayer.OnInitializedListener listener = new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                youTubePlayer.loadVideo("SESuctdE9vM");
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

            }
        };

        youTubePlayerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                youTubePlayerView.initialize("AIzaSyBfR4v5Q_oycxmIZwzLMYqzlMlO3HtOI_Q", listener);
            }
        });
    }
}
