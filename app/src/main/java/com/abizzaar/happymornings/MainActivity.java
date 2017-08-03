package com.abizzaar.happymornings;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;

import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;

public class MainActivity extends AppCompatActivity {

    AlarmManager alarmManager;
    TimePicker timePicker;
    TextView updateText;
    Context context;
    Calendar calendar;
    Calendar currentTimeCalendar;
    PendingIntent pendingIntent;
    Intent myIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // initialize
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        timePicker = (TimePicker) findViewById(R.id.timePicker);
        updateText = (TextView) findViewById(R.id.updateText);
        Button alarmOn = (Button) findViewById(R.id.alarmOn);
        Button alarmOff = (Button) findViewById(R.id.alarmOff);
        currentTimeCalendar = Calendar.getInstance();

        // create intent for Alarm Receiver class
        myIntent = new Intent(this, AlarmReceiver.class);



        // when alarmOn button is clicked
        alarmOn.setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {

                        // set calendar stuff
                        calendar = Calendar.getInstance();
                        calendar.set(Calendar.HOUR_OF_DAY, timePicker.getHour());
                        calendar.set(Calendar.MINUTE, timePicker.getMinute());
                        calendar.set(Calendar.SECOND, 0);

                        // track for if alarm is being set for new day
                        // TO DO!
                        int presentMinutes = currentTimeCalendar.get(Calendar.HOUR_OF_DAY) * 60 + currentTimeCalendar.get(Calendar.MINUTE);
                        int alarmMinutes = calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE);

                        if (alarmMinutes < presentMinutes) calendar.add(Calendar.DAY_OF_MONTH, 1);

                        // get minute and hour and display it
                        int hour = timePicker.getHour();
                        int minute = timePicker.getMinute();
                        String hourString = String.valueOf(hour);
                        String minuteString = String.valueOf(minute);
                        if (hour > 12) hourString = String.valueOf(hour - 12);
                        if (minute < 10) minuteString = "0" + String.valueOf(minute);

                        SetAlarmText("Alarm set for " + hourString + ":" + minuteString);

                        // tell AlarmReceiver that alarmOn was clicked
                        myIntent.putExtra("extra", true);

                        // delay intent until time set in alarm
                        pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                        Log.i("Calendar time is", String.valueOf(alarmMinutes) + " minutes");
                        Log.i("Current time", String.valueOf(presentMinutes));
                        // set the Alarm Manager
                        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                    }
                }
        );

        // when alarmOff button is clicked
        alarmOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SetAlarmText("Alarm is off!");

                myIntent.putExtra("extra", false);
                sendBroadcast(myIntent);

                if (pendingIntent != null )alarmManager.cancel(pendingIntent);
            }
        });

    }

    // change text
    private void SetAlarmText (String output) {
        updateText.setText(output);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
