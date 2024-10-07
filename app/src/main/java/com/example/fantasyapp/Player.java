package com.example.fantasyapp;

import java.io.Serializable;

public class Player implements Serializable {
    private String shortCountryName;
    private String playerId;
    private String playerName;
    private String role;
    private String playerImageUrl;
    private Boolean isSelected = false;

    public Player(String shortCountryName, String playerId, String playerName, String role,String playerImageUrl) {
        this.shortCountryName = shortCountryName;
        this.playerId = playerId;
        this.playerName = playerName;
        this.role = role;
        this.playerImageUrl = playerImageUrl;
    }

    public String getShortCountryName() {
        return shortCountryName;
    }

    public String getPlayerId() {
        return playerId;
    }

    public String getPlayerName() {
        return playerName;
    }

    public String getRole() {
        return role;
    }

    public String getPlayerImageUrl() {
        return playerImageUrl;
    }

    public Boolean getSelected() {
        return isSelected;
    }

    public void setSelected(Boolean selected) {
        isSelected = selected;
    }
}
