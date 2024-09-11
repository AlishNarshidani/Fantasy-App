package com.example.fantasyapp;

import java.io.Serializable;

public class Player implements Serializable {
    private String shortCountryName;
    private String playerId;
    private String playerName;
    private String role;

    public Player(String shortCountryName, String playerId, String playerName, String role) {
        this.shortCountryName = shortCountryName;
        this.playerId = playerId;
        this.playerName = playerName;
        this.role = role;
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
}
