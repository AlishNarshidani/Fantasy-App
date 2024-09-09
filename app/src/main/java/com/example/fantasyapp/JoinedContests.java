package com.example.fantasyapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class JoinedContests extends AppCompatActivity {

    LinearLayout scoreSummary;
    ImageView team1Flag,team2Flag;
    TextView team1Name,team2Name;
    TextView team1TotalScore,team2TotalScore;
    TextView team1Overs,team2Overs;
    TextView matchResult;

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

        Match match = (Match) getIntent().getSerializableExtra("match");
        String match_id= match.getId();

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
    }
}