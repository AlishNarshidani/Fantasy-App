package com.example.fantasyapp;

import android.content.Intent;
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
import androidx.viewpager2.widget.ViewPager2;

import com.android.volley.VolleyError;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class TeamRankingsInContest extends AppCompatActivity {

    private CricApiService cricApiService;

    LinearLayout scoreSummary;
    ImageView team1Flag,team2Flag;
    TextView team1Name,team2Name;
    TextView team1TotalScore,team2TotalScore;
    TextView team1Overs,team2Overs;
    TextView matchResult;

    String contestId, match_id;

    FirebaseAuth auth;
    FirebaseFirestore db;

    ArrayList<String> prizesList;
    ArrayList<String> teamIds;

    ViewPager2 viewPager;
    TabLayout tabLayout;

    Match match;
    String caller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_team_rankings_in_contest);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        cricApiService = new CricApiService(this);


        caller = getIntent().getStringExtra("caller");
        contestId = getIntent().getStringExtra("contest_id");
        match = (Match) getIntent().getSerializableExtra("match");
        match_id = getIntent().getStringExtra("match_id");

        db= FirebaseFirestore.getInstance();
        auth= FirebaseAuth.getInstance();

        prizesList = new ArrayList<>();
        teamIds = new ArrayList<>();

        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);

        scoreSummary = findViewById(R.id.scoreSummary);
        team1Flag = findViewById(R.id.team1Flag);
        team2Flag = findViewById(R.id.team2Flag);
        team1Name = findViewById(R.id.team1Name);
        team2Name = findViewById(R.id.team2Name);
        team1TotalScore = findViewById(R.id.team1TotalScore);
        team2TotalScore = findViewById(R.id.team2TotalScore);
        team1Overs = findViewById(R.id.team1Overs);
        team2Overs = findViewById(R.id.team2Overs);
        matchResult = findViewById(R.id.matchResult);


        team1Flag.setImageResource(match.getTeam1ImageResId());
        team2Flag.setImageResource(match.getTeam2ImageResId());
        team1Name.setText(match.getTeam1ShortName());
        team2Name.setText(match.getTeam2ShortName());

        scoreSummary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getApplicationContext(),Scorecard.class);
                i.putExtra("match_id",match_id);
                startActivity(i);
            }
        });


        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        fetchMatchScore(match_id);
                    }
                });
            }
        },0,600000);

        fetchAllContestDetails();
    }


    private void fetchMatchScore(String matchId)
    {
        cricApiService.getCricScore(matchId, new CricApiService.DataCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    Log.d("API_RESPONSE", response.toString());
                    String apiStatus=response.getString("status");
                    Log.d("API_STATUS", apiStatus);

                    JSONArray matches=response.getJSONArray("data");
                    Log.d("MATCH_COUNT", "Total matches: " + matches.length());

                    for(int i=0;i<matches.length();i++)
                    {
                        JSONObject match=matches.getJSONObject(i);
                        String id = match.getString("id");

                        if(matchId.equals(id))
                        {
                            String t1s = match.getString("t1s");
                            String t2s = match.getString("t2s");
                            String match_status = match.getString("status");
                            String team1runs_str,team1overs_str,team2runs_str,team2overs_str;

                            if(t1s.isEmpty()) {
                                team1runs_str = "Yet to Bat";
                                team1overs_str = "";
                            }else {
                                String[] t1scoreArr = t1s.split(" ");

                                team1runs_str = t1scoreArr[0];
                                team1overs_str = t1scoreArr[1];
                            }

                            if(t2s.isEmpty()) {
                                team2runs_str = "Yet to Bat";
                                team2overs_str = "";
                            }else {
                                String[] t2scoreArr = t2s.split(" ");

                                team2runs_str = t2scoreArr[0];
                                team2overs_str = t2scoreArr[1];
                            }


                            team1TotalScore.setText(team1runs_str);
                            team1Overs.setText(team1overs_str);
                            team2TotalScore.setText(team2runs_str);
                            team2Overs.setText(team2overs_str);
                            matchResult.setText(match_status);
                        }
                    }

                }catch (JSONException e)
                {
                    Log.e("JSON_ERROR_SCORECARD", "Error parsing JSON", e);
                    Toast.makeText(getApplicationContext(), "JSON parsing error", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(VolleyError error) {
                Log.e("API_ERROR", "Error fetching data", error);
                Toast.makeText(getApplicationContext(), "Error fetching data", Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void fetchAllContestDetails()
    {
        FirebaseUser user=auth.getCurrentUser();
        String userId=user.getUid();

        // Reference to the document in the 'users' collection
        DocumentReference contestsDocRef = db.collection("contests").document(contestId);

        contestsDocRef.get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();

                        if(document!=null && document.exists()) {

                            // The document exists, get all fields as a Map
                            Map<String, Object> fetchUserData = document.getData();

                            if (fetchUserData != null) {

                                String numberOfWinnersStr = document.get("numberOfWinners").toString();
                                int numberOfWinnersInt = Integer.parseInt(numberOfWinnersStr);

                                for(int i=1;i<=numberOfWinnersInt;i++)
                                {
                                    String prizeId = "Prize"+i;

                                    String prizeAmount = document.get(prizeId).toString();

                                    prizesList.add(prizeAmount);
                                }

                                teamIds = (ArrayList<String>) document.get("team_ids");

                                Log.d("teamIds", "teamIds: "+teamIds);


                                setupViewPager();
                            }

                        } else {
                            Log.d("error", "No such document!");
                            //Toast.makeText(getActivity(), "No such document!", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        Log.d("error", "Error fetching data!");
                    }
                });

    }

    private void setupViewPager()
    {
        RankingsViewPagerAdapter adapter = new RankingsViewPagerAdapter(this,prizesList,teamIds, match_id, match, contestId, caller);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(1);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("Team Rankings");
                    break;
                case 1:
                    tab.setText("Winnings");
                    break;
            }
        }).attach();
    }
}