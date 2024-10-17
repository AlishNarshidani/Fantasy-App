package com.example.fantasyapp;

import java.io.Serializable;

public class Match implements Serializable {
    private String team1ShortName;
    private String team2ShortName;
    private int team1ImageResId;
    private int team2ImageResId;
    private String score;
    private String id;
    private String series;
    private String matchType;

    public Match(String team1ShortName, String team2ShortName, int team1ImageResId, int team2ImageResId, String score, String id, String series, String matchType) {
        this.team1ShortName = team1ShortName;
        this.team2ShortName = team2ShortName;
        this.team1ImageResId = team1ImageResId;
        this.team2ImageResId = team2ImageResId;
        this.score = score;
        this.id=id;
        this.series=series;
        this.matchType=matchType;
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

    public String getId()
    {
        return id;
    }

    public String getSeries()
    {
        return series;
    }

    public String getMatchType()
    {
        return matchType;
    }
}