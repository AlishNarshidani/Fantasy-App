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
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.VolleyError;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class JoinedContests extends AppCompatActivity {

    private CricApiService cricApiService;

    LinearLayout scoreSummary;
    ImageView team1Flag,team2Flag;
    TextView team1Name,team2Name;
    TextView team1TotalScore,team2TotalScore;
    TextView team1Overs,team2Overs;
    TextView matchResult;
    Match match;
    String match_id;

    RecyclerView recyclerView;

    contestAdapter adapter;

    List<Map<String, Object>> contestsList;
    FirebaseFirestore db;
    FirebaseAuth auth;

    String caller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_joined_contests);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        cricApiService=new CricApiService(this);

        caller = getIntent().getStringExtra("caller");
        match = (Match) getIntent().getSerializableExtra("match");
        match_id= match.getId();


        recyclerView = findViewById(R.id.displayAllParticipatedContestRecyclerView);

        db = FirebaseFirestore.getInstance();
        auth= FirebaseAuth.getInstance();

        contestsList = new ArrayList<>();

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


        checkJoinedContests();

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


    public void checkJoinedContests()
    {
        db.collection("contests")
                .whereEqualTo("match_id",match_id)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if(task.isSuccessful())
                        {
                            if (!task.getResult().isEmpty()) {

                                contestsList.clear();

                                // At least one public contest is available for the match
                                for (QueryDocumentSnapshot document : task.getResult()) {

                                    // Get contest data as a map and add to the list
                                    Map<String, Object> contestData = document.getData();

                                    List<String> teamIds = (List<String>) contestData.get("team_ids");


                                    if (teamIds.size() != 0){

                                        for(String teamId : teamIds)
                                        {
                                            String [] parts = teamId.split("_");
                                            Log.d("check", "onComplete: "+parts);

                                            if (parts.length == 3) {

                                                String fetchedMatchId = parts[0];
                                                String fetchedUserId = parts[1];
                                                String fetchedTeamId = parts[2];

                                                if(fetchedUserId.equals(auth.getUid()))
                                                {
                                                    Log.d("participated", "onComplete: "+contestData.get("contest_id"));
                                                    contestsList.add(contestData);
                                                    break;
                                                }

                                            }
                                        }

                                    }
                                }

                                loadContests();
                                Log.d("Contests", "Fetched contests: " + contestsList);

                            } else {
                                // No public contest is available for the match
                                Log.d("NoJoinedContest", "No Joined contests available for the match.");
                            }
                        } else {
                            // Handle the error
                            Log.d("FirestoreError", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }


    public void loadContests()
    {
        adapter = new contestAdapter(JoinedContests.this,contestsList, caller, match);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setAdapter(adapter);
    }
}