package com.example.fantasyapp;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;

public class CreateTeamViewPagerAdapter extends FragmentStateAdapter {

    private ArrayList<Player> batsmanList;
    private ArrayList<Player> bowlerList;
    private ArrayList<Player> wk_BatsmanList;
    private ArrayList<Player> allRounderList;

    public CreateTeamViewPagerAdapter(@NonNull FragmentActivity fragmentActivity, ArrayList<Player> batsmanList,ArrayList<Player> wk_BatsmanList,ArrayList<Player> bowlerList,ArrayList<Player> allRounderList) {
        super(fragmentActivity);
        this.batsmanList=batsmanList;
        this.bowlerList=bowlerList;
        this.wk_BatsmanList=wk_BatsmanList;
        this.allRounderList=allRounderList;

        Log.d("wk size in CTVPA", "CreateTeamViewPagerAdapter: "+wk_BatsmanList.size());
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Log.d("wk size in CTVPA create Fragment", "CreateTeamViewPagerAdapter2: "+wk_BatsmanList.size());
        Log.d("Fragment Position", "Position: " + position);
        switch (position)
        {
            case 0:
                Log.d("a", "called keeper: ");
                return new WicketKeeperFragment(wk_BatsmanList);
            case 1:
                Log.d("a", "called batsman: ");
                return new BatsmanFragment(batsmanList);
            case 2:
                Log.d("a", "called allrounder: ");
                return new AllRounderFragment(allRounderList);
            case 3:
                Log.d("a", "called bowler: ");
                return new BowlerFragment(bowlerList);
            default:
                Log.d("a", "called default: ");
                return new WicketKeeperFragment(wk_BatsmanList);
        }
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}
