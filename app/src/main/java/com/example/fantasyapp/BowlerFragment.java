package com.example.fantasyapp;

import android.os.Bundle;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class BowlerFragment extends Fragment {

    RecyclerView recyclerView;
    PlayerAdapter playerAdapter;
    ArrayList<Player> bowlerList;
    String team_1;
    String team_2;

    public BowlerFragment(ArrayList<Player> bowlerList,String team_1,String team_2) {
        this.bowlerList=bowlerList;
        this.team_1=team_1;
        this.team_2=team_2;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_bowler, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewLive);
        playerAdapter = new PlayerAdapter(getContext(), bowlerList,team_1,team_2);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(playerAdapter);

        return view;
    }
}