package com.example.multitagandroidapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        configureSignInScreenBtn();
        configureCreateAccountScreenBtn();
    }

    public void configureSignInScreenBtn()
    {
        Button toSignInScreenBtn = (Button) findViewById(R.id.signInScreenBtn);
        toSignInScreenBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                startActivity(new Intent(MainActivity.this, SignInScreen.class));
            }
        });
    }

    public void configureCreateAccountScreenBtn()
    {
        Button toCreateAccScreenBtn = (Button) findViewById(R.id.createAccountScreenBtn);
        toCreateAccScreenBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                startActivity(new Intent(MainActivity.this, CreateAccountScreen.class));
            }
        });
    }
}