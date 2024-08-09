package com.example.fantasyapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MatchAdapter extends RecyclerView.Adapter<MatchAdapter.MatchViewHolder> {

    private List<String> matchList;

    public MatchAdapter(List<String> matchList) {
        this.matchList = matchList;
    }

    @NonNull
    @Override
    public MatchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_match, parent, false);
        return new MatchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MatchViewHolder holder, int position) {
        String match = matchList.get(position);
        holder.matchTextView.setText(match);
    }

    @Override
    public int getItemCount() {
        return matchList.size();
    }

    static class MatchViewHolder extends RecyclerView.ViewHolder {
        TextView matchTextView;

        public MatchViewHolder(@NonNull View itemView) {
            super(itemView);
            matchTextView = itemView.findViewById(R.id.matchTextView);
        }
    }
}