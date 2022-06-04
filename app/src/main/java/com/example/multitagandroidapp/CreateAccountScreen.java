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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

public class CreateAccountScreen extends AppCompatActivity
{

    private FirebaseAuth mAuth;
    private EditText editTextEmail, editTextUsername, editTextPassword, editTextConfirmPassword;
    private ProgressBar progressBar;

    private List<String> allUsernames;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account_screen);
        configureBackFromSignInBtn();

        allUsernames = new ArrayList<String>();
        //Getting all usernames used from database
        FirebaseDatabase.getInstance().getReference("Usernames").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                allUsernames.clear();
                for (DataSnapshot data : snapshot.getChildren())
                {
                    String temp = data.getValue(String.class);
                    allUsernames.add(temp.toLowerCase());
                }
                Toast.makeText(CreateAccountScreen.this, allUsernames.size() + "", Toast.LENGTH_SHORT).show();
            }//end onDataChange method

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(CreateAccountScreen.this, "Unable to retrieve usernames", Toast.LENGTH_LONG).show();
            }
        });

        //Initializing variables
        mAuth = FirebaseAuth.getInstance();
        editTextUsername = (EditText) findViewById(R.id.createAccUsernameInput);
        editTextEmail = (EditText) findViewById(R.id.createAccEmailInput);
        editTextPassword = (EditText) findViewById(R.id.createAccPasswordInput);
        editTextConfirmPassword = (EditText) findViewById(R.id.createAccConfirmPasswordInput);

        progressBar = (ProgressBar) findViewById(R.id.createAccProgressBar);

        configureCreateAccBtn();
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
            }
        });
    }

    private void registerUser()
    {
        String username = editTextUsername.getText().toString().trim(); // Getting username provided from text input
        String email = editTextEmail.getText().toString().trim(); // Getting email provided from text input
        String password = editTextPassword.getText().toString().trim(); // Getting password provided from text input
        String confirmPass = editTextConfirmPassword.getText().toString().trim(); // Getting password from confirm password text input

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

        if (allUsernames.contains(username.toLowerCase()))
        {
            editTextUsername.setError("Username taken!");
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
            editTextPassword.setError("A password cannot have spaces!");
            editTextPassword.requestFocus();
            return;
        }

        if (!password.equals(confirmPass))
        {
            editTextConfirmPassword.setError("that isn't the same password. cmon");
            editTextConfirmPassword.requestFocus();
            return;
        }

        //Add the user to Firebase if requirements above are met
        progressBar.setVisibility(View.VISIBLE); // Give the user visual feedback of loading

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) //if the user was registered successfully
                        {
                            User user = new User(username, email); // Creating user object

                            FirebaseDatabase.getInstance().getReference("Users") //Name of collection (found in Firebase)
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid()) //Get current user
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) //Checking if the data was inserted into database
                                        {

                                            if (task.isSuccessful()) //If the user was added to the database successfully
                                            {
                                                allUsernames.add(username.toLowerCase());
                                                FirebaseDatabase.getInstance().getReference("Usernames").setValue(allUsernames);
                                                Toast.makeText(CreateAccountScreen.this, "Registered successfully!", Toast.LENGTH_LONG).show();
                                                progressBar.setVisibility(View.GONE);

                                                finish(); //Go to home screen
                                            }
                                            else //If the user was not added to the database successfully
                                            {
                                                Toast.makeText(CreateAccountScreen.this, "something broke and failed. Try again!", Toast.LENGTH_LONG).show();
                                                progressBar.setVisibility(View.GONE);
                                            }
                                        }//end onComplete method
                                    });
                        }//end of if user was registered successfully (if-statement)
                        else //if the user wasn't registered successfully
                        {
                            Toast.makeText(CreateAccountScreen.this, "something broke and failed. Try again!", Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });

    }//end of registerUser method
}//end of CreateAccountScreen class