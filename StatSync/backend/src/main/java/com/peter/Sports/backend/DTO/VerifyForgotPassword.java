package com.peter.Sports.backend.DTO;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VerifyForgotPassword {
    @Size(min = 3)
    private String email;
    private String verificationCode;
}
