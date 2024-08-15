package com.example.fantasyapp;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;


public class ProfileFragment extends Fragment {

    ImageButton userButton,walletButton,rulesButton,contectButton,termButton,logoutButton;
    TextView email, userEmail;


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

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        String email = sharedPreferences.getString("email", "user");

        userEmail = view.findViewById(R.id.textView4);
        userEmail.setText(email);

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


        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                AlertDialog.Builder alert = new AlertDialog.Builder(getActivity())
                        .setTitle("EXIT ?")
                        .setIcon(R.drawable.baseline_person_24)
                        .setMessage("Are you Sure you want to log out ?")
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyAppPrefs", MODE_PRIVATE);

                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.remove("isLoggedIn");
                                editor.apply();

                                Intent intent = new Intent(getActivity(), MainActivity.class);
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });

                alert.setCancelable(false);

                alert.show();
            }
        });
        return view;
    }
}