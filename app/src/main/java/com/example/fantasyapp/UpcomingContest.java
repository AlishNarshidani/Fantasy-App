package com.example.fantasyapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UpcomingContest extends AppCompatActivity {

    AppCompatButton createTeam, viewTeams;
    Match match;

    FirebaseFirestore db;
    FirebaseAuth auth;

    List<Map<String, Object>> contestsList;

    RecyclerView recyclerView;
    contestAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_upcoming_contest);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        recyclerView = findViewById(R.id.displayAllContestRecyclerView);

        createTeam = findViewById(R.id.createTeam);
        viewTeams = findViewById(R.id.viewTeams);
        db = FirebaseFirestore.getInstance();
        auth= FirebaseAuth.getInstance();
        contestsList = new ArrayList<>();

        match = (Match) getIntent().getSerializableExtra("match");

        checkAvailableContests();
        checkAvailableContests();

        createTeam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), CreateTeam.class);
                i.putExtra("match",match);
                startActivity(i);
            }
        });

        viewTeams.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), ViewTeams.class);
                i.putExtra("match",match);
                startActivity(i);
            }
        });
    }

    public void checkAvailableContests()
    {
        db.collection("contests")
                .whereEqualTo("match_id",match.getId())
                .whereEqualTo("contestType","public")
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


                                    boolean alreadyRegistered = false;

                                    if(teamIds.size() == 0)
                                    {
                                        contestsList.add(contestData);

                                    } else {

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
                                                    Log.d("already participated", "onComplete: "+contestData.get("contest_id"));

                                                    alreadyRegistered = true;
                                                    break;
                                                }

                                            }
                                        }

                                        if (!alreadyRegistered) {
                                            contestsList.add(contestData);
                                        }

                                    }
                                }

                                loadContests();
                                Log.d("Contests", "Fetched contests: " + contestsList);

                            } else {
                                // No public contest is available for the match
                                Log.d("PublicContest", "No public contests available for the match.");

                                createDefaultPublicContest();

                            }
                        } else {
                            // Handle the error
                            Log.d("FirestoreError", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    public void createDefaultPublicContest()
    {
        String matchId = match.getId();
        String userId = auth.getUid();

        Map<String, Object> newContest = new HashMap<>();
        newContest.put("contestType", "public");
        newContest.put("match_id", matchId);
        newContest.put("team_ids", new ArrayList<String>()); // Add an empty team ID list (or initialize it with default teams)
        newContest.put("entry_fee", 50);  // Default entry fee
        newContest.put("prize_pool", 2400);  // Default prize pool
        newContest.put("max_teams", 50);  // Default max teams allowed
        newContest.put("numberOfWinners",3);

        // Add the contest document to Firestore

        db.collection("contests")
                .add(newContest)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("PublicContest", "Default public contest created with ID: " + documentReference.getId());

                        //added document id as the contest id
                        documentReference.update("contest_id", documentReference.getId())
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d("PublicContest", "Document ID added as a field successfully");
                                        // Reload or update your contests view if needed
                                        checkAvailableContests();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d("FirestoreError", "Error updating document with contest ID", e);
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("FirestoreError", "Error creating default public contest", e);
                    }
                });
    }

    public void loadContests()
    {
        adapter = new contestAdapter(UpcomingContest.this,contestsList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100 && resultCode == RESULT_OK) {
            // Refresh the available contests after a successful registration
            Log.d("contestRefresh", "contestRefresh: ");
            checkAvailableContests();
        }
    }
}