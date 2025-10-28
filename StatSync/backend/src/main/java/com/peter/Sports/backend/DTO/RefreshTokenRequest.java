package com.peter.Sports.backend.DTO;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class RefreshTokenRequest {
    private String refreshToken;

    public RefreshTokenRequest() {}

    public RefreshTokenRequest(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}