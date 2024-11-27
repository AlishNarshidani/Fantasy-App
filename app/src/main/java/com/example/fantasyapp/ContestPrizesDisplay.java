package com.example.fantasyapp;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class ContestPrizesDisplay extends Fragment {

    ArrayList<String> prizesList;
    LinearLayout rankingsContainer;

    public ContestPrizesDisplay(ArrayList<String> prizesList) {

        this.prizesList = prizesList;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_contest_prizes_display, container, false);

        rankingsContainer = view.findViewById(R.id.rankings_container);

        for(int i=0;i<prizesList.size();i++)
        {
            // Create a container layout for each item
            LinearLayout itemLayout = new LinearLayout(getContext());
            itemLayout.setOrientation(LinearLayout.HORIZONTAL);
            itemLayout.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            itemLayout.setPadding(0, 16, 0, 16);

            // Set background color based on odd/even index
            if (i % 2 == 0) {
                itemLayout.setBackgroundColor(Color.parseColor("#FFFFFF")); // White for even rows
            } else {
                itemLayout.setBackgroundColor(Color.parseColor("#F5F5F5")); // Light gray for odd rows
            }

            // Create TextView for the rank
            TextView rankTextView = new TextView(getContext());
            rankTextView.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
            rankTextView.setText("Rank "+(i+1));
            rankTextView.setTextSize(16);
            rankTextView.setTextColor(Color.BLACK);
            rankTextView.setPadding(16, 0, 16, 0);


            // Create TextView for the amount
            TextView amountTextView = new TextView(getContext());
            amountTextView.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            amountTextView.setText("₹"+prizesList.get(i));
            amountTextView.setTextSize(16);
            amountTextView.setTextColor(Color.BLACK);
            amountTextView.setPadding(16, 0, 16, 0);


            // Add the TextViews to the item layout
            itemLayout.addView(rankTextView);
            itemLayout.addView(amountTextView);

            // Add the item layout to the main container
            rankingsContainer.addView(itemLayout);

        }

        return view;
    }
}