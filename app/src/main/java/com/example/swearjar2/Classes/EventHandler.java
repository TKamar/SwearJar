package com.example.swearjar2.Classes;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.CalendarContract;

import java.util.Calendar;

public class EventHandler {

    private static final String TAG = EventHandler.class.getSimpleName();

    public void addEvent(Context context, String title, long startTime, long finishTime) {
        Calendar beginTime = Calendar.getInstance();
        beginTime.setTimeInMillis(startTime);
        Calendar endTime = Calendar.getInstance();
        endTime.setTimeInMillis(finishTime);
        Intent intent = new Intent(Intent.ACTION_INSERT)
                .setData(CalendarContract.Events.CONTENT_URI)
                .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginTime.getTimeInMillis())
                .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime.getTimeInMillis())
                .putExtra(CalendarContract.Events.TITLE, title);
        context.startActivity(intent);
    }

    public void setReminder(Context context, long eventId, long reminderTime) {
        ContentValues values = new ContentValues();
        values.put(CalendarContract.Reminders.EVENT_ID, eventId);
        values.put(CalendarContract.Reminders.MINUTES, reminderTime);
        values.put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT);
        context.getContentResolver().insert(CalendarContract.Reminders.CONTENT_URI, values);
    }

    public void updateEvent(Context context, long eventId, String title, long startTime, long endTime) {
        ContentValues values = new ContentValues();
        values.put(CalendarContract.Events.TITLE, title);
        values.put(CalendarContract.Events.DTSTART, startTime);
        values.put(CalendarContract.Events.DTEND, endTime);
        Uri updateUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, eventId);
        context.getContentResolver().update(updateUri, values, null, null);
    }
}
