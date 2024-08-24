package com.example.fantasyapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class ContestFragment extends Fragment {

    RecyclerView recyclerView;
    MatchAdapter adapter;
    ArrayList<Match> matchList;
    CricApiService cricApiService;



    private static final List<String> INTERNATIONAL_TEAMS = Arrays.asList(
            "India", "Australia", "England", "South Africa", "New Zealand", "Pakistan", "Sri Lanka", "West Indies", "Bangladesh", "Afghanistan", "Zimbabwe", "Ireland"
    );

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contest, container, false);



        recyclerView = view.findViewById(R.id.recyclerView);
        matchList = new ArrayList<>();
        adapter = new MatchAdapter(matchList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        cricApiService = new CricApiService(getContext());
        fetchMatches();

        ViewCompat.setOnApplyWindowInsetsListener(view.findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        return view;
    }

    private void fetchMatches()
    {
        cricApiService.getMatches(new CricApiService.DataCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                try
                {
                    Log.d("API_RESPONSE", response.toString());
                    String apiStatus=response.getString("status");
                    Log.d("API_STATUS", apiStatus);

                    if(apiStatus.equals("failure"))
                    {
                        cricApiService.changeApiKey();
                        fetchMatches();
                        return;
                    }

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
                            String matchStarted=match.getString("matchStarted");
                            String matchEnded=match.getString("matchEnded");

                            JSONObject teamInfo1 = match.getJSONArray("teamInfo").getJSONObject(0);
                            JSONObject teamInfo2 = match.getJSONArray("teamInfo").getJSONObject(1);

                            String team1ShortName = teamInfo1.has("shortname") ? teamInfo1.getString("shortname") : "Unknown";
                            String team2ShortName = teamInfo2.has("shortname") ? teamInfo2.getString("shortname") : "Unknown";

                            int team1ImageResId = getTeamImageResId(team1ShortName);
                            int team2ImageResId = getTeamImageResId(team2ShortName);

                            String score = match.getJSONArray("score").getJSONObject(0).getString("r") + "/" +
                                    match.getJSONArray("score").getJSONObject(0).getString("w") + " (" +
                                    match.getJSONArray("score").getJSONObject(0).getString("o") + " ov)";

                            Log.d("MATCH_INFO", "Match: " + team1 + " vs " + team2+" "+matches.length());
                            Log.d("MATCH_STATUS", "matchStarted: " + matchStarted + ", matchEnded: " + matchEnded);

//                            if((INTERNATIONAL_TEAMS.contains(team1) || INTERNATIONAL_TEAMS.contains(team2)) && !status.equals("Match not started")) {
//                                Match matchData = new Match(team1ShortName, team2ShortName, team1ImageResId, team2ImageResId, score);
//                                matchList.add(matchData);
//                                Log.d("MATCH_ADDED", "Added match: " + team1ShortName + " vs " + team2ShortName + " ," + status);
//                                adapter.notifyItemInserted(matchList.size() - 1);
//                            }

                            if(matchStarted.equals("true") && matchEnded.equals("false") && !status.contains("No result") )
                            {
                                Match matchData = new Match(team1ShortName, team2ShortName, team1ImageResId, team2ImageResId, score);
                                matchList.add(matchData);
                                Log.d("MATCH_ADDED", "Added match: " + team1ShortName + " vs " + team2ShortName + " ," + status);
                                adapter.notifyItemInserted(matchList.size() - 1);
                            }
                        }
                        Log.d("FINAL_MATCH_LIST", "Matches in list: " + matchList.size());
                    }
                    else
                    {
                        Log.e("JSON_ERROR2", "No data key in JSON response");
                        Toast.makeText(getContext(), "No 'data' key in JSON response", Toast.LENGTH_SHORT).show();
                    }
                }
                catch (JSONException e)
                {
                    Log.e("JSON_ERROR", "Error parsing JSON", e);
//                    Toast.makeText(getContext(), "JSON parsing error", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(VolleyError error)
            {
                Log.e("API_ERROR", "Error fetching data", error);
                Toast.makeText(getContext(),"Error fetching data",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private int getTeamImageResId(String teamShortName) {
        switch (teamShortName) {
            case "RSA":
                return R.drawable.rsa;
            case "WI":
                return R.drawable.wi;
            default:
                return R.drawable.eng;
        }
    }
}