package com.example.fantasyapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RankingsAdapter extends RecyclerView.Adapter<RankingsAdapter.RankingsViewHolder> {

    private Context context;
    private List<List<Object>> rankingsList;
    private Map<String, Integer> pointsMap;
    private Match match;

    public RankingsAdapter(Context context, List<List<Object>> rankingsList, Map<String, Integer> pointsMap, Match match) {
        this.context = context;
        this.rankingsList = rankingsList;
        this.pointsMap = pointsMap;
        this.match = match;
    }

    @NonNull
    @Override
    public RankingsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rankings_view_layout, parent, false);
        return new RankingsAdapter.RankingsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RankingsViewHolder holder, int position) {


        List<Object> teamData = rankingsList.get(position);
        double points = (double) teamData.get(0);
        String teamId = (String) teamData.get(1);
        String userId = (String) teamData.get(2);
        String userName = (String) teamData.get(3);
        int rank = (int) teamData.get(4);

        String caller = (String) teamData.get(5);
        Log.d("caller", "onBindViewHolder: "+caller);

        String[] parts = teamId.split("_");
        String teamNumber = parts[2];

        holder.team_rank.setText(String.valueOf(rank));
        holder.user_name.setText(userName +" (" +teamNumber +")");
        holder.team_points.setText(String.valueOf(points));

        if(caller.equals("myTeam"))
        {

            holder.itemView.setBackgroundColor(Color.parseColor("#B3F2A3"));

        } else if(caller.equals("otherTeam")){

            holder.itemView.setBackgroundColor(Color.parseColor("#FFFFFF"));
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, ViewFullTeamPoints.class);
                i.putExtra("pointsMap",new HashMap<>(pointsMap));
                i.putExtra("teamId",teamId);
                i.putExtra("rank",rank);
                i.putExtra("user_name",userName);
                i.putExtra("points",points);
                i.putExtra("match",match);
                context.startActivity(i);
            }
        });

    }

    @Override
    public int getItemCount() {
        return rankingsList.size();
    }

    static class RankingsViewHolder extends RecyclerView.ViewHolder {

        TextView team_rank, user_name, team_points;

        public RankingsViewHolder(@NonNull View itemView) {
            super(itemView);
            team_rank = itemView.findViewById(R.id.team_rank);
            user_name = itemView.findViewById(R.id.user_name);
            team_points = itemView.findViewById(R.id.team_points);
        }
    }
}
