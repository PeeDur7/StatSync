package com.peter.Sports.backend.DTO;

import jakarta.validation.constraints.Size;

public class VerifyForgotPassword {
    @Size(min = 3)
    private String email;
    private String verificationCode;
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getVerificationCode() {
        return verificationCode;
    }
    
    public void setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
    }
}