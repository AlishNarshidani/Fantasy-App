package com.example.fantasyapp;

import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TeamRankingsInContest extends AppCompatActivity {

    String contestId, match_id;

    FirebaseAuth auth;
    FirebaseFirestore db;

    ArrayList<String> prizesList;
    ArrayList<String> teamIds;

    ViewPager2 viewPager;
    TabLayout tabLayout;

    Match match;

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


        contestId = getIntent().getStringExtra("contest_id");
        match = (Match) getIntent().getSerializableExtra("match");
        match_id = getIntent().getStringExtra("match_id");

        db= FirebaseFirestore.getInstance();
        auth= FirebaseAuth.getInstance();

        prizesList = new ArrayList<>();
        teamIds = new ArrayList<>();

        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);

        fetchAllContestDetails();
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
        RankingsViewPagerAdapter adapter = new RankingsViewPagerAdapter(this,prizesList,teamIds, match_id, match);
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