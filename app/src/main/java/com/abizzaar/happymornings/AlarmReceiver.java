package com.abizzaar.happymornings;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by therapyos on 8/1/17.
 */

public class AlarmReceiver extends BroadcastReceiver {



    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("The Receiver is working", "Nice!");

        boolean extra = intent.getExtras().getBoolean("extra");

        Intent serviceIntent = new Intent(context, PlayRingtone.class);
        serviceIntent.putExtra("extra", extra);
        context.startService(serviceIntent);

    }
}
