package com.example.multitagandroidapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Pattern;

public class CreateAccountScreen extends AppCompatActivity
{

    //private FirebaseAuth mAuth;
    private EditText editTextEmail, editTextUsername, editTextPassword;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account_screen);
        configureBackFromSignInBtn();
        configureCreateAccBtn();

        //mAuth = FirebaseAuth.getInstance();

        editTextUsername = (EditText) findViewById(R.id.createAccUsernameInput);
        editTextEmail = (EditText) findViewById(R.id.createAccEmailInput);
        editTextPassword = (EditText) findViewById(R.id.createAccPasswordInput);

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
                registerUser();
                progressBar.setVisibility(View.VISIBLE);
            }
        });
    }

    private void registerUser()
    {
        String username = editTextUsername.getText().toString().trim(); // Getting username provided from text input
        String email = editTextEmail.getText().toString().trim(); // Getting email provided from text input
        String password = editTextPassword.getText().toString().trim(); // Getting password provided from text input

        //Username validation
        Pattern space = Pattern.compile("\\s");
        Pattern numeric = Pattern.compile("\\D");

        if (username.isEmpty())
        {
            editTextUsername.setError("A username is required!");
            editTextUsername.requestFocus();
            return;
        }

        if (username.length() < 3)
        {
            editTextUsername.setError("Username has to be at LEAST 3 characters!");
            editTextUsername.requestFocus();
            return;
        }

        if (!numeric.matcher(username).find()) //Checks for a non digit character
        {
            editTextUsername.setError("A username cannot be numbers!");
            editTextUsername.requestFocus();
            return;
        }

        if (space.matcher(username).find()) // checks for a white space character
        {
            editTextUsername.setError("A username cannot have white spaces!");
            editTextUsername.requestFocus();
            return;
        }

        //Email validation
        if (email.isEmpty())
        {
            editTextEmail.setError("An email is required!");
            editTextEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) // checks for a good email
        {
            editTextEmail.setError("no way jose. provide a good email");
            editTextEmail.requestFocus();
            return;
        }

        //Password validation
        Pattern uppercase = Pattern.compile("[A-Z]");
        Pattern digit = Pattern.compile("[0-9]");

        if (password.isEmpty())
        {
            editTextPassword.setError("Password is required!");
            editTextPassword.requestFocus();
            return;
        }

        if (password.length() < 6)
        {
            editTextPassword.setError("Password has to at LEAST be 6 characters! duh");
            editTextPassword.requestFocus();
            return;
        }

        if (!uppercase.matcher(password).find())
        {
            editTextPassword.setError("Password has to include at LEAST 1 capital letter!");
            editTextPassword.requestFocus();
            return;
        }

        if (!digit.matcher(password).find())
        {
            editTextPassword.setError("Password has to include at LEAST 1 number!");
            editTextPassword.requestFocus();
            return;
        }

        if (space.matcher(password).find())
        {
            editTextPassword.setError("A password cannot have white spaces!");
            editTextPassword.requestFocus();
            return;
        }

    }//end of registerUser method
}//end of CreateAccountScreen class