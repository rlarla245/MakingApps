package com.updatetest.kotlinsurfaceview

import android.media.MediaPlayer
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.SurfaceHolder
import android.view.SurfaceView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), SurfaceHolder.Callback, MediaPlayer.OnPreparedListener {
    var surfaceHolder : SurfaceHolder? = null
    var mediaPlayer : MediaPlayer? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        surfaceHolder = mainactivity_surfaceview.holder
        surfaceHolder!!.addCallback(this)

        mainactivity_button_rewind.setOnClickListener {
            var position = mediaPlayer!!.currentPosition - 5000
            mediaPlayer!!.seekTo(position)
        }

        mainactivity_button_preview.setOnClickListener {
            var position = mediaPlayer!!.currentPosition + 5000
            mediaPlayer!!.seekTo(position)
        }

        mainactivity_button_stop.setOnClickListener {
            mediaPlayer!!.pause()
        }

        mainactivity_button_start.setOnClickListener {
            mediaPlayer!!.start()
        }
    }

    override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
    }

    override fun surfaceDestroyed(holder: SurfaceHolder?) {
    }

    override fun surfaceCreated(holder: SurfaceHolder?) {
        var url = "https://www.rmp-streaming.com/media/bbb-360p.mp4"
        mediaPlayer = MediaPlayer()
        mediaPlayer!!.setDisplay(holder)

        mediaPlayer!!.setDataSource(url)

        mediaPlayer!!.prepare()
        mediaPlayer!!.setOnPreparedListener(this)
    }

    override fun onPrepared(mp: MediaPlayer?) {
        mediaPlayer!!.start()
    }
}
