package com.example.swearjar2.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.swearjar2.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class LoginActivity extends AppCompatActivity {

    private EditText emailEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private TextView signUp;
    private TextView forgonPasswordButton;
    private FirebaseAuth firebaseAuth;
//    private SharedPreferences prefs;

    private AdView rAAdView;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Log.d("LogActivity", "onCreate: we are here");
        findViews();
        rAAdView = findViewById(R.id.ra_ad_view_login);
        rAAdView.loadAd(new AdRequest.Builder().build());
        firebaseAuth = FirebaseAuth.getInstance();


        //Initialize shared preferences
//        prefs = getSharedPreferences("tasks_data", MODE_PRIVATE);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                if(intent != null) {
                    startActivity(intent);
                }
            }
        });

    }


    private void findViews() {
      // Initialize UI elements
      emailEditText = findViewById(R.id.editTextEmail);
      passwordEditText = findViewById(R.id.editTextPassword);
      loginButton = findViewById(R.id.buttonSignIn);
      signUp = findViewById(R.id.textViewSignUp);
    }


    private void login() {
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();

//        if(!email.isEmpty() && !password.isEmpty()){
//            // Check user information against shared preferences
//            String storedEmail = prefs.getString("email", "");
//            String storedPassword = prefs.getString("password", "");
//
//            if (email.equals(storedEmail) && password.equals(storedPassword)) {
//                Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show();
//                finish();
//            } else {
//                Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show();
//            }
//        }else{
//            Toast.makeText(this, "Please enter both email and password", Toast.LENGTH_SHORT).show();
//        }

        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(LoginActivity.this, "Logged in successfully!", Toast.LENGTH_LONG).show();
                    finish();
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                }
                else
                    Toast.makeText(LoginActivity.this, "Email or Password are incorrect!", Toast.LENGTH_LONG).show();
            }
        });
    }


    private void forgotPassword(View v) {
        EditText resetMail = new EditText(v.getContext());
        AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(v.getContext());
        passwordResetDialog.setTitle("Reset Password");
        passwordResetDialog.setMessage("Enter your email to receive a link for password reset");
        passwordResetDialog.setView(resetMail);

        passwordResetDialog.setPositiveButton("Send", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String mail = resetMail.getText().toString();
                firebaseAuth.sendPasswordResetEmail(mail).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(@NonNull Void unused) {
                        Toast.makeText(LoginActivity.this, "Reset link sent to your mail!", Toast.LENGTH_LONG).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(LoginActivity.this, "Error!", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        passwordResetDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        passwordResetDialog.create().show();
    }



}
