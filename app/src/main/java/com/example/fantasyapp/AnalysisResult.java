package com.example.fantasyapp;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;

public class AnalysisResult extends AppCompatActivity {

    private LinearLayout topBatsmenLayout;
    private LinearLayout topBowlersLayout;
    private LinearLayout topAllroundersLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analysis_result);

        topBatsmenLayout = findViewById(R.id.topBatsmenLayout);
        topBowlersLayout = findViewById(R.id.topBowlersLayout);
        topAllroundersLayout = findViewById(R.id.topAllroundersLayout);

        String topBatsmen = getIntent().getStringExtra("top_batsmen");
        String topBowlers = getIntent().getStringExtra("top_bowlers");
        String topAllrounders = getIntent().getStringExtra("top_allrounders");
        String playerImagesString = getIntent().getStringExtra("player_images");

        HashMap<String, String> playerImages = new HashMap<>();
        try {
            JSONObject playerImagesJson = new JSONObject(playerImagesString);
            Iterator<String> keys = playerImagesJson.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                String value = playerImagesJson.getString(key);
                playerImages.put(key, value);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            JSONArray topBatsmenArray = new JSONArray(topBatsmen);
            JSONArray topBowlersArray = new JSONArray(topBowlers);
            JSONArray topAllroundersArray = new JSONArray(topAllrounders);

            displayPlayers(topBatsmenArray, topBatsmenLayout, playerImages);
            displayPlayers(topBowlersArray, topBowlersLayout, playerImages);
            displayPlayers(topAllroundersArray, topAllroundersLayout, playerImages);

        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error parsing data", Toast.LENGTH_SHORT).show();
        }
    }

    private void displayPlayers(JSONArray playersArray, LinearLayout layout, HashMap<String, String> playerImages) {
        for (int i = 0; i < playersArray.length(); i++) {
            try {
                String playerName = playersArray.getString(i);
                String playerImageUrl = playerImages.get(playerName);

                LinearLayout playerLayout = new LinearLayout(this);
                playerLayout.setOrientation(LinearLayout.VERTICAL);
                playerLayout.setPadding(8, 8, 8, 8);

                ImageView playerImageView = new ImageView(this);
                LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams(300, 300);
                playerImageView.setLayoutParams(imageParams);
                Picasso.get().load(playerImageUrl).into(playerImageView);

                TextView playerNameTextView = new TextView(this);
                playerNameTextView.setText(playerName);
                playerNameTextView.setPadding(0, 8, 0, 0);
                playerNameTextView.setTextAlignment(TextView.TEXT_ALIGNMENT_CENTER);

                playerLayout.addView(playerImageView);
                playerLayout.addView(playerNameTextView);

                layout.addView(playerLayout);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}