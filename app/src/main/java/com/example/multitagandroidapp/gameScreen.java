package com.example.multitagandroidapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class gameScreen extends AppCompatActivity
{
    private boolean isHost;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_screen);

        //Initializing variables
        isHost = getIntent().getExtras().getBoolean("isHost");
        username = getIntent().getExtras().getString("username");


    }//end onCreate method
}//end gameScreen class