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

    public CapVcAdapter(Context context, ArrayList<Player> playerList) {
        this.context = context;
        this.playerList = playerList;
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

        return convertView;
    }
}
