package com.example.multitagandroidapp;

import java.util.ArrayList;
import java.util.List;

public class Room
{
    public String owner;
    public int currentPlayers;
    public List<String> chatHistory;

    public Room()
    {

    }

    public Room(String owner)
    {
        this.owner = owner;
        this.currentPlayers = 1;
        this.chatHistory = new ArrayList<String>();
    }

    public String getOwner()
    {
        return owner;
    }
}
