package com.example.fantasyapp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;


public class ContestRankingsFragment extends Fragment {

    ArrayList<String> teamIds;
    String match_id;
    RecyclerView recyclerViewRankings;
    RecyclerView recyclerViewRankingsOfCurrentUser;

    RankingsAdapter adapter;
    RankingsAdapter adapterOfCurrentUser;

    CricApiService cricApiService;

    Map<String, Integer> pointsMap;
    FirebaseAuth auth;
    FirebaseFirestore db;
    int totalTeams;


    List<List<String>> allTeams;

    List<List<Object>> myTeams;
    List<List<Object>> finalTeamPointsList;

    Match match;

    public ContestRankingsFragment(ArrayList<String> teamIds, String match_id, Match match) {
        this.teamIds = teamIds;
        this.match_id = match_id;
        this.match = match;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_contest_rankings, container, false);

        recyclerViewRankings = view.findViewById(R.id.recyclerViewRankings);
        recyclerViewRankingsOfCurrentUser = view.findViewById(R.id.recyclerViewRankingsOfCurrentUser);

        db= FirebaseFirestore.getInstance();
        auth= FirebaseAuth.getInstance();
        totalTeams = teamIds.size();

        cricApiService = new CricApiService(getContext());
        pointsMap = new HashMap<>();
        myTeams = new ArrayList<>();
        allTeams = new ArrayList<>();
        finalTeamPointsList = new ArrayList<>();


        getFantasyPointsOfEachPlayer(match_id, new OnFantasyPointFetchCompleteListener() {
            @Override
            public void onSuccess() {
                final AtomicInteger completedTeams = new AtomicInteger(0);

                for(int i=0;i<teamIds.size();i++)
                {

                    String team_id = teamIds.get(i);

                    getEachTeamPlayers(team_id, new OnTeamFetchCompleteListener() {
                        @Override
                        public void onSuccess() {

                            Log.d("myTeamSize", "myTeamSize: "+myTeams.size());
                            Log.d("allTeamSize", "allTeamSize: "+allTeams.size());

                            if(completedTeams.incrementAndGet() == totalTeams)
                            {
                                calculateTeamPoints();

                            }

                        }

                        @Override
                        public void onFailure() {

                        }
                    });

                }

            }

            @Override
            public void onFailure() {

            }
        });


