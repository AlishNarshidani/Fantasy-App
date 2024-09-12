package com.example.fantasyapp;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class PlayerAdapter extends RecyclerView.Adapter<PlayerAdapter.PlayerViewHolder> {

    private List<Player> playerList;
    private Context context;

    public PlayerAdapter(Context context, List<Player> playerList) {
        this.context=context;
        this.playerList=playerList;
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
        holder.playerCountry.setText(player.getShortCountryName());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(player.getSelected())
                {
                    player.setSelected(false);
                    holder.itemView.setBackgroundColor(Color.parseColor("#FFFFFF"));
                }
                else {
                    player.setSelected(true);
                    holder.itemView.setBackgroundColor(Color.parseColor("#DFFFD6"));
                }
            }
        });

        holder.addRemoveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(player.getSelected())
                {
                    player.setSelected(false);
                    holder.itemView.setBackgroundColor(Color.parseColor("#FFFFFF"));
                }
                else {
                    player.setSelected(true);
                    holder.itemView.setBackgroundColor(Color.parseColor("#DFFFD6"));
                }
            }
        });
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
