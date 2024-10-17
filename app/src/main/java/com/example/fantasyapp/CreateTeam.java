package com.example.fantasyapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class CreateTeam extends AppCompatActivity implements OnPlayerSelectedListener {

    TabLayout tabLayout;
    ViewPager2 viewPager;
    CricApiService cricApiService;
    Match match;
    String match_id;

    String team_1,team_2,series,matchType;

    AppCompatButton previewTeam,nextBtn,analysis;

    Spinner countrySpinner;
    String selectedCountry = null;

    private ArrayList<JSONObject> entireSquad = new ArrayList<>();
    private ArrayList<Player> selectedPlayers = new ArrayList<>();

    private ArrayList<Player> batsmanList = new ArrayList<>();
    private ArrayList<Player> bowlerList = new ArrayList<>();
    private ArrayList<Player> wk_BatsmanList = new ArrayList<>();
    private ArrayList<Player> allRounderList = new ArrayList<>();

    private HashMap<String, String> playerImages = new HashMap<>();

    private int wkCount = 0;
    private int batsmanCount = 0;
    private int allRounderCount = 0;
    private int bowlerCount = 0;

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
        team_1 = match.getTeam1ShortName();
        team_2 = match.getTeam2ShortName();
        series=match.getSeries();
        matchType=match.getMatchType();

        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
        previewTeam = findViewById(R.id.previewTeam);
        nextBtn = findViewById(R.id.nextBtn);
        analysis=findViewById(R.id.analysis);
        countrySpinner = findViewById(R.id.countrySpinner);

        nextBtn.setEnabled(false);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.cricket_playing_countries, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        countrySpinner.setAdapter(adapter);

        countrySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCountry = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        cricApiService = new CricApiService(this);
        cricApiService.getSquads(match_id, new CricApiService.DataCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    Log.d("API_RESPONSE", response.toString());
                    String apiStatus=response.getString("status");
                    Log.d("API_STATUS", apiStatus);

                    JSONArray data=response.getJSONArray("data");

                    String country = null;
                    if (series.contains("tour of")) {
                        String[] parts = series.split("tour of")[1].trim().split(",");
                        country = parts[0];
                    } else {
                        countrySpinner.setVisibility(View.VISIBLE);
                        country=selectedCountry;
                    }

                    for(int i=0;i<data.length();i++)
                    {
                        JSONObject teamObj = data.getJSONObject(i);
                        String teamName = teamObj.getString("teamName");
                        String shortCountryName = teamObj.getString("shortname");
                        Log.d("teamName", "Team: "+teamName);

                        String opposition = shortCountryName.equals(team_1) ? team_2 : team_1;

                        JSONArray players=teamObj.getJSONArray("players");

                        for(int j=0;j<players.length();j++)
                        {
                            JSONObject playerObj = players.getJSONObject(j);
                            String playerId = playerObj.getString("id");
                            String playerName = playerObj.getString("name");
                            String role = playerObj.getString("role");
                            String playerImageUrl = playerObj.getString("playerImg");

                            Player player = new Player(shortCountryName,playerId,playerName,role,playerImageUrl);

                            JSONObject playerDetails = new JSONObject();
                            playerDetails.put("name", playerName);
                            playerDetails.put("Role", role);
                            playerDetails.put("opposition", opposition);
                            if (country != null) {
                                playerDetails.put("country", country);
                            }
                            playerDetails.put("Format", matchType);

                            entireSquad.add(playerDetails);
                            playerImages.put(playerName, playerImageUrl);

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





        previewTeam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selectedPlayers.size() == 11) {
                    Intent intent = new Intent(getApplicationContext(), PreviewTeam.class);
                    intent.putExtra("caller","CreateTeam");
                    intent.putExtra("selectedPlayers", selectedPlayers);
                    intent.putExtra("team_1",team_1);
                    intent.putExtra("team_2",team_2);
                    startActivity(intent);
                } else {
                    Toast.makeText(CreateTeam.this, "Select 11 Players", Toast.LENGTH_SHORT).show();
                }
            }
        });




        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),SelectCapVc.class);
                intent.putExtra("match",match);
                intent.putExtra("match_id",match_id);
                intent.putExtra("selectedPlayers",selectedPlayers);
                intent.putExtra("team_1",team_1);
                intent.putExtra("team_2",team_2);
                startActivity(intent);
            }
        });

        analysis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendPlayerDataToServer();
            }
        });
    }

    private void sendPlayerDataToServer() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://10.0.2.2:5000/predict";

        JSONObject playersObject = new JSONObject();
        for (JSONObject playerDetails : entireSquad) {
            try {
                String playerName = playerDetails.getString("name");
                playerDetails.remove("name");
                playersObject.put(playerName, playerDetails);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("players", playersObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, requestBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("API_RESPONSE", response.toString());
                        Toast.makeText(CreateTeam.this, "Data sent successfully", Toast.LENGTH_SHORT).show();
                        handleApiResponse(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("API_ERROR", "Error sending data", error);
                Toast.makeText(CreateTeam.this, "Error sending data", Toast.LENGTH_SHORT).show();
            }
        });

        queue.add(jsonObjectRequest);
    }

    private void handleApiResponse(JSONObject response) {
        try {
            JSONArray topBatsmen = response.getJSONArray("top_batsmen");
            JSONArray topBowlers = response.getJSONArray("top_bowlers");
            JSONArray topAllrounders = response.getJSONArray("top_allrounders");

            JSONObject playerImagesJson = new JSONObject(playerImages);

            Intent intent = new Intent(CreateTeam.this, AnalysisResult.class);
            intent.putExtra("top_batsmen", topBatsmen.toString());
            intent.putExtra("top_bowlers", topBowlers.toString());
            intent.putExtra("top_allrounders", topAllrounders.toString());
            intent.putExtra("player_images", playerImagesJson.toString());

            startActivity(intent);

        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error parsing response", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupViewPager()
    {
        CreateTeamViewPagerAdapter adapter = new CreateTeamViewPagerAdapter(this,batsmanList,wk_BatsmanList,bowlerList,allRounderList,team_1,team_2);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(1);
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
                switch (position) {
                    case 0:
                        tab.setText("WK(0)");
                        break;
                    case 1:
                        tab.setText("BAT(0)");
                        break;
                    case 2:
                        tab.setText("AR(0)");
                        break;
                    case 3:
                        tab.setText("BOWL(0)");
                        break;
                }
        }).attach();
    }

    @Override
    public void onPlayerSelected(Player player) {
        if (selectedPlayers.size() < 11) {
            selectedPlayers.add(player);
            Log.d("onPlayerSelected", "onPlayerSelected: "+player.getPlayerName());
            Log.d("count", "onPlayerSelected: "+selectedPlayers.size());

            if(player.getRole().equals("Batsman"))
            {
                batsmanCount++;
                tabLayout.getTabAt(1).setText("BAT(" + batsmanCount + ")");

            } else if (player.getRole().equals("Bowler")) {
                bowlerCount++;
                tabLayout.getTabAt(3).setText("BOWL(" + bowlerCount + ")");

            } else if (player.getRole().equals("WK-Batsman")) {
                wkCount++;
                tabLayout.getTabAt(0).setText("WK(" + wkCount + ")");

            } else {
                allRounderCount++;
                tabLayout.getTabAt(2).setText("AR(" + allRounderCount + ")");
            }


            if(selectedPlayers.size() == 11)
            {
                nextBtn.setBackgroundResource(R.drawable.gradient_background);
                nextBtn.setEnabled(true);
            }


        } else {
            Log.d("count", "onPlayerSelected: "+selectedPlayers.size());
            //Toast.makeText(this, "You can only select 11 players", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onPlayerDeselected(Player player) {
        selectedPlayers.remove(player);
        Log.d("onPlayerDeselected", "onPlayerDeselected: "+player.getPlayerName());
        Log.d("count", "onPlayerDeselected: "+selectedPlayers.size());

        if(player.getRole().equals("Batsman"))
        {
            batsmanCount--;
            tabLayout.getTabAt(1).setText("BAT(" + batsmanCount + ")");

        } else if (player.getRole().equals("Bowler")) {
            bowlerCount--;
            tabLayout.getTabAt(3).setText("BOWL(" + bowlerCount + ")");

        } else if (player.getRole().equals("WK-Batsman")) {
            wkCount--;
            tabLayout.getTabAt(0).setText("WK(" + wkCount + ")");

        } else {
            allRounderCount--;
            tabLayout.getTabAt(2).setText("AR(" + allRounderCount + ")");
        }


        if(selectedPlayers.size() < 11)
        {
            nextBtn.setBackgroundResource(R.drawable.verified_background);
            nextBtn.setEnabled(false);
        }
    }

    @Override
    public int getTotalSelectedPlayers() {
        return selectedPlayers.size();
    }

    @Override
    public int getMaxPlayers() {
        return 11;
    }
}