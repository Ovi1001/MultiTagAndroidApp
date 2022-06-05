package com.example.multitagandroidapp;

public class Room
{
    public String owner, ownerID, player, playerID;
    public int currentPlayers;

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
    }

    public String getOwner()
    {
        return owner;
    }
}
