package com.example.fantasyapp;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class home extends AppCompatActivity {

    RecyclerView recyclerView;
    MatchAdapter adapter;
    ArrayList<String> matchList;
    CricApiService cricApiService;

    private static final List<String> INTERNATIONAL_TEAMS = Arrays.asList(
            "India", "Australia", "England", "South Africa", "New Zealand", "Pakistan", "Sri Lanka", "West Indies", "Bangladesh", "Afghanistan", "Zimbabwe", "Ireland"
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        recyclerView=findViewById(R.id.recyclerView);
        matchList=new ArrayList<>();
        adapter= new MatchAdapter(matchList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        cricApiService=new CricApiService(this);
        fetchMatches();
    }

    private void fetchMatches()
    {
        cricApiService.getMatches(new CricApiService.DataCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                try
                {
                    Log.d("API_RESPONSE", response.toString());
                    if(response.has("data"))
                    {
                        JSONArray matches=response.getJSONArray("data");
                        for(int i=0;i<matches.length();i++)
                        {
                            JSONObject match=matches.getJSONObject(i);
                            JSONArray teams=match.getJSONArray("teams");
                            String team1=teams.getString(0);
                            String team2=teams.getString(1);
                            String status=match.getString("status");

                            Log.d("MATCH_INFO", "Match: " + team1 + " vs " + team2);

                            if((INTERNATIONAL_TEAMS.contains(team1) || INTERNATIONAL_TEAMS.contains(team2)) && !status.equals("Match not started")) {
                                String info = team1 + " vs " + team2;
                                matchList.add(info);
                                Log.d("MATCH_ADDED", "Added match: " + info+" ,"+status);
                                adapter.notifyItemInserted(matchList.size() - 1);
                            }
                        }
                        Log.d("FINAL_MATCH_LIST", "Matches in list: " + matchList.size());
                    }
                    else
                    {
                        Log.e("JSON_ERROR2", "No data key in JSON response");
                        Toast.makeText(home.this, "No 'data' key in JSON response", Toast.LENGTH_SHORT).show();
                    }
                }
                catch (JSONException e)
                {
                    Log.e("JSON_ERROR", "Error parsing JSON", e);
                    Toast.makeText(home.this, "JSON parsing error", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(VolleyError error)
            {
                Log.e("API_ERROR", "Error fetching data", error);
                Toast.makeText(home.this,"Error fetching data",Toast.LENGTH_SHORT).show();
            }
        });
    }
}