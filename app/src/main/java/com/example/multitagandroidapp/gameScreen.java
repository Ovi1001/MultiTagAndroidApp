package com.example.multitagandroidapp;

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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class gameScreen extends AppCompatActivity
{
    private CountDownTimer timer;
    private long timeLeftInMilliseconds;
    private TextView timerText, lettersText, hostNameText, playerNameText,
            hostNameWord, playerNameWord;
    private EditText wordInput;
    private ImageView hostArrow, playerArrow;
    private boolean match, isHost;
    private String username;
    private ArrayList<String> usedWords;
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

        timerText = (TextView) findViewById(R.id.gameScreenTimerText);
        lettersText = (TextView) findViewById(R.id.gameScreenLettersText);
        hostNameText = (TextView) findViewById(R.id.gameScreenHostNameText);
        playerNameText = (TextView) findViewById(R.id.gameScreenPlayerNameText);
        hostNameWord = (TextView) findViewById(R.id.gameScreenHostWordText);
        playerNameWord = (TextView) findViewById(R.id.gameScreenPlayerWordText);
        wordInput = (EditText) findViewById(R.id.gameScreenInputWordEditText);
        hostArrow = (ImageView) findViewById(R.id.gameScreenHostArrow);
        playerArrow = (ImageView) findViewById(R.id.gameScreenPlayerArrow);
        usedWords = new ArrayList<String>();
        handler = new Handler();
        timeLeftInMilliseconds = 5000;

        configureWordInput();

        //Host will always go first
        if (isHost)
        {
            wordInput.setEnabled(true);
        }
        else
        {
            wordInput.setEnabled(false);
        }
        //When the game starts, give them 15 seconds
        resetTimer(15000);
    }//end onCreate method

    //Configures the word input
    private void configureWordInput()
    {
        wordInput.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                    isWord(wordInput.getText().toString().trim());
                    wordInput.setText("");
                    wordInput.clearFocus();
                }
                return false;
            }
        });
    }

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
        toast();
    }//end isWord method

    private void toast()
    {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(gameScreen.this, match + "", Toast.LENGTH_SHORT).show();
            }
        }, 600);
    }

    @Override
    public void onBackPressed()
    {

    }
}//end gameScreen class