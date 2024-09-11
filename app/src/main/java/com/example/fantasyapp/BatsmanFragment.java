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

import java.util.ArrayList;

public class BatsmanFragment extends Fragment {

    RecyclerView recyclerView;
    PlayerAdapter playerAdapter;
    ArrayList<Player> batsmanList;

    public BatsmanFragment(ArrayList<Player> batsmanList) {
        this.batsmanList=batsmanList;

        Log.d("size of array", "BatsmanFragment: "+batsmanList.size());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_batsman, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewLive);
        playerAdapter = new PlayerAdapter(getContext(), batsmanList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(playerAdapter);

        return view;
    }
}