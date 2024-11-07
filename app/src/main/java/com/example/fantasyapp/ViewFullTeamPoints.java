package com.example.fantasyapp;

import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViewFullTeamPoints extends AppCompatActivity {


    FirebaseFirestore db;
    FirebaseAuth auth;

    LinearLayout wicketKeepersLayout;
    LinearLayout batsmanLayout;
    LinearLayout allRoundersLayout;
    LinearLayout bowlersLayout;

    LinearLayout mainLayout;

    ArrayList<Player> batsmanList = new ArrayList<>();
    ArrayList<Player> bowlerList = new ArrayList<>();
    ArrayList<Player> allRounderList = new ArrayList<>();
    ArrayList<Player> wk_BatsmanList = new ArrayList<>();

    ArrayList<Player> selectedPlayers = new ArrayList<>();

    Match match;

    String team_1,team_2;

    String captainId, viceCaptainId;

    String match_id,user_id,team_number;

    HashMap<String,Integer> pointsMap;

    TextView team_rank, team_points, user_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_view_full_team_points);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mainLayout = findViewById(R.id.mainLayout);
        wicketKeepersLayout = findViewById(R.id.wicketKeepersLayout);
        batsmanLayout = findViewById(R.id.batsmanLayout);
        allRoundersLayout = findViewById(R.id.allRoundersLayout);
        bowlersLayout = findViewById(R.id.bowlersLayout);

        team_rank = findViewById(R.id.team_rank);
        team_points = findViewById(R.id.team_points);
        user_name = findViewById(R.id.user_name);

        db = FirebaseFirestore.getInstance();
        auth= FirebaseAuth.getInstance();


        pointsMap = (HashMap<String,Integer>) getIntent().getSerializableExtra("pointsMap");
        String teamId = getIntent().getStringExtra("teamId");
        match = (Match) getIntent().getSerializableExtra("match");
        int rank = getIntent().getIntExtra("rank",0);
        double points = getIntent().getDoubleExtra("points",0);
        String userNameStr = getIntent().getStringExtra("user_name");

        team_rank.setText(String.valueOf(rank));
        team_points.setText(String.valueOf(points));
        user_name.setText(userNameStr);

        team_1 = match.getTeam1ShortName();
        team_2 = match.getTeam2ShortName();

        String[] parts = teamId.split("_");
        match_id = parts[0];
        user_id = parts[1];
        team_number = parts[2];

        Log.d("check", "onCreate: "+pointsMap+" "+teamId+" "+team_1+" "+team_2);

        getTeamDetails(match_id, team_number, new TeamDataFetchCallBack() {
            @Override
            public void onSuccess() {

                Log.d("gotTeamDetails", "onSuccess: ");
                captainId = selectedPlayers.get(0).getPlayerId();
                viceCaptainId = selectedPlayers.get(1).getPlayerId();
                Log.d("captainId", "onSuccess: "+captainId);
                Log.d("captainName", "onSuccess: "+selectedPlayers.get(0).getPlayerName());
                Log.d("viceCaptainId", "onSuccess: "+viceCaptainId);
                Log.d("viceCaptainName", "onSuccess: "+selectedPlayers.get(1).getPlayerName());

                dividePlayers();

            }

            @Override
            public void onFailure() {
                Log.d("gotTeamDetails", "onFailure: ");
            }
        });


    }


    public void dividePlayers()
    {
        if(selectedPlayers!=null)
        {
            for(Player player : selectedPlayers)
            {
                String role = player.getRole();
                String playerId = player.getPlayerId();
                String playerName = player.getPlayerName();
                String shortCountryName = player.getShortCountryName();

                if(role.equals("Batsman"))
                {
                    Log.d("player", "id: "+playerId+" name: "+playerName+" role: "+role+" shortname: "+shortCountryName);
                    batsmanList.add(player);
                    Log.d("batsmanList.size(): ", "onSuccess: "+batsmanList.size());

                } else if (role.equals("Bowler")) {

                    Log.d("player", "id: "+playerId+" name: "+playerName+" role: "+role+" shortname: "+shortCountryName);
                    bowlerList.add(player);
                    Log.d("bowlerList.size(): ", "onSuccess: "+bowlerList.size());

                } else if (role.equals("WK-Batsman")) {
                    Log.d("player", "id: "+playerId+" name: "+playerName+" role: "+role+" shortname: "+shortCountryName);
                    wk_BatsmanList.add(player);
                    Log.d("wk_BatsmanList.size(): ", "onSuccess: "+wk_BatsmanList.size());

                } else {
                    Log.d("player", "id: "+playerId+" name: "+playerName+" role: "+role+" shortname: "+shortCountryName);
                    allRounderList.add(player);
                    Log.d("allRounderList.size(): ", "onSuccess: "+allRounderList.size());

                }
            }
        }

        addPlayersToLayout(wk_BatsmanList,wicketKeepersLayout);
        addPlayersToLayout(batsmanList,batsmanLayout);
        addPlayersToLayout(allRounderList,allRoundersLayout);
        addPlayersToLayout(bowlerList,bowlersLayout);
    }


    public void getTeamDetails(String match_id, String team_id, TeamDataFetchCallBack teamDataFetchCallBack)
    {
        String documentId = match_id+"_"+user_id+"_"+team_id;

        db.collection("teams")
                .document(documentId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        if(documentSnapshot.exists())
                        {
                            selectedPlayers.clear();

                            // This document matches the current matchId and userId
                            Map<String, Object> teamData = documentSnapshot.getData();

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

                            selectedPlayers.add(captain);
                            selectedPlayers.add(viceCaptain);
                            selectedPlayers.addAll(players);
                        }


                        teamDataFetchCallBack.onSuccess();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Preview Team FirestoreError", "onFailure: ");
                        teamDataFetchCallBack.onFailure();
                    }
                });
    }


    private void addPlayersToLayout(ArrayList<Player> players, LinearLayout parentLayout)
    {
        if(players.size()!=0)
        {
            int rowCount = (int) Math.ceil(players.size() / 4.0);

            for(int i=0;i<rowCount;i++)
            {
                LinearLayout rowLayout = new LinearLayout(this);
                rowLayout.setOrientation(LinearLayout.HORIZONTAL);
                rowLayout.setGravity(Gravity.CENTER_HORIZONTAL);
                rowLayout.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1
                ));

                for(int j=i*4; j<(i+1)*4 && j<players.size(); j++)
                {

                    Player player = players.get(j);
                    String player_id = player.getPlayerId();

                    View playerView;

                    // Inflate or create the player view (e.g., an ImageView with a TextView)
                    if(player_id.equals(captainId) || player_id.equals(viceCaptainId))
                    {
                        if(player_id.equals(captainId))
                        {
                            playerView = LayoutInflater.from(this).inflate(R.layout.item_cap_player_view_with_points, rowLayout, false);
                        } else {
                            playerView = LayoutInflater.from(this).inflate(R.layout.item_vc_player_view_with_points, rowLayout, false);
                        }

                    } else {

                        playerView = LayoutInflater.from(this).inflate(R.layout.item_player_view_with_points, rowLayout, false);
                    }

                    // Set the player image and name
                    ImageView playerImage = playerView.findViewById(R.id.playerImage);
                    TextView playerName = playerView.findViewById(R.id.playerName);
                    TextView playerPoints = playerView.findViewById(R.id.playerPoints);

                    Picasso.get()
                            .load(player.getPlayerImageUrl())
                            .error(R.drawable.usericon)
                            .into(playerImage);

                    if(player.getShortCountryName().equals(team_1)) {
                        playerName.setBackgroundTintList(ContextCompat.getColorStateList(ViewFullTeamPoints.this, R.color.black));
                        playerName.setTextColor(getResources().getColor(R.color.white));
                    }

                    else {
                        playerName.setBackgroundTintList(ContextCompat.getColorStateList(ViewFullTeamPoints.this, R.color.white));
                        playerName.setTextColor(getResources().getColor(R.color.black));
                    }

                    playerName.setText(player.getPlayerName());

                    if(player_id.equals(captainId) || player_id.equals(viceCaptainId))
                    {
                        if(player_id.equals(captainId))
                        {
                            playerPoints.setText(String.valueOf(pointsMap.getOrDefault(player_id, 0) * 2) + " pts");
                        } else {
                            double points = pointsMap.getOrDefault(player_id, 0) * 1.5;
                            playerPoints.setText(String.valueOf(String.valueOf(points)) + " pts");
                        }
                    }

                    if(!player_id.equals(captainId) && !player_id.equals(viceCaptainId)) {
                        playerPoints.setText(String.valueOf(pointsMap.getOrDefault(player_id,0)) + " pts");
                    }

                    rowLayout.addView(playerView);
                }

                parentLayout.addView(rowLayout);

            }
        }
    }



    public interface TeamDataFetchCallBack {
        void onSuccess();
        void onFailure();
    }
}