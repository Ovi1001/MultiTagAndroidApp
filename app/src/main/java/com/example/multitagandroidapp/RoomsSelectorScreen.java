package com.example.multitagandroidapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class RoomsSelectorScreen extends AppCompatActivity
{

    private FirebaseUser user;
    private DatabaseReference referenceUsers;

    private String userID;
    private String username;

    private ListView lobbiesList;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rooms_selector_screen);

        //Initializing variables
        lobbiesList = (ListView) findViewById(R.id.roomsSelectorLobbiesListElement);
        user = FirebaseAuth.getInstance().getCurrentUser();
        referenceUsers = FirebaseDatabase.getInstance().getReference("Users");
        userID = user.getUid(); //Get the user's unique ID
        TextView welcomeText = (TextView) findViewById(R.id.roomsScreenWelcomeText);

        //Configuring buttons
        configureSignOutBtn();
        configureCreateRoomBtn();

        referenceUsers.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                User userProfile = snapshot.getValue(User.class);

                if (userProfile != null)
                {
                    username = userProfile.username;
                    welcomeText.setText("Hi, " + username);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) //if did not successfully retrieve username
            {
                welcomeText.setText("N/A");
            }
        });//end of getting username and changing text

        //Gets all of the current open rooms and displays them
        FirebaseDatabase.getInstance().getReference("Rooms").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                List<String> roomsList = new ArrayList<String>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren())
                {
                    Room temp = snapshot.getValue(Room.class);
                    //Toast.makeText(RoomsSelectorScreen.this, temp.owner, Toast.LENGTH_LONG).show();
                    roomsList.add(temp.owner + "'s room");
                }

                //Displays all of the rooms on the screen
                ArrayAdapter arrayAdapter = new ArrayAdapter(RoomsSelectorScreen.this, android.R.layout.simple_list_item_1, roomsList);
                lobbiesList.setAdapter(arrayAdapter); //Changes the list UI
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { //if something goes wrong
                Toast.makeText(RoomsSelectorScreen.this, "Something went wrong. Try again", Toast.LENGTH_LONG).show();
            }
        });

        //Checks for room number picked
        lobbiesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
                if (pos == 0)
                {
                    Toast.makeText(RoomsSelectorScreen.this, "apple", Toast.LENGTH_LONG).show();
                }
                else if (pos == 1)
                {
                    Toast.makeText(RoomsSelectorScreen.this, "orange", Toast.LENGTH_LONG).show();
                }
            }
        });

    }//end onCreate method

    //Creates a game room in the lobbies screen
    private void createRoom()
    {
        Room room = new Room(username);
        FirebaseDatabase.getInstance().getReference("Rooms") //name of collection
                .child(room.owner + "'s room").setValue(room); //Adding to database

        Intent intent = new Intent(RoomsSelectorScreen.this, LobbyScreen.class);
        intent.putExtra("username", username); //For passing information between activities
        startActivity(intent);
    }

    private void configureCreateRoomBtn()
    {
        Button createRoom = (Button) findViewById(R.id.roomsSelectorCreateRoomBtn);
        createRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createRoom();
            }
        });
    }

    private void configureSignOutBtn() //Signs the user out & closes activity
    {
        Button signOut = (Button) findViewById(R.id.roomsSelectorSignOutBtn);
        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSignOutRoomDialog(v);
            }
        });
    }

    //Configures the pop-up on screen when clicking sign out
    private void openSignOutRoomDialog(View v)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to sign out?");
        builder.setNegativeButton("No way jose", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Method is empty bc just needs to close dialog
            }
        });

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() { //If the user clicks yes
            @Override
            public void onClick(DialogInterface dialog, int which) {
                FirebaseAuth.getInstance().signOut();
                finish();
            }});

        AlertDialog message = builder.create();
        message.show();
    }
} //end RoomsSelectorScreen