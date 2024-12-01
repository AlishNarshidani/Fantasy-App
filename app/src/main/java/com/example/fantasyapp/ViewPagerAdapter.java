package com.example.fantasyapp;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import java.util.ArrayList;

public class ViewPagerAdapter extends FragmentStateAdapter {

    private ArrayList<Match> liveMatches;
    private ArrayList<Match> upcomingMatches;
    private ArrayList<Match> recentMatches;


    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity, ArrayList<Match> liveMatches, ArrayList<Match> upcomingMatches,ArrayList<Match> recentMatches) {
        super(fragmentActivity);
        this.liveMatches = liveMatches;
        this.upcomingMatches = upcomingMatches;
        this.recentMatches = recentMatches;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new LiveMatchesFragment(liveMatches);
            case 1:
                return new UpcomingMatchesFragment(upcomingMatches);
            case 2:
                return new RecentMatchesFragment(recentMatches);
            default:
                return new LiveMatchesFragment(liveMatches);
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}