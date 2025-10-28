package com.peter.Sports.backend.DTO;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
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
