package com.example.fantasyapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CapVcAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Player> playerList;
    private int selectedCaptain = -1;
    private int selectedViceCaptain = -1;

    public interface OnPlayerSelectionChangedListener {
        void onCaptainSelected(String captainPos);
        void onViceCaptainSelected(String viceCaptainPos);
    }

    private OnPlayerSelectionChangedListener listener;

    public CapVcAdapter(Context context, ArrayList<Player> playerList, OnPlayerSelectionChangedListener listener) {
        this.context = context;
        this.playerList = playerList;
        this.listener = listener;
    }

    @Override
    public int getCount() {
        return playerList.size();
    }

    @Override
    public Object getItem(int position) {
        return playerList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null)
        {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_select_cap_vc,parent,false);
        }

        Player player = playerList.get(position);
        ImageView playerImage = convertView.findViewById(R.id.playerImage);
        TextView playerName = convertView.findViewById(R.id.playerName);
        TextView playerRole = convertView.findViewById(R.id.playerRole);
        TextView playerCountry = convertView.findViewById(R.id.playerCountry);
        RadioButton radioCaptainButton = convertView.findViewById(R.id.radioCaptainButton);
        RadioButton radioViceCaptainButton = convertView.findViewById(R.id.radioViceCaptainButton);

        Picasso.get()
                .load(player.getPlayerImageUrl())
                .error(R.drawable.usericon)
                .into(playerImage);

        playerName.setText(player.getPlayerName());

        if(player.getRole().equals("Batsman"))
        {
            playerRole.setText("BAT");

        } else if (player.getRole().equals("Bowler")) {
            playerRole.setText("BOWL");
        } else if (player.getRole().equals("WK-Batsman")) {
            playerRole.setText("WK");
        } else {
            playerRole.setText("AR");
        }

        playerCountry.setText(player.getShortCountryName());

        // Set radio button listeners
        radioCaptainButton.setOnClickListener(v -> {
            if(listener!=null)
            {
                if (selectedViceCaptain == position) {
                    setSelectedViceCaptain(-1); // Unset vice-captain if it was this player
                    listener.onViceCaptainSelected("");
                }
                setSelectedCaptain(position);

                listener.onCaptainSelected(String.valueOf(position));
            }
        });

        radioViceCaptainButton.setOnClickListener(v -> {
            if(listener!=null)
            {
                if (selectedCaptain == position) {
                    setSelectedCaptain(-1); // Unset captain if it was this player
                    listener.onCaptainSelected("");
                }
                setSelectedViceCaptain(position);

                listener.onViceCaptainSelected(String.valueOf(position));
            }
        });

        // Set radio button states and colors
        if (position == selectedCaptain) {
            radioCaptainButton.setChecked(true);
            radioCaptainButton.setBackgroundResource(R.drawable.gradient_background);
        } else {
            radioCaptainButton.setChecked(false);
            radioCaptainButton.setBackgroundResource(R.drawable.verified_background);
        }

        if (position == selectedViceCaptain) {
            radioViceCaptainButton.setChecked(true);
            radioViceCaptainButton.setBackgroundResource(R.drawable.gradient_background);
        } else {
            radioViceCaptainButton.setChecked(false);
            radioViceCaptainButton.setBackgroundResource(R.drawable.verified_background);
        }

        return convertView;
    }


    // Methods to update captain and vice-captain
    public void setSelectedCaptain(int position) {
        selectedCaptain = position;
        notifyDataSetChanged(); // Refresh the list view to apply changes
    }

    public void setSelectedViceCaptain(int position) {
        selectedViceCaptain = position;
        notifyDataSetChanged(); // Refresh the list view to apply changes
    }

    public int getSelectedCaptain() {
        return selectedCaptain;
    }

    public int getSelectedViceCaptain() {
        return selectedViceCaptain;
    }

}
