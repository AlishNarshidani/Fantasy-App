package com.example.fantasyapp;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class PreviewTeam extends AppCompatActivity {

    TextView teamDisp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_preview_team);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        teamDisp = findViewById(R.id.teamDisp);

        ArrayList<Player> selectedPlayers = (ArrayList<Player>) getIntent().getSerializableExtra("selectedPlayers");

        String ans = "";

        if(selectedPlayers != null) {
            for (Player player : selectedPlayers) {
                ans = ans + player.getPlayerName() + "\n";
            }
        }

        teamDisp.setText(ans);
    }
}