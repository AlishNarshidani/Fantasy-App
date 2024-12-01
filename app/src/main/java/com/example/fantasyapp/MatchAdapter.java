package com.example.fantasyapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class  MatchAdapter extends RecyclerView.Adapter<MatchAdapter.MatchViewHolder> {

    private List<Match> matchList;
    private Context context;
    private String matchStatus;

    public MatchAdapter(Context context,List<Match> matchList,String matchStatus) {
        this.matchList = matchList;
        this.context=context;
        this.matchStatus=matchStatus;
    }

    @NonNull
    @Override
    public MatchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_match, parent, false);
        return new MatchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MatchViewHolder holder, int position) {
        Match match = matchList.get(position);
        holder.team1ShortName.setText(match.getTeam1ShortName());
        holder.team2ShortName.setText(match.getTeam2ShortName());
        holder.matchScore.setText(match.getScore());
        holder.team1Logo.setImageResource(match.getTeam1ImageResId());
        holder.team2Logo.setImageResource(match.getTeam2ImageResId());

        holder.itemView.setOnClickListener(v -> {

            if(matchStatus.equals("live"))
            {
                Intent i=new Intent(context,JoinedContests.class);
                i.putExtra("match",match);
                i.putExtra("caller","joined");
                context.startActivity(i);
            }
            else if(matchStatus.equals("upcoming"))
            {
                Intent i=new Intent(context,UpcomingContest.class);
                i.putExtra("match",match);
                context.startActivity(i);
            }
            else if(matchStatus.equals("recent"))
            {
                Intent i=new Intent(context,JoinedContests.class);
                i.putExtra("match",match);
                i.putExtra("caller","recent");
                context.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return matchList.size();
    }

    static class MatchViewHolder extends RecyclerView.ViewHolder {
        ImageView team1Logo, team2Logo;
        TextView team1ShortName, team2ShortName, matchScore;

        public MatchViewHolder(@NonNull View itemView) {
            super(itemView);
            team1Logo = itemView.findViewById(R.id.team1Logo);
            team2Logo = itemView.findViewById(R.id.team2Logo);
            team1ShortName = itemView.findViewById(R.id.team1ShortName);
            team2ShortName = itemView.findViewById(R.id.team2ShortName);
            matchScore = itemView.findViewById(R.id.matchScore);
        }
    }
}