package com.example.fantasyapp;

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


        if(wk_BatsmanList.size()!=0)
        {
            int rowCount = (int) Math.ceil(wk_BatsmanList.size() / 4.0);

            for(int i=0;i<rowCount;i++)
            {
                LinearLayout rowLayout = new LinearLayout(this);
                rowLayout.setOrientation(LinearLayout.HORIZONTAL);

                for(int j=i*4; j<(i+1)*4 && j<wk_BatsmanList.size(); j++)
                {

                    Player player = wk_BatsmanList.get(j);

                    // Inflate or create the player view (e.g., an ImageView with a TextView)
                    View playerView = LayoutInflater.from(this).inflate(R.layout.item_player_view, rowLayout, false);

                    // Set the player image and name
                    ImageView playerImage = playerView.findViewById(R.id.playerImage);
                    TextView playerName = playerView.findViewById(R.id.playerName);

                    Picasso.get()
                            .load(player.getPlayerImageUrl())
                            .error(R.drawable.usericon)
                            .into(playerImage);
                    playerName.setText(player.getPlayerName());

                    rowLayout.addView(playerView);
                }

                wicketKeepersLayout.addView(rowLayout);

            }
        }

        if(batsmanList.size()!=0)
        {
            int rowCount = (int) Math.ceil(batsmanList.size() / 4.0);

            for(int i=0;i<rowCount;i++)
            {
                LinearLayout rowLayout = new LinearLayout(this);
                rowLayout.setOrientation(LinearLayout.HORIZONTAL);

                for(int j=i*4; j<(i+1)*4 && j<batsmanList.size(); j++)
                {

                    Player player = batsmanList.get(j);

                    // Inflate or create the player view (e.g., an ImageView with a TextView)
                    View playerView = LayoutInflater.from(this).inflate(R.layout.item_player_view, rowLayout, false);

                    // Set the player image and name
                    ImageView playerImage = playerView.findViewById(R.id.playerImage);
                    TextView playerName = playerView.findViewById(R.id.playerName);

                    Picasso.get()
                            .load(player.getPlayerImageUrl())
                            .error(R.drawable.usericon)
                            .into(playerImage);
                    playerName.setText(player.getPlayerName());

                    rowLayout.addView(playerView);
                }

                batsmanLayout.addView(rowLayout);

            }
        }

        if(allRounderList.size()!=0)
        {
            int rowCount = (int) Math.ceil(allRounderList.size() / 4.0);

            for(int i=0;i<rowCount;i++)
            {
                LinearLayout rowLayout = new LinearLayout(this);
                rowLayout.setOrientation(LinearLayout.HORIZONTAL);

                for(int j=i*4; j<(i+1)*4 && j<allRounderList.size(); j++)
                {

                    Player player = allRounderList.get(j);

                    // Inflate or create the player view (e.g., an ImageView with a TextView)
                    View playerView = LayoutInflater.from(this).inflate(R.layout.item_player_view, rowLayout, false);

                    // Set the player image and name
                    ImageView playerImage = playerView.findViewById(R.id.playerImage);
                    TextView playerName = playerView.findViewById(R.id.playerName);

                    Picasso.get()
                            .load(player.getPlayerImageUrl())
                            .error(R.drawable.usericon)
                            .into(playerImage);
                    playerName.setText(player.getPlayerName());

                    rowLayout.addView(playerView);
                }

                allRoundersLayout.addView(rowLayout);

            }
        }

        if(bowlerList.size()!=0)
        {
            int rowCount = (int) Math.ceil(bowlerList.size() / 4.0);

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

                for(int j=i*4; j<(i+1)*4 && j<bowlerList.size(); j++)
                {

                    Player player = bowlerList.get(j);

                    // Inflate or create the player view (e.g., an ImageView with a TextView)
                    View playerView = LayoutInflater.from(this).inflate(R.layout.item_player_view, rowLayout, false);

                    // Set the player image and name
                    ImageView playerImage = playerView.findViewById(R.id.playerImage);
                    TextView playerName = playerView.findViewById(R.id.playerName);

                    Picasso.get()
                            .load(player.getPlayerImageUrl())
                            .error(R.drawable.usericon)
                            .into(playerImage);
                    playerName.setText(player.getPlayerName());

                    rowLayout.addView(playerView);
                }

                bowlersLayout.addView(rowLayout);

            }
        }







//        teamDisp = findViewById(R.id.teamDisp);
//
//        ArrayList<Player> selectedPlayers = (ArrayList<Player>) getIntent().getSerializableExtra("selectedPlayers");
//
//        String ans = "";
//
//        if(selectedPlayers != null) {
//            for (Player player : selectedPlayers) {
//                ans = ans + player.getPlayerName() + "\n";
//            }
//        }
//
//        teamDisp.setText(ans);
    }
}