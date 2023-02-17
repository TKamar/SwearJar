package com.example.swearjar2.Interfaces;

import com.example.swearjar2.Classes.Task;

import java.util.ArrayList;

public interface Callback_Tasks {
    public void hideActionBar();
    public void showActionBar();
    public void addReminder(final String reminderTitle, final String reminderDTS, final long reminderTIM);
    public void updateReminder(final String reminderTitle, final String reminderDTS, final long reminderTIM, final int reminderId, final int reminderPosition);
}
