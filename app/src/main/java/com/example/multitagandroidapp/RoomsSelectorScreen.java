package com.example.multitagandroidapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
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

        //What is being displayed on the lobbiesList listView
        List<String> list = new ArrayList<String>();
        list.add("Apple");
        list.add("Orange");
        list.add("Banana");
        list.add("Grapes");

        ArrayAdapter arrayAdapter = new ArrayAdapter(RoomsSelectorScreen.this, android.R.layout.simple_list_item_1, list);
        lobbiesList.setAdapter(arrayAdapter); //Changes the list

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