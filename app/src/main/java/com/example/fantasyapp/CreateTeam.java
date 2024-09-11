package com.example.fantasyapp;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.android.volley.VolleyError;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class CreateTeam extends AppCompatActivity {

    TabLayout tabLayout;
    ViewPager2 viewPager;
    CricApiService cricApiService;
    Match match;
    String match_id;

    private ArrayList<Player> batsmanList = new ArrayList<>();
    private ArrayList<Player> bowlerList = new ArrayList<>();
    private ArrayList<Player> wk_BatsmanList = new ArrayList<>();
    private ArrayList<Player> allRounderList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_team);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        match = (Match) getIntent().getSerializableExtra("match");
        match_id = match.getId();

        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);

        cricApiService = new CricApiService(this);
        cricApiService.getSquads(match_id, new CricApiService.DataCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    Log.d("API_RESPONSE", response.toString());
                    String apiStatus=response.getString("status");
                    Log.d("API_STATUS", apiStatus);

                    JSONArray data=response.getJSONArray("data");
                    for(int i=0;i<data.length();i++)
                    {
                        JSONObject teamObj = data.getJSONObject(i);
                        String teamName = teamObj.getString("teamName");
                        String shortCountryName = teamObj.getString("shortname");
                        Log.d("teamName", "Team: "+teamName);

                        JSONArray players=teamObj.getJSONArray("players");

                        for(int j=0;j<players.length();j++)
                        {
                            JSONObject playerObj = players.getJSONObject(j);
                            String playerId = playerObj.getString("id");
                            String playerName = playerObj.getString("name");
                            String role = playerObj.getString("role");

                            Player player = new Player(shortCountryName,playerId,playerName,role);

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

                    setupViewPager();

                }catch (JSONException e){
                    Log.e("JSON_ERROR_SQUAD", "Error parsing JSON", e);
                    Toast.makeText(getApplicationContext(), "JSON parsing error", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(VolleyError error) {

            }
        });
    }

    private void setupViewPager()
    {
        CreateTeamViewPagerAdapter adapter = new CreateTeamViewPagerAdapter(this,batsmanList,wk_BatsmanList,bowlerList,allRounderList);
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
                switch (position) {
                    case 0:
                        tab.setText("WK (0)");
                        break;
                    case 1:
                        tab.setText("BAT (0)");
                        break;
                    case 2:
                        tab.setText("AR (0)");
                        break;
                    case 3:
                        tab.setText("BOWL (0)");
                        break;
                }
        }).attach();
    }
}