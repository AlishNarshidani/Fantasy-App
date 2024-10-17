package com.example.fantasyapp;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class RegisterTeamAdapter extends RecyclerView.Adapter<RegisterTeamAdapter.RegisterTeamViewHolder>{


    private Context context;
    private List<List<Player>> listOfAllTeams;
    private String match_id, user_id;

    String registerTeamId = "";

    int registerPos = -1;

    public RegisterTeamAdapter(Context context, List<List<Player>> listOfAllTeams, String match_id, String user_id) {
        this.context = context;
        this.listOfAllTeams = listOfAllTeams;
        this.match_id = match_id;
        this.user_id = user_id;
    }

    @NonNull
    @Override
    public RegisterTeamViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_register_team_layout, parent, false);
        return new RegisterTeamAdapter.RegisterTeamViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RegisterTeamViewHolder holder, int position) {

        List<Player> team = listOfAllTeams.get(position);

        String teamIdStr = "Team " + String.valueOf(position+1);

        holder.teamIdTextView.setText(teamIdStr);

        Player captain = team.get(0);
        Player viceCaptain = team.get(1);

        // Load captain and vice-captain images using Glide or Picasso
        Picasso.get()
                .load(captain.getPlayerImageUrl())
                .error(R.drawable.usericon)
                .into(holder.captainImage);

        Picasso.get()
                .load(viceCaptain.getPlayerImageUrl())
                .error(R.drawable.usericon)
                .into(holder.viceCaptainImage);

        holder.captainName.setText(captain.getPlayerName());
        holder.viceCaptainName.setText(viceCaptain.getPlayerName());

        if(registerPos == position)
        {
            holder.teamSelectedTicker.setImageResource(R.drawable.baseline_verified_24);
            holder.teamSelectedLayout.setBackgroundResource(R.drawable.verified_background);
        }
        else
        {
            holder.teamSelectedTicker.setImageResource(0);
            holder.teamSelectedLayout.setBackgroundResource(0);
        }

        holder.teamCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerTeamId = match_id + "_" + user_id + "_" + "team" + String.valueOf(holder.getAdapterPosition() + 1);
                Log.d("registerTeamId", "in Adapter onClick: "+registerTeamId);
                registerPos = holder.getAdapterPosition();
                notifyDataSetChanged();

//                Intent intent = new Intent(context, PreviewTeam.class);
//                intent.putExtra("caller", "ViewTeams");
//                intent.putExtra("teamId", "team"+String.valueOf(holder.getAdapterPosition() + 1));
//                intent.putExtra("match_id", match_id);
//                intent.putExtra("team_1", team_1);
//                intent.putExtra("team_2", team_2);
//                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return listOfAllTeams.size();
    }

    static class RegisterTeamViewHolder extends RecyclerView.ViewHolder{

        TextView teamIdTextView;
        ImageView captainImage, viceCaptainImage, teamSelectedTicker;
        TextView captainName, viceCaptainName;
        CardView teamCard;
        LinearLayout teamSelectedLayout;

        public RegisterTeamViewHolder(@NonNull View itemView) {
            super(itemView);

            teamIdTextView = itemView.findViewById(R.id.teamId);
            captainImage = itemView.findViewById(R.id.captainImage);
            viceCaptainImage = itemView.findViewById(R.id.viceCaptainImage);
            captainName = itemView.findViewById(R.id.captainName);
            viceCaptainName = itemView.findViewById(R.id.viceCaptainName);
            teamCard = itemView.findViewById(R.id.team_card);
            teamSelectedTicker = itemView.findViewById(R.id.teamSelectedTicker);
            teamSelectedLayout = itemView.findViewById(R.id.teamSelectedLayout);
        }
    }

    public int getSelectedPosition() {
        return registerPos;
    }
}
