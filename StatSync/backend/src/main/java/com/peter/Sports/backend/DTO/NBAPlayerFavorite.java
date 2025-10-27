package com.peter.Sports.backend.DTO;

import jakarta.validation.constraints.NotNull;

public class NBAPlayerFavorite {
    @NotNull
    private String playerId;

    public NBAPlayerFavorite() {
    }

    public String getPlayerId() {
        return playerId;
    }

    public void setPlayer(String playerId) {
        this.playerId = playerId;
    }

}

