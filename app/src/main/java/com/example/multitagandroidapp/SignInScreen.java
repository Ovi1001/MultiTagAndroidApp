package com.example.multitagandroidapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class SignInScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in_screen);
        configureBackFromSignInBtn();
    }

    private void configureBackFromSignInBtn()
    {
        Button signInBackButton = (Button) findViewById(R.id.backFromSignInBtn);
        signInBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}