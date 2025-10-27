package com.peter.Sports.backend.DTO;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ForgotPasswordsConfirmation {
    @Size(min = 3)
    private String email;

    @Size(min = 6)
    private String newPassword;
    @Size(min = 6)
    private String confirmPassword;
}
