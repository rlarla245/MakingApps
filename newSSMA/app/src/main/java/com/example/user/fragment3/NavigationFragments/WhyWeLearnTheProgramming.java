package com.example.user.fragment3.NavigationFragments;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import com.example.user.fragment3.R;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

public class WhyWeLearnTheProgramming extends YouTubeBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_why_we_learn_the_programming);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        final YouTubePlayerView youTubePlayerView1 = (YouTubePlayerView)findViewById(R.id.first_youtubeplayerview);

        final YouTubePlayer.OnInitializedListener listener1 = new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, final YouTubePlayer youTubePlayer, boolean b) {
                youTubePlayer.loadVideo("SESuctdE9vM");
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

            }
        };

        youTubePlayerView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                youTubePlayerView1.initialize("AIzaSyBfR4v5Q_oycxmIZwzLMYqzlMlO3HtOI_Q", listener1);
            }
        });

        final YouTubePlayerView youTubePlayer2 = (YouTubePlayerView)findViewById(R.id.second_youtubeplayerview);

        final YouTubePlayer.OnInitializedListener listener2 = new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, final YouTubePlayer youTubePlayer, boolean b) {
                youTubePlayer.loadVideo("GQqjeAVdpcM");
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

            }
        };

        youTubePlayer2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                youTubePlayer2.initialize("AIzaSyBfR4v5Q_oycxmIZwzLMYqzlMlO3HtOI_Q", listener2);
            }
        });
    }
}
