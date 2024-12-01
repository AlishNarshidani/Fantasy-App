package com.example.fantasyapp;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;

public class RankingsViewPagerAdapter extends FragmentStateAdapter {

    private ArrayList<String> prizesList;
    private ArrayList<String> teamIds;
    String match_id;
    Match match;
    String caller;
    String contestId;

    public RankingsViewPagerAdapter(@NonNull FragmentActivity fragmentActivity, ArrayList<String> prizesList, ArrayList<String> teamIds, String match_id, Match match, String contestId, String caller) {
        super(fragmentActivity);
        this.prizesList = prizesList;
        this.teamIds = teamIds;
        this.match_id = match_id;
        this.match = match;
        this.caller = caller;
        this.contestId = contestId;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new ContestRankingsFragment(teamIds, match_id, match, contestId, prizesList, caller);
            case 1:
                return new ContestPrizesDisplay(prizesList);
            default:
                return new ContestRankingsFragment(teamIds, match_id, match, contestId, prizesList, caller);
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
