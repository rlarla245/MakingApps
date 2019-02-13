package com.updatetest.kotlintimedatepicker

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.TimePicker
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import kotlin.math.min

class MainActivity : AppCompatActivity() {
    var cal = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mainactivity_button_date.setOnClickListener {
            showDate()
        }

        mainactivity_button_time.setOnClickListener {
            showTime()
        }
    }

    fun showDate() {
        DatePickerDialog(this, DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            Toast.makeText(this, "${year}년 ${month}월 ${dayOfMonth}일입니다.", Toast.LENGTH_SHORT).show()
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE)).show()
    }

    fun showTime() {
        TimePickerDialog(this, TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
            Toast.makeText(this, "${hourOfDay}시 ${minute}분입니다.", Toast.LENGTH_SHORT).show()

        }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true).show()
    }
}
