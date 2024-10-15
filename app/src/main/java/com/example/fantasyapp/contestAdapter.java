package com.example.fantasyapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Map;

public class contestAdapter extends RecyclerView.Adapter<contestAdapter.contestViewHolder> {

    private Context context;
    private List<Map<String, Object>> contestsList;

    public contestAdapter(Context context, List<Map<String, Object>> contestsList) {

        this.context = context;
        this.contestsList = contestsList;
    }

    @NonNull
    @Override
    public contestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contest_layout, parent, false);
        return new contestAdapter.contestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull contestViewHolder holder, int position) {
        Map<String, Object> contest = contestsList.get(position);

        // Bind data to the views in the contestViewHolder
        String contestTypeStr = contest.get("contestType").toString();
        String prizePoolStr = contest.get("prize_pool").toString();
        String totalSpotsStr = contest.get("max_teams").toString();
        String matchIdStr = contest.get("match_id").toString();
        String contestIdStr = contest.get("contest_id").toString();
        List<String> teamIds = (List<String>) contest.get("team_ids");
        String entryFeeStr = contest.get("entry_fee").toString();
        String numberOfWinnersStr = contest.get("numberOfWinners").toString();

        int firstPrizeInt = Integer.parseInt(prizePoolStr) / 2;

        int numberOfRegisteredTeams = teamIds != null ? teamIds.size() : 0;

        Log.d("teamRegistered", "onBindViewHolder: "+numberOfRegisteredTeams);


        int spotsLeftInt = Integer.parseInt(totalSpotsStr) - numberOfRegisteredTeams;

        int totalSpotsInt = Integer.parseInt(totalSpotsStr);
        int numberOfWinnersInt = Integer.parseInt(numberOfWinnersStr);

        double winPerc = ((double) numberOfWinnersInt /totalSpotsInt)*100;

        holder.prizePool.setText("₹"+prizePoolStr);
        holder.firstPrize.setText("₹"+String.valueOf(firstPrizeInt));
        holder.spotsLeft.setText(String.valueOf(spotsLeftInt)+" spots left");
        holder.totalSpots.setText(totalSpotsStr+" spots");
        holder.entryFeesBtn.setText("₹"+entryFeeStr);
        holder.spotsFilledBar.setMax(totalSpotsInt);
        holder.spotsFilledBar.setProgress(numberOfRegisteredTeams, true);
        holder.winningPercentage.setText(String.valueOf(winPerc)+"%");

        holder.contestCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        holder.entryFeesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, RegisterTeamInContest.class);
                i.putExtra("contest_id",contestIdStr);
                i.putExtra("match_id",matchIdStr);
                ((Activity) context).startActivityForResult(i,100);
            }
        });

    }

    @Override
    public int getItemCount() {
        return contestsList.size();
    }

    static class contestViewHolder extends RecyclerView.ViewHolder
    {
        TextView prizePool, spotsLeft, totalSpots, firstPrize, winningPercentage;
        AppCompatButton entryFeesBtn;
        ProgressBar spotsFilledBar;
        LinearLayout contestCard;

        public contestViewHolder(@NonNull View itemView) {
            super(itemView);
            prizePool = itemView.findViewById(R.id.prizePool);
            spotsLeft = itemView.findViewById(R.id.spotsLeft);
            totalSpots = itemView.findViewById(R.id.totalSpots);
            firstPrize = itemView.findViewById(R.id.firstPrize);
            winningPercentage = itemView.findViewById(R.id.winningPercentage);

            entryFeesBtn = itemView.findViewById(R.id.entryFeesBtn);
            spotsFilledBar = itemView.findViewById(R.id.spotsFilledBar);

            contestCard = itemView.findViewById(R.id.contestCard);
        }
    }
}
