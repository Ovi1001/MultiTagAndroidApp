package com.example.multitagandroidapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LobbyScreen extends AppCompatActivity {

    private Room room;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby_screen);

        TextView roomName = (TextView) findViewById(R.id.lobbyScreenRoomNameText);
        DatabaseReference roomReference = FirebaseDatabase.getInstance().getReference("Rooms");
        roomReference.child(getIntent().getExtras().getString("username") + "'s room")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot)
                    {
                        room = snapshot.getValue(Room.class);
                        roomName.setText(room.owner + "'s room"); //Get the username passed and set it equal to room name
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) //if something goes wrong & end activity
                    {
                        Toast.makeText(LobbyScreen.this, "Something went wrong. Try again", Toast.LENGTH_LONG).show();
                        finish();
                    }
                });

    }//end onCreate method
}//end LobbyScreen class