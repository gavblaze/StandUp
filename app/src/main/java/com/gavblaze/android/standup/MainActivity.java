package com.gavblaze.android.standup;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.Date;

import static android.app.AlarmManager.ELAPSED_REALTIME_WAKEUP;

public class MainActivity extends AppCompatActivity {

    private static final String CHANNEL_ID = "primary_notification_channel";
    private static final String ACTION_SEND_NOTIFICATION = BuildConfig.APPLICATION_ID + ".ACTION_SEND_NOTIFICATION";
    private static final int NOTIFICATION_ID = 0;
    private BroadcastReceiver mBroadcastReceiver;
    private NotificationManager mNotificationManager;
    private ToggleButton mToggleButton;
    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToggleButton = findViewById(R.id.alarmToggle);

        mBroadcastReceiver = new AlarmReceiver();
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        /*register receiver*/
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_SEND_NOTIFICATION);
        this.registerReceiver(mBroadcastReceiver, filter);

        /*Pending intent when toggle button clicked*/
        Intent notifyIntent = new Intent(this, AlarmReceiver.class);
        /*To track the state of the alarm, you need a boolean variable that is true if the alarm exists, and false otherwise*/
        boolean alarmUp = (PendingIntent.getBroadcast(this, NOTIFICATION_ID, notifyIntent, PendingIntent.FLAG_NO_CREATE) != null);
        mToggleButton.setChecked(alarmUp);
        final PendingIntent pendingIntent = PendingIntent.getBroadcast(this, NOTIFICATION_ID, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        final AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        createNotificationChannel();


        mToggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                String toastMessage;
                // If the Toggle is turned on, set the repeating alarm with
                // a 15 minute interval.
                long repeatInterval = AlarmManager.INTERVAL_FIFTEEN_MINUTES;
                long triggerTime = SystemClock.elapsedRealtime() + repeatInterval;

                if (isChecked) {
                    toastMessage = getString(R.string.stand_up_alarm_on);
                    if (alarmManager != null) {
                        alarmManager.setInexactRepeating(ELAPSED_REALTIME_WAKEUP, triggerTime, repeatInterval, pendingIntent);
                    }

                  //  alarmManager.setAlarmClock(new AlarmManager.AlarmClockInfo(triggerTime, pendingIntent), pendingIntent);

                } else {
                    // Cancel notification if the alarm is turned off.
                    mNotificationManager.cancelAll();

                    if (alarmManager != null) {
                        alarmManager.cancel(pendingIntent);
                    }
                    toastMessage = getString(R.string.stand_up_alarm_off);
                }
                Toast.makeText(getApplicationContext(), toastMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Stand up notification", NotificationManager.IMPORTANCE_HIGH);
            channel.enableVibration(true);
            channel.setDescription("Notifies every 15 minutes to stand up and walk");
            channel.enableLights(true);
            channel.setLightColor(Color.RED);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    protected void onDestroy() {
        this.unregisterReceiver(mBroadcastReceiver);
        super.onDestroy();
    }
}
