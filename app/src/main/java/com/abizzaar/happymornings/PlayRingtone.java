package com.abizzaar.happymornings;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import java.security.Provider;

/**
 * Created by therapyos on 8/1/17.
 */

public class PlayRingtone extends Service {

    MediaPlayer mediaSong;
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
            mediaSong = MediaPlayer.create(this, R.raw.song);
            mediaSong.start();
            musicIsPlaying = true;
            Log.i("music was not playing", "music is now playing");
        }
        else if (startId == 0 && musicIsPlaying) {
            mediaSong.stop();
            mediaSong.reset();
            musicIsPlaying = false;
            Log.i("music was playing", "it is now not playing");
        }
        else Log.i("no change to", "state of music");


        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


}
