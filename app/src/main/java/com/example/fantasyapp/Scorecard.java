package com.example.fantasyapp;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Scorecard extends AppCompatActivity {

    private CricApiService cricApiService;
    private LinearLayout inningsContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_scorecard);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        inningsContainer= findViewById(R.id.inningsContainer);
        cricApiService=new CricApiService(this);
        String match_id=getIntent().getStringExtra("match_id");
        fetchScorecard(match_id);
    }

    private void fetchScorecard(String matchId) {
        cricApiService.getScorecard(matchId, new CricApiService.DataCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    Log.d("API_RESPONSE", response.toString());
                    String apiStatus=response.getString("status");
                    Log.d("API_STATUS", apiStatus);

                    JSONObject data=response.getJSONObject("data");
                    JSONArray score=data.getJSONArray("score");
                    JSONArray scorecard=data.getJSONArray("scorecard");
                    Log.d("MATCH_DATA","Data:"+data);

                    for(int i=0;i<score.length();i++)
                    {
                        JSONObject inningDetail=score.getJSONObject(i);
                        String inningName=inningDetail.getString("inning");
                        JSONObject scorecardInning=scorecard.getJSONObject(i);
                        addInningsView(inningName,scorecardInning,inningDetail);
                    }
                } catch (JSONException e) {
                    Log.e("JSON_ERROR_SCORECARD", "Error parsing JSON", e);
                    Toast.makeText(Scorecard.this, "JSON parsing error", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(VolleyError error) {
                Log.e("API_ERROR", "Error fetching data", error);
                Toast.makeText(Scorecard.this, "Error fetching data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addInningsView(String inningName,JSONObject scorecardInning, JSONObject inningDetail) throws JSONException {
        View inningsView = getLayoutInflater().inflate(R.layout.item_innings, inningsContainer, false);
        TextView inningsName = inningsView.findViewById(R.id.inningsName);
        TextView inningsStats = inningsView.findViewById(R.id.inningsStats);
        ImageView expandCollapseIcon = inningsView.findViewById(R.id.expandCollapseIcon);
        LinearLayout inningsDetails = inningsView.findViewById(R.id.inningsDetails);

        inningsName.setText(inningName);

        int runs = inningDetail.getInt("r");
        int wickets = inningDetail.getInt("w");
        double overs = inningDetail.getDouble("o");
        inningsStats.setText(String.format("%d/%d in %.1f overs", runs, wickets, overs));

        inningsName.setOnClickListener(v -> {
            if (inningsDetails.getVisibility() == View.GONE) {
                inningsDetails.setVisibility(View.VISIBLE);
                expandCollapseIcon.setImageResource(R.drawable.ic_expand_less);
            } else {
                inningsDetails.setVisibility(View.GONE);
                expandCollapseIcon.setImageResource(R.drawable.ic_expand_more);
            }
        });

        View headerView=getLayoutInflater().inflate(R.layout.item_batsman_header,inningsDetails,false);
        inningsDetails.addView(headerView);
        // Populate innings details
        JSONArray battingArray = scorecardInning.getJSONArray("batting");
        for (int i = 0; i < battingArray.length(); i++) {
            JSONObject batsman = battingArray.getJSONObject(i);
            View batsmanView = getLayoutInflater().inflate(R.layout.item_batsman, inningsDetails, false);

            TextView batsmanName = batsmanView.findViewById(R.id.batsmanName);
            TextView batsmanRuns = batsmanView.findViewById(R.id.batsmanRuns);
            TextView batsmanBalls = batsmanView.findViewById(R.id.batsmanBalls);
            TextView batsmanFours = batsmanView.findViewById(R.id.batsmanFours);
            TextView batsmanSixes = batsmanView.findViewById(R.id.batsmanSixes);
            TextView batsmanStrikeRate = batsmanView.findViewById(R.id.batsmanStrikeRate);
            TextView dismissalText = batsmanView.findViewById(R.id.dismissalText);

            batsmanName.setText(batsman.getJSONObject("batsman").getString("name"));
            batsmanRuns.setText(batsman.getString("r"));
            batsmanBalls.setText(batsman.getString("b"));
            batsmanFours.setText(batsman.getString("4s"));
            batsmanSixes.setText(batsman.getString("6s"));
            batsmanStrikeRate.setText(batsman.getString("sr"));
            dismissalText.setText(batsman.getString("dismissal-text"));

            inningsDetails.addView(batsmanView);
        }

        View spaceView = new View(this);
        spaceView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                16
        ));
        inningsDetails.addView(spaceView);

        View bowlerHeaderView = getLayoutInflater().inflate(R.layout.item_bowler_header, inningsDetails, false);
        inningsDetails.addView(bowlerHeaderView);

        JSONArray bowlingArray = scorecardInning.getJSONArray("bowling");
        for (int i = 0; i < bowlingArray.length(); i++) {
            JSONObject bowler = bowlingArray.getJSONObject(i);
            View bowlerView = getLayoutInflater().inflate(R.layout.item_bowler, inningsDetails, false);

            TextView bowlerName = bowlerView.findViewById(R.id.bowlerName);
            TextView bowlerOvers = bowlerView.findViewById(R.id.bowlerOvers);
            TextView bowlerMaidens = bowlerView.findViewById(R.id.bowlerMaidens);
            TextView bowlerWickets = bowlerView.findViewById(R.id.bowlerWickets);
            TextView bowlerRuns = bowlerView.findViewById(R.id.bowlerRuns);
            TextView bowlerEconomy = bowlerView.findViewById(R.id.bowlerEconomy);

            bowlerName.setText(bowler.getJSONObject("bowler").getString("name"));
            bowlerOvers.setText(bowler.getString("o"));
            bowlerMaidens.setText(bowler.getString("m"));
            bowlerWickets.setText(bowler.getString("w"));
            bowlerRuns.setText(bowler.getString("r"));
            bowlerEconomy.setText(bowler.getString("eco"));

            inningsDetails.addView(bowlerView);
        }

        inningsContainer.addView(inningsView);
    }
}