        return view;
    }

    public void getFantasyPointsOfEachPlayer(String match_id, OnFantasyPointFetchCompleteListener listener)
    {
        Log.d("1", "getFantasyPointsOfEachPlayer: fetching data");
        cricApiService.getFantasyPoints(match_id, new CricApiService.DataCallback() {
            @Override
            public void onSuccess(JSONObject response) {

                try{

                    Log.d("API_RESPONSE", response.toString());
                    String apiStatus = response.getString("status");
                    Log.d("API_STATUS", apiStatus);


                    if(apiStatus.equals("success") && response.has("data"))
                    {
                        JSONObject dataObject = response.getJSONObject("data");

                        if(dataObject.has("totals"))
                        {
                            JSONArray totalsArray = dataObject.getJSONArray("totals");

                            for (int i = 0; i < totalsArray.length(); i++) {

                                JSONObject total = totalsArray.getJSONObject(i);
                                String id = total.getString("id");
                                String name = total.getString("name");
                                int points = total.getInt("points");
                                pointsMap.put(id, points);
                            }

                            listener.onSuccess();
                        }
                    }



                } catch (JSONException e){
                    Log.e("JSON_ERROR_SQUAD", "Error parsing JSON", e);
                    Toast.makeText(getContext(), "JSON parsing error", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(VolleyError error) {

            }
        });
    }

    public void getEachTeamPlayers(String team_id, OnTeamFetchCompleteListener listener)
    {
        FirebaseUser user=auth.getCurrentUser();
        String userId=user.getUid();

        DocumentReference teamDocRef = db.collection("teams").document(team_id);

        teamDocRef.get().addOnSuccessListener(documentSnapshot -> {

            if(documentSnapshot.exists()) {

                Map<String, Object> teamData = documentSnapshot.getData();

                String fetchedUserId = (String) teamData.get("userId");


                List<String> team = new ArrayList<>();

                Map<String, Object> captainData = (Map<String, Object>) teamData.get("captain");
                String captainId = (String) captainData.get("playerId");
                team.add(captainId);

                Map<String, Object> viceCaptainData = (Map<String, Object>) teamData.get("viceCaptain");
                String viceCaptainId = (String) viceCaptainData.get("playerId");
                team.add(viceCaptainId);


                List<Map<String, Object>> playersData = (List<Map<String, Object>>) teamData.get("players");

                for (Map<String, Object> playerData : playersData) {

                    String playerId = (String) playerData.get("playerId");

                    team.add(playerId);
                }

                team.add(team_id); // 12th index
                team.add(fetchedUserId); // 13th index




                getUserNameFromUserId(fetchedUserId, new OnUserNameFetchCompleteListener() {
                    @Override
                    public void onSuccess(String userName) {

                        team.add(userName); // 14th index
                        allTeams.add(team);

                        listener.onSuccess();

                    }

                    @Override
                    public void onFailure() {
                        Log.d("getUserNameFromUserId FirestoreError", "onFailure: ");
                    }
                });



            }

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("getEachTeamPlayers FirestoreError", "onFailure: ");
                listener.onFailure();
            }
        });

    }

    public void getUserNameFromUserId(String user_id, OnUserNameFetchCompleteListener listener)
    {
        db.collection("users").document(user_id).get()
                .addOnSuccessListener(documentSnapshot -> {

                    if(documentSnapshot.exists()) {

                        Map<String, Object> userData = documentSnapshot.getData();
                        String userName = (String) userData.get("name");
                        listener.onSuccess(userName);

                    } else {
                        listener.onFailure();
                    }

                }).addOnFailureListener(e -> {
                    Log.d("getUserName FirestoreError", "Failed to fetch user data: " + e.getMessage());
                    listener.onFailure();
                });
    }

    public void calculateTeamPoints()
    {
        for(List<String> team : allTeams)
        {
            String captainId = team.get(0);
            String viceCaptainId = team.get(1);

            String teamId = team.get(11);
            String userId = team.get(12);
            String userName = team.get(13);

            double teamPoints = 0;

            // Captain points (2x multiplier)
            teamPoints += pointsMap.getOrDefault(captainId, 0) * 2;

            // Vice-captain points (1.5x multiplier)
            teamPoints += pointsMap.getOrDefault(viceCaptainId, 0) * 1.5;

            // Points for other players
            for (int i = 2; i <=11 ; i++) {
                String playerId = team.get(i);
                teamPoints += pointsMap.getOrDefault(playerId, 0);
            }



            List<Object> teamData = new ArrayList<>();
            teamData.add(teamPoints); // 0 index
            teamData.add(teamId); // 1 index
            teamData.add(userId); // 2 index
            teamData.add(userName); // 3 index

            finalTeamPointsList.add(teamData);
        }

        finalTeamPointsList.sort((team1,team2) -> Double.compare((double) team2.get(0), (double) team1.get(0)));

        //String ans="";

        for(List<Object> list : finalTeamPointsList)
        {
            double points = (double) list.get(0);
            String teamId = (String) list.get(1);
            String userId = (String) list.get(2);
            String userName = (String) list.get(3);

            Log.d("calculateTeamPoints", "user: "+userName+" points: "+points+" teamId: "+teamId+" userId: "+userId);

            //ans = ans + "user: "+userName+" points: "+points+" teamId: "+teamId+" userId: "+userId + "\n";
        }

        addTeamRanks();

    }

    public void addTeamRanks()
    {
        FirebaseUser user=auth.getCurrentUser();
        String userId=user.getUid();

        int rank = 1;
        for(List<Object> list : finalTeamPointsList)
        {
            list.add(rank); // 4 index
            rank++;

            String fetchedUserId = (String) list.get(2);

            if(fetchedUserId.equals(userId))
            {
                list.add("myTeam"); // 5 index
                myTeams.add(list);
            } else {
                list.add("otherTeam"); // 5 index
            }
        }

        adapterOfCurrentUser = new RankingsAdapter(getContext(),myTeams,pointsMap, match);
        recyclerViewRankingsOfCurrentUser.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewRankingsOfCurrentUser.setAdapter(adapterOfCurrentUser);


        adapter = new RankingsAdapter(getContext(),finalTeamPointsList,pointsMap, match);
        recyclerViewRankings.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewRankings.setAdapter(adapter);
    }



    public interface OnUserNameFetchCompleteListener {
        void onSuccess(String userName);
        void onFailure();
    }

    public interface OnFantasyPointFetchCompleteListener {
        void onSuccess();
        void onFailure();
    }

    public interface OnTeamFetchCompleteListener {
        void onSuccess();
        void onFailure();
    }
}