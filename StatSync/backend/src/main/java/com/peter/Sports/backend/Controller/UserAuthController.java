package com.peter.Sports.backend.Controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.peter.Sports.backend.DTO.AuthRequest;
import com.peter.Sports.backend.DTO.AuthResponse;
import com.peter.Sports.backend.DTO.ForgotPasswordsConfirmation;
import com.peter.Sports.backend.DTO.RefreshTokenRequest;
import com.peter.Sports.backend.DTO.VerifyForgotPassword;
import com.peter.Sports.backend.Model.Users;
import com.peter.Sports.backend.Service.AuthenticationService;
import com.peter.Sports.backend.Service.JwtService;
import com.peter.Sports.backend.Service.MyUserDetailService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
public class UserAuthController {
    private final MyUserDetailService service;
    private final AuthenticationService authenticationService;
    private final JwtService jwtService;

    public UserAuthController(MyUserDetailService service,AuthenticationManager authenticationManager,
        AuthenticationService authenticationService, JwtService jwtService) {
        this.service = service;
        this.authenticationService = authenticationService;
        this.jwtService = jwtService;
    }

    @GetMapping("/home")
    public String handleAuthenticatedHome(@AuthenticationPrincipal UserDetails userDetails){
        return userDetails.getUsername();
    }

    @PostMapping("/register")
    public String addUser(@Valid @RequestBody Users user){
         return service.addUser(user);
    }

    @PostMapping("/authenticate")
    public AuthResponse login(@Valid @RequestBody AuthRequest authRequest){
        return authenticationService.authenticatedAndGetToken(authRequest);
    }
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String authHeader){
        String token = authHeader.substring(7);
        return authenticationService.logout(token);
    }

    @GetMapping("/isTokenExpired")
    public Boolean isTokenExpired(@RequestParam String token){
        return jwtService.isTokenExpired(token);
    }

    @PostMapping("/refresh")
    public AuthResponse refresh(@RequestBody RefreshTokenRequest request){
        return authenticationService.refresh(request);
    }

    //in front end make this first
    @GetMapping("/send/password/recovery")
    public ResponseEntity<?> sendForgotPasswordRecovery(@RequestParam String email){
        try {
            authenticationService.sendForgotPasswordRecovery(email);
            return ResponseEntity.ok("Sent Forgot Password");
        } catch (RuntimeException e) {
            if (e.getMessage().equals("User not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
            } else if (e.getMessage().equals("Email has already been sent")) {
                return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body("Email has already been sent. Please wait 15 minutes from last request");
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error");
        }
    }

    //call this service after sentForgotPasswordRecovery and then if valid, call createNewPassword
    @PostMapping("/verify/forgot/password")
    public ResponseEntity<?> verifyCodeAndPassword(@RequestBody VerifyForgotPassword input){
        Boolean verified = authenticationService.verifyVerificationCode(input);
        return verified ? ResponseEntity.ok("Success verifying code") : 
            ResponseEntity.ok("Verification code denied");
    }

    //call this service in the front end if verifyCodeAndPassword is valid
    @PostMapping("/create/new/password")
    public ResponseEntity<?> createNewPassword(@RequestBody ForgotPasswordsConfirmation input){
        Boolean resetPassword = authenticationService.createNewPassword(input);
        return resetPassword ? ResponseEntity.ok("Password has been reset") : 
            ResponseEntity.ok("Did not reset password");
    }


}
