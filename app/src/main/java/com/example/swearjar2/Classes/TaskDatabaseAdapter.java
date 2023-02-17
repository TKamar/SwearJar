package com.example.swearjar2.Classes;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;


public class TaskDatabaseAdapter {

    private DatabaseHelper remindersDatabaseHelper;
    private static final String DATABASE_NAME = "dba_reminders";
    private static final int DATABASE_VERSION = 1;
    private final Context mCtx;
    private SQLiteDatabase remindersDatabase;

    private static final String TABLE_REMINDER = "tbl_rmd";

    private static final String COL_REMINDER_ID = "rmd_id";
    private static final String COL_REMINDER_TITLE = "rmd_ttl";
    private static final String COL_REMINDER_DTS = "rmd_dts";

    private static final int INDEX_REMINDER_ID  = 0;
    private static final int INDEX_REMINDER_TITLE = INDEX_REMINDER_ID + 1;
    private static final int INDEX_REMINDER_DTS = INDEX_REMINDER_ID + 2;

    private static final String CREATE_TABLE_REMINDER =
            "CREATE TABLE if not exists " + TABLE_REMINDER + " ( " +
                    COL_REMINDER_ID + " INTEGER PRIMARY KEY autoincrement, " +
                    COL_REMINDER_TITLE + " TEXT, " +
                    COL_REMINDER_DTS + " TEXT );";


    public TaskDatabaseAdapter(Context mCtx) {
        this.mCtx = mCtx;
    }

    public void open() throws SQLException {
        remindersDatabaseHelper = new DatabaseHelper(mCtx);
        remindersDatabase = remindersDatabaseHelper.getWritableDatabase();
    }

    public void updateReminder(int reminderId, String nReminderTitle, String nReminderDTS){

        ContentValues reminderValues = new ContentValues();

        reminderValues.put(COL_REMINDER_TITLE, nReminderTitle);
        reminderValues.put(COL_REMINDER_DTS, nReminderDTS);

        remindersDatabase.update(TABLE_REMINDER, reminderValues,
                COL_REMINDER_ID + "=?", new String[]{String.valueOf(reminderId)});

    }

    public void deleteReminder(int reminderId) {
        remindersDatabase.delete(TABLE_REMINDER, COL_REMINDER_ID + "=?", new String[]{String.valueOf(reminderId)});
    }

    public int createReminder(String reminderTitle, String reminderDTS) {
        ContentValues reminderValues = new ContentValues();

        reminderValues.put(COL_REMINDER_TITLE, reminderTitle);
        reminderValues.put(COL_REMINDER_DTS, reminderDTS);

        remindersDatabase.insert(TABLE_REMINDER, null, reminderValues);

        int newReminderId= fetchReminders().get(0).getTaskId();
        return newReminderId;
    }

    public List<Task> fetchReminders() {
        Task task;
        List<Task> taskList =new ArrayList<>();

        Cursor reminderTableCursor = remindersDatabase.query(TABLE_REMINDER, new String[]{COL_REMINDER_ID,
                        COL_REMINDER_TITLE,COL_REMINDER_DTS},
                null, null, null, null, COL_REMINDER_ID +" DESC",null
        );

        if (reminderTableCursor != null){
            while (reminderTableCursor.moveToNext()){

                int reminderId = reminderTableCursor.getInt(INDEX_REMINDER_ID);
                String reminderTitle = reminderTableCursor.getString(INDEX_REMINDER_TITLE);
                String reminderDTS = reminderTableCursor.getString(INDEX_REMINDER_DTS);

                String reminderDOF = reminderDTS.substring(7,17);
                String reminderTOF = reminderDTS.substring(0,5);
                long reminderTIM = getReminderTIMFromDTS(reminderDTS);

                task = new Task(reminderId, reminderTitle, reminderDOF, reminderTOF, reminderTIM);

                taskList.add(task);}
        }

        if(taskList.size()!=0){
            ReminderComparator reminderComparator = new ReminderComparator();
            Collections.sort(taskList,reminderComparator);
        }

        return taskList;
    }

    protected long getReminderTIMFromDTS(String reminderDTS){
        DateFormat dateFormat=new SimpleDateFormat("HH:mm  dd/MM/yyyy");
        Date date = null;
        try {
            date=dateFormat.parse(reminderDTS);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        long reminderTIM = 0;

        if (date != null) {
            reminderTIM = date.getTime();
        }

        return reminderTIM;
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }
        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_TABLE_REMINDER);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_REMINDER);
            onCreate(db);
        }
    }

    protected static class ReminderComparator implements Comparator<Task> {

        public int compare(Task taskOne, Task taskTwo){
            if(taskOne.getTaskTIM()== taskTwo.getTaskTIM()){
                return 0;
            }
            else if(taskOne.getTaskTIM() > taskTwo.getTaskTIM()){
                return 1;
            }
            else {
                return -1;
            }
        }

    }
}
