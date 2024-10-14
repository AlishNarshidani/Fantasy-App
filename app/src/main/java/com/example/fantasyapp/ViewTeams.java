package com.example.fantasyapp;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class ViewTeams extends AppCompatActivity {

    FirebaseFirestore db;
    FirebaseAuth auth;
    Map<String, List<Player>> allTeams;
    Match match;
    List<List<Player>> listOfAllTeams;

    String match_id;
    String team_1,team_2;


    RecyclerView recyclerView;
    TeamShortViewAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_view_teams);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        match = (Match) getIntent().getSerializableExtra("match");
        match_id = match.getId();
        team_1 = match.getTeam1ShortName();
        team_2 = match.getTeam2ShortName();

        allTeams = new TreeMap<>();
        listOfAllTeams  = new ArrayList<>();

        recyclerView = findViewById(R.id.displayAllTeamsRecyclerView);

        db = FirebaseFirestore.getInstance();
        auth= FirebaseAuth.getInstance();

        fetchAllTeams(new FetchCallBack() {
            @Override
            public void onSuccess() {

                int totalTeamsSize = allTeams.size();

                for(int i=1;i<=totalTeamsSize;i++)
                {
                    String teamid = "team"+String.valueOf(i);
                    List<Player> tempTeam = new ArrayList<>();

                    tempTeam = allTeams.get(teamid);

                    listOfAllTeams.add(tempTeam);
                }

                adapter = new TeamShortViewAdapter(ViewTeams.this,listOfAllTeams, team_1, team_2, match_id);
                recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                recyclerView.setAdapter(adapter);

            }

            @Override
            public void onFailure() {

            }
        });


    }

    public interface FetchCallBack{
        void onSuccess();
        void onFailure();
    }

    public void fetchAllTeams(FetchCallBack fetchCallBack)
    {
        String match_id = match.getId();
        String user_id = auth.getUid();

        db.collection("teams")
                .get()
                .addOnCompleteListener(task -> {

                    if(task.isSuccessful()) {

                        for(QueryDocumentSnapshot document : task.getResult()) {

                            String documentId = document.getId();

                            String[] parts = documentId.split("_");

                            if (parts.length == 3)
                            {
                                String fetchedMatchId = parts[0];
                                String fetchedUserId = parts[1];
                                String fetchedTeamId = parts[2];

                                if (fetchedMatchId.equals(match_id) && fetchedUserId.equals(user_id)) {

                                    Log.d("key team", "fetchAllTeams: "+fetchedTeamId);

                                    List<Player> team = new ArrayList<>();

                                    // This document matches the current matchId and userId
                                    Map<String, Object> teamData = document.getData();

                                    Map<String, Object> captainData = (Map<String, Object>) teamData.get("captain");

                                    Player captain = new Player(
                                            (String) captainData.get("playerCountry"),
                                            (String) captainData.get("playerId"),
                                            (String) captainData.get("playerName"),
                                            (String) captainData.get("playerRole"),
                                            (String) captainData.get("playerImageUrl")
                                    );



                                    Map<String, Object> viceCaptainData = (Map<String, Object>) teamData.get("viceCaptain");
                                    Player viceCaptain = new Player(
                                            (String) viceCaptainData.get("playerCountry"),
                                            (String) viceCaptainData.get("playerId"),
                                            (String) viceCaptainData.get("playerName"),
                                            (String) viceCaptainData.get("playerRole"),
                                            (String) viceCaptainData.get("playerImageUrl")
                                    );



                                    // Extract list of players
                                    List<Map<String, Object>> playersData = (List<Map<String, Object>>) teamData.get("players");
                                    List<Player> players = new ArrayList<>();
                                    for (Map<String, Object> playerData : playersData) {
                                        Player player = new Player(
                                                (String) playerData.get("playerCountry"),
                                                (String) playerData.get("playerId"),
                                                (String) playerData.get("playerName"),
                                                (String) playerData.get("playerRole"),
                                                (String) playerData.get("playerImageUrl")
                                        );
                                        players.add(player);
                                    }



                                    // Now you have captain, vice-captain, and the players list
                                    Log.d("captain", "Captain: " + captain.getPlayerName());
                                    Log.d("viceCaptain", "Vice-Captain: " + viceCaptain.getPlayerName());

                                    for (Player player : players) {
                                        Log.d("players",player.getPlayerName());
                                    }

                                    team.add(captain);
                                    team.add(viceCaptain);
                                    team.addAll(players);

                                    allTeams.put(fetchedTeamId,team);
                                }

                            }

                        }

                        fetchCallBack.onSuccess();
                    } else {
                        Log.d("FirestoreError", "Error getting documents: ", task.getException());
                        fetchCallBack.onFailure();
                    }

                });

    }
}