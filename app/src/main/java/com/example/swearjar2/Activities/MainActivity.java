package com.example.swearjar2.Activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import com.example.swearjar2.Classes.NotificationReceiver;
import com.example.swearjar2.Classes.RecyclerTouchListener;
import com.example.swearjar2.Classes.Task;
import com.example.swearjar2.Classes.TaskAdapter;
import com.example.swearjar2.Classes.TaskDatabaseAdapter;
import com.example.swearjar2.Classes.TaskFSD;
import com.example.swearjar2.Interfaces.Callback_Tasks;
import com.example.swearjar2.R;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static android.view.View.GONE;

public class MainActivity extends AppCompatActivity implements ActionMode.Callback, Callback_Tasks {

    private List<Task> taskList = new ArrayList<>();
    private RecyclerView reminderRecyclerView;
    private TaskAdapter reminderLAdapter;
    private TaskDatabaseAdapter remindersDatabaseAdapter;
    private ActionMode rAActionMode;
    private boolean rAIsMultiSelect = false;
    private List<Integer> rASelectedPositions = new ArrayList<>();
    private FloatingActionButton rAActionButton;
    protected TextView rAESTitleTextView;
    protected TextView rAESContentTextView;
    protected LinearLayout rAESLinearLayout;
    View rARootLayout;
    private RewardedAd mRewardedAd;
    private AdView rAAdView;
    private InterstitialAd mInterstitial;
    private FirebaseAnalytics mFirebaseAnalytics;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        rAAdView = findViewById(R.id.ra_ad_view);
        rAAdView.loadAd(new AdRequest.Builder().build());
        initInterstitial();

        Float elevation = 0.0f;
        this.getSupportActionBar().setElevation(elevation);

        rARootLayout = findViewById(R.id.ch_root_layout);

        remindersDatabaseAdapter = new TaskDatabaseAdapter(this);
        remindersDatabaseAdapter.open();

        initializeReminderList();

        reminderRecyclerView = (RecyclerView) findViewById(R.id.reminders_recycler_view);
        rAESTitleTextView = findViewById(R.id.ra_empty_state_title_text_view);
        rAESContentTextView = findViewById(R.id.ra_empty_state_text_view);
        rAESLinearLayout = findViewById(R.id.ra_empty_state_linear_layout);

