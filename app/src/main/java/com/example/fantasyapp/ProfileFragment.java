package com.example.fantasyapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;


public class ProfileFragment extends Fragment {

    ImageButton userButton,walletButton,rulesButton,contectButton,termButton,logoutButton;


    @SuppressLint("WrongViewCast")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        userButton = view.findViewById(R.id.userButton);
        walletButton = view.findViewById(R.id.walletButton);
        rulesButton = view.findViewById(R.id.rulesButton);
        contectButton = view.findViewById(R.id.contectButton);
        termButton = view.findViewById(R.id.termsButton);
        logoutButton = view.findViewById(R.id.logutButton);

        // Set listeners or manipulate UI elements
        rulesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), FantsyPoint.class);
                startActivity(intent);
            }
        });
        userButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ProfiePage.class);
                startActivity(intent);
            }
        });


        return view;
    }
}