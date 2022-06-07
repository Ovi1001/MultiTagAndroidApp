package com.example.multitagandroidapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class gameScreen extends AppCompatActivity
{
    private CountDownTimer timer;
    private DatabaseReference roomReference;
    private long timeLeftInMilliseconds;
    private TextView timerText, lettersText, hostNameText, playerNameText,
            hostNameWord, playerNameWord, wordErrorText;
    private EditText wordInput;
    private ImageView hostArrow, playerArrow;
    private boolean match, isHost;
    private String username, userID, hostName, hostID, oppenentName, oppenentID, message, useLetters;
    private String[] lettersList = {"ie", "er", "co", "za", "wo", "low", "po", "uck", "lp", "om", "on",
    "ns", "sus", "tu", "q", "go", "re", "ing", "pe", "ut", "fr", "vo", "as", "pha", "th", "sh", "ght",
    "ou", "se", "ip", "oar", "ion", "va"};

    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_screen);

        //Initializing variables
        isHost = getIntent().getExtras().getBoolean("isHost");
        username = getIntent().getExtras().getString("username");
        hostName = getIntent().getExtras().getString("hostName");
        hostID = getIntent().getExtras().getString("hostID");
        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        if (isHost)
        {
            oppenentID = getIntent().getExtras().getString("oppenentID");
            oppenentName = getIntent().getExtras().getString("oppenentName");
        }
        message = "";

        roomReference = FirebaseDatabase.getInstance().getReference("Rooms")
                .child(hostName + "'s room");
        timerText = (TextView) findViewById(R.id.gameScreenTimerText);
        lettersText = (TextView) findViewById(R.id.gameScreenLettersText);
        hostNameText = (TextView) findViewById(R.id.gameScreenHostNameText);
        playerNameText = (TextView) findViewById(R.id.gameScreenPlayerNameText);
        hostNameWord = (TextView) findViewById(R.id.gameScreenHostWordText);
        playerNameWord = (TextView) findViewById(R.id.gameScreenPlayerWordText);
        wordErrorText = (TextView) findViewById(R.id.gameScreenWordErrorText);
        wordInput = (EditText) findViewById(R.id.gameScreenInputWordEditText);
        hostArrow = (ImageView) findViewById(R.id.gameScreenHostArrow);
        playerArrow = (ImageView) findViewById(R.id.gameScreenPlayerArrow);
        handler = new Handler();
        timeLeftInMilliseconds = 5000;

        configureWordInput();

        //This is using a different type of loop
        ArrayList<String> wordList = new ArrayList<>();
        for (int i = 0; i < lettersList.length; i++)
        {
            wordList.add(lettersList[i]);
        }
        wordList.remove(3);

        roomReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Room room = snapshot.getValue(Room.class);
                lettersText.setText(room.currentLetters);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //Host will always go first
        if (isHost)
        {
            getNewLetters();
            playerArrow.setVisibility(View.INVISIBLE);
            hostNameText.setText("You");
            playerNameText.setText(oppenentName);

            //Squashes any errors and gets the game ready
            HashMap<String, Object> update = new HashMap<>();
            update.put("turn", false);
            update.put("word", "");
            FirebaseDatabase.getInstance().getReference("Users").child(userID).updateChildren(update);

            //Checks for any updates
            FirebaseDatabase.getInstance().getReference("Users").child(userID).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User user = snapshot.getValue(User.class);
                    if (user.turn == true)
                    {
                        getNewLetters();
                        wordInput.setVisibility(View.VISIBLE);
                        playerArrow.setVisibility(View.INVISIBLE);
                        hostArrow.setVisibility(View.VISIBLE);
                    }
                    playerNameWord.setText(user.word.toLowerCase());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        else if (!isHost) //If not the host
        {
            //Squashes any errors and gets the game ready
            HashMap<String, Object> update = new HashMap<>();
            update.put("turn", false);
            update.put("word", "");
            FirebaseDatabase.getInstance().getReference("Users").child(userID).updateChildren(update);

            playerArrow.setVisibility(View.INVISIBLE);
            wordInput.setVisibility(View.INVISIBLE);
            playerNameText.setText("You");
            hostNameText.setText(hostName);

            //Set an on click listener for when it is the player's turn
            FirebaseDatabase.getInstance().getReference("Users").child(userID).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User user = snapshot.getValue(User.class);
                    if (user.turn == true)
                    {
                        getNewLetters();
                        wordInput.setVisibility(View.VISIBLE);
                        playerArrow.setVisibility(View.VISIBLE);
                        hostArrow.setVisibility(View.INVISIBLE);
                    }
                    hostNameWord.setText(user.word);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        //When the game starts, give them 15 seconds

    }//end onCreate method

    private void getNewLetters()
    {
        int random = (int) (Math.random() * lettersList.length);
        lettersText.setText(lettersList[random]);
        useLetters = lettersList[random];
        HashMap<String, Object> update = new HashMap<>();
        update.put("currentLetters", lettersList[random]);
        roomReference.updateChildren(update);
    }

    //Configures the word input
    private void configureWordInput()
    {
        wordInput.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER)
                {
                    wordErrorText.setVisibility(View.INVISIBLE);
                    String extractedWord = wordInput.getText().toString().trim();
                    //Pass the word that was typed to the other person
                    if (isHost) //pass the word to the player
                    {
                        hostNameWord.setText(extractedWord);
                        HashMap<String, Object> preUpdate = new HashMap<>();
                        preUpdate.put("word", extractedWord);
                        FirebaseDatabase.getInstance().getReference("Users").child(oppenentID).updateChildren(preUpdate);
                    }
                    else //pass the word to the host
                    {
                        playerNameWord.setText(extractedWord);
                        HashMap<String, Object> preUpdate = new HashMap<>();
                        preUpdate.put("word", extractedWord);
                        FirebaseDatabase.getInstance().getReference("Users").child(hostID).updateChildren(preUpdate);
                    }
//                    //Check if the word exists in the english dictionary
//                    if (extractedWord.toLowerCase().contains(useLetters))
                    isWord(extractedWord);
//                    else
//                        message = "Use letters provided!";

                    //Give it loading time
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run()
                        {
                            if (match)
                            {
                                wordInput.setText("");
                                wordInput.clearFocus();
                                if (isHost)
                                {
                                    wordInput.setVisibility(View.INVISIBLE);
                                    playerArrow.setVisibility(View.VISIBLE);
                                    hostArrow.setVisibility(View.INVISIBLE);
                                    HashMap<String, Object> update = new HashMap<>();
                                    update.put("turn", true);
                                    FirebaseDatabase.getInstance().getReference("Users").child(oppenentID).updateChildren(update);
                                    HashMap<String, Object> update2 = new HashMap<>();
                                    update2.put("turn", false);
                                    FirebaseDatabase.getInstance().getReference("Users").child(userID).updateChildren(update2);
                                }
                                else if(!isHost)
                                {
                                    wordInput.setVisibility(View.INVISIBLE);
                                    playerArrow.setVisibility(View.INVISIBLE);
                                    hostArrow.setVisibility(View.VISIBLE);
                                    HashMap<String, Object> update = new HashMap<>();
                                    update.put("turn", true);
                                    FirebaseDatabase.getInstance().getReference("Users").child(hostID).updateChildren(update);
                                    HashMap<String, Object> update2 = new HashMap<>();
                                    update2.put("turn", false);
                                    FirebaseDatabase.getInstance().getReference("Users").child(userID).updateChildren(update2);
                                }
                            }
                            else //If the word match is false
                            {
                                wordErrorText.setVisibility(View.VISIBLE);
                                message = "Not a word";
                                wordInput.setText("");
                            }
                        }
                    }, 600);
                    wordErrorText.setText(message);
                }//end of enter detection from keyboard
                return false;
            }
        });
    }//end configureWordInput method

    private void resetTimer(long timeLeft)
    {
        timer = new CountDownTimer(timeLeft, 1000) {
            @Override
            public void onTick(long time) {;
                timerText.setText(time / 1000 + "");
            }

            @Override
            public void onFinish() {
                blowUp();
            }
        }.start();
    }

    private void blowUp()
    {

    }

    //Determines whether a given string is a word; Loading time is 0.5 seconds
    private void isWord(String word)
    {
        String[] wordFound = {""};
        String url = "https://api.dictionaryapi.dev/api/v2/entries/en/" + word;
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    JSONObject jsonObject = response.getJSONObject(0);
                    wordFound[0] = jsonObject.getString("word");
                } catch (Exception e) {

                }
            }//end onResponse method
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        MySingleton.getInstance(this).addToRequestQueue(jsonArrayRequest);

        handler.postDelayed(new Runnable() {
            @Override
            public void run()
            {
                match = wordFound[0].equalsIgnoreCase(word);
            }
        }, 500);
    }//end isWord method

    @Override
    public void onBackPressed()
    {

    }
}//end gameScreen class