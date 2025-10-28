package com.peter.Sports.backend.DTO;

import org.springframework.data.annotation.Id;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

//Data Transfer Object
//Sent from the frontend during login, not saved in the db
@Data
@Getter
@Setter
public class AuthRequest {
    @Id
    private String id;

    @NotBlank
    private String identifier;

    @NotBlank
    @Size(min = 6)
    private String password;

    private String refreshToken;

    public AuthRequest(){

    }

    public String getID(){
        return id;
    }

    public String getPassword(){
        return password;
    }

    public void setPassword(String password){
        this.password = password;
    }
    public String getRefreshToken() {
        return refreshToken;
    }
    
    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getIdentifier(){
        return identifier;
    }
    
    public void setIdentifier(String identifier){
        this.identifier = identifier;
    }

}
