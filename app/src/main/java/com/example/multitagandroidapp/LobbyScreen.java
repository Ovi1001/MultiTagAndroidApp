package com.example.multitagandroidapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LobbyScreen extends AppCompatActivity {

    private String username; //Current user's name
    private DatabaseReference roomReference;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby_screen);

        //Initializing variables
        TextView roomName = (TextView) findViewById(R.id.lobbyScreenRoomNameText);
        roomReference = FirebaseDatabase.getInstance().getReference("Rooms")
                .child(getIntent().getExtras().getString("roomName") + "'s room");
        username = getIntent().getExtras().getString("username");

        //Instantiate room object / Change title of LobbyScreen
        roomReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot)
                    {
                        Room room = snapshot.getValue(Room.class);
                        roomName.setText(room.owner + "'s room"); //Get the username passed and set it equal to room name

                        //Adds the player that joined lobby to the database
                        if (!room.owner.equals(username))
                        {
                            room.player = username;
                            room.playerID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                            room.currentPlayers = 2;
                            roomReference.setValue(room);
                        }
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