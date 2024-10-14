package com.example.fantasyapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class TeamShortViewAdapter extends RecyclerView.Adapter<TeamShortViewAdapter.TeamViewHolder> {

    private List<List<Player>> listOfAllTeams;
    private Context context;
    private String team_1, team_2, match_id;

    public TeamShortViewAdapter(Context context, List<List<Player>> listOfAllTeams, String team_1, String team_2, String match_id) {
        this.context = context;
        this.listOfAllTeams = listOfAllTeams;
        this.team_1 = team_1;
        this.team_2 = team_2;
        this.match_id = match_id;
    }

    @NonNull
    @Override
    public TeamViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_short_team_view, parent, false);
        return new TeamViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TeamViewHolder holder, int position) {
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

        holder.teamCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PreviewTeam.class);
                intent.putExtra("caller", "ViewTeams");
                intent.putExtra("teamId", "team"+String.valueOf(holder.getAdapterPosition() + 1));
                intent.putExtra("match_id", match_id);
                intent.putExtra("team_1", team_1);
                intent.putExtra("team_2", team_2);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return listOfAllTeams.size();
    }

    static class TeamViewHolder extends RecyclerView.ViewHolder{

        TextView teamIdTextView;
        ImageView captainImage, viceCaptainImage;
        TextView captainName, viceCaptainName;
        CardView teamCard;

        public TeamViewHolder(@NonNull View itemView) {
            super(itemView);
            teamIdTextView = itemView.findViewById(R.id.teamId);
            captainImage = itemView.findViewById(R.id.captainImage);
            viceCaptainImage = itemView.findViewById(R.id.viceCaptainImage);
            captainName = itemView.findViewById(R.id.captainName);
            viceCaptainName = itemView.findViewById(R.id.viceCaptainName);
            teamCard = itemView.findViewById(R.id.team_card);
        }
    }
}
