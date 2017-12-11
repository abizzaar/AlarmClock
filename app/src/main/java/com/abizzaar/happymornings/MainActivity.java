package com.abizzaar.happymornings;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;

import android.content.Intent;
import java.util.Calendar;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    AlarmManager alarmManager;
    TimePicker timePicker;
    TextView updateText;
    Calendar calendar;
    Calendar currentTimeCalendar;
    PendingIntent pendingIntent;
    Intent myIntent;

    // ringtone stuff
    private static final String LOG_TAG = "AudioRecordTest";
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private static String mFileName = null;

    // private RecordButton mRecordButton;
    private MediaRecorder mRecorder = null;
    boolean mStartRecording;
    private ConstraintLayout recorderLayout;
    private Button recordButton;

    // Requesting permission to RECORD_AUDIO
    private boolean permissionToRecordAccepted = false;
    private String[] permissions = {Manifest.permission.RECORD_AUDIO};


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
        alarmOn.setOnClickListener(alarmOnClicker);

        // when alarmOff button is clicked
        alarmOff.setOnClickListener(alarmOffClicker);

        // RINGTONE STUFF

        // Record to the external cache directory for visibility
        mFileName = getExternalCacheDir().getAbsolutePath();
        mFileName += "/audiorecordtest.3gp";

        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);

        // UI stuff
        recorderLayout = (ConstraintLayout) findViewById(R.id.RecorderLayout);
        recordButton = (Button) findViewById(R.id.recordButton);
        mStartRecording = true;


        recordButton.setOnClickListener(recordButtonClicker);
    }

    View.OnClickListener alarmOnClicker = new View.OnClickListener() {

        @Override
        public void onClick(View view) {

            // set calendar stuff
            calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, timePicker.getHour());
            calendar.set(Calendar.MINUTE, timePicker.getMinute());
            calendar.set(Calendar.SECOND, 0);

            // track for if alarm is being set for new day
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


            // tell AlarmReceiver that alarmOn was clicked
            myIntent.putExtra("extra", true);
            myIntent.putExtra("songPath", mFileName);

            SetAlarmText("Alarm set for " + hourString + ":" + minuteString);
            recordButton.setText("Record");



            // delay intent until time set in alarm
            pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            Log.i("Calendar time is", String.valueOf(alarmMinutes) + " minutes");
            Log.i("Current time", String.valueOf(presentMinutes));

            recorderLayout.setVisibility(View.VISIBLE);

        }
    };

    View.OnClickListener alarmOffClicker = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            SetAlarmText("Alarm is off!");

            myIntent.putExtra("extra", false);
            sendBroadcast(myIntent);

            if (pendingIntent != null) alarmManager.cancel(pendingIntent);

        }
    };


    View.OnClickListener recordButtonClicker = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            onRecord(mStartRecording);
            if (mStartRecording) {
                recordButton.setText("Stop");
            }
            else {
                recorderLayout.setVisibility(View.INVISIBLE);
            }
            mStartRecording = !mStartRecording;
        }
    };

    // change text
    private void SetAlarmText (String output) {
        updateText.setText(output);
    }


    private void onRecord(boolean start) {
        if (start) {
            startRecording();
        } else {
            stopRecording();
        }
    }

    private void startRecording() {
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(mFileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }
        mRecorder.start();
    }

    private void stopRecording() {
        mRecorder.stop();
        mRecorder.reset();
        mRecorder.release();
        mRecorder = null;

        // set the Alarm Manager
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
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
