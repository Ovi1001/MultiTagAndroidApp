package com.example.multitagandroidapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Pattern;

public class SignInScreen extends AppCompatActivity
{

    private FirebaseAuth mAuth;
    private EditText editTextEmail, editTextPassword;
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in_screen);
        configureBackFromSignInBtn();

        mAuth = FirebaseAuth.getInstance();

        editTextEmail = (EditText) findViewById(R.id.signInUsernameInput);
        editTextPassword = (EditText) findViewById(R.id.signInPasswordInput);

        progressBar = (ProgressBar) findViewById(R.id.signInProgressBar);

        configureSignInBtn();
    }

    private void configureBackFromSignInBtn()
    {
        Button signInBackButton = (Button) findViewById(R.id.backFromSignInBtn);
        signInBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void configureSignInBtn()
    {
        Button signInBtn = (Button) findViewById(R.id.signInBtn);
        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userLogin();
            }
        });
    }

    private void userLogin()
    {
        String email = editTextEmail.getText().toString().trim(); //Getting email provided from text input
        String password = editTextPassword.getText().toString().trim(); //Getting password provided from text input

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
        Pattern space = Pattern.compile("\\s");
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

        //Sign in the user if the requirements are met
        progressBar.setVisibility(View.VISIBLE); //Give the user visual feedback for loading

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() { //Checks to see if the task was completed
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) // if the user was signed in successfully
                {
                    progressBar.setVisibility(View.GONE);
                    startActivity(new Intent(SignInScreen.this, RoomsSelectorScreen.class));
                }
                else // if the user sign in failed
                {
                    Toast.makeText(SignInScreen.this, "Failed to login! Try again", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }//end userLogin method
}//end SignInScreen class