package com.example.swearjar2.Classes;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.swearjar2.R;

import java.util.ArrayList;
import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ReminderViewHolder>{

    private Context reminderAdapterContext;
    private List<Task> taskList;
    private List<Integer> selectedPositions = new ArrayList<>();

    public TaskAdapter(Context reminderAdapterContext, List<Task> taskList) {
        this.reminderAdapterContext = reminderAdapterContext;
        this.taskList = taskList;
    }

    @Override
    public ReminderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.tasks_list, parent, false);

        return new ReminderViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ReminderViewHolder holder, int position) {

        holder.rTitleTextView.setText(taskList.get(position).getTaskTitle());
        holder.rTOFTextView.setText(taskList.get(position).getTaskTOF());
        holder.rDOFTextView.setText(taskList.get(position).getTaskDOF());

        if (selectedPositions.contains(position)){
            holder.reminderListRowFrameLayout.setForeground(new ColorDrawable(ContextCompat.getColor(reminderAdapterContext,R.color.recyclerViewItemSelectionColor)));
        }
        else {
            holder.reminderListRowFrameLayout.setForeground(new ColorDrawable(ContextCompat.getColor(reminderAdapterContext,android.R.color.transparent)));
        }

    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    protected class ReminderViewHolder extends RecyclerView.ViewHolder {
        protected TextView rTitleTextView;
        protected TextView rTOFTextView;
        protected TextView rDOFTextView;
        protected FrameLayout reminderListRowFrameLayout;

        public ReminderViewHolder(View view) {
            super(view);
            rTitleTextView = (TextView) view.findViewById(R.id.reminder_title_tvw);
            rTOFTextView = (TextView) view.findViewById(R.id.reminder_tof_tvw);
            rDOFTextView = view.findViewById(R.id.reminder_dof_tvw);
            reminderListRowFrameLayout = view.findViewById(R.id.reminder_list_row_frame_layout);
        }
    }


    public void setSelectedPositions(int previousPosition, List<Integer> selectedPositions) {
        this.selectedPositions = selectedPositions;

        if(previousPosition!=-1){
            notifyItemChanged(previousPosition);
        }

        if(this.selectedPositions.size()>0){
            notifyItemChanged(this.selectedPositions.get(0));
        }

    }


    public Task getItem(int position){
        return taskList.get(position);
    }


}