package com.example.multitagandroidapp;

public class User
{
    public String username, email, word;
    public boolean leave, start, turn;
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
        this.start = false;
        this.turn = false;
    }
}
