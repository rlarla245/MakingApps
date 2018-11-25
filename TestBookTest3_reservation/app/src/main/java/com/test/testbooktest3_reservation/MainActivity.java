package com.test.testbooktest3_reservation;

import android.graphics.Color;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.TimePicker;

public class MainActivity extends AppCompatActivity {
    public Chronometer timenow, laptime;
    public RadioButton calenderButton, timeSettingButton;
    public ImageView defaultImage;
    public CalendarView calender;
    public TimePicker timePick;
    public Button reserveTimePickButton, reserveButton, reserveQuitButton;
    public TextView gettingYear, gettingMonth, gettingDay, gettingHour, gettingMinute;

    int selectYear;
    int selectMonth;
    int selectDay;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("시간 예약");

        timenow = (Chronometer) findViewById(R.id.mainactivity_chronometer_timenow);
        laptime = (Chronometer) findViewById(R.id.mainactivity_chronometer_laptime);

        calenderButton = (RadioButton) findViewById(R.id.mainactivity_radiobutton_calender);
        timeSettingButton = (RadioButton) findViewById(R.id.mainactivity_radiobutton_timesetting);

        defaultImage = (ImageView) findViewById(R.id.mainactivity_imageview_image);

        calender = (CalendarView) findViewById(R.id.mainactivity_calendarview_calendar);

        timePick = (TimePicker) findViewById(R.id.mainactivity_timepicker_timepick);

        reserveTimePickButton = (Button) findViewById(R.id.mainacticity_button_timepick);
        reserveButton = (Button) findViewById(R.id.mainactivity_button_reserve);
        reserveQuitButton = (Button) findViewById(R.id.mainactivity_button_quit);

        gettingYear = (TextView) findViewById(R.id.mainactiivty_textview_year);
        gettingMonth = (TextView) findViewById(R.id.mainactivity_textview_month);
        gettingDay = (TextView) findViewById(R.id.mainactivity_textview_day);
        gettingHour = (TextView) findViewById(R.id.mainactivity_textview_hout);
        gettingMinute = (TextView) findViewById(R.id.mainactivity_textview_minute);

        calender.setVisibility(View.INVISIBLE);
        timePick.setVisibility(View.INVISIBLE);

        calenderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                defaultImage.setVisibility(View.INVISIBLE);
                calender.setVisibility(View.VISIBLE);
                timePick.setVisibility(View.INVISIBLE);
            }
        });

        timeSettingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                defaultImage.setVisibility(View.INVISIBLE);
                timePick.setVisibility(View.VISIBLE);
                calender.setVisibility(View.INVISIBLE);
            }
        });

        timenow.setText("현재 시각: " + Integer.toString(timePick.getCurrentHour()) + " "
                + Integer.toString(timePick.getCurrentMinute()));

        reserveTimePickButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                laptime.setBase(SystemClock.elapsedRealtime());
                laptime.start();
                laptime.setTextColor(Color.GREEN);
            }
        });

        reserveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                laptime.stop();
                laptime.setTextColor(Color.BLUE);

                gettingYear.setText(Integer.toString(selectYear));
                gettingMonth.setText(Integer.toString(selectMonth));
                gettingDay.setText(Integer.toString(selectDay));

                gettingHour.setText(Integer.toString(timePick.getCurrentHour()));
                gettingMinute.setText(Integer.toString(timePick.getCurrentMinute()));
            }
        });

        reserveQuitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                laptime.stop();
                laptime.setText("예약에 걸린 시간: 00:00");
                laptime.setTextColor(Color.BLACK);

                gettingYear.setText("0000");
                gettingMonth.setText("00");
                gettingDay.setText("00");
                gettingHour.setText("00");
                gettingMinute.setText("00");
            }
        });

        calender.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                selectYear = year;
                selectMonth = month + 1;
                selectDay = dayOfMonth;
            }
        });
    }
}
