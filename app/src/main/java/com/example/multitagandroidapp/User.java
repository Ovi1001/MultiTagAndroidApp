package com.example.multitagandroidapp;

public class User
{
    public String username, email;
    public int wins;

    public User()
    {

    }
    public User(String username, String email)
    {
        this.username = username;
        this.email = email;
        wins = 0;
    }
}
