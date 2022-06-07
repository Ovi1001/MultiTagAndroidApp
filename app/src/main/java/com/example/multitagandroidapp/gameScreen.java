package com.example.multitagandroidapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class gameScreen extends AppCompatActivity
{
    private TextView output;
    private EditText wordSearch;
    private Button search;

    private boolean match;
    private boolean isHost;
    private String username;

    Handler handler;
    RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_screen);

        //Initializing variables
        isHost = getIntent().getExtras().getBoolean("isHost");
        username = getIntent().getExtras().getString("username");

        output = (TextView) findViewById(R.id.lobbyScreenTextView);
        wordSearch = (EditText) findViewById(R.id.gameScreenTextInput);
        search = (Button) findViewById(R.id.gameScreenGo);

        handler = new Handler();


        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String word = wordSearch.getText().toString().trim();
                isWord(word);

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(gameScreen.this, match + "", Toast.LENGTH_SHORT).show();
                    }
                }, 600);
            }
        });

    }//end onCreate method

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
    }
}//end gameScreen class