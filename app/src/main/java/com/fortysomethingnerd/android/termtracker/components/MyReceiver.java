package com.fortysomethingnerd.android.termtracker.components;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.fortysomethingnerd.android.termtracker.R;

import static com.fortysomethingnerd.android.termtracker.utilities.Constants.*;
import static com.fortysomethingnerd.android.termtracker.utilities.Constants.NOTIFICATION_TITLE_KEY;

public class MyReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(LOG_TAG, "onReceive: ");

        String channel_id = intent.getStringExtra(NOTIFICATION_CHANNEL_ID_KEY);
        int notificationId = intent.getIntExtra(NOTIFICATION_ID, 0);
        String title = intent.getStringExtra(NOTIFICATION_TITLE_KEY);
        String text = intent.getStringExtra(NOTIFICATION_TEXT_KEY);
        PendingIntent pendingIntent = intent.getParcelableExtra(NOTIFICATION_PENDING_INTENT_KEY);

        Toast.makeText(context, text + " (Notification ID " + notificationId + ")", Toast.LENGTH_LONG).show();

        Notification notification = new NotificationCompat.Builder(context, channel_id)
                .setSmallIcon(R.drawable.ic_alarm_notification)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentTitle(title)
                .setContentText(text)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(text))
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build();

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(notificationId, notification);
    }

    }
