package com.peter.Sports.backend.DTO;

import jakarta.validation.constraints.Size;

public class ForgotPasswordsConfirmation {
    @Size(min = 3)
    private String email;

    @Size(min = 6)
    private String newPassword;
    
    @Size(min = 6)
    private String confirmPassword;
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getNewPassword() {
        return newPassword;
    }
    
    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
    
    public String getConfirmPassword() {
        return confirmPassword;
    }
    
    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}