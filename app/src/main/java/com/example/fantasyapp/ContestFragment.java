package com.example.fantasyapp;

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
import android.widget.Toast;

import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class ContestFragment extends Fragment {

    RecyclerView recyclerView;
    MatchAdapter adapter;
    ArrayList<Match> matchList;
    CricApiService cricApiService;
    private Set<String> fetchedMatchIds = new HashSet<>();
    int count=0;

    private static final List<String> INTERNATIONAL_TEAMS = Arrays.asList(
            "India", "Australia", "England", "South Africa", "New Zealand", "Pakistan", "Sri Lanka", "West Indies", "Bangladesh", "Afghanistan", "Zimbabwe", "Ireland","United States","Canada"
    );

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contest, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        matchList = new ArrayList<>();
        adapter = new MatchAdapter(getContext(),matchList);
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
        count=count+1;
        Log.d("Count:",String.valueOf(count));
        final int[] offsets={0,25,50,75};
        for(int offset:offsets)
        {
            Log.d("Offset10:",String.valueOf(offset));
            cricApiService.getMatches(offset,new CricApiService.DataCallback() {
                @Override
                public void onSuccess(JSONObject response) {
                    Log.d("offset11:","Going into success");
                    try
                    {
                        Log.d("offset12:", response.toString());
                        String apiStatus=response.getString("status");
                        Log.d("API_STATUS", apiStatus);

//                        if(apiStatus.equals("failure"))
//                        {
//                            Log.d("offset13:","failure");
//                            cricApiService.changeApiKey();
//                            fetchMatches();
//                            return;
//                        }

                        if(response.has("data"))
                        {
                            Log.d("offset14:","data");
                            JSONArray matches=response.getJSONArray("data");
                            Log.d("MATCH_COUNT", "Total matches: " + matches.length());

                            for(int i=0;i<matches.length();i++)
                            {
                                JSONObject match=matches.getJSONObject(i);
                                String matchId = match.getString("id");

//                                if (fetchedMatchIds.contains(matchId))
//                                {
//                                    Log.d("DUPLICATE_MATCH", "Duplicate match found: " + matchId);
//                                    continue;
//                                }
//
//                                fetchedMatchIds.add(matchId);

                                JSONArray teams=match.getJSONArray("teams");
                                String team1=teams.getString(0);
                                String team2=teams.getString(1);
                                String status=match.getString("status");
                                String matchStarted=match.getString("matchStarted");
                                String matchEnded=match.getString("matchEnded");
                                String name=match.getString("name");
                                String id=match.getString("id");

                                JSONObject teamInfo1 = match.getJSONArray("teamInfo").getJSONObject(0);
                                JSONObject teamInfo2 = match.getJSONArray("teamInfo").getJSONObject(1);

                                String team1ShortName = teamInfo1.has("shortname") ? teamInfo1.getString("shortname") : "Unknown";
                                String team2ShortName = teamInfo2.has("shortname") ? teamInfo2.getString("shortname") : "Unknown";

                                int team1ImageResId = getTeamImageResId(team1ShortName);
                                int team2ImageResId = getTeamImageResId(team2ShortName);

//                            String score = match.getJSONArray("score").getJSONObject(0).getString("r") + "/" +
//                                    match.getJSONArray("score").getJSONObject(0).getString("w") + " (" +
//                                    match.getJSONArray("score").getJSONObject(0).getString("o") + " ov)";

                                Log.d("MATCH_INFO", "Match: " + team1 + " vs " + team2+" matchStarted: " + matchStarted + ", matchEnded: " + matchEnded+" "+matches.length());
                                Log.d("MATCH_STATUS", "matchStarted: " + matchStarted + ", matchEnded: " + matchEnded);

                            if((INTERNATIONAL_TEAMS.contains(team1) || INTERNATIONAL_TEAMS.contains(team2)) && !status.equals("Match not started") && matchStarted.equals("true") && matchEnded.equals("false") && !status.contains("No result")) {
                                Match matchData = new Match(team1ShortName, team2ShortName, team1ImageResId, team2ImageResId, "LIVE",id);
                                matchList.add(matchData);
                                Log.d("MATCH_ADDED", "Added match: " + team1ShortName + " vs " + team2ShortName + " ," + status);
                                adapter.notifyItemInserted(matchList.size() - 1);
                            }

//                                if(matchStarted.equals("true") && matchEnded.equals("false") && !status.contains("No result") )
//                                {
//                                    Match matchData = new Match(team1ShortName, team2ShortName, team1ImageResId, team2ImageResId, "LIVE");
//                                    matchList.add(matchData);
//                                    Log.d("MATCH_ADDED", "Added match: " + team1ShortName + " vs " + team2ShortName + " matchStarted: " + matchStarted + ", matchEnded: " + matchEnded+", status:"+status+", name:"+name);
//                                    adapter.notifyItemInserted(matchList.size() - 1);
//                                }
//                                else
//                                {
//                                    Log.d("MATCH_FILTERED_OUT", "Filtered out match: " + team1ShortName + " vs " + team2ShortName + " ," + status);
//                                }
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
    }

    private int getTeamImageResId(String teamShortName) {
        switch (teamShortName) {
            case "RSA":
                return R.drawable.rsa;
            case "WI":
                return R.drawable.wi;
            case "AFG":
                return R.drawable.afg;
            case "AUS":
                return R.drawable.aus;
            case "BAN":
                return R.drawable.ban;
            case "IND":
                return R.drawable.ind;
            case "IRE":
                return R.drawable.ire;
            case "NZ":
                return R.drawable.nz;
            case "PAK":
                return R.drawable.pak;
            case "SL":
                return R.drawable.sl;
            case "ENG":
                return R.drawable.eng;
            default:
                return R.drawable.eng;
        }
    }
}