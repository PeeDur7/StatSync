package com.peter.Sports.backend.DTO;

public class AuthResponse {
    private String accessToken;
    private String refreshToken;

    public AuthResponse(){

    }
    public AuthResponse(String accessToken, String refreshToken){
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
    public String getAccessToken(){
        return accessToken;
    }
    public String getRefreshToken(){
        return refreshToken;
    }
}
