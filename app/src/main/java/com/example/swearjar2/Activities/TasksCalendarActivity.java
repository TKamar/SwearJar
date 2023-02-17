package com.example.swearjar2.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.Toast;

import com.example.swearjar2.Classes.Task;
import com.example.swearjar2.R;
import com.google.gson.Gson;

public class TasksCalendarActivity extends AppCompatActivity {

    private CalendarView calendarView;
    private Button markTaskDoneButton;
    private SharedPreferences prefs;
    private Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks_calendar);

//        // Initialize UI elements
//        calendarView = findViewById(R.id.calendarView);
//        markTaskDoneButton = findViewById(R.id.markTaskDoneButton);

        // Initialize shared preferences and Gson
        prefs = getSharedPreferences("tasks_data", MODE_PRIVATE);
        gson = new Gson();

        // Get tasks from shared preferences and display them on calendar
        String taskJson = prefs.getString("task", "");
        if (!taskJson.isEmpty()) {
            Task task = gson.fromJson(taskJson, Task.class);
            // Code for displaying task on calendar goes here
        }

        markTaskDoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                markTaskDone();
            }
        });
    }

    private void markTaskDone() {
        // Code for marking task as done goes here
        Toast.makeText(this, "Task marked as done", Toast.LENGTH_SHORT).show();
    }
}
