package com.example.fantasyapp;

public class Match {
    private String team1ShortName;
    private String team2ShortName;
    private int team1ImageResId;
    private int team2ImageResId;
    private String score;

    public Match(String team1ShortName, String team2ShortName, int team1ImageResId, int team2ImageResId, String score) {
        this.team1ShortName = team1ShortName;
        this.team2ShortName = team2ShortName;
        this.team1ImageResId = team1ImageResId;
        this.team2ImageResId = team2ImageResId;
        this.score = score;
    }

    public String getTeam1ShortName() {
        return team1ShortName;
    }

    public String getTeam2ShortName() {
        return team2ShortName;
    }

    public int getTeam1ImageResId() {
        return team1ImageResId;
    }

    public int getTeam2ImageResId() {
        return team2ImageResId;
    }

    public String getScore() {
        return score;
    }
}