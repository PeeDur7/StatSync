package com.peter.Sports.backend.DTO;


import jakarta.validation.constraints.NotNull;


public class NFLPlayerFavorite {
    @NotNull
    private String playerId;

    public NFLPlayerFavorite(){

    }

    public String getPlayerId(){
        return playerId;
    }


    public void setPlayerId(String playerId){
        this.playerId = playerId;
    }
    
}
