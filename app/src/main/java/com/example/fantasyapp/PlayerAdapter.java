package com.example.fantasyapp;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class PlayerAdapter extends RecyclerView.Adapter<PlayerAdapter.PlayerViewHolder> {

    private List<Player> playerList;
    private Context context;
    String team_1;
    String team_2;


    private OnPlayerSelectedListener listener;

    public PlayerAdapter(Context context, List<Player> playerList,String team_1,String team_2) {
        this.context=context;
        this.team_1=team_1;
        this.team_2=team_2;
        this.playerList=playerList;

        if (context instanceof OnPlayerSelectedListener) {
            listener = (OnPlayerSelectedListener) context;
        }

    }

    @NonNull
    @Override
    public PlayerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_player_layout_create_team, parent, false);
        return new PlayerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlayerAdapter.PlayerViewHolder holder, int position) {
        Player player = playerList.get(position);
        Log.d("role in adapter", "onBindViewHolder: "+player.getRole());

        Picasso.get()
                .load(player.getPlayerImageUrl())
                .error(R.drawable.usericon)
                .into(holder.playerImage);

        holder.playerName.setText(player.getPlayerName());
        String teamName = player.getShortCountryName();

        if (teamName.equals(team_1)) {
            holder.playerCountry.setBackgroundResource(R.drawable.verified_background);  // Set background for Team 1
        } else if (teamName.equals(team_2)) {
            holder.playerCountry.setBackgroundResource(R.drawable.gradient_background);  // Set background for Team 2
        } else {
            holder.playerCountry.setBackgroundResource(R.drawable.gradient_background);  // Default background color
        }

        holder.playerCountry.setText(teamName);


        if(player.getSelected())
        {
            holder.itemView.setBackgroundColor(Color.parseColor("#DFFFD6"));
            holder.addRemoveBtn.setBackgroundResource(R.drawable.decrement);
        } else {
            holder.itemView.setBackgroundColor(Color.parseColor("#FFFFFF"));
            holder.addRemoveBtn.setBackgroundResource(R.drawable.baseline_add_circle_outline_24);
        }

        
        holder.itemView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(player.getSelected())
                {
                    player.setSelected(false);
                    onPlayerDeselected(player);
                    Log.d("selected count", "" +listener.getTotalSelectedPlayers() + " MAX: "+listener.getMaxPlayers());
                    holder.itemView.setBackgroundColor(Color.parseColor("#FFFFFF"));
                    holder.addRemoveBtn.setBackgroundResource(R.drawable.baseline_add_circle_outline_24);
                }
                else {
                    if(listener.getTotalSelectedPlayers() < listener.getMaxPlayers()) {
                        player.setSelected(true);
                        onPlayerSelected(player);
                        Log.d("selected count", "" +listener.getTotalSelectedPlayers() + " MAX: "+listener.getMaxPlayers());
                        holder.itemView.setBackgroundColor(Color.parseColor("#DFFFD6"));
                        holder.addRemoveBtn.setBackgroundResource(R.drawable.decrement);
                    } else {
                        Toast.makeText(context, "You can only select up to 11 players", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        holder.addRemoveBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(player.getSelected())
                {
                    player.setSelected(false);
                    onPlayerDeselected(player);
                    Log.d("selected count", "" +listener.getTotalSelectedPlayers() + " MAX: "+listener.getMaxPlayers());
                    holder.itemView.setBackgroundColor(Color.parseColor("#FFFFFF"));
                    holder.addRemoveBtn.setBackgroundResource(R.drawable.baseline_add_circle_outline_24);
                }
                else {
                    if(listener.getTotalSelectedPlayers() < listener.getMaxPlayers()) {
                        player.setSelected(true);
                        onPlayerSelected(player);
                        Log.d("selected count", "" +listener.getTotalSelectedPlayers() + " MAX: "+listener.getMaxPlayers());
                        holder.itemView.setBackgroundColor(Color.parseColor("#DFFFD6"));
                        holder.addRemoveBtn.setBackgroundResource(R.drawable.decrement);
                    } else {
                        Toast.makeText(context, "You can only select up to 11 players", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }



    private void onPlayerSelected(Player player) {
        if (listener != null) {
            listener.onPlayerSelected(player);
        }
        Log.d("PlayerAdapter", "Player selected: " + player.getPlayerName());
        //updateTabTitle(); // Call to update tab title
    }

    private void onPlayerDeselected(Player player) {
        if (listener != null) {
            listener.onPlayerDeselected(player);
        }
        //updateTabTitle(); // Call to update tab title
    }



    @Override
    public int getItemCount() {
        return playerList.size();
    }

    static class PlayerViewHolder extends RecyclerView.ViewHolder {
        ImageView playerImage;
        TextView playerName, playerCountry;
        AppCompatButton addRemoveBtn;

        public PlayerViewHolder(@NonNull View itemView) {
            super(itemView);
            playerImage = itemView.findViewById(R.id.player_image);
            playerName = itemView.findViewById(R.id.player_name);
            playerCountry = itemView.findViewById(R.id.player_country);
            addRemoveBtn = itemView.findViewById(R.id.addRemoveBtn);
        }
    }
}
