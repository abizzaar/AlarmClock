package com.abizzaar.happymornings;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.security.Provider;

/**
 * Created by therapyos on 8/1/17.
 */

public class PlayRingtone extends Service {

    MediaPlayer mediaPlayer;
    boolean musicIsPlaying = false;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        // make service id based on whether alarm was on (bool is 1) or not
        startId = 0;
        if (intent.getExtras().getBoolean("extra")) startId = 1;

        Log.i("startId is : ", String.valueOf(startId));


        if (startId == 1 && !musicIsPlaying) {


            mediaPlayer = new MediaPlayer();
            try {
                mediaPlayer.setDataSource(intent.getExtras().getString("songPath"));
                mediaPlayer.setLooping(true);
                mediaPlayer.prepare();
                mediaPlayer.start();
            } catch (IOException e) {}

            musicIsPlaying = true;
            Log.i("music was not playing", "music is now playing");

            // set up notification and intents
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            Intent intentMainActivity = new Intent(this.getApplicationContext(), MainActivity.class);
            PendingIntent pendingIntentMainActivity = PendingIntent.getActivity(this, 0, intentMainActivity, 0);

            // set parameters for notification
            Notification notificationPopup = new NotificationCompat.Builder(this)
                    .setContentTitle("An alarm is going off!")
                    .setContentText("Click Me!")
                    .setContentIntent(pendingIntentMainActivity)
                    .setAutoCancel(true)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .build();

            notificationManager.notify(0, notificationPopup);
        }
        else if (startId == 0 && musicIsPlaying) {
            mediaPlayer.stop();
            mediaPlayer.reset();
            musicIsPlaying = false;
            Log.i("music was playing", "it is now not playing");
        }
        else {
            Log.i("no change to", "state of music");
        }


        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


}
