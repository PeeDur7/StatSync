package com.peter.Sports.backend.Model;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;



@Document(collection = "users")
@AllArgsConstructor
public class Users {
    @Id
    private String id;

    @NotBlank
    @Indexed(unique = true)
    private String name;

    @NotBlank
    @Email
    @Indexed(unique = true)
    @Size(min = 3)
    private String email;

    @NotBlank
    @Size(min = 6)
    private String password;

    private String role;
    private String refreshToken;
    private List<String> FavoriteNFLPlayerIds;
    private List<String> FavoriteNBAPlayerId;

    private String verificationCode;
    private LocalDateTime verificationCodeExpiredTime;

    public Users(){

    }

    public Users (String name, String email, String password){
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public String getID(){
        return id;
    }

    public String getName(){
        return name;
    }
    
    public String getEmail(){
        return email;
    }
    
    public String getPassword(){
        return password;
    }

    public String getRole(){
        return role;
    }

    public void setName(String name){
        this.name = name;
    }
    
    public void setEmail(String email){
        this.email = email;
    }

    public void setPassword(String password){
        this.password = password;
    }

    public void setRole(String role){
        this.role = role;
    }
    public String getRefreshToken() {
        return refreshToken;
    }
    
    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
    
    public List<String> getFavoriteNFL() {
        return FavoriteNFLPlayerIds;
    }
    
    public void setFavoriteNFL(List<String> FavoriteNFL) {
        this.FavoriteNFLPlayerIds = FavoriteNFL;
    }
    
    public List <String> getFavoriteNBA() {
        return FavoriteNBAPlayerId;
    }
    
    public void setFavoriteNBA(List<String> FavoriteNBA) {
        this.FavoriteNBAPlayerId = FavoriteNBA;
    }

    public String getVerificationCode(){
        return verificationCode;
    }    

    public void setVerificationCode(String verificationCode){
        this.verificationCode = verificationCode;
    }

    public LocalDateTime getVerificationCodeExpiredTime(){
        return verificationCodeExpiredTime;
    }

    public void setVerificationCodeExpiredTime(LocalDateTime verificationCodeExpiredTime){
        this.verificationCodeExpiredTime = verificationCodeExpiredTime;
    }
}

