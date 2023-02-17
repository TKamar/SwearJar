package com.example.swearjar2.Classes;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.swearjar2.R;

public class ReminderReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        int eventId = intent.getIntExtra("event_id", 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "event_reminder_channel")
//                .setSmallIcon(R.drawable.ic_event_reminder)
                .setContentTitle("Event Reminder")
                .setContentText("The event is about to start in 30 minutes")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(eventId, builder.build());
    }
}
