package com.example.fantasyapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.Transaction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class RegisterTeamInContest extends AppCompatActivity {

    FirebaseFirestore db;
    FirebaseAuth auth;

    Map<String, List<Player>> allTeams;
    List<List<Player>> listOfAllTeams;

    String contestId, match_id, entryFeeStr;
    int entryFee;

    RecyclerView recyclerView;
    RegisterTeamAdapter adapter;

    AppCompatButton joinContest;

    Long fetchedDepositMoney,fetchedWithdrawableMoney,fetchedBonusMoney,totalMoney;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register_team_in_contest);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        recyclerView = findViewById(R.id.selectFromAllTeamsRecyclerView);
        joinContest = findViewById(R.id.joinContest);

        allTeams = new TreeMap<>();
        listOfAllTeams = new ArrayList<>();

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();


        contestId = getIntent().getStringExtra("contest_id");
        match_id = getIntent().getStringExtra("match_id");
        entryFeeStr = getIntent().getStringExtra("entryFee");
        entryFee = Integer.parseInt(entryFeeStr);


        fetchAllTeams(new PopulateCallBack() {
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

                String user_id = auth.getUid();

                adapter = new RegisterTeamAdapter(RegisterTeamInContest.this,listOfAllTeams,match_id,user_id);
                recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onFailure() {

            }
        });



        joinContest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                joinContest.setEnabled(false);
                joinContest.setClickable(false);

                String user_id = auth.getUid();
                int registerPos = adapter.getSelectedPosition();

                if(registerPos == -1)
                {
                    Toast.makeText(RegisterTeamInContest.this, "Select Team to Register", Toast.LENGTH_SHORT).show();
                } else {


                    checkBalance(new OnBalanceCheckListener() {
                        @Override
                        public void onSuccess() {

                            String registerTeamId = match_id + "_" + user_id + "_" + "team" + (registerPos+1);
                            Log.d("teamid", "onClick: "+registerTeamId);

                            //continue from here, to register team

                            db.runTransaction(new Transaction.Function<Void>() {

                                @Nullable
                                @Override
                                public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {

                                    DocumentSnapshot snapshot = transaction.get(db.collection("contests").document(contestId));

                                    if(snapshot.exists())
                                    {
                                        // Get the current team_ids list
                                        List<String> teamIds = (List<String>) snapshot.get("team_ids");
                                        if (teamIds == null) {
                                            teamIds = new ArrayList<>();  // Initialize if null
                                        }


                                        // Check if the number of registered teams is less than 10
                                        if (teamIds.size() < 10) {
                                            // Add the new team ID to the list
                                            teamIds.add(registerTeamId);

                                            // Update the contest document in the transaction
                                            transaction.update(db.collection("contests").document(contestId), "team_ids", teamIds);
                                        } else {
                                            // If contest is full, throw an exception to abort the transaction
                                            throw new FirebaseFirestoreException("Contest is full",
                                                    FirebaseFirestoreException.Code.ABORTED);
                                        }


                                    } else {
                                        // If the document doesn't exist, throw an exception to abort the transaction
                                        throw new FirebaseFirestoreException("Contest not found",
                                                FirebaseFirestoreException.Code.NOT_FOUND);
                                    }

                                    return null;
                                }
                            }).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {

                                    // Success: team was added
                                    Log.d("Firestore", "Joined Contest successfully.");
                                    //handle success
                                    Toast.makeText(getApplicationContext(), "Joined Contest successfully.", Toast.LENGTH_SHORT).show();


                                    deductMoney(new TransactionCompleteListener() {
                                        @Override
                                        public void onSuccess() {

                                        }

                                        @Override
                                        public void onFailure() {

                                            Toast.makeText(getApplicationContext(), "Joined Contest but Money Not Deducted ", Toast.LENGTH_SHORT).show();

                                        }
                                    });



                                    Intent returnIntent = new Intent();
                                    setResult(RESULT_OK, returnIntent);
                                    finish();


                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                    if (e instanceof FirebaseFirestoreException &&
                                            ((FirebaseFirestoreException) e).getCode() == FirebaseFirestoreException.Code.ABORTED) {
                                        // Contest was full
                                        Log.d("Firestore", "Contest is full. Cannot register more teams.");
                                        //handle failure
                                        Toast.makeText(getApplicationContext(), "Contest Full", Toast.LENGTH_SHORT).show();


                                    } else {
                                        // Other error
                                        Log.e("FirestoreError", "Error registering team: ", e);
                                    }

                                    Intent returnIntent = new Intent();
                                    setResult(RESULT_OK, returnIntent);
                                    finish();

                                }
                            });


                        }

                        @Override
                        public void onFailure() {

                        }
                    });


                }
            }
        });

    }

    public interface OnBalanceCheckListener {
        void onSuccess();
        void onFailure();
    }

    public interface TransactionCompleteListener {
        void onSuccess();
        void onFailure();
    }

    public interface PopulateCallBack {
        void onSuccess();
        void onFailure();
    }


    public void checkBalance(OnBalanceCheckListener onBalanceCheckListener)
    {
        FirebaseUser user=auth.getCurrentUser();
        String userId=user.getUid();

        DocumentReference userDocRef = db.collection("users").document(userId);

        userDocRef.get().addOnSuccessListener(documentSnapshot -> {
            if(documentSnapshot.exists()){
                fetchedDepositMoney = documentSnapshot.getLong("deposit money");
                fetchedWithdrawableMoney = documentSnapshot.getLong("withdrawable money");
                fetchedBonusMoney = documentSnapshot.getLong("bonus money");

                if (fetchedBonusMoney == null) fetchedBonusMoney = 0L;
                if (fetchedDepositMoney == null) fetchedDepositMoney = 0L;
                if (fetchedWithdrawableMoney == null) fetchedWithdrawableMoney = 0L;

                totalMoney = fetchedDepositMoney+fetchedWithdrawableMoney+fetchedBonusMoney;


                if(totalMoney >= entryFee)
                {
                    onBalanceCheckListener.onSuccess();
                } else {
                    Toast.makeText(getApplicationContext(), "Insufficient Balance", Toast.LENGTH_SHORT).show();

                    Intent returnIntent = new Intent();
                    setResult(RESULT_OK, returnIntent);
                    finish();
                }


            } else {
                Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show();
            }

        }).addOnFailureListener(e -> Toast.makeText(this, "Failed to fetch balance", Toast.LENGTH_SHORT).show());


    }


    public void deductMoney(TransactionCompleteListener transactionCompleteListener)
    {
        FirebaseUser user=auth.getCurrentUser();
        String userId=user.getUid();

        DocumentReference userDocRef = db.collection("users").document(userId);


        userDocRef.get().addOnSuccessListener(documentSnapshot -> {
            if(documentSnapshot.exists()){
                fetchedDepositMoney = documentSnapshot.getLong("deposit money");
                fetchedWithdrawableMoney = documentSnapshot.getLong("withdrawable money");
                fetchedBonusMoney = documentSnapshot.getLong("bonus money");

                if (fetchedBonusMoney == null) fetchedBonusMoney = 0L;
                if (fetchedDepositMoney == null) fetchedDepositMoney = 0L;
                if (fetchedWithdrawableMoney == null) fetchedWithdrawableMoney = 0L;

                totalMoney = fetchedDepositMoney+fetchedWithdrawableMoney+fetchedBonusMoney;


                if(totalMoney >= entryFee)
                {
                    long remainingAmount = entryFee;

                    // Deduct from bonusMoney first
                    if (fetchedBonusMoney >= remainingAmount) {
                        fetchedBonusMoney -= remainingAmount;
                        remainingAmount = 0;
                    } else {
                        remainingAmount -= fetchedBonusMoney;
                        fetchedBonusMoney = 0L;
                    }

                    // Deduct from depositMoney if needed
                    if (remainingAmount > 0) {
                        if (fetchedDepositMoney >= remainingAmount) {
                            fetchedDepositMoney -= remainingAmount;
                            remainingAmount = 0;
                        } else {
                            remainingAmount -= fetchedDepositMoney;
                            fetchedDepositMoney = 0L;
                        }
                    }

                    // Deduct from withdrawableMoney if needed
                    if (remainingAmount > 0) {
                        if (fetchedWithdrawableMoney >= remainingAmount) {
                            fetchedWithdrawableMoney -= remainingAmount;
                            remainingAmount = 0;
                        } else {
                            fetchedWithdrawableMoney = 0L;
                        }
                    }


                    Map<String, Object> transactionData = new HashMap<>();
                    transactionData.put("userId", userId);
                    transactionData.put("transactionType", "Contest Entry");
                    transactionData.put("amount", entryFee);
                    transactionData.put("transactionStatus", "Completed");
                    transactionData.put("transactionDate", new com.google.firebase.Timestamp(new java.util.Date()));
                    transactionData.put("contestId",contestId);


                    db.collection("transactions")
                            .add(transactionData)
                            .addOnSuccessListener(documentReference -> {
                                String transactionId = documentReference.getId();
                                Log.d("transactionId", "transactionId: "+transactionId);
                            })
                            .addOnFailureListener(e -> {
                                Log.d("transaction", "failed");
                            });


                    Map<String, Object> updates = new HashMap<>();
                    updates.put("bonus money", fetchedBonusMoney);
                    updates.put("deposit money", fetchedDepositMoney);
                    updates.put("withdrawable money", fetchedWithdrawableMoney);

                    // Proceed with deducting the amount
                    userDocRef.update(updates)
                            .addOnSuccessListener(aVoid -> transactionCompleteListener.onSuccess())
                            .addOnFailureListener(e -> {
                                Toast.makeText(this, "Failed to update balance", Toast.LENGTH_SHORT).show();
                                transactionCompleteListener.onFailure();
                            });


                } else {
                    Toast.makeText(getApplicationContext(), "Insufficient Balance", Toast.LENGTH_SHORT).show();

                    Intent returnIntent = new Intent();
                    setResult(RESULT_OK, returnIntent);
                    finish();
                }

            } else {
                Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> Toast.makeText(this, "Failed to fetch balance", Toast.LENGTH_SHORT).show());


    }


    public void fetchAllTeams(RegisterTeamInContest.PopulateCallBack populateCallBack)
    {
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

                        populateCallBack.onSuccess();
                    } else {
                        Log.d("FirestoreError", "Error getting documents: ", task.getException());
                        populateCallBack.onFailure();
                    }

                });

    }

}