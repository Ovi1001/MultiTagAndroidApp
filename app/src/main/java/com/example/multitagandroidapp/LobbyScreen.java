package com.example.multitagandroidapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class LobbyScreen extends AppCompatActivity {

    private String username, userID, hostName, oppenentName; //Current user's name
    private boolean isHost;
    private Handler handler;
    private DatabaseReference roomReference;
    private ValueEventListener leaveChange, playerJoin;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby_screen);

        //Configuring buttons and methods
        configureLeaveRoomBtn();

        //Initializing variables
        handler = new Handler();
        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
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
                        hostName = room.owner;
                        roomName.setText(room.owner + "'s room"); //Get the username passed and set it equal to room name

                        //Adds the player that joined lobby to the database
                        isHost = (room.owner).equalsIgnoreCase(username);
                        updateTextNames(room.player.equals(""));
                        if (!isHost)
                        {
                            HashMap<String, Object> update = new HashMap<>();
                            update.put("player", username);
                            update.put("playerID", userID);
                            update.put("currentPlayers", 2);
                            roomReference.updateChildren(update);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) //if something goes wrong & end activity
                    {
                        Toast.makeText(LobbyScreen.this, "Something went wrong. Try again", Toast.LENGTH_LONG).show();
                        finish();
                    }
                });

        handler.postDelayed(new Runnable() { //Going to wait 3 seconds before checking isHost
            @Override
            public void run()
            {
                //Create the listener for if a player joins
                if (isHost)
                {
                    playerJoin = new ValueEventListener()
                    {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot)
                        {
                            Room room = snapshot.getValue(Room.class);
                            oppenentName = room.player;
                            updateTextNames(oppenentName.equals(""));
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    };
                    //Actually attach the listener to the database
                    roomReference.addValueEventListener(playerJoin);
                }

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
                                FirebaseDatabase.getInstance().getReference("Users").child(userID).updateChildren(update);
                                finish();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    };
                    //Actually attach the listener to the database
                    FirebaseDatabase.getInstance().getReference("Users").child(userID).addValueEventListener(leaveChange);
                }
            }
        }, 3000); //end of handler call

    }//end onCreate method

    private void updateTextNames(boolean left)
    {
        TextView hostNameText = (TextView) findViewById(R.id.lobbyScreenHostNameText);
        TextView playerNameText = (TextView) findViewById(R.id.lobbyScreenPlayerNameText);
        if (isHost)
        {
            hostNameText.setText("You");
            playerNameText.setText(oppenentName);
        }

        if (!isHost)
        {
            hostNameText.setText(hostName);
            playerNameText.setText("You");
        }
        else if (left)
        {
            playerNameText.setText("Finding Oppenent...");
        }
    }

    //When the activity closes / finishes
    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        if (!isHost)
            FirebaseDatabase.getInstance().getReference("Users").child(userID).removeEventListener(leaveChange);
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
            roomReference.removeEventListener(playerJoin);
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
        }//end else statement
        finish();
    }//end leaveRoom method

    @Override
    public void onBackPressed()
    {
        leaveRoom();
    }
}//end LobbyScreen class