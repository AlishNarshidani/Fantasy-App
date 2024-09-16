package com.example.fantasyapp;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class PreviewTeam extends AppCompatActivity {

    LinearLayout wicketKeepersLayout;
    LinearLayout batsmanLayout;
    LinearLayout allRoundersLayout;
    LinearLayout bowlersLayout;

    LinearLayout mainLayout;

    ArrayList<Player> batsmanList = new ArrayList<>();
    ArrayList<Player> bowlerList = new ArrayList<>();
    ArrayList<Player> allRounderList = new ArrayList<>();
    ArrayList<Player> wk_BatsmanList = new ArrayList<>();

    String team_1,team_2;

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

        mainLayout = findViewById(R.id.mainLayout);
        wicketKeepersLayout = findViewById(R.id.wicketKeepersLayout);
        batsmanLayout = findViewById(R.id.batsmanLayout);
        allRoundersLayout = findViewById(R.id.allRoundersLayout);
        bowlersLayout = findViewById(R.id.bowlersLayout);

        ArrayList<Player> selectedPlayers = (ArrayList<Player>) getIntent().getSerializableExtra("selectedPlayers");
        team_1 = getIntent().getStringExtra("team_1");
        team_2 = getIntent().getStringExtra("team_2");



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

                    // Inflate or create the player view (e.g., an ImageView with a TextView)
                    View playerView = LayoutInflater.from(this).inflate(R.layout.item_player_view, rowLayout, false);

                    // Set the player image and name
                    ImageView playerImage = playerView.findViewById(R.id.playerImage);
                    TextView playerName = playerView.findViewById(R.id.playerName);

                    Picasso.get()
                            .load(player.getPlayerImageUrl())
                            .error(R.drawable.usericon)
                            .into(playerImage);

                    if(player.getShortCountryName().equals(team_1)) {
                        playerName.setBackgroundTintList(ContextCompat.getColorStateList(PreviewTeam.this, R.color.black));
                        playerName.setTextColor(getResources().getColor(R.color.white));
                    }

                    else {
                        playerName.setBackgroundTintList(ContextCompat.getColorStateList(PreviewTeam.this, R.color.white));
                        playerName.setTextColor(getResources().getColor(R.color.black));
                    }

                    playerName.setText(player.getPlayerName());

                    rowLayout.addView(playerView);
                }

                parentLayout.addView(rowLayout);

            }
        }
    }
}