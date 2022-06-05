package com.example.multitagandroidapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class LobbyScreen extends AppCompatActivity {

    private String username; //Current user's name
    private boolean isHost;
    private DatabaseReference roomReference;
    private ValueEventListener leaveChange;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby_screen);

        //Configuring buttons
        configureLeaveRoomBtn();

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
                        isHost = room.owner.equals(username);
                        if (!isHost)
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

        if (!isHost)
        {
            //Create the listener for if the host leaves
            leaveChange = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User user = snapshot.getValue(User.class);
                    if (user.leave)
                    {
                        Toast.makeText(LobbyScreen.this, "Host has left", Toast.LENGTH_SHORT).show();
                        HashMap<String, Object> update = new HashMap<>();
                        update.put("leave", false);
                        FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().
                                getCurrentUser().getUid()).updateChildren(update);
                        finish();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            };
            //Actually attach the listener to the database
            FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().
                    getCurrentUser().getUid()).addValueEventListener(leaveChange);
        }
    }//end onCreate method

    //When the activity closes / finishes
    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().
            getCurrentUser().getUid()).removeEventListener(leaveChange);
    }

    private void configureLeaveRoomBtn()
    {
        Button leaveRoomBtn = (Button) findViewById(R.id.lobbyScreenCancelRoomBtn);
        leaveRoomBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                leaveRoom();
            }
        });
    }

    //Delete the room or open up the space
    private void leaveRoom()
    {
        if (!isHost)
        {
            HashMap<String, Object> update = new HashMap<>();
            update.put("currentPlayers", 1);
            update.put("player", "");
            update.put("playerID", "");
            roomReference.updateChildren(update);
        }
        else
        { //if the hosts leaves
            roomReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Room room = snapshot.getValue(Room.class);
                    if (room.currentPlayers == 1)
                    {
                        roomReference.removeValue();
                    }
                    //tell the other player that the lobby no longer exists
                    else if (room.currentPlayers == 2)
                    {
                        FirebaseDatabase.getInstance().getReference("Users")
                                .child(room.playerID).child("leave").setValue(true);
                        roomReference.removeValue();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(LobbyScreen.this, "Something went wrong. Try again", Toast.LENGTH_LONG).show();
                }
            });
        }
        finish();
    }

    @Override
    public void onBackPressed()
    {
        leaveRoom();
    }
}//end LobbyScreen class