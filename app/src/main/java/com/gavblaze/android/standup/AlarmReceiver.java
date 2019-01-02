package com.gavblaze.android.standup;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import static android.support.v4.app.NotificationCompat.DEFAULT_ALL;
import static android.support.v4.app.NotificationCompat.PRIORITY_HIGH;

public class AlarmReceiver extends BroadcastReceiver {

    private NotificationManager mNotificationManager;
    private static final int NOTIFICATION_ID = 0;

    // Notification channel ID.
    private static final String CHANNEL_ID = "primary_notification_channel";

    @Override
    public void onReceive(Context context, Intent intent) {

        mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        deliverNotification(context);
    }

    private void deliverNotification(Context context) {

        Intent contentIntent = new Intent(context, MainActivity.class);
        PendingIntent contentPendingIntent = PendingIntent.getActivity
                (context, NOTIFICATION_ID, contentIntent, PendingIntent
                        .FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID);
        builder.setContentTitle("Stand Up Alert")
                .setContentText("You should stand up and walk around now!")
                .setSmallIcon(R.drawable.ic_stand_up)
                .setDefaults(DEFAULT_ALL)
                .setPriority(PRIORITY_HIGH)
                .setContentIntent(contentPendingIntent)
                .setAutoCancel(true);

        mNotificationManager.notify(NOTIFICATION_ID, builder.build());
    }
}
