package com.example.swearjar2.Classes;

public class Task {
    private int taskId;
    private String taskTitle;
    private String taskDOF;
    private String taskTOF;
    private long taskTIM;


    public Task(int reminderId, String reminderTitle, String reminderDOF, String reminderTOF, long reminderTIM) {
        this.taskId = reminderId;
        this.taskTitle = reminderTitle;
        this.taskDOF = reminderDOF;
        this.taskTOF = reminderTOF;
        this.taskTIM = reminderTIM;
    }

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public String getTaskTitle() {
        return taskTitle;
    }

    public void setTaskTitle(String taskTitle) {
        this.taskTitle = taskTitle;
    }

    public String getTaskDOF() {
        return taskDOF;
    }

    public void setTaskDOF(String taskDOF) {
        this.taskDOF = taskDOF;
    }

    public String getTaskTOF() {
        return taskTOF;
    }

    public void setTaskTOF(String taskTOF) {
        this.taskTOF = taskTOF;
    }

    public long getTaskTIM() {
        return taskTIM;
    }

    public void setTaskTIM(long taskTIM) {
        this.taskTIM = taskTIM;
    }

}
