package com.example.multitagandroidapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class CreateAccountScreen extends AppCompatActivity {

    //private FirebaseAuth mAuth;
    //private EditText editTextEmail, editTextUsername, editTextPassword;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account_screen);
        configureBackFromSignInBtn();
        configureCreateAccBtn();

        //mAuth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.createAccProgressBar);
    }

    private void configureBackFromSignInBtn()
    {
        Button signInBackButton = (Button) findViewById(R.id.backFromCreateAccScreenBtn);
        signInBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void configureCreateAccBtn()
    {
        Button createAccBtn = (Button) findViewById(R.id.createAccBtn);
        createAccBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //registerUser();
                progressBar.setVisibility(View.VISIBLE);
            }
        });
    }

//    private void registerUser()
//    {
//        String email = ((EditText) findViewById(R.id.createAccUsernameInput)).getText().toString().trim(); // Getting email provided from text input
//        String password = ((EditText) findViewById(R.id.createAccPasswordInput)).getText().toString().trim(); // Getting password provided from text input
//    }


}