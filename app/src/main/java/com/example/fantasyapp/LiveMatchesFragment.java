package com.example.fantasyapp;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;

public class LiveMatchesFragment extends Fragment {

    RecyclerView recyclerView;
    MatchAdapter adapter;
    ArrayList<Match> matchList;

    public LiveMatchesFragment(ArrayList<Match> matchList) {
        this.matchList = matchList;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_live_matches, container, false);
        recyclerView = view.findViewById(R.id.recyclerViewLive);
        adapter = new MatchAdapter(getContext(), matchList,true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        return view;
    }
}