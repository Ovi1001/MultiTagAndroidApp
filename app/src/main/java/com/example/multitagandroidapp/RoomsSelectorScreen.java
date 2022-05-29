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

public class RoomsSelectorScreen extends AppCompatActivity
{

    private FirebaseUser user;
    private DatabaseReference reference;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rooms_selector_screen);

        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        userID = user.getUid(); //Get the user's unique ID
        TextView welcomeText = (TextView) findViewById(R.id.roomsScreenWelcomeText);

        //Toast.makeText(RoomsSelectorScreen.this, userID + "", Toast.LENGTH_LONG).show();

        reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                User userProfile = snapshot.getValue(User.class);

                if (userProfile != null)
                {
                    welcomeText.setText("Welcome, " + userProfile.username);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {
                welcomeText.setText("N/A");
            }
        });
    }//end onCreate method
} //end RoomsSelectorScreen