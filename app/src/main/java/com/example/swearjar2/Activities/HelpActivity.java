package com.example.swearjar2.Activities;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.swearjar2.R;

public class HelpActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        getSupportActionBar().setElevation(0);
        getSupportActionBar().setTitle(R.string.help_activity_action_bar_title);

        TextView helpActivityTextView=findViewById(R.id.help_activity_text_view);


        helpActivityTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                sendHelpEmail();

            }
        });
    }

    private void sendHelpEmail() {
        String[] helpEmailReceiverAddresses={"ktomer5@gmail.com"};
        Intent sendHelpEmailIntent = new Intent(Intent.ACTION_SENDTO);
        sendHelpEmailIntent.setData(Uri.parse("mailto:"));
        sendHelpEmailIntent.putExtra(Intent.EXTRA_EMAIL, helpEmailReceiverAddresses);

        if (sendHelpEmailIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(sendHelpEmailIntent);
        }

    }






}
