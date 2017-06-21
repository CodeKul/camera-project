package com.codekul.alarmmanager;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = new Intent("com.codekul.alarm");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1234,intent, PendingIntent.FLAG_UPDATE_CURRENT);
        //AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);

        //manager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 5000, 10000 *50, pendingIntent);

        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        int ALARM_TYPE = AlarmManager.RTC_WAKEUP;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            am.setExactAndAllowWhileIdle(ALARM_TYPE, System.currentTimeMillis() + 5000, pendingIntent);
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            am.setExact(ALARM_TYPE, System.currentTimeMillis() + 5000, pendingIntent);
        else
            am.set(ALARM_TYPE, System.currentTimeMillis() + 5000, pendingIntent);
    }
}
