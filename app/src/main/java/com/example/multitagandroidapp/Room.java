package com.example.multitagandroidapp;

import java.util.ArrayList;
import java.util.List;

public class Room
{
    public String owner, ownerID, player, playerID;
    public int currentPlayers;
    public List<String> chatHistory;

    public Room()
    {

    }

    public Room(String owner, String ownerID)
    {
        this.owner = owner;
        this.ownerID = ownerID;
        this.player = "";
        this.playerID = "";
        this.currentPlayers = 1;
        this.chatHistory = new ArrayList<String>();
    }

    public String getOwner()
    {
        return owner;
    }
}
