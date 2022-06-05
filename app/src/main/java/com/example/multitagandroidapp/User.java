package com.example.multitagandroidapp;

public class User
{
    public String username, email;
    public boolean leave;
    public int wins;

    public User()
    {

    }
    public User(String username, String email)
    {
        this.username = username;
        this.email = email;
        this.wins = 0;
        this.leave = false;
    }
}
