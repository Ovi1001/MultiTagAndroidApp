package com.example.multitagandroidapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
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

    private String username, userID, hostName, oppenentName, oppenentID; //Current user's name
    private int currentPlayers;
    private boolean isHost, finishedLoading;
    private Button startBtn;
    private Handler handler;
    private DatabaseReference roomReference;
    private ValueEventListener leaveChange, playerJoin;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby_screen);

        //Configuring buttons and methods
        configureLeaveRoomBtn();

        //Initializing variables
        startBtn = (Button) findViewById(R.id.lobbyScreenStartBtn);
        startBtn.setEnabled(false);
        handler = new Handler(); //Used to give delays before running code (loading time)
        finishedLoading = false;
        progressBar = (ProgressBar) findViewById(R.id.lobbyScreenProgressBar);
        progressBar.setVisibility(View.VISIBLE);
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
                    }
                });

        //Wait 3 seconds before checking isHost (Gives time to load)
        handler.postDelayed(new Runnable() {
            @Override
            public void run()
            {
                if (isHost)
                {
                    //Start button only works for host
                    configureStartBtn();
                    //Create the listener for the host if a new player joins
                    playerJoin = new ValueEventListener()
                    {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot)
                        {
                            Room room = snapshot.getValue(Room.class);
                            currentPlayers = room.currentPlayers;
                            oppenentID = room.playerID;
                            oppenentName = room.player;
                            updateTextNames(oppenentName.equals(""));
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    };
                    //Actually attach the listener to the database (We do it this way so we can remove it later)
                    roomReference.addValueEventListener(playerJoin);
                }

                //Create the listener for the player if the host leaves
                if (!isHost)
                {
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
                            if (user.start)
                            {
                                startGame();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    };
                    //Actually attach the listener to the database
                    FirebaseDatabase.getInstance().getReference("Users").child(userID).addValueEventListener(leaveChange);
                }

                progressBar.setVisibility(View.GONE); //Done loading
                finishedLoading = true;
            }//end run method in handler call
        }, 1500); //end of handler call

    }//end onCreate method

    //Only the host has access to click this button
    private void configureStartBtn()
    {
        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if (currentPlayers == 2)
                {
                    FirebaseDatabase.getInstance().getReference("Users")
                            .child(oppenentID).child("start").setValue(true);
                    startGame();
                }
                else
                { //If tries to start with not enough players
                    Toast.makeText(LobbyScreen.this, "Not enough players!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }//end configureStartBtn method

    private void startGame()
    {
        Toast.makeText(LobbyScreen.this, "Starting...", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(LobbyScreen.this, gameScreen.class);
        startActivity(intent);
    }

    //Changes the text on the client screen
    private void updateTextNames(boolean left)
    {
        TextView hostNameText = (TextView) findViewById(R.id.lobbyScreenHostNameText);
        TextView playerNameText = (TextView) findViewById(R.id.lobbyScreenPlayerNameText);
        if (isHost)
        {
            startBtn.setEnabled(true);
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
            startBtn.setEnabled(false);
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
        if (finishedLoading) //Had to finish loading before leaving
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
                    public void onDataChange(@NonNull DataSnapshot snapshot)
                    {
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
        }
        else //If the user tries to leave while loading
        {
            Toast.makeText(LobbyScreen.this, "Please wait before leaving!", Toast.LENGTH_SHORT).show();
        }
    }//end leaveRoom method

    //If the back button bottom left is clicked
    @Override
    public void onBackPressed()
    {
        leaveRoom();
    }
}//end LobbyScreen class