        reminderLAdapter = new TaskAdapter(this, taskList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        reminderRecyclerView.setLayoutManager(layoutManager);
        reminderRecyclerView.setItemAnimator(new DefaultItemAnimator());
        reminderRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(this, reminderRecyclerView, new RecyclerTouchListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (rAIsMultiSelect) {
                    multiSelect(position);
                }

                else {
                    openEditDialog(position);
                }

            }

            @Override
            public void onItemLongClick(View view, int position) {
                if (!rAIsMultiSelect) {
                    rASelectedPositions = new ArrayList<>();
                    rAIsMultiSelect = true;

                    if (rAActionMode == null) {
                        rAActionButton.hide();
                        rAActionMode = startActionMode(MainActivity.this);
                    }
                }

                multiSelect(position);
            }
        }));
        reminderRecyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        reminderRecyclerView.setAdapter(reminderLAdapter);

        setNextReminderAlarm();
        setRAEmptyState();

        rAActionButton = findViewById(R.id.new_reminder_fab);
        rAActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadAd();
                loadRewardAd();
                showRewardAd();
                openNewReminderDialog();
            }
        });
    }

    private void initInterstitial() {
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        loadAd();
        loadRewardAd();
    }

    private void showInterstitial() {
        if (mInterstitial != null) {
            mInterstitial.show(MainActivity.this);
        } else {
//            Log.d(Constants.TAG, "The interstitial ad wasn't ready yet.");
        }
    }

    private void openNewReminderDialog(){
        FragmentManager oNRFSD = getSupportFragmentManager();
        TaskFSD newReminderFSD =  TaskFSD.newInstance(0,"","","",0,0,false);
        FragmentTransaction transaction = oNRFSD.beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.add(android.R.id.content, newReminderFSD).addToBackStack(null).commit();
    }

    private int getNextReminderAPosition(){
        int nRAPosition = 0;

        int i = 0;
        while (i >= 0 && i<= taskList.size()-1){
            Task bReminder = taskList.get(i);
            long nowTIM = getNowTIM();
            long bReminderTIM = bReminder.getTaskTIM();

            if(bReminderTIM > nowTIM){
                nRAPosition=i;
                i= taskList.size();
            }

            ++i;
        }

        return nRAPosition;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void setNextReminderAlarm() {

        if (taskList != null) {

            if (taskList.size() != 0) {

                int nRAPosition = getNextReminderAPosition();

                Task latestTask = taskList.get(nRAPosition);

                long nowTIM = getNowTIM();
                long lReminderTIM = latestTask.getTaskTIM();


                if (lReminderTIM >= nowTIM) {

                    Intent rARIntent = new Intent(this, NotificationReceiver.class);
                    rARIntent.putExtra("lReminderTitle", latestTask.getTaskTitle());
                    PendingIntent rARPIntent = PendingIntent.getBroadcast(this, 100, rARIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
//                    @SuppressLint("UnspecifiedImmutableFlag") PendingIntent rARPIntent = PendingIntent.getBroadcast(this, 100, rARIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    AlarmManager rAAlarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);


                    int SDK_INT = Build.VERSION.SDK_INT;
                    if (SDK_INT < Build.VERSION_CODES.KITKAT) {
                        assert rAAlarmManager != null;
                        rAAlarmManager.set(AlarmManager.RTC_WAKEUP, lReminderTIM, rARPIntent);

                    } else if (SDK_INT >= Build.VERSION_CODES.KITKAT && SDK_INT < Build.VERSION_CODES.M) {
                        assert rAAlarmManager != null;
                        rAAlarmManager.setExact(AlarmManager.RTC_WAKEUP, lReminderTIM, rARPIntent);

                    } else if (SDK_INT >= Build.VERSION_CODES.M) {
                        assert rAAlarmManager != null;
                        rAAlarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, lReminderTIM, rARPIntent);
                    }


                }


            }

        }

    }

    private void openEditDialog(int reminderPosition){

        Task selectedTask = taskList.get(reminderPosition);
        int reminderId = selectedTask.getTaskId();
        String reminderTitle = selectedTask.getTaskTitle();
        String reminderDOF = selectedTask.getTaskDOF();
        String reminderTOF = selectedTask.getTaskTOF();
        long reminderTIM = selectedTask.getTaskTIM();

        FragmentManager openERFSD = getSupportFragmentManager();
        TaskFSD editReminderFSD =  TaskFSD.newInstance(reminderId,reminderTitle,reminderDOF,reminderTOF,reminderTIM,reminderPosition,true);
        FragmentTransaction oEditRFSDTransaction = openERFSD.beginTransaction();
        oEditRFSDTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        oEditRFSDTransaction.add(android.R.id.content, editReminderFSD).addToBackStack(null).commit();
    }

    private int getNewReminderAddPosition(Task newTask) {

        List<Task> remindersSortList = taskList;
        remindersSortList.add(newTask);

        ReminderComparator reminderComparator = new ReminderComparator();
        Collections.sort(remindersSortList, reminderComparator);

        int newReminderPosition = 0;

        int i = 0;
        while (i >= 0 && i <= (remindersSortList.size() - 1)) {

            int reminderId = taskList.get(i).getTaskId();

            if (reminderId == newTask.getTaskId()) {
                newReminderPosition = i;
            }

            ++i;
        }

        return newReminderPosition;
    }

    @SuppressLint("InvalidAnalyticsName")
    public void addReminder(final String reminderTitle, final String reminderDTS, final long reminderTIM) {

        Bundle bundle = new Bundle();
        bundle.putString("Enter to add task", "add task");
        mFirebaseAnalytics.logEvent("Adding Task", bundle);

        final int[] newReminderId = new int[1];

        Runnable addReminderRunnable = new Runnable() {
            @Override
            public void run() {
                newReminderId[0] = remindersDatabaseAdapter.createReminder(reminderTitle, reminderDTS);

            }
        };
        Thread addReminderThread = new Thread(addReminderRunnable);
        addReminderThread.start();
        try {
            addReminderThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        int reminderId = newReminderId[0];
        String reminderDOF = reminderDTS.substring(7, 17);
        String reminderTOF = reminderDTS.substring(0, 5);
        Task newTask = new Task(reminderId, reminderTitle, reminderDOF, reminderTOF, reminderTIM);

        addReminderToList(newTask);

        setRAEmptyState();
        setNextReminderAlarm();

        Snackbar.make(rARootLayout, R.string.reminder_set_c,Snackbar.LENGTH_SHORT).show();
    }

    @SuppressLint("InvalidAnalyticsName")
    public void updateReminder(final String reminderTitle, final String reminderDTS, final long reminderTIM, final int reminderId, final int reminderPosition) {
        Bundle bundle = new Bundle();
        bundle.putString("Enter to update task", "update task");
        mFirebaseAnalytics.logEvent("Updating Task", bundle);
        Runnable updateReminderRunnable = new Runnable() {
            @Override
            public void run() {
                remindersDatabaseAdapter.updateReminder(reminderId,reminderTitle,reminderDTS);
            }
        };
        Thread updateReminderThread = new Thread(updateReminderRunnable);
        updateReminderThread.start();
        try {
            updateReminderThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        updateReminderListItem(reminderTitle,reminderDTS,reminderTIM,reminderPosition);

        setNextReminderAlarm();

        Snackbar.make(rARootLayout, "Reminder Updated",Snackbar.LENGTH_SHORT).show();
    }

    public void hideActionBar() {
        getSupportActionBar().hide();
    }

    public void showActionBar() {
        getSupportActionBar().show();
    }

    private void multiSelect(int position) {
        Task selectedTask = reminderLAdapter.getItem(position);
        if (selectedTask != null) {
            if (rAActionMode != null) {
                int previousPosition = -1;
                if (rASelectedPositions.size() > 0) {
                    previousPosition = rASelectedPositions.get(0);
                }
                rASelectedPositions.clear();
                rASelectedPositions.add(position);

                reminderLAdapter.setSelectedPositions(previousPosition, rASelectedPositions);
            }
        }
    }

    private void initializeReminderList() {
        Runnable initializeRListRunnable = new Runnable() {
            @Override
            public void run() {
                taskList = remindersDatabaseAdapter.fetchReminders();
            }
        };
        Thread initializeRListThread = new Thread(initializeRListRunnable);
        initializeRListThread.start();
        try {
            initializeRListThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void addReminderToList(Task newTask) {
        int newReminderPosition = getNewReminderAddPosition(newTask);
        reminderLAdapter.notifyDataSetChanged();

        if(newReminderPosition>=0 && newReminderPosition <= (taskList.size()-1)){
            reminderRecyclerView.scrollToPosition(newReminderPosition);
        }

    }

    private long getNowTIM() {
        Date nowDate = new Date();
        long nowTIM = nowDate.getTime();
        return nowTIM;
    }

    private void deleteReminderListItem(int reminderPosition) {
        taskList.remove(reminderPosition);
        reminderLAdapter.notifyItemRemoved(reminderPosition);
        setNextReminderAlarm();
    }

    private void updateReminderListItem(String reminderTitle, String reminderDTS, long reminderTIM, int reminderPosition) {
        String reminderDOF = reminderDTS.substring(7,17);
        String reminderTOF = reminderDTS.substring(0,5);

        taskList.get(reminderPosition).setTaskTitle(reminderTitle);
        taskList.get(reminderPosition).setTaskDOF(reminderDOF);
        taskList.get(reminderPosition).setTaskTOF(reminderTOF);
        taskList.get(reminderPosition).setTaskTIM(reminderTIM);

        ReminderComparator reminderComparator = new ReminderComparator();
        Collections.sort(taskList,reminderComparator);

        reminderLAdapter.notifyDataSetChanged();
    }

    private void setRAEmptyState() {

        if (taskList.size() == 0) {

            if (reminderRecyclerView.getVisibility() == View.VISIBLE) {
                reminderRecyclerView.setVisibility(GONE);
            }

            if (rAESLinearLayout.getVisibility() == GONE) {
                rAESLinearLayout.setVisibility(View.VISIBLE);

                String rAESTitle = getResources().getString(R.string.ra_empty_state_title);
                String rAESText = getResources().getString(R.string.ra_empty_state_text);

                rAESTitleTextView.setText(rAESTitle);
                rAESContentTextView.setText(rAESText);
            }


        } else {

            if (rAESLinearLayout.getVisibility() == View.VISIBLE) {
                rAESLinearLayout.setVisibility(GONE);
            }

            if (reminderRecyclerView.getVisibility() == GONE) {
                reminderRecyclerView.setVisibility(View.VISIBLE);
            }

        }


    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        MenuInflater inflater = mode.getMenuInflater();
        inflater.inflate(R.menu.ra_action_view_menu, menu);
        return true;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.ra_action_copy:
                if (rASelectedPositions.size() > 0) {

                    String selectedReminderTitle = taskList.get(rASelectedPositions.get(0)).getTaskTitle();

                    ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("l", selectedReminderTitle);
                    clipboard.setPrimaryClip(clip);
                }

                rAActionMode.finish();

                String rACopyConfirmationText = getResources().getString(R.string.ra_copy_confirmation_text);

                Snackbar.make(rARootLayout,rACopyConfirmationText,Snackbar.LENGTH_SHORT).show();

                return true;

            case R.id.ra_action_edit:

                if (rASelectedPositions.size() > 0) {
                    int selectedPosition = rASelectedPositions.get(0);
                    openEditDialog(selectedPosition);
                }

                rAActionMode.finish();
                return true;


            case R.id.ra_action_delete:
                AlertDialog.Builder deleteRDialogBuilder = new AlertDialog.Builder(this);
                deleteRDialogBuilder.setTitle(getResources().getString(R.string.delete_reminder_dialog_title));
                deleteRDialogBuilder.setMessage(getResources().getString(R.string.delete_reminder_dialog_message));
                deleteRDialogBuilder.setNegativeButton(getResources().getString(R.string.del_reminder_dialog_negative_button), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        showRewardAd();
                        dialogInterface.dismiss();
                        rAActionMode.finish();
                    }
                });

                deleteRDialogBuilder.setPositiveButton(getResources().getString(R.string.delete_reminder_dialog_positive_button), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        showRewardAd();
                        if (rASelectedPositions.size() > 0) {

                            int selectedPosition = rASelectedPositions.get(0);
                            final int sReminderId = taskList.get(selectedPosition).getTaskId();

                            Runnable deleteRRunnable = new Runnable() {
                                @Override
                                public void run() {
                                    remindersDatabaseAdapter.deleteReminder(sReminderId);
                                }
                            };
                            Thread deleteRThread = new Thread(deleteRRunnable);
                            deleteRThread.start();
                            try {
                                deleteRThread.join();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                            deleteReminderListItem(selectedPosition);
                            setRAEmptyState();
                        }

                        dialogInterface.dismiss();

                        rAActionMode.finish();
                    }
                });
                deleteRDialogBuilder.create().show();

                return true;


            default:

        }
        return false;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {

        rAActionButton.show();
        rAActionMode = null;
        rAIsMultiSelect = false;

        int previousPosition = -1;
        if (rASelectedPositions.size() > 0) {
            previousPosition = rASelectedPositions.get(0);
        }
        rASelectedPositions = new ArrayList<>();

        reminderLAdapter.setSelectedPositions(previousPosition, new ArrayList<Integer>());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.rem_activity_options_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.rem_activity_help:
                openHelp();
                return true;

            case R.id.rem_activity_sa:
                shareApp();
                return true;

            default:
                return false;
        }
    }

    protected void shareApp(){
        Intent sAIntent = new Intent();
        sAIntent.setAction(Intent.ACTION_SEND);
        sAIntent.putExtra(Intent.EXTRA_TEXT,"Reminder App is a fast and simple app that I use to schedule tasks. Get it from: https://play.google.com/store/apps/details?id="+getPackageName());
        sAIntent.setType("text/plain");
        Intent.createChooser(sAIntent,"Share via");
        startActivity(sAIntent);
    }

    protected void openHelp(){
        Intent remToHelpActivityIntent = new Intent(MainActivity.this,HelpActivity.class);
        startActivity(remToHelpActivityIntent);
    }

    protected static class ReminderComparator implements Comparator<Task> {

        public int compare(Task taskOne, Task taskTwo) {
            if (taskOne.getTaskTIM() == taskTwo.getTaskTIM()) {
                return 0;
            } else if (taskOne.getTaskTIM() > taskTwo.getTaskTIM()) {
                return 1;
            } else {
                return -1;
            }
        }


    }

    private void loadAd() {
        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(this, "ca-app-pub-3940256099942544/1033173712", adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        mInterstitial = interstitialAd;
//                        Log.i(Constants.TAG, "onAdLoaded");
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
//                        Log.d(Constants.TAG, loadAdError.toString());
                        mInterstitial = null;
                    }
                });
    }


    private void loadRewardAd() {
        AdRequest adRequest = new AdRequest.Builder().build();
        RewardedAd.load(this, "ca-app-pub-4622401001520120/6648157451",
                adRequest, new RewardedAdLoadCallback() {
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        System.out.println("failed");
                        mRewardedAd = null;
                    }

                    @Override
                    public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                        mRewardedAd = rewardedAd;
                        mRewardedAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                            @Override
                            public void onAdClicked() {
                            }

                            @Override
                            public void onAdDismissedFullScreenContent() {
                                mRewardedAd = null;
                            }

                            @Override
                            public void onAdFailedToShowFullScreenContent(AdError adError) {
                                mRewardedAd = null;
                            }

                            @Override
                            public void onAdImpression() {
                            }

                            @Override
                            public void onAdShowedFullScreenContent() {
                            }
                        });
//
                    }
                });
    }

    private void showRewardAd() {
        if (mRewardedAd != null) {
            Activity activityContext = MainActivity.this;
            mRewardedAd.show(activityContext, new OnUserEarnedRewardListener() {
                @Override
                public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                    // Handle the reward.
                    int rewardAmount = rewardItem.getAmount();
                    String rewardType = rewardItem.getType();
                }
            });
        } else {
        }
    }

}
