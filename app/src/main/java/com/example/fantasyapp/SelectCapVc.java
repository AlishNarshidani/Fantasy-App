package com.example.fantasyapp;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SelectCapVc extends AppCompatActivity {

    ListView listView;

    CapVcAdapter capVcAdapter;


    private int selectedCaptain = -1;
    private int selectedViceCaptain = -1;

    TextView captainTextView, viceCaptainTextView;
    AppCompatButton saveTeam;

    ImageView captainImageView, viceCaptainImageView;

    String match_id;

    FirebaseAuth auth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_select_cap_vc);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        captainTextView = findViewById(R.id.captainName);
        viceCaptainTextView = findViewById(R.id.viceCaptainName);
        captainImageView = findViewById(R.id.captainImage);
        viceCaptainImageView = findViewById(R.id.viceCaptainImage);

        saveTeam = findViewById(R.id.saveTeam);
        listView = findViewById(R.id.listView);

        ArrayList<Player> selectedPlayers = (ArrayList<Player>) getIntent().getSerializableExtra("selectedPlayers");
        match_id = getIntent().getStringExtra("match_id");

        capVcAdapter = new CapVcAdapter(this, selectedPlayers, new CapVcAdapter.OnPlayerSelectionChangedListener() {
            @Override
            public void onCaptainSelected(String captainPos) {
                if(!captainPos.isEmpty()) {

                    int capPos = Integer.parseInt(captainPos);
                    Player player = selectedPlayers.get(capPos);

                    Picasso.get()
                            .load(player.getPlayerImageUrl())
                            .error(R.drawable.usericon)
                            .into(captainImageView);

                    String playerName = player.getPlayerName();

                    captainTextView.setText(playerName);
                } else {

                    captainImageView.setImageResource(R.drawable.usericon);
                    captainTextView.setText(captainPos);
                }
            }

            @Override
            public void onViceCaptainSelected(String viceCaptainPos) {
                if(!viceCaptainPos.isEmpty()) {

                    int vcPos = Integer.parseInt(viceCaptainPos);
                    Player player = selectedPlayers.get(vcPos);

                    Picasso.get()
                            .load(player.getPlayerImageUrl())
                            .error(R.drawable.usericon)
                            .into(viceCaptainImageView);

                    String playerName = player.getPlayerName();

                    viceCaptainTextView.setText(playerName);

                } else {

                    viceCaptainImageView.setImageResource(R.drawable.usericon);
                    viceCaptainTextView.setText(viceCaptainPos);
                }
            }
        });

        listView.setAdapter(capVcAdapter);





        saveTeam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int capPos = capVcAdapter.getSelectedCaptain();
                int vcPos = capVcAdapter.getSelectedViceCaptain();

                Log.d("cap", "onClick: "+capPos);
                Log.d("vc", "onClick: "+vcPos);
                if(capPos != -1 && vcPos != -1)
                {
//                    captainTextView.setText((selectedPlayers.get(capPos)).getPlayerName());
//
//                    viceCaptainTextView.setText((selectedPlayers.get(vcPos)).getPlayerName());
//


                    ArrayList<Player> selectedPlayersExceptCapAndVc = new ArrayList<>();

                    for(int i=0;i<selectedPlayers.size();i++)
                    {
                        if(i!=capPos && i!=vcPos)
                        {
                            selectedPlayersExceptCapAndVc.add(selectedPlayers.get(i));
                        }
                    }


                    insertTeamData(selectedPlayers, selectedPlayersExceptCapAndVc, capPos, vcPos, new OnTeamDataInsertionCompletionListener() {
                        @Override
                        public void onSuccess() {
                            Toast.makeText(SelectCapVc.this, "inserted", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure() {
                            Toast.makeText(SelectCapVc.this, "Failed", Toast.LENGTH_SHORT).show();
                        }
                    });

                } else {
                    Toast.makeText(SelectCapVc.this, "Please Select Both C & VC", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }





    private void insertTeamData(ArrayList<Player> selectedPlayers, ArrayList<Player> selectedPlayersExceptCapAndVc, int capPosition, int vcPosition, OnTeamDataInsertionCompletionListener listener)
    {
        db = FirebaseFirestore.getInstance();

        auth = FirebaseAuth.getInstance();
        FirebaseUser user=auth.getCurrentUser();
        String userId=user.getUid();

        String matchId = match_id;

        db.collection("teams")
                .whereEqualTo("userId",userId)
                .whereEqualTo("matchId",matchId)
                .get()
                .addOnCompleteListener(task -> {

                    if(task.isSuccessful()) {
                        int nextTeamNumber = 1; // Start with team1 if no teams exist

                        // Determine the next team number
                        for(QueryDocumentSnapshot document : task.getResult()) {

                            String docId = document.getId();

                            // Extract the team number from document ID
                            String[] parts = docId.split("_");
                            String teamId = parts[2];

                            int currentTeamNumber = Integer.parseInt(teamId.replaceAll("\\D+", ""));

                            // Update nextTeamNumber to be the highest found + 1
                            nextTeamNumber = Math.max(nextTeamNumber, currentTeamNumber + 1);
                        }


                        // Generate the next team ID and custom document ID
                        String nextTeamId = "team" + nextTeamNumber;
                        String documentId = matchId + "_" + userId + "_" + nextTeamId;

                        Player captainObj = selectedPlayers.get(capPosition);
                        Player viceCaptainObj = selectedPlayers.get(vcPosition);

                        // Captain details
                        Map<String, Object> captain = new HashMap<>();
                        captain.put("playerId",captainObj.getPlayerId());
                        captain.put("playerName", captainObj.getPlayerName());
                        captain.put("playerRole", captainObj.getRole());
                        captain.put("playerImageUrl",captainObj.getPlayerImageUrl());
                        captain.put("playerCountry",captainObj.getShortCountryName());

                        // Vice-captain details
                        Map<String, Object> viceCaptain = new HashMap<>();
                        viceCaptain.put("playerId",viceCaptainObj.getPlayerId());
                        viceCaptain.put("playerName", viceCaptainObj.getPlayerName());
                        viceCaptain.put("playerRole", viceCaptainObj.getRole());
                        viceCaptain.put("playerImageUrl",viceCaptainObj.getPlayerImageUrl());
                        viceCaptain.put("playerCountry",viceCaptainObj.getShortCountryName());


                        // Create a list to hold the player maps
                        List<Map<String, Object>> players = new ArrayList<>();

                        for(Player player : selectedPlayersExceptCapAndVc)
                        {
                            players.add(new HashMap<String, Object>() {{
                                put("playerId",player.getPlayerId());
                                put("playerName", player.getPlayerName());
                                put("playerRole", player.getRole());
                                put("playerImageUrl",player.getPlayerImageUrl());
                                put("playerCountry",player.getShortCountryName());
                            }});
                        }



                        // Create a new team object
                        Map<String, Object> team = new HashMap<>();
                        team.put("teamId", nextTeamId);
                        team.put("matchId", matchId);
                        team.put("userId", userId);
                        team.put("captain",captain);
                        team.put("viceCaptain",viceCaptain);
                        team.put("players",players);


                        db.collection("teams")
                                .document(documentId)
                                .set(team)
                                .addOnSuccessListener(aVoid -> {
                                    Log.d("Firestore", "Team successfully added with ID: " + documentId);
                                    listener.onSuccess();
                                })
                                .addOnFailureListener(e -> Log.d("Firestore", "Error adding team: ", e));

                    } else {
                        Log.d("Firestore", "Error getting documents.", task.getException());
                    }

                });
    }




    public interface OnTeamDataInsertionCompletionListener {
        void onSuccess();
        void onFailure();
    }
}