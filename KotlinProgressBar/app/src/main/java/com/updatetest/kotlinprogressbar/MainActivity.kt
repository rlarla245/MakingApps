package com.updatetest.kotlinprogressbar

import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var progressbar : ProgressBar? = findViewById(R.id.mainactivity_progressbar)
        var buttonVisible : Button? = findViewById(R.id.mainactivity_button1)
        var buttonStart : Button? = findViewById(R.id.mainactivity_button2)

        buttonVisible?.setOnClickListener {
            progressbar?.visibility = View.VISIBLE
        }

        buttonStart?.setOnClickListener {
            object : AsyncTask<Void, Void, Void>() {
                override fun doInBackground(vararg params: Void?): Void? {
                    for (i in 0..100) {
                        progressbar?.setProgress(i)
                        Thread.sleep(200)
                    }
                    return null
                }
            }.execute()
        }
    }
